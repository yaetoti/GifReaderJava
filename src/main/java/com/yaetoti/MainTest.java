package com.yaetoti;

import com.yaetoti.gif.blocks.*;
import com.yaetoti.gif.io.GifOutput;
import com.yaetoti.gif.io.GifReader;
import com.yaetoti.gif.utils.GifColorTableType;
import com.yaetoti.gif.utils.GifLzwUtils;
import com.yaetoti.io.DataInputLE;
import com.yaetoti.io.DataOutputLE;
import com.yaetoti.ppm.PpmImage;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class MainTest {
  public static void main(String[] args) throws IOException {
    var file = new RandomAccessFile("D:\\Projects\\Minecraft\\GifReader\\fuckingframe.gif", "r");
    //var file = new RandomAccessFile("D:\\Projects\\Minecraft\\GifReader\\test.gif", "r");
    var input = new DataInputLE(file);

    ArrayList<GifElement> elements = new ArrayList<>();
    GifReader reader = new GifReader(input);
    //GifOutput output = new GifOutput(new DataOutputLE(new DataOutputStream(new FileOutputStream("fuckingframe.gif"))));

    // 2951 - bug
    //  01110100 01000000 - last bytes. EOI = 1 00000001. Fit in 9 bits. Need 11. Have 10

    int frame = 0;

    GifColorTable gifColorTable = null;
    GifImageDescriptor descriptor = null;

    while (true) {
      var element = reader.ReadElement();
      var type = element.GetElementType();

      if (type == GifElementType.TRAILER) {
        break;
      }

      if (type == GifElementType.COLOR_TABLE) {
        gifColorTable = (GifColorTable) element;
        continue;
      }

      if (type == GifElementType.IMAGE_DESCRIPTOR) {
        descriptor = (GifImageDescriptor) element;
        continue;
      }

      if (type == GifElementType.TABLE_BASED_IMAGE_DATA) {
        System.out.println("Decoding frame " + frame);

        try {
          GifTableBasedImageData data = (GifTableBasedImageData) element;
          Objects.requireNonNull(data.imageData);

          var file1 = new FileOutputStream("dump.hex");
          file1.write(data.imageData);
          file1.close();

          byte[] bytes = GifLzwUtils.Decode(data.lzwMinimumCodeSize, data.imageData);

          // Writing to ppm
          Objects.requireNonNull(descriptor);
          Objects.requireNonNull(gifColorTable);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          for (byte index : bytes) {
            int address = index & 0xFF;
            baos.write(gifColorTable.table[address * 3]);
            baos.write(gifColorTable.table[address * 3 + 1]);
            baos.write(gifColorTable.table[address * 3 + 2]);
          }

          new PpmImage(descriptor.imageWidth, descriptor.imageHeight, baos.toByteArray()).SaveToFile("whoreson.ppm");


        } catch (IndexOutOfBoundsException e) {
          e.printStackTrace();
          System.out.println("Error decoding frame " + frame);
          break;
        }

        System.out.println("Decoded");

        ++frame;
        continue;
      }
    }

    file.close();
  }
}
