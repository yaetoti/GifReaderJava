package com.yaetoti.io;

import com.yaetoti.gif.utils.GifUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GifUtilityTest {
  @Test
  public void TestEncodeTableSize() {
    Assertions.assertEquals(0, GifUtils.EncodeTableSize(0));
    Assertions.assertEquals(0, GifUtils.EncodeTableSize(2));

    Assertions.assertEquals(1, GifUtils.EncodeTableSize(3));
    Assertions.assertEquals(1, GifUtils.EncodeTableSize(4));

    Assertions.assertEquals(2, GifUtils.EncodeTableSize(5));
    Assertions.assertEquals(2, GifUtils.EncodeTableSize(8));

    Assertions.assertEquals(3, GifUtils.EncodeTableSize(9));
    Assertions.assertEquals(3, GifUtils.EncodeTableSize(16));

    Assertions.assertEquals(4, GifUtils.EncodeTableSize(17));
    Assertions.assertEquals(4, GifUtils.EncodeTableSize(32));

    Assertions.assertEquals(5, GifUtils.EncodeTableSize(33));
    Assertions.assertEquals(5, GifUtils.EncodeTableSize(64));

    Assertions.assertEquals(6, GifUtils.EncodeTableSize(65));
    Assertions.assertEquals(6, GifUtils.EncodeTableSize(128));

    Assertions.assertEquals(7, GifUtils.EncodeTableSize(129));
    Assertions.assertEquals(7, GifUtils.EncodeTableSize(256));
  }

  @Test
  public void TestDecodeTableSize() {
    Assertions.assertEquals(2, GifUtils.DecodeTableSize(0));
    Assertions.assertEquals(4, GifUtils.DecodeTableSize(1));
    Assertions.assertEquals(8, GifUtils.DecodeTableSize(2));
    Assertions.assertEquals(16, GifUtils.DecodeTableSize(3));
    Assertions.assertEquals(32, GifUtils.DecodeTableSize(4));
    Assertions.assertEquals(64, GifUtils.DecodeTableSize(5));
    Assertions.assertEquals(128, GifUtils.DecodeTableSize(6));
    Assertions.assertEquals(256, GifUtils.DecodeTableSize(7));
  }
}
