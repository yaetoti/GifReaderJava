package com.yaetoti.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BitStreamTest {
  @Test
  public void TestGetLongBE() {
    {
      byte[] bytes = new byte[] {
        (byte) 0b10001000,
        (byte) 0b11000110,
      };

      BitInputStreamBE in = new BitInputStreamBE(bytes);
      Assertions.assertEquals(0, in.GetLong(3));
      Assertions.assertEquals(1, in.GetLong(3));
      Assertions.assertEquals(2, in.GetLong(3));
      Assertions.assertEquals(3, in.GetLong(3));
      Assertions.assertEquals(4, in.GetLong(3));
      Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> in.GetLong(3));
    }
    {
      byte[] bytes = new byte[] {
        (byte) 0b11111110,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
      };

      BitInputStreamBE in = new BitInputStreamBE(bytes);
      Assertions.assertEquals(-2, in.GetLong());
      Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, in::GetLong);
    }
  }

  @Test
  public void TestGetLongLE() {
    {
      byte[] bytes = new byte[] {
        (byte) 0b00000101,
        (byte) 0b00111001,
      };

      BitInputStreamLE in = new BitInputStreamLE(bytes);
      Assertions.assertEquals(0, in.GetLong(3));
      Assertions.assertEquals(1, in.GetLong(3));
      Assertions.assertEquals(2, in.GetLong(3));
      Assertions.assertEquals(3, in.GetLong(3));
      Assertions.assertEquals(4, in.GetLong(3));
      Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> in.GetLong(3));
    }
    {
      byte[] bytes = new byte[] {
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111110,
      };

      BitInputStreamLE in = new BitInputStreamLE(bytes);
      Assertions.assertEquals(-2, in.GetLong());
      Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, in::GetLong);
    }
  }

  @Test
  public void TestWriteLongBE() {
    {
      byte[] bytes = new byte[] {
        (byte) 0b10001000,
        (byte) 0b01000110,
      };

      BitOutputStreamBE out = new BitOutputStreamBE();
      out.PutLong(0, 3);
      out.PutLong(1, 3);
      out.PutLong(2, 3);
      out.PutLong(3, 3);
      out.PutLong(4, 3);
      var result = out.ToByteArray();

      Assertions.assertArrayEquals(bytes, result);
    }
    {
      byte[] bytes = new byte[] {
        (byte) 0b11111110,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
      };

      BitOutputStreamBE out = new BitOutputStreamBE();
      out.PutLong(-2);
      var result = out.ToByteArray();

      Assertions.assertArrayEquals(bytes, result);
    }
  }

  @Test
  public void TestWriteLongLE() {
    {
      byte[] bytes = new byte[] {
        (byte) 0b00000101,
        (byte) 0b00111000,
      };

      BitOutputStreamLE out = new BitOutputStreamLE();
      out.PutLong(0, 3);
      out.PutLong(1, 3);
      out.PutLong(2, 3);
      out.PutLong(3, 3);
      out.PutLong(4, 3);
      var result = out.ToByteArray();

      Assertions.assertArrayEquals(bytes, result);
    }
    {
      byte[] bytes = new byte[] {
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111111,
        (byte) 0b11111110,
      };

      BitOutputStreamLE out = new BitOutputStreamLE();
      out.PutLong(-2);
      var result = out.ToByteArray();

      Assertions.assertArrayEquals(bytes, result);
    }
  }

  @Test
  public void TestInteroperabilityBE() {
    BitOutputStreamBE out = new BitOutputStreamBE();
    out.PutLong(1, 1);
    out.PutByte((byte) -69);
    out.PutShort((short) -6969);
    out.PutInt(-69696969);
    out.PutLong(-696969696969L);

    BitInputStreamBE in = new BitInputStreamBE(out.ToByteArray());
    Assertions.assertEquals(1, in.GetLong(1));
    Assertions.assertEquals(-69, in.GetByte());
    Assertions.assertEquals(-6969, in.GetShort());
    Assertions.assertEquals(-69696969, in.GetInt());
    Assertions.assertEquals(-696969696969L, in.GetLong());
  }

  @Test
  public void TestInteroperabilityLE() {
    BitOutputStreamLE out = new BitOutputStreamLE();
    out.PutLong(1, 1);
    out.PutByte((byte) 0xAA);
    out.PutShort((short) 0xAAAA);
    out.PutInt(0xAAAAAAAA);
    out.PutLong(0xAAAAAAAAAAAAAAAAL);

    BitInputStreamLE in = new BitInputStreamLE(out.ToByteArray());
    Assertions.assertEquals(1, in.GetLong(1));
    Assertions.assertEquals((byte)0xAA, in.GetByte());
    Assertions.assertEquals((short)0xAAAA, in.GetShort());
    Assertions.assertEquals(0xAAAAAAAA, in.GetInt());
    Assertions.assertEquals(0xAAAAAAAAAAAAAAAAL, in.GetLong());
  }
}
