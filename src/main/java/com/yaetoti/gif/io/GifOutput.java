package com.yaetoti.gif.io;

import com.yaetoti.gif.utils.GifBlockLabel;
import com.yaetoti.gif.utils.GifExtensionLabel;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;

public final class GifOutput {
  @NotNull
  private DataOutput m_output;

  public GifOutput(@NotNull DataOutput output) {
    m_output = output;
  }

  public void SetOutput(@NotNull DataOutput output) {
    m_output = output;
  }

  public void ReadHeader() {

  }

  public void ReadLogicalScreenDescriptor() {

  }

  public void ReadColorTable() {

  }

  public void ReadGraphicControlExtension() {

  }

  public void ReadPlainTextExtension() {

  }

  public void ReadImageDescriptor() {

  }

  public void ReadTableBasedImageData() {

  }

  public void ReadApplicationExtension() {

  }

  public void ReadCommentExtension() {

  }



  public void WriteBlockId(GifBlockLabel type) throws IOException {
    m_output.writeByte(type.GetId());
  }

  public void WriteExtensionId(GifExtensionLabel type) throws IOException {
    m_output.writeByte(type.GetId());
  }

  public void WriteSubBlocks(byte @NotNull[] data) throws IOException {
    int remainingBytes = data.length;
    while (true) {
      // Zero-block at the end
      if (remainingBytes == 0) {
        m_output.writeByte(0);
        return;
      }

      // Remaining bytes
      if (remainingBytes <= 255) {
        m_output.writeByte(remainingBytes);
        m_output.write(data, data.length - remainingBytes, remainingBytes);
        remainingBytes = 0;
        continue;
      }

      // Full block
      m_output.writeByte(255);
      m_output.write(data, data.length - remainingBytes, 255);
      remainingBytes -= 255;
    }
  }
}
