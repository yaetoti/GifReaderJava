package com.yaetoti;

import com.yaetoti.bytes.ByteSequenceUtils;
import com.yaetoti.gif.blocks.*;
import com.yaetoti.gif.io.*;
import com.yaetoti.gif.utils.GifLzwUtils;
import com.yaetoti.io.DataInputLE;
import com.yaetoti.io.DataOutputLE;
import com.yaetoti.bytes.ByteSequence;
import com.yaetoti.quantization.MedianCut;

import java.io.*;
import java.util.*;

public class GifPlainRecoder {
  // Take any reader
  // Write to any file

  public static void Recode(RandomAccessFile input, DataOutputLE output) throws IOException {
    GifReader in = new GifReader(new DataInputLE(input));
    GifOutput out = new GifOutput(output);

    // remove unnecessary extensions:
    // - comment extension
    // - application extension
    // - plain text extension and preceding graphics control extensions

    // Store last graphics control extension, image descriptor, local color table
    // Remove them after rewriting an image
    // remove then when meet plain text extension

    // When finally got image data:
    // - decode data
    // - use active color table to get colors

    // NO WAY, WE NEED TO MERGE COLORS
    // Unsupported, lol NAH, THATS NOT HOW WE DO IT

    // If image uses local color table - write it

    // Analysis pass
    ArrayList<ByteSequence[]> colorsOfTables = new ArrayList<>();
    boolean globalColorTablePresent = false;
    boolean usesTransparencyFlag = false;
    int backgroundColorIndex = 0;

    while (true) {
      GifElement element = in.ReadElement();
      GifElementType type = element.GetElementType();

      if (type == GifElementType.TRAILER) {
        break;
      }

      if (type == GifElementType.LOGICAL_SCREEN_DESCRIPTOR) {
        GifLogicalScreenDescriptor descriptorElement = element.As();
        backgroundColorIndex = descriptorElement.backgroundColorIndex;
        globalColorTablePresent = descriptorElement.isGlobalColorTablePresent;
        continue;
      }

      if (type == GifElementType.COLOR_TABLE) {
        GifColorTable colorTableElement = element.As();
        if (colorTableElement.table.length == 0) {
          continue;
        }

        colorsOfTables.add(ByteSequence.ArrayOf(colorTableElement.table, 3));
        continue;
      }

      if (type == GifElementType.GRAPHIC_CONTROL_EXTENSION) {
        GifGraphicsControlExtension extension = element.As();
        if (extension.transparentColorFlag) {
          usesTransparencyFlag = true;
        }

        continue;
      }
    }


    // Create result table from colors (reserve 1 for transparency if it's used)
    // Create map for global table if it's used

    // If we have global table - map background color index to new color in result table
    // Create map for every local table
    // If have


    // Collecting used colors
    HashSet<ByteSequence> uniqueColors = new HashSet<>();
    for (ByteSequence[] colors : colorsOfTables) {
      uniqueColors.addAll(List.of(colors));
    }

    // Reducing colors if necessary
    int targetColorCount = Math.min(usesTransparencyFlag ? 255 : 256, uniqueColors.size());
    int transparentColorIndex = usesTransparencyFlag ? targetColorCount : 0;

    ByteSequence[] newGlobalTable = uniqueColors.toArray(new ByteSequence[0]);
    if (uniqueColors.size() != targetColorCount) {
      newGlobalTable = MedianCut.Reduce(newGlobalTable, 3, targetColorCount);
    }

    // Adding a transparency color
    ByteSequence[] finalColors = newGlobalTable;
    if (usesTransparencyFlag) {
      finalColors = new ByteSequence[targetColorCount + 1];
      System.arraycopy(newGlobalTable, 0, finalColors, 0, targetColorCount);
      finalColors[targetColorCount] = ByteSequence.Of(0, 0, 0);
    }

    // Mapping global table and background color
    Map<Integer, Integer> globalColorMap = null;
    if (globalColorTablePresent) {
      globalColorMap = ByteSequenceUtils.MapClosestIndices(colorsOfTables.getFirst(), newGlobalTable);
      backgroundColorIndex = globalColorMap.get(backgroundColorIndex);
    }



    // Writing pass
    input.seek(0);

    GifGraphicsControlExtension graphicsControlExtension = null;
    GifImageDescriptor imageDescriptor = null;
    GifColorTable localColorTable = null;
    GifTableBasedImageData imageData = null;

    while (true) {
      var element = in.ReadElement();
      GifElementType type = element.GetElementType();

      if (type == GifElementType.LOGICAL_SCREEN_DESCRIPTOR) {
        GifLogicalScreenDescriptor descriptorElement = element.As();
        descriptorElement.backgroundColorIndex = backgroundColorIndex;
        descriptorElement.globalColorTableSize = finalColors.length;
        descriptorElement.isGlobalColorTablePresent = true;
        out.WriteElement(element);
        continue;
      }

      if (type == GifElementType.GRAPHIC_CONTROL_EXTENSION) {
        graphicsControlExtension = element.As();
        continue;
      }

      if (type == GifElementType.PLAIN_TEXT_EXTENSION) {
        graphicsControlExtension = null;
        continue;
      }

      if (type == GifElementType.IMAGE_DESCRIPTOR) {
        imageDescriptor = element.As();
        continue;
      }

      if (type == GifElementType.TABLE_BASED_IMAGE_DATA) {
        imageData = element.As();

        // No data?
        if (imageData.imageData == null) {
          continue;
        }

        int prevTransparentColorIndex = 0;
        if (graphicsControlExtension != null) {
          prevTransparentColorIndex = graphicsControlExtension.transparentColorIndex;
          graphicsControlExtension.transparentColorIndex = transparentColorIndex;
          // TODO plain
          //graphicsControlExtension.disposalMethod = ;
          out.WriteGraphicControlExtension(graphicsControlExtension);
        }

        assert imageDescriptor != null;
        imageDescriptor.isLocalColorTablePresent = false;
        imageDescriptor.isLocalColorTableSorted = false;
        imageDescriptor.localColorTableSize = 0;

        // Create color map, recode data
        Map<Integer, Integer> colorMap = globalColorMap;
        if (localColorTable != null) {
          colorMap = ByteSequenceUtils.MapClosestIndices(ByteSequence.ArrayOf(localColorTable.table, 3), newGlobalTable);
        }

        // Recoding image
        byte[] decoded = GifLzwUtils.decode(imageData.lzwMinimumCodeSize, imageData.imageData);
        for (int i = 0; i < decoded.length; i++) {
          assert colorMap != null;
          int index = decoded[i] & 0xFF;
          if (graphicsControlExtension != null && graphicsControlExtension.transparentColorFlag && index == prevTransparentColorIndex) {
            decoded[i] = (byte)transparentColorIndex;
            continue;
          }

          decoded[i] = colorMap.get(decoded[i] & 0xFF).byteValue();
        }

        byte[] encoded = GifLzwUtils.encode(8, decoded);
        imageData.lzwMinimumCodeSize = 8;
        imageData.imageData = encoded;

        if (graphicsControlExtension != null) {
          out.WriteGraphicControlExtension(graphicsControlExtension);
        }

        out.WriteImageDescriptor(imageDescriptor);
        out.WriteTableBasedImageData(imageData);

        // Free data
        graphicsControlExtension = null;
        imageDescriptor = null;
        localColorTable = null;
        imageData = null;

        continue;
      }

      if (type == GifElementType.COLOR_TABLE) {
        GifColorTable colorTableElement = element.As();
        if (colorTableElement.type == GifColorTable.Type.LOCAL) {
          localColorTable = element.As();
          continue;
        }

        colorTableElement.table = ByteSequence.ToByteArray(finalColors, 3);
        out.WriteElement(colorTableElement);
        continue;
      }

      if (type == GifElementType.TRAILER) {
        out.WriteTrailer();
        break;
      }

      if (type == GifElementType.COMMENT_EXTENSION) {
        continue;
      }

      out.WriteElement(element);
    }
  }

  public static void main(String[] args) throws IOException {
    RandomAccessFile file = new RandomAccessFile("image.gif", "r");
    FileOutputStream out = new FileOutputStream("plain.gif");

    GifPlainRecoder.Recode(file, new DataOutputLE(new DataOutputStream(out)));
    System.out.println("Done");

    out.close();
  }
}
