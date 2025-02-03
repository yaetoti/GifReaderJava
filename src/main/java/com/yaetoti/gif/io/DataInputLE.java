package com.yaetoti.gif.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;

public class DataInputLE implements DataInput {
  private final DataInput m_input;
  private final byte[] buffer = new byte[Long.BYTES];

  public DataInputLE(DataInput input) {
    m_input = input;
  }

  @Override
  public void readFully(byte @NotNull [] b) throws IOException {
    m_input.readFully(b);
  }

  @Override
  public void readFully(byte @NotNull [] b, int off, int len) throws IOException {
    m_input.readFully(b, off, len);
  }

  @Override
  public int skipBytes(int n) throws IOException {
    return m_input.skipBytes(n);
  }

  @Override
  public boolean readBoolean() throws IOException {
    return m_input.readBoolean();
  }

  @Override
  public byte readByte() throws IOException {
    return m_input.readByte();
  }

  @Override
  public int readUnsignedByte() throws IOException {
    return m_input.readUnsignedByte();
  }

  @Override
  public short readShort() throws IOException {
    return (short) readUnsignedShort();
  }

  @Override
  public int readUnsignedShort() throws IOException {
    readFully(buffer, 0, Short.BYTES);
    return ((buffer[0] & 0xff))
      + ((buffer[1] & 0xff) << 8);
  }

  @Override
  public char readChar() throws IOException {
    return m_input.readChar();
  }

  @Override
  public int readInt() throws IOException {
    readFully(buffer, 0, Integer.BYTES);
    return ((buffer[0] & 0xff))
      + ((buffer[1] & 0xff) << 8)
      + ((buffer[2] & 0xff) << 16)
      + ((buffer[3] & 0xff) << 24);
  }

  @Override
  public long readLong() throws IOException {
    readFully(buffer, 0, Long.BYTES);
    return ((buffer[0] & 0xFFL))
      + ((buffer[1] & 0xFFL) << 8)
      + ((buffer[2] & 0xFFL) << 16)
      + ((buffer[3] & 0xFFL) << 24)
      + ((buffer[4] & 0xFFL) << 32)
      + ((buffer[5] & 0xFFL) << 40)
      + ((buffer[6] & 0xFFL) << 48)
      + ((buffer[7] & 0xFFL) << 56);
  }

  @Override
  public float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }

  @Override
  public double readDouble() throws IOException {
    return Double.longBitsToDouble(readLong());
  }

  @Override
  public String readLine() throws IOException {
    return m_input.readLine();
  }

  @Override
  public @NotNull String readUTF() throws IOException {
    return m_input.readUTF();
  }
}
