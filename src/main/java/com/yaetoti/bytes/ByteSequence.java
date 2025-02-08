package com.yaetoti.bytes;

import com.yaetoti.utils.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Arrays;
import java.util.Objects;

public final class ByteSequence implements IByteSequenceView {
  private final byte[] m_buffer;
  private final int m_offset;
  private final int m_length;

  public ByteSequence() {
    m_buffer = new byte[0];
    m_offset = 0;
    m_length = 0;
  }

  public ByteSequence(byte... bytes) {
    m_buffer = bytes;
    m_offset = 0;
    m_length = bytes.length;
  }

  public ByteSequence(byte[] bytes, int offset, int length) {
    m_buffer = bytes;
    m_offset = offset;
    m_length = length;
  }

  @Override
  public byte GetByte(int index) {
    return m_buffer[m_offset + index];
  }

  @Override
  public int GetUnsignedByte(int index) {
    return m_buffer[m_offset + index] & 0xFF;
  }

  @Override
  public int Length() {
    return m_buffer.length;
  }

  @Override
  public byte @NotNull [] ToByteArray() {
    return Arrays.copyOfRange(m_buffer, m_offset, m_offset + m_length);
  }

  public @NotNull ByteSequenceView AsView() {
    return new ByteSequenceView(m_buffer, m_offset, m_length);
  }

  public @NotNull ByteSequence Append(byte value) {
    byte[] bytes = new byte[m_length + 1];
    System.arraycopy(m_buffer, m_offset, bytes, 0, m_buffer.length);
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

  public static ByteSequence Of(byte... buffer) {
    byte[] bytes = new byte[buffer.length];
    System.arraycopy(buffer, 0, bytes, 0, buffer.length);
    return new ByteSequence(bytes);
  }

  public static ByteSequence Of(byte[] buffer, int offset, int length) {
    assert buffer.length >= offset + length;

    byte[] bytes = new byte[length];
    System.arraycopy(buffer, offset, bytes, 0, length);
    return new ByteSequence(bytes);
  }

  public static ByteSequence Of(int... buffer) {
    byte[] bytes = new byte[buffer.length];
    for (int i = 0; i < buffer.length; i++) {
      bytes[i] = (byte) buffer[i];
    }

    return new ByteSequence(bytes);
  }

  public static ByteSequence[] ArrayOf(byte[] bytes, int elementSize) {
    assert bytes.length % elementSize == 0;

    int size = bytes.length / elementSize;
    ByteSequence[] byteSequences = new ByteSequence[size];

    for (int i = 0; i < size; i++) {
      byteSequences[i] = ByteSequence.Of(bytes, i * elementSize, elementSize);
    }

    return byteSequences;
  }

  public static byte[] ToByteArray(ByteSequence[] sequences, @Range(from = 0, to = Integer.MAX_VALUE) int elementSize) {
    byte[] bytes = new byte[sequences.length * elementSize];
    for (int i = 0; i < sequences.length; i++) {
      assert sequences[i] != null;
      assert sequences[i].Length() == elementSize;
      System.arraycopy(sequences[i].m_buffer, 0, bytes, i * elementSize, elementSize);
    }

    return bytes;
  }
}

