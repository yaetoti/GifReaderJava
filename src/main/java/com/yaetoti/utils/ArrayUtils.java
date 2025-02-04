package com.yaetoti.utils;

public class ArrayUtils {
  public static int Sum(int... values) {
    int sum = 0;
    for (int value : values) {
      sum += value;
    }

    return sum;
  }
}
