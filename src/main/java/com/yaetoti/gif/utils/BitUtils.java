package com.yaetoti.gif.utils;

import org.jetbrains.annotations.Range;

public final class BitUtils {
  static byte CreateByteMask(@Range(from = 0, to = 8) int bits) {
    return (byte)((1 << bits) - 1);
  }

  public static byte MaskBits(byte value, @Range(from = 0, to = 8) int bits) {
    return (byte)(value & CreateByteMask(bits));
  }

  public static int GetBitsRequired(int value) {
    if (value == 0) {
      return 1;
    }

    if (value == Integer.MIN_VALUE) {
      return 32;
    }

    return Integer.SIZE - Integer.numberOfLeadingZeros(Math.abs(value));
  }
}

