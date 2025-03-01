package com.yaetoti.io;

import com.yaetoti.utils.BitUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public final class BitInputStreamLE {
  private byte[] m_buffer;
  private long m_bufferBits;
  private long m_offsetBits;
  private int m_position;
  private int m_positionBits;

  public BitInputStreamLE(byte @NotNull[] buffer) {
    m_buffer = buffer;
    m_bufferBits = (long)buffer.length * Byte.SIZE;
    m_offsetBits = 0;
    m_position = 0;
    m_positionBits = 0;
  }

  public BitInputStreamLE(byte @NotNull[] buffer, @Range(from = 0, to = Integer.MAX_VALUE * 8L) long offsetBits, @Range(from = 0, to = Integer.MAX_VALUE * 8L) long bufferBits) {
    m_buffer = buffer;
    m_bufferBits = bufferBits;
    m_offsetBits = offsetBits;
    m_position = (int) (offsetBits / Byte.SIZE);
    m_positionBits = (int) (offsetBits % Byte.SIZE);
  }

  public void Reset() {
    m_position = (int) (m_offsetBits / Byte.SIZE);
    m_positionBits = (int) (m_offsetBits % Byte.SIZE);
  }

  public long GetRemainingBits() {
    return m_bufferBits - m_offsetBits - ((long) m_position * Byte.SIZE + m_positionBits);
  }

  public boolean HasBits(long amount) {
    return GetRemainingBits() >= amount;
  }

  public byte GetByte(@Range(from = 0, to = Byte.SIZE) int bits) {
    return (byte)GetLong(bits);
  }

  public byte GetByte() {
    return (byte)GetLong(Byte.SIZE);
  }

  public short GetShort(@Range(from = 0, to = Short.SIZE) int bits) {
    return (short)GetLong(bits);
  }

  public short GetShort() {
    return (short)GetLong(Short.SIZE);
  }

  public int GetInt(@Range(from = 0, to = Integer.SIZE) int bits) {
    return (int)GetLong(bits);
  }

  public int GetInt() {
    return (int)GetLong(Integer.SIZE);
  }

  public long GetLong(@Range(from = 0, to = Long.SIZE) int bits) {
    if (!HasBits(bits)) {
      throw new IndexOutOfBoundsException();
    }

    long result = 0;
    while (bits != 0) {
      int remainingBits = Byte.SIZE - m_positionBits;
      int howMuchToTake = Math.min(remainingBits, bits);
      int howMuchToShiftRight = remainingBits - howMuchToTake;
      long part = BitUtils.MaskBits((long)(m_buffer[m_position] >>> howMuchToShiftRight), howMuchToTake);
      bits -= howMuchToTake;
      result |= part << bits;

      if (howMuchToTake == remainingBits) {
        m_position++;
        m_positionBits = 0;
      }
      else {
        m_positionBits += howMuchToTake;
      }
    }

    return result;
  }

  public long GetLong() {
    return GetLong(Long.SIZE);
  }
}
