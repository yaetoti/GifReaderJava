package com.yaetoti.io;

import com.yaetoti.gif.utils.GifLzwUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GifLzwUtilsTest {
  @Test
  public void TestEncode() {
    byte[] data = new byte[] {
      1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1
    };

    byte[] encoded = GifLzwUtils.Encode(2, data);

    byte[] expected = new byte[] {
      (byte) 0b10001100,
      (byte) 0b00101101,
      (byte) 0b10011001,
      (byte) 0b01010111,
    };

    Assertions.assertArrayEquals(expected, encoded);
  }

  @Test
  public void TestDecode() throws IOException {
    byte[] encoded = new byte[] {
      (byte) 0b10001100,
      (byte) 0b00101101,
      (byte) 0b10011001,
      (byte) 0b10000111,
      (byte) 0b00100101
    };

    byte[] decoded = GifLzwUtils.Decode(2, encoded);

    byte[] expected = new byte[] {
      1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2
    };

    Assertions.assertArrayEquals(expected, decoded);
  }

  @Test
  public void TestInteroperability() throws IOException {
    byte[] data = new byte[] {
      1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1
    };

    byte[] encoded = GifLzwUtils.Encode(2, data);
    byte[] decoded = GifLzwUtils.Decode(2, encoded);
    Assertions.assertArrayEquals(data, decoded);
  }
}
