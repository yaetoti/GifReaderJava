package com.yaetoti.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;

public class DataOutputLE implements DataOutput {
  private final DataOutput m_output;

  public DataOutputLE(DataOutput output) {
    m_output = output;
  }

  @Override
  public void write(int b) throws IOException {

  }

  @Override
  public void write(byte @NotNull[] b) throws IOException {
    m_output.write(b);
  }

  @Override
  public void write(byte @NotNull[] b, int off, int len) throws IOException {
    m_output.write(b, off, len);
  }

  @Override
  public void writeBoolean(boolean v) throws IOException {
    m_output.writeBoolean(v);
  }

  @Override
  public void writeByte(int v) throws IOException {
    m_output.writeByte(v);
  }

  @Override
  public void writeShort(int v) throws IOException {
    m_output.write(v & 0xFF);
    m_output.write((v >>> 8) & 0xFF);
  }

  @Override
  public void writeChar(int v) throws IOException {
    m_output.write(v & 0xFF);
    m_output.write((v >>> 8) & 0xFF);
  }

  @Override
  public void writeInt(int v) throws IOException {
    m_output.write(v & 0xFF);
    m_output.write((v >>> 8) & 0xFF);
    m_output.write((v >>> 16) & 0xFF);
    m_output.write((v >>> 24) & 0xFF);
  }

  @Override
  public void writeLong(long v) throws IOException {
    m_output.write((int) (v & 0xFF));
    m_output.write((int) ((v >>> 8) & 0xFF));
    m_output.write((int) ((v >>> 16) & 0xFF));
    m_output.write((int) ((v >>> 24) & 0xFF));
    m_output.write((int) ((v >>> 32) & 0xFF));
    m_output.write((int) ((v >>> 40) & 0xFF));
    m_output.write((int) ((v >>> 48) & 0xFF));
    m_output.write((int) ((v >>> 56) & 0xFF));
  }

  @Override
  public void writeFloat(float v) throws IOException {
    writeInt(Float.floatToIntBits(v));
  }

  @Override
  public void writeDouble(double v) throws IOException {
    writeLong(Double.doubleToLongBits(v));
  }

  @Override
  public void writeBytes(@NotNull String s) throws IOException {
    for (int i = 0; i < s.length(); i++) {
      m_output.writeByte((int)(s.charAt(i)) & 0xFF);
    }
  }

  @Override
  public void writeChars(@NotNull String s) throws IOException {
    for (int i = 0; i < s.length(); i++) {
      writeChar(s.charAt(i));
    }
  }

  @Override
  public void writeUTF(@NotNull String s) throws IOException {
    m_output.writeUTF(s);
  }
}
