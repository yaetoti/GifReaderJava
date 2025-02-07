package com.yaetoti;

import com.yaetoti.bytes.ByteSequenceUtils;
import com.yaetoti.gif.blocks.*;
import com.yaetoti.gif.io.GifOutput;
import com.yaetoti.gif.io.GifReader;
import com.yaetoti.gif.utils.GifLzwUtils;
import com.yaetoti.io.DataInputLE;
import com.yaetoti.io.DataOutputLE;
import com.yaetoti.quantization.MedianCut;
import com.yaetoti.utils.BitUtils;
import com.yaetoti.bytes.ByteSequence;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;

public class MainQuantizationTest {
  public static void main(String[] args) throws IOException {
    //var file = new RandomAccessFile("image1.gif", "r");
    var file = new RandomAccessFile("image.gif", "r");
    //var file = new RandomAccessFile("image2.gif", "r");
    //var file = new RandomAccessFile("ebalo.gif", "r");
    //var file = new RandomAccessFile("ebalo2.gif", "r");
    var input = new DataInputLE(file);

    ArrayList<GifElement> elements = new ArrayList<>();
    GifReader reader = new GifReader(input);

    while (true) {
      var element = reader.ReadElement();
      var type = element.GetElementType();
      elements.add(element);

      if (type == GifElementType.TRAILER) {
        break;
      }

      if (type == GifElementType.COLOR_TABLE) {
        GifColorTable table = element.As();
        if (table.type == GifColorTable.Type.LOCAL) {
          System.out.println("Local color table found. Unsupported.");
          file.close();
          return;
        }
      }
    }

    file.close();



    // Writing


    // Extracting header and LSD
    GifHeader header = elements.get(0).As();
    GifLogicalScreenDescriptor lsd = elements.get(1).As();
    if (!lsd.isGlobalColorTablePresent) {
      System.out.println("Global color table not found. Unsupported.");
      file.close();
      return;
    }

    // Extracting global color palette
    GifColorTable colorTable = elements.get(2).As();
    ByteSequence[] globalPalette = ByteSequence.ArrayOf(colorTable.table, 3);

    // Calculating reduction count
    int tableSize = lsd.globalColorTableSize;
    int reductionCount = BitUtils.GetBitLength(tableSize) - 1;

    // Perform reduction passes
    int usedTableSize = tableSize;

    for (int i = 0; i < reductionCount; ++i) {
      // Reduce palette
      ByteSequence[] reducedPalette = MedianCut.Reduce(globalPalette, 3, usedTableSize);
      Map<Integer, Integer> colorMap = ByteSequenceUtils.MapClosestIndices(globalPalette, reducedPalette);

      // Open files
      FileOutputStream fos = new FileOutputStream("quantization/recoded" + usedTableSize + ".gif");
      GifOutput output = new GifOutput(new DataOutputLE(new DataOutputStream(fos)));

      // Create table element
      GifColorTable reducedTableElement = new GifColorTable();
      reducedTableElement.type = GifColorTable.Type.GLOBAL;
      reducedTableElement.table = ByteSequence.ToByteArray(reducedPalette, 3);

      // Recode elements
      for (GifElement element : elements) {
        var type = element.GetElementType();

        // Change table size
        if (type == GifElementType.LOGICAL_SCREEN_DESCRIPTOR) {
          GifLogicalScreenDescriptor descriptor = element.As();
          descriptor.globalColorTableSize = usedTableSize;
          output.WriteElement(descriptor);
          continue;
        }

        // Write reduced table
        if (type == GifElementType.COLOR_TABLE) {
          output.WriteElement(reducedTableElement);
          continue;
        }

        // Recode image indices
        if (type == GifElementType.TABLE_BASED_IMAGE_DATA) {
          GifTableBasedImageData imageData = element.As();
          byte[] encoded = imageData.imageData;

          // No data
          if (imageData.imageData == null) {
            output.WriteElement(imageData);
            continue;
          }

          // Remapping indices
          byte[] decoded = GifLzwUtils.decode(imageData.lzwMinimumCodeSize, encoded);
          for (int j = 0; j < decoded.length; j++) {
            int currIndex = decoded[j] & 0xFF;
            int newIndex = colorMap.get(currIndex);
            decoded[j] = (byte)newIndex;
          }

          // Encoding data
          encoded = GifLzwUtils.encode(8, decoded);

          // Writing
          GifTableBasedImageData newImageData = new GifTableBasedImageData();
          newImageData.lzwMinimumCodeSize = 8;
          newImageData.imageData = encoded;
          output.WriteElement(newImageData);
          continue;
        }

        // Write other elements
        output.WriteElement(element);

        // Exit on trailer
        if (type == GifElementType.TRAILER) {
          break;
        }
      }

      // Go to next file
      usedTableSize >>= 1;
      System.out.println("Saved");
    }

    System.out.println("Done");
  }
}
