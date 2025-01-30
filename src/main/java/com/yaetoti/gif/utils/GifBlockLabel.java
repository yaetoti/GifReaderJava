package com.yaetoti.gif.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public enum GifBlockLabel {
  EXTENSION(GifBlockLabel.EXTENSION_ID),
  IMAGE_DESCRIPTOR(GifBlockLabel.IMAGE_DESCRIPTOR_ID),
  TRAILER(GifBlockLabel.TRAILER_ID);

  public static final int EXTENSION_ID = 0x21;
  public static final int IMAGE_DESCRIPTOR_ID = 0x2C;
  public static final int TRAILER_ID = 0x3B;

  private final int m_id;

  GifBlockLabel(int id) {
    m_id = id;
  }

  @Range(from = 0, to = 255)
  public int GetId() {
    return m_id;
  }

  @NotNull
  public static GifBlockLabel FromId(int id) {
    return switch (id) {
      case EXTENSION_ID -> EXTENSION;
      case IMAGE_DESCRIPTOR_ID -> IMAGE_DESCRIPTOR;
      case TRAILER_ID -> TRAILER;
      default -> throw new IllegalArgumentException("Invalid gif block id: " + id);
    };
  }
}
