package com.yaetoti.utils;

import java.util.Arrays;
import java.util.Objects;

public final class ByteSequence {
  private final byte[] m_buffer;

  private ByteSequence() {
    m_buffer = new byte[0];
  }

  private ByteSequence(byte... bytes) {
    m_buffer = bytes;
  }

  public static ByteSequence Empty() {
    return new ByteSequence(new byte[0]);
  }

  public static ByteSequence Of(byte... buffer) {
    byte[] bytes = new byte[buffer.length];
    System.arraycopy(buffer, 0, bytes, 0, buffer.length);
    return new ByteSequence(bytes);
  }

  public static ByteSequence Of(int... buffer) {
    byte[] bytes = new byte[buffer.length];
    for (int i = 0; i < buffer.length; i++) {
      bytes[i] = (byte) buffer[i];
    }

    return new ByteSequence(bytes);
  }

  public byte At(int index) {
    return m_buffer[index];
  }

  public int AtUnsigned(int index) {
    return m_buffer[index] & 0xFF;
  }

  public int Length() {
    return m_buffer.length;
  }

  public byte[] ToByteArray() {
    return Arrays.copyOf(m_buffer, m_buffer.length);
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
    StringBuilder sb = new StringBuilder();
    sb.append("ByteSequence{");
    for (byte b : m_buffer) {
      sb.append(" ");
      sb.append(StringUtils.ToHexString(b));
    }

    sb.append(" }");
    return sb.toString();
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

