package com.yaetoti.gif.utils;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class IoUtils {
  public static void WriteByteArray(PrintStream out, byte @NotNull [] array) {
    StringBuilder sb = new StringBuilder();
    int pos = 0;

    while (pos < array.length) {
      if (pos % 16 == 0) {
        sb.append(String.format("%08d", pos));
        sb.append(": ");
      }

      sb.append(String.format("%02X", array[pos]));
      ++pos;

      if (pos % 16 == 0) {
        sb.append('\n');
      } else if (pos != array.length) {
        sb.append(' ');
      }
    }

    out.println(sb);
  }
}
