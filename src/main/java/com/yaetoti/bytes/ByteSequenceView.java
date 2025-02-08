package com.yaetoti.bytes;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public final class ByteSequenceView implements IByteSequenceView {
  private final byte[] m_buffer;
  private final int m_offset;
  private final int m_length;

  public ByteSequenceView() {
    m_buffer = new byte[0];
    m_offset = 0;
    m_length = 0;
  }

  public ByteSequenceView(byte... buffer) {
    m_buffer = buffer;
    m_offset = 0;
    m_length = buffer.length;
  }

  public ByteSequenceView(byte[] buffer, int offset, int length) {
    assert offset + length <= buffer.length;
    m_buffer = buffer;
    m_offset = offset;
    m_length = length;
  }

  @Override
  public byte GetByte(int index) {
    return m_buffer[m_offset + index];
  }

  @Override
  public int GetUnsignedByte(int index) {
    return m_buffer[index] & 0xFF;
  }

  @Override
  public int Length() {
    return m_length;
  }

  @Override
  public byte @NotNull [] ToByteArray() {
    return Arrays.copyOfRange(m_buffer, m_offset, m_offset + m_length);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ByteSequenceView that)) return false;
    if (m_length != that.m_length) return false;
    if (m_offset != that.m_offset) return false;
    for (int i = m_offset; i < m_offset + m_length; ++i) {
      if (!Objects.deepEquals(m_buffer[i], that.m_buffer[i])) return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hash = 1;
    for (int i = 0; i < m_length; i++) {
      hash = 31 * hash + m_buffer[m_offset + i];
    }

    hash = 31 * hash + m_offset;
    hash = 31 * hash + m_length;
    return hash;
  }


  // Static

  public static ByteSequenceView Of(short... values) {
    byte[] bytes = new byte[values.length];
    for (int i = 0; i < values.length; i++) {
      bytes[i] = (byte) values[i];
    }

    return new ByteSequenceView(bytes);
  }

  public static ByteSequenceView Of(int... values) {
    byte[] bytes = new byte[values.length];
    for (int i = 0; i < values.length; i++) {
      bytes[i] = (byte) values[i];
    }

    return new ByteSequenceView(bytes);
  }

  public static ByteSequenceView Of(long... values) {
    byte[] bytes = new byte[values.length];
    for (int i = 0; i < values.length; i++) {
      bytes[i] = (byte) values[i];
    }

    return new ByteSequenceView(bytes);
  }

  public static byte[] ToByteArray(int elementSize, ByteSequenceView... views) {
    byte[] buffer = new byte[elementSize * views.length];
    int offset = 0;
    for (ByteSequenceView view : views) {
      for (int j = 0; j < elementSize; ++j) {
        buffer[offset++] = view.GetByte(j);
      }
    }

    return buffer;
  }

  public static byte[] ToByteArray(ByteSequenceView... views) {
    int size = 0;
    for (ByteSequenceView view : views) {
      size += view.Length();
    }

    byte[] buffer = new byte[size];
    int offset = 0;
    for (ByteSequenceView view : views) {
      for (int j = 0; j < view.Length(); ++j) {
        buffer[offset++] = view.GetByte(j);
      }
    }

    return buffer;
  }
}
