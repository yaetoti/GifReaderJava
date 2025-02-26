package com.yaetoti.paf.io;

import com.yaetoti.io.DataOutputLE;
import com.yaetoti.paf.blocks.PafDescriptor;
import com.yaetoti.paf.blocks.PafImageData;
import com.yaetoti.paf.blocks.PafIndices;
import com.yaetoti.paf.utils.PafIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PafOutput {
  @NotNull
  private DataOutputLE m_output;

  public PafOutput(@NotNull DataOutputLE output) {
    m_output = output;
  }

  public void SetOutput(@NotNull DataOutputLE output) {
    m_output = output;
  }

  public void WriteHeader() throws IOException {
    m_output.write("PAF".getBytes(StandardCharsets.US_ASCII));
  }

  public void WriteDescriptor(@NotNull PafDescriptor element) throws IOException {
    m_output.writeInt(element.width);
    m_output.writeInt(element.height);
    m_output.writeInt(element.frames);
  }

  public void WriteIndices(@NotNull PafIndices element) throws IOException {
    for (PafIndex index : element.indices) {
      WriteIndex(index);
    }
  }

  public void WriteImageData(@NotNull PafImageData element) throws IOException {
    assert element.data != null;

    m_output.write(element.data);
  }

  public void WriteIndex(@NotNull PafIndex index) throws IOException {
    m_output.writeLong(index.frameOffset);
    m_output.writeLong(index.frameSize);
    m_output.writeLong(index.delay);
  }
}
