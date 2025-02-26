package com.yaetoti.paf.io;

import com.yaetoti.io.DataInputLE;
import com.yaetoti.paf.blocks.PafDescriptor;
import com.yaetoti.paf.blocks.PafHeader;
import com.yaetoti.paf.blocks.PafImageData;
import com.yaetoti.paf.blocks.PafIndices;
import com.yaetoti.paf.utils.PafIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class PafInput {
  @NotNull
  private DataInputLE m_input;

  public PafInput(@NotNull DataInputLE input) {
    m_input = input;
  }

  public void SetInput(@NotNull DataInputLE input) {
    m_input = input;
  }

  @NotNull
  public PafHeader ReadHeader() throws IOException {
    byte[] signature = new byte[3];
    m_input.readFully(signature);

    String signatureString = new String(signature);
    if (!signatureString.equals("PAF")) {
      throw new IOException("Invalid signature");
    }

    return new PafHeader();
  }

  @NotNull
  public PafDescriptor ReadDescriptor() throws IOException {
    PafDescriptor descriptor = new PafDescriptor();
    descriptor.width = m_input.readInt();
    descriptor.height = m_input.readInt();
    descriptor.frames = m_input.readInt();
    return descriptor;
  }

  @NotNull
  public PafIndices ReadIndices(int framesCount) throws IOException {
    PafIndices indices = new PafIndices();
    for (int i = 0; i < framesCount; i++) {
      indices.indices.add(ReadIndex());
    }

    return indices;
  }

  public PafImageData ReadImageData(int frameSize) throws IOException {
    PafImageData imageData = new PafImageData();
    imageData.data = new byte[frameSize];
    m_input.readFully(imageData.data);
    return imageData;
  }

  @NotNull
  public PafIndex ReadIndex() throws IOException {
    PafIndex index = new PafIndex();
    index.frameOffset = m_input.readLong();
    index.frameSize = m_input.readLong();
    index.delay = m_input.readLong();
    return index;
  }
}
