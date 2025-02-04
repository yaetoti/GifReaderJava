package com.yaetoti.utils;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class IoUtils {
  public static void WriteByteArrayHex(PrintStream out, byte @NotNull [] array) {
    StringBuilder sb = new StringBuilder();
    int pos = 0;

    while (pos < array.length) {
      if (pos % 16 == 0) {
        sb.append(String.format("%08d", pos));
        sb.append(": ");
      }

      sb.append(StringUtils.ToHexString(array[pos]));
      ++pos;

      if (pos % 16 == 0) {
        sb.append('\n');
      } else if (pos != array.length) {
        sb.append(' ');
      }
    }

    out.println(sb);
  }

  public static void WriteByteArrayBin(PrintStream out, byte @NotNull [] array) {
    StringBuilder sb = new StringBuilder();
    int pos = 0;

    while (pos < array.length) {
      if (pos % 16 == 0) {
        sb.append(String.format("%08d", pos));
        sb.append(": ");
      }

      sb.append(String.format("%8s", Integer.toBinaryString(array[pos] & 0xFF)).replace(' ', '0'));
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
