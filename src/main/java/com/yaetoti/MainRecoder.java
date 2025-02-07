package com.yaetoti;

import com.yaetoti.bytes.ByteSequence;
import com.yaetoti.gif.blocks.GifColorTable;
import com.yaetoti.gif.blocks.GifElementType;
import com.yaetoti.gif.blocks.GifImageDescriptor;
import com.yaetoti.gif.blocks.GifTableBasedImageData;
import com.yaetoti.gif.io.GifOutput;
import com.yaetoti.gif.io.GifReader;
import com.yaetoti.gif.utils.GifLzwUtils;
import com.yaetoti.io.DataInputLE;
import com.yaetoti.io.DataOutputLE;
import com.yaetoti.ppm.PpmImage;

import java.io.*;

public class MainRecoder {
  public static void main(String[] args) throws IOException {
    RandomAccessFile file = new RandomAccessFile("image.gif", "r");
    FileOutputStream output = new FileOutputStream("simple.gif");

    GifReader in = new GifReader(new DataInputLE(file));
    GifOutput out = new GifOutput(new DataOutputLE(new DataOutputStream(output)));

    int targetSize = 0;
    int width = 0;
    int height = 0;

    int frame = 0;

    while (true) {
      var element = in.ReadElement();
      var type = element.GetElementType();

      if (type == GifElementType.IMAGE_DESCRIPTOR) {
        GifImageDescriptor desc = (GifImageDescriptor) element;
        targetSize = desc.imageWidth * desc.imageHeight;
        width = desc.imageWidth;
        height = desc.imageHeight;
      }

      if (type == GifElementType.TABLE_BASED_IMAGE_DATA) {
        GifTableBasedImageData imageData = element.As();
        if (imageData.imageData == null) {
          out.WriteElement(element);
          continue;
        }

        byte[] decoded = GifLzwUtils.decode(imageData.lzwMinimumCodeSize, imageData.imageData);
        assert decoded.length == targetSize;
        byte[] encoded = GifLzwUtils.encode(imageData.lzwMinimumCodeSize, decoded);
        //assert encoded.length == imageData.imageData.length;
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
    System.out.println("Done");
  }
}
