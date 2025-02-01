package com.yaetoti.gif.utils;

import java.util.Arrays;
import java.util.Objects;

public final class ByteSequence {
  private final byte[] m_buffer;

  private ByteSequence() {
    m_buffer = new byte[0];
  }

  private ByteSequence(byte[] buffer) {
    m_buffer = buffer;
  }

  public static ByteSequence Empty() {
    return new ByteSequence(new byte[0]);
  }

  public static ByteSequence Of(byte value) {
    return new ByteSequence(new byte[] { value });
  }

  public static ByteSequence Of(byte[] buffer) {
    byte[] bytes = new byte[buffer.length];
    System.arraycopy(buffer, 0, bytes, 0, buffer.length);
    return new ByteSequence(bytes);
  }

  public ByteSequence Append(byte value) {
    byte[] bytes = new byte[m_buffer.length + 1];
    System.arraycopy(m_buffer, 0, bytes, 0, m_buffer.length);
    bytes[m_buffer.length] = value;
    return new ByteSequence(bytes);
  }

  public ByteSequence Append(byte[] buffer) {
    byte[] bytes = new byte[m_buffer.length + buffer.length];
    System.arraycopy(m_buffer, 0, bytes, 0, m_buffer.length);
    System.arraycopy(buffer, 0, bytes, m_buffer.length, buffer.length);
    return new ByteSequence(bytes);
  }

  @Override
  public String toString() {
    return "ByteSequence{" +
      "m_buffer=" + Arrays.toString(m_buffer) +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ByteSequence that)) {
      return false;
    }

    return Objects.deepEquals(m_buffer, that.m_buffer);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(m_buffer);
  }
}

