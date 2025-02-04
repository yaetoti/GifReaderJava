package com.yaetoti;

import com.yaetoti.gif.blocks.GifElement;
import com.yaetoti.gif.blocks.GifElementType;
import com.yaetoti.gif.io.*;
import com.yaetoti.io.DataInputLE;
import com.yaetoti.io.DataOutputLE;

import java.io.IOException;

public class GifPlainRecoder {
  // Take any reader
  // Write to any file

  public static void recode(DataInputLE input, DataOutputLE output) throws IOException {
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


    while (true) {
      GifElement element = in.ReadElement();
      GifElementType type = element.GetElementType();

      if (type == GifElementType.COMMENT_EXTENSION
        || type == GifElementType.APPLICATION_EXTENSION) {
        continue;
      }

      if (type == GifElementType.TRAILER) {
        out.WriteTrailer();
        return;
      }
    }
  }
}
