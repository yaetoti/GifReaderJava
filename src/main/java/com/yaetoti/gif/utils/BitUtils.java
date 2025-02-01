package com.yaetoti.gif.utils;

import org.jetbrains.annotations.Range;

public final class BitUtils {
  static byte CreateByteMask(@Range(from = 0, to = Byte.SIZE) int bits) {
    return (byte)((1 << bits) - 1);
  }

  static short CreateShortMask(@Range(from = 0, to = Short.SIZE) int bits) {
    return (short)((1 << bits) - 1);
  }

  static int CreateIntMask(@Range(from = 0, to = Integer.SIZE) int bits) {
    return (1 << bits) - 1;
  }

  static long CreateLongMask(@Range(from = 0, to = Long.SIZE) int bits) {
    return ((1L << bits) - 1L);
  }

  public static byte MaskBits(byte value, @Range(from = 0, to = Byte.SIZE) int bits) {
    return (byte)(value & CreateByteMask(bits));
  }

  public static short MaskBits(short value, @Range(from = 0, to = Short.SIZE) int bits) {
    return (short)(value & CreateShortMask(bits));
  }

  public static int MaskBits(int value, @Range(from = 0, to = Integer.SIZE) int bits) {
    return value & CreateIntMask(bits);
  }

  public static long MaskBits(long value, @Range(from = 0, to = Long.SIZE) int bits) {
    return value & CreateLongMask(bits);
  }

  /**
   * @param value value
   * @return The amount of bits in which a value can be represented
   */
  public static int GetBitLength(int value) {
    if (value == 0) {
      return 1;
    }

    if (value == Integer.MIN_VALUE) {
      return Integer.SIZE;
    }

    return Integer.SIZE - Integer.numberOfLeadingZeros(Math.abs(value));
  }

  /**
   * @param value value
   * @return The amount of bits in which a value can be represented
   */
  public static int GetBitLength(long value) {
    if (value == 0) {
      return 1;
    }

    if (value == Long.MIN_VALUE) {
      return Long.SIZE;
    }

    return Long.SIZE - Long.numberOfLeadingZeros(Math.abs(value));
  }
}

