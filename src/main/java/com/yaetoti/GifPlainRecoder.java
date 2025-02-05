package com.yaetoti;

import com.yaetoti.gif.blocks.*;
import com.yaetoti.gif.io.*;
import com.yaetoti.io.DataInputLE;
import com.yaetoti.io.DataOutputLE;
import com.yaetoti.bytes.ByteSequence;
import com.yaetoti.quantization.MedianCut;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class GifPlainRecoder {
  // Take any reader
  // Write to any file

  public static void Recode(DataInputLE input, DataOutputLE output) throws IOException {
    GifReader in = new GifReader(input);
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
    boolean usesTransparencyFlag = false;

    while (true) {
      GifElement element = in.ReadElement();
      GifElementType type = element.GetElementType();

      if (type == GifElementType.TRAILER) {
        break;
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

    // Preparing tables
    HashSet<ByteSequence> uniqueColors = new HashSet<>();
    for (ByteSequence[] colors : colorsOfTables) {
      uniqueColors.addAll(List.of(colors));
    }

    ByteSequence[] resultColors = uniqueColors.toArray(new ByteSequence[0]);

    // Reducing colors if necessary
    int targetColorCount = Math.min(256 - (usesTransparencyFlag ? 1 : 0), resultColors.length);
    if (resultColors.length != targetColorCount) {
      resultColors = MedianCut.Reduce(resultColors, 3, targetColorCount);
    }

    // Adding a transparency color
    if (usesTransparencyFlag) {
      ByteSequence[] finalColors = new ByteSequence[targetColorCount + 1];
      System.arraycopy(resultColors, 0, finalColors, 0, targetColorCount);
      finalColors[targetColorCount] = ByteSequence.Of(0, 0, 0);
      resultColors = finalColors;
    }



  }

  public static void main(String[] args) throws IOException {
    RandomAccessFile file = new RandomAccessFile("image1.gif", "r");
    FileOutputStream out = new FileOutputStream("plain.gif");

    GifPlainRecoder.Recode(new DataInputLE(file), new DataOutputLE(new DataOutputStream(out)));
    System.out.println("Done");

    out.close();
  }
}
