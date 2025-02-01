package com.yaetoti.ppm;

import java.io.FileOutputStream;
import java.io.IOException;

public final class PpmImage {
  private final int m_width;
  private final int m_height;
  private final byte[] m_data;

  public PpmImage(int width, int height, byte[] data) {
    assert width > 0 && height > 0;
    assert data != null;
    assert data.length == width * height * 3;

    m_width = width;
    m_height = height;
    m_data = data;
  }

  public void SaveToFile(String filename) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(filename)) {
      // Header
      String header = String.format("P6\n%d %d\n255\n", m_width, m_height);
      fos.write(header.getBytes());
      // Data
      fos.write(m_data);
    }
  }
}
