package com.yaetoti.gif;

import com.yaetoti.gif.blocks.*;
import com.yaetoti.gif.io.GifReader;
import com.yaetoti.gif.io.LittleEndianDataInput;
import com.yaetoti.gif.utils.*;
import com.yaetoti.ppm.PpmImage;

import java.io.*;

public class Main {
  public static void main(String[] args) throws IOException {
    //RandomAccessFile file = new RandomAccessFile("image.gif", "r");
    //RandomAccessFile file = new RandomAccessFile("image1.gif", "r");
    RandomAccessFile file = new RandomAccessFile("E:\\PremiereExport\\Miraculous-london-Full-HD.gif", "r");
    GifReader reader = new GifReader(new LittleEndianDataInput(file));

    byte[] globalColorTable = null;
    byte[] localColorTable = null;
    int frames = 0;

    int width = 0;
    int height = 0;

    try (ScopedTimer timer = new ScopedTimer("GIF Reading")) {
      while (true) {
        GifElement element = reader.ReadElement();
        GifElementType type = element.GetElementType();

        // Trailer - exit
        if (type == GifElementType.TRAILER) {
          break;
        }

        // Save color table
        if (type == GifElementType.COLOR_TABLE) {
          if (globalColorTable == null) {
            globalColorTable = element.<GifColorTable>As().table;
            continue;
          }

          localColorTable = element.<GifColorTable>As().table;
          continue;
        }

        // Save image resolution
        if (type == GifElementType.IMAGE_DESCRIPTOR) {
          GifImageDescriptor descriptor = element.As();
          width = descriptor.imageWidth;
          height = descriptor.imageHeight;
          continue;
        }

        // Save specific frame into PPM
        if (type == GifElementType.TABLE_BASED_IMAGE_DATA) {
          // Limit frames count
          ++frames;
          if (frames == 200) {
            break;
          }

//          // Check for frame number
//          if (frames % 10 != 0) {
//            continue;
//          }

          // Check for image data availability
          GifTableBasedImageData imageData = element.As();
          if (imageData.imageData == null) {
            continue;
          }

          // Check for color table availability
          byte[] activeColorTable = globalColorTable == null ? localColorTable : globalColorTable;
          if (activeColorTable == null) {
            continue;
          }

          // Decode indices
          byte[] indices = GifLzwUtils.decode(imageData.lzwMinimumCodeSize, imageData.imageData);

          // Add corresponding colors to a buffer
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          for (byte index : indices) {
            int intIndex = index & 0xff;
            out.write(activeColorTable[intIndex * 3]);
            out.write(activeColorTable[intIndex * 3 + 1]);
            out.write(activeColorTable[intIndex * 3 + 2]);
          }

          // Save to PPM
          new PpmImage(width, height, out.toByteArray()).SaveToFile("frames/Masterpiece" + frames + ".ppm");
          System.out.println("Saved");
          continue;
        }
      }
    }

    file.close();
    return;

//
//    for (int i = 0; i < result.length; i++) {
//      ////System.out.println("[" + i + "]: " + result[i]);
//    }


    // Read from any stream
    // Read from memory
    // Read from file

    // Read next block
    // Read all blocks
    // Read block #N

    // Index blocks in file


    // Indexing:
    // Frame:
    // - [GraphicsControlExtension]
    // - Image Descriptor
    // - [Local Color Table]
    // - Image Table Data


    // Get how many bytes we consumed on error to rewind


    // Determinism concept
    // - Skip plain text frames because we don't need them

    // Problems:
    // - How do we analyze before writing? - Read into buffer - Write buffer - Use stream-wrapper over buffer


//    RandomAccessFile file = new RandomAccessFile("image.gif", "r");
//    file.close();
//
//    new GifLexer().Parse(new DataInputStream(new FileInputStream("image.gif")));
  }
}
