package com.yaetoti.gif.utils;

import com.yaetoti.utils.BitUtils;
import org.jetbrains.annotations.Range;

public class GifUtils {
  private GifUtils() {
    assert false : "Utility class.";
  }

  /**
   * @param tableSize Size of a color table in range [0; 256]
   * @return 3-bit representation of a table size
   */
  public static int EncodeTableSize(@Range(from = 0, to = 256) int tableSize) {
    if (tableSize == 0) return 0;
    // -1 for shift amount and -1 for GIF format
    return BitUtils.GetBitLength(tableSize - 1) - 1;
  }

  /**
   * @param tableSize 3-bit representation of a table size
   * @return actual table size in range [2; 256]
   */
  public static int DecodeTableSize(@Range(from = 0, to = 7) int tableSize) {
    return (1 << (tableSize + 1));
  }
}
