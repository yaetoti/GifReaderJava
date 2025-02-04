package com.yaetoti.utils;

public final class StringUtils {
  public static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  public static String ToHexString(byte value) {
    char[] hexChars = new char[2];
    hexChars[1] = DIGITS[value & 0xF];
    hexChars[0] = DIGITS[(value >> 4) & 0xF];
    return new String(hexChars);
  }

  public static String ToHexString(short value) {
    char[] hexChars = new char[4];
    hexChars[3] = DIGITS[value & 0xF];
    hexChars[2] = DIGITS[(value >> 4) & 0xF];
    hexChars[1] = DIGITS[(value >> 8) & 0xF];
    hexChars[0] = DIGITS[(value >> 12) & 0xF];
    return new String(hexChars);
  }

  public static String ToHexString(int value) {
    char[] hexChars = new char[8];
    hexChars[7] = DIGITS[value & 0xF];
    hexChars[6] = DIGITS[(value >> 4) & 0xF];
    hexChars[5] = DIGITS[(value >> 8) & 0xF];
    hexChars[4] = DIGITS[(value >> 12) & 0xF];
    hexChars[3] = DIGITS[(value >> 16) & 0xF];
    hexChars[2] = DIGITS[(value >> 20) & 0xF];
    hexChars[1] = DIGITS[(value >> 24) & 0xF];
    hexChars[0] = DIGITS[(value >> 28) & 0xF];
    return new String(hexChars);
  }

  public static String ToHexString(long value) {
    char[] hexChars = new char[16];
    hexChars[15] = DIGITS[(int) (value & 0xF)];
    hexChars[14] = DIGITS[(int) ((value >> 4) & 0xF)];
    hexChars[13] = DIGITS[(int) ((value >> 8) & 0xF)];
    hexChars[12] = DIGITS[(int) ((value >> 12) & 0xF)];
    hexChars[11] = DIGITS[(int) ((value >> 16) & 0xF)];
    hexChars[10] = DIGITS[(int) ((value >> 20) & 0xF)];
    hexChars[9] = DIGITS[(int) ((value >> 24) & 0xF)];
    hexChars[8] = DIGITS[(int) ((value >> 28) & 0xF)];
    hexChars[7] = DIGITS[(int) ((value >> 32) & 0xF)];
    hexChars[6] = DIGITS[(int) ((value >> 36) & 0xF)];
    hexChars[5] = DIGITS[(int) ((value >> 40) & 0xF)];
    hexChars[4] = DIGITS[(int) ((value >> 44) & 0xF)];
    hexChars[3] = DIGITS[(int) ((value >> 48) & 0xF)];
    hexChars[2] = DIGITS[(int) ((value >> 52) & 0xF)];
    hexChars[1] = DIGITS[(int) ((value >> 56) & 0xF)];
    hexChars[0] = DIGITS[(int) ((value >> 60) & 0xF)];
    return new String(hexChars);
  }
}
