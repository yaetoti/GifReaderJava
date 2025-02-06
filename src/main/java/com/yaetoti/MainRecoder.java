package com.yaetoti;

import com.yaetoti.gif.blocks.GifElementType;
import com.yaetoti.gif.blocks.GifTableBasedImageData;
import com.yaetoti.gif.io.GifOutput;
import com.yaetoti.gif.io.GifReader;
import com.yaetoti.gif.utils.GifLzwUtils;
import com.yaetoti.io.DataInputLE;
import com.yaetoti.io.DataOutputLE;

import java.io.*;

public class MainRecoder {
  public static void main(String[] args) throws IOException {
    RandomAccessFile file = new RandomAccessFile("image.gif", "r");
    FileOutputStream output = new FileOutputStream("simple.gif");

    GifReader in = new GifReader(new DataInputLE(file));
    GifOutput out = new GifOutput(new DataOutputLE(new DataOutputStream(output)));



    while (true) {
      var element = in.ReadElement();
      var type = element.GetElementType();

      if (type == GifElementType.TABLE_BASED_IMAGE_DATA) {
        GifTableBasedImageData imageData = element.As();
        if (imageData.imageData == null) {
          out.WriteElement(element);
          continue;
        }

        byte[] decoded = GifLzwUtils.decode(imageData.lzwMinimumCodeSize, imageData.imageData);
        byte[] encoded = GifLzwUtils.encode(imageData.lzwMinimumCodeSize, decoded);
        imageData.imageData = encoded;

        out.WriteElement(element);
        continue;
      }

      out.WriteElement(element);

      if (type == GifElementType.TRAILER) {
        break;
      }
    }

    file.close();
  }
}
