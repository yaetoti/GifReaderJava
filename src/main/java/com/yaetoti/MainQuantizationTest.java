package com.yaetoti;

import com.yaetoti.gif.blocks.*;
import com.yaetoti.gif.io.GifOutput;
import com.yaetoti.gif.io.GifReader;
import com.yaetoti.gif.utils.GifLzwUtils;
import com.yaetoti.io.DataInputLE;
import com.yaetoti.io.DataOutputLE;
import com.yaetoti.quantization.MedianCut;
import com.yaetoti.utils.BitUtils;
import com.yaetoti.utils.ByteSequence;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainQuantizationTest {
  public static double ColorDistanceSquare(ByteSequence c1, ByteSequence c2) {
    int[] differences = new int[c1.Length()];
    for (int i = 0; i < c1.Length(); i++) {
      differences[i] = c1.AtUnsigned(i) - c2.AtUnsigned(i);
    }

    int result = 0;
    for (int difference : differences) {
      result += difference * difference;
    }

    return result;
  }

  public static int FindClosestColor(ByteSequence target, ArrayList<ByteSequence> palette) {
    int bestIndex = 0;
    double bestDistance = Double.MAX_VALUE;

    for (int i = 0; i < palette.size(); i++) {
      ByteSequence color = palette.get(i);
      double distance = ColorDistanceSquare(target, color);
      if (distance < bestDistance) {
        bestDistance = distance;
        bestIndex = i;
      }
    }

    return bestIndex;
  }

  public static Map<Integer, Integer> CreateColorMap(ArrayList<ByteSequence> oldPalette, ArrayList<ByteSequence> newPalette) {
    HashMap<Integer, Integer> colorMap = new HashMap<>();
    for (int i = 0; i < oldPalette.size(); i++) {
      ByteSequence color = oldPalette.get(i);
      int newIndex = FindClosestColor(color, newPalette);
      colorMap.put(i, newIndex);
    }

    return colorMap;
  }

  public static void main(String[] args) throws IOException {
    //var file = new RandomAccessFile("image1.gif", "r");
    var file = new RandomAccessFile("image.gif", "r");
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

    GifHeader header = elements.get(0).As();
    GifLogicalScreenDescriptor lsd = elements.get(1).As();
    if (!lsd.isGlobalColorTablePresent) {
      System.out.println("Global color table not found. Unsupported.");
      file.close();
      return;
    }

    int tableSize = lsd.globalColorTableSize;
    int outputSize = BitUtils.GetBitLength(tableSize);

    GifColorTable colorTable = elements.get(2).As();
    ArrayList<ByteSequence> palette = new ArrayList<>();
    for (int index = 0; index < lsd.globalColorTableSize; index++) {
      var component1 = colorTable.table[index * 3];
      var component2 = colorTable.table[index * 3 + 1];
      var component3 = colorTable.table[index * 3 + 2];
      palette.add(ByteSequence.Of(component1, component2, component3));
    }

    for (int i = 0; i < outputSize; ++i) {
      ArrayList<ByteSequence> newPalette = MedianCut.Cut(palette, tableSize);
      Map<Integer, Integer> colorMap = CreateColorMap(palette, newPalette);

      // Recode
      FileOutputStream fos = new FileOutputStream("quantization/recoded" + tableSize + ".gif");
      GifOutput output = new GifOutput(new DataOutputLE(new DataOutputStream(fos)));

      // New table
      GifColorTable newTable = new GifColorTable();
      {
        byte[] bytes = new byte[tableSize * 3];
        for (int j = 0; j < tableSize; j++) {
          var color = newPalette.get(j);
          var oldcolor = palette.get(j);
          bytes[j * 3] = color.At(0);
          bytes[j * 3 + 1] = color.At(1);
          bytes[j * 3 + 2] = color.At(2);
        }

        newTable.type = GifColorTable.Type.GLOBAL;
        newTable.table = bytes;
      }

      // TODO differs 0x00001850, 6226 byte d0dc ... and so on fix asap

      for (GifElement element : elements) {
        var type = element.GetElementType();

        if (type == GifElementType.LOGICAL_SCREEN_DESCRIPTOR) {
          GifLogicalScreenDescriptor descriptor = element.As();
          descriptor.globalColorTableSize = tableSize;
          output.WriteElement(descriptor);
          continue;
        }

        if (type == GifElementType.COLOR_TABLE) {
          output.WriteElement(newTable);
          continue;
        }

        if (type == GifElementType.TABLE_BASED_IMAGE_DATA) {
          GifTableBasedImageData imageData = element.As();
          var encoded = imageData.imageData;

          if (imageData.imageData == null) {
            output.WriteElement(newTable);
            continue;
          }

          // Remapping indices
          byte[] indices = GifLzwUtils.decode(imageData.lzwMinimumCodeSize, imageData.imageData);
          for (int j = 0; j < indices.length; j++) {
            int currIndex = indices[j] & 0xFF;
            int newIndex = colorMap.get(currIndex);
            indices[j] = (byte)newIndex;
          }

          // Encoding data
          imageData.imageData = GifLzwUtils.encode(8, indices);
          output.WriteElement(imageData);
          continue;
        }

        output.WriteElement(element);

        if (type == GifElementType.TRAILER) {
          break;
        }
      }

      // Go to next file
      tableSize >>= 1;
      System.out.println("Saved");
    }

    System.out.println("Done");
  }
}
