package com.yaetoti.io;

import com.yaetoti.utils.BitUtils;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

public final class BitOutputStreamBE {
  public static final int INITIAL_CAPACITY = 16;
  private byte[] m_buffer;
  private byte m_remainder;
  private int m_bytes;
  private int m_remainderBits;

  public BitOutputStreamBE() {
    m_buffer = new byte[INITIAL_CAPACITY];
    m_bytes = 0;
    m_remainder = 0;
    m_remainderBits = 0;
  }

  public BitOutputStreamBE(int capacity) {
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
    PutLong(value, Byte.SIZE);
  }

  public void PutByte(byte value, @Range(from = 0, to = Byte.SIZE) int bits) {
    PutLong(value, Byte.SIZE);
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
    while (bits != 0) {
      int remainingBits = Byte.SIZE - m_remainderBits;
      int howMuchFit = Math.min(remainingBits, bits);
      int howMuchShiftLeft = m_remainderBits;

      long takenValue = BitUtils.MaskBits(value, howMuchFit);
      value >>>= howMuchFit;
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
