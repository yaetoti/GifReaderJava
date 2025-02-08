package com.yaetoti;

import com.yaetoti.gif.blocks.GifElement;
import com.yaetoti.gif.blocks.GifElementType;
import com.yaetoti.gif.io.GifReader;
import com.yaetoti.io.DataInputLE;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class MainGifViewer {
  public static void main(String[] args) throws IOException {
    var file = new RandomAccessFile("plain.gif", "r");
    var input = new DataInputLE(file);

    ArrayList<GifElement> elements = new ArrayList<>();
    GifReader reader = new GifReader(input);

    while (true) {
      var element = reader.ReadElement();
      var type = element.GetElementType();

      if (type == GifElementType.TRAILER) {
        break;
      }
    }

    file.close();
  }
}
