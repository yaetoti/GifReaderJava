package com.yaetoti.gif;

import com.yaetoti.gif.io.GifInput;
import com.yaetoti.gif.io.LittleEndianDataInput;
import com.yaetoti.gif.utils.*;
import com.yaetoti.ppm.PpmImage;

import java.io.*;
import java.util.Arrays;

// TODO return blocks from all read methods
// TODO Add WriteBlock method?
// TODO Can't add readBlock because it must be context aware. But can create a class for it

public class Main {
  public static void main(String[] args) throws IOException {
    //WriteByteArray(new byte[] { 0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18 });


    RandomAccessFile file = new RandomAccessFile("image.gif", "r");
    //RandomAccessFile file = new RandomAccessFile("image1.gif", "r");
    //RandomAccessFile file = new RandomAccessFile("E:\\PremiereExport\\Miraculous-london-Full-HD.gif", "r");
    GifInput input = new GifInput(new LittleEndianDataInput(file));

    // TODO test
    byte[] globalColorTable = null;

    int frames = 0;

    try (ScopedTimer timer = new ScopedTimer("GIF reading")) {
      var version = input.ReadHeader();
      //System.out.println(version);
      var lsd = input.ReadLogicalScreenDescriptor();
      //System.out.println(lsd);
      if (lsd.isGlobalColorTablePresent) {
        var colorTable = input.ReadColorTable(lsd.globalColorTableSize);

        // TODO test
        System.out.println("Saving global color table");
        globalColorTable = Arrays.copyOf(colorTable.table, colorTable.table.length);

        //System.out.println(Arrays.toString(colorTable));
      }

      while (true) {
        var label = input.ReadBlockLabel();
        //System.out.println(label);

        if (label == GifBlockLabel.TRAILER) {
          break;
        }

        if (label == GifBlockLabel.IMAGE_DESCRIPTOR) {
          var descriptor = input.ReadImageDescriptor();
          //System.out.println(descriptor);

          if (descriptor.isLocalColorTablePresent) {
            var colorTable = input.ReadColorTable(descriptor.localColorTableSize);
            //System.out.println(Arrays.toString(colorTable));
          }

          var imageData = input.ReadTableBasedImageData();
          //System.out.println("Image data");
          //System.out.println(imageData);

          // TODO decoding

          //System.out.println("Encoded indices:");
          //IoUtils.WriteByteArrayBin(System.out, imageData.imageData);

          try {
            if (imageData.imageData != null && frames == 10) {
              byte[] decodedData = GifLzwUtils.decode(imageData.lzwMinimumCodeSize, imageData.imageData);

              //System.out.println("Encoded indices:");
              //IoUtils.WriteByteArrayBin(System.out, imageData.imageData);

              //System.out.println("Decoded indices:");
              //IoUtils.WriteByteArrayBin(System.out, decodedData);

              // TODO save to ppm
              // Data to colors

              if (globalColorTable != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                for (byte index : decodedData) {
                  int intIndex = index & 0xff;
                  out.write(globalColorTable[intIndex * 3]);
                  out.write(globalColorTable[intIndex * 3 + 1]);
                  out.write(globalColorTable[intIndex * 3 + 2]);
                }

                System.out.println("Writing data to a file");
                new PpmImage(descriptor.imageWidth, descriptor.imageHeight, out.toByteArray()).SaveToFile("Masterpiece.ppm");
                System.out.println("Shaved");
              }
            }
          } catch (Exception e) {
            //System.out.println("Decoding failed");
            //System.out.println(e.getMessage());
            e.printStackTrace();
          }

          // TODO debug
          ++frames;
          if (frames == 190) {
            break;
          }

          continue;
        }

        if (label == GifBlockLabel.EXTENSION) {
          var extensionLabel  = input.ReadExtensionLabel();
          //System.out.println(extensionLabel);

          if (extensionLabel == GifExtensionLabel.COMMENT) {
            var extension = input.ReadCommentExtension();
            //System.out.println(extension);
            continue;
          }

          if (extensionLabel == GifExtensionLabel.APPLICATION) {
            var extension = input.ReadApplicationExtension();
            //System.out.println(extension);
            continue;
          }

          if (extensionLabel == GifExtensionLabel.PLAIN_TEXT) {
            var extension = input.ReadPlainTextExtension();
            //System.out.println(extension);
            continue;
          }

          if (extensionLabel == GifExtensionLabel.GRAPHICS_CONTROL) {
            var extension = input.ReadGraphicControlExtension();
            System.out.println(extension);
            continue;
          }
        }
      }
    }

    file.close();

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
