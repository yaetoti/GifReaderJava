package com.yaetoti.io;

import com.yaetoti.utils.BitUtils;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

public final class BitOutputStreamLE {
  public static final int INITIAL_CAPACITY = 16;
  private byte[] m_buffer;
  private byte m_remainder;
  private int m_bytes;
  private int m_remainderBits;

  public BitOutputStreamLE() {
    m_buffer = new byte[INITIAL_CAPACITY];
    m_bytes = 0;
    m_remainder = 0;
    m_remainderBits = 0;
  }

  public BitOutputStreamLE(int capacity) {
    assert capacity > 0;
    m_buffer = new byte[capacity];
    m_bytes = 0;
    m_remainder = 0;
    m_remainderBits = 0;
  }

  void Reset() {
    m_buffer = new byte[INITIAL_CAPACITY];
    m_bytes = 0;
    m_remainder = 0;
    m_remainderBits = 0;
  }

  public void EnsureCapacityBytes(int capacity) {
    assert capacity > 0;
    int minGrowth = capacity - m_buffer.length;
    if (minGrowth > 0) {
      int prefLength = m_buffer.length + Math.max(minGrowth, m_buffer.length);
      m_buffer = Arrays.copyOf(m_buffer, prefLength);
    }
  }

  public void EnsureBytesFit(int amount) {
    assert amount > 0;
    EnsureCapacityBytes(m_bytes + amount);
  }

  public void PutByte(byte value) {
    PutByte(value, Byte.SIZE);
  }

  /// value's bits should stick to the 0 bit
  /// 00011111, 5 bits
  public void PutByte(byte value, @Range(from = 0, to = Byte.SIZE) int bits) {
    while (bits != 0) {
      int remainingBits = Byte.SIZE - m_remainderBits;
      int howMuchFit = Math.min(remainingBits, bits);
      int howMuchLeft = bits - howMuchFit;
      int howMuchShiftRight = bits - howMuchFit;
      int howMuchShiftLeft = remainingBits - howMuchFit;

      byte takenValue = (byte)(value >> howMuchShiftRight);
      value = BitUtils.MaskBits(value, howMuchLeft);
      bits -= howMuchFit;

      m_remainder |= (byte)(takenValue << howMuchShiftLeft);
      m_remainderBits += howMuchFit;

      if (m_remainderBits == Byte.SIZE) {
        EnsureBytesFit(1);
        m_buffer[m_bytes++] = m_remainder;
        m_remainder = 0;
        m_remainderBits = 0;
      }
    }
  }

  public void PutShort(short value) {
    PutLong(value, Short.SIZE);
  }

  public void PutShort(short value, @Range(from = 0, to = Short.SIZE) int bits) {
    PutLong(value, bits);
  }

  public void PutInt(int value) {
    PutLong(value, Integer.SIZE);
  }

  public void PutInt(int value, @Range(from = 0, to = Integer.SIZE) int bits) {
    PutLong(value, bits);
  }

  public void PutLong(long value) {
    PutLong(value, Long.SIZE);
  }

  public void PutLong(long value, @Range(from = 0, to = Long.SIZE) int bits) {
    int fullBits = bits / Byte.SIZE * Byte.SIZE;
    int remainingBits = bits % Byte.SIZE;

    if (remainingBits != 0) {
      PutByte((byte)(value >>> fullBits), remainingBits);
    }

    while (fullBits != 0) {
      fullBits -= Byte.SIZE;
      PutByte((byte)(value >>> fullBits));
    }
  }

  public byte[] ToByteArray() {
    int resultSize = m_bytes + (m_remainderBits == 0 ? 0 : 1);
    byte[] result = new byte[resultSize];
    System.arraycopy(m_buffer, 0, result, 0, m_bytes);
    if (m_remainderBits != 0) {
      result[resultSize - 1] = (byte) (m_remainder & 0xFF);
    }

    return result;
  }

  public long GetBitsCount() {
    return (long)m_bytes * Byte.SIZE + m_remainderBits;
  }
}

