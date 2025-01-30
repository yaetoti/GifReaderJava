package com.yaetoti.gif.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public enum GifExtensionLabel {
  APPLICATION(GifExtensionLabel.APPLICATION_EXTENSION_ID),
  COMMENT(GifExtensionLabel.COMMENT_EXTENSION_ID),
  GRAPHICS_CONTROL(GifExtensionLabel.GRAPHICS_CONTROL_EXTENSION_ID),
  PLAIN_TEXT(GifExtensionLabel.PLAIN_TEXT_EXTENSION_ID);

  public static final int APPLICATION_EXTENSION_ID = 0xFF;
  public static final int COMMENT_EXTENSION_ID = 0xFE;
  public static final int GRAPHICS_CONTROL_EXTENSION_ID = 0xF9;
  public static final int PLAIN_TEXT_EXTENSION_ID = 0x01;

  private final int m_id;

  GifExtensionLabel(int id) {
    m_id = id;
  }

  @Range(from = 0, to = 255)
  public int GetId() {
    return m_id;
  }

  @NotNull
  public static GifExtensionLabel FromId(int id) {
    return switch (id) {
      case APPLICATION_EXTENSION_ID -> APPLICATION;
      case COMMENT_EXTENSION_ID -> COMMENT;
      case GRAPHICS_CONTROL_EXTENSION_ID -> GRAPHICS_CONTROL;
      case PLAIN_TEXT_EXTENSION_ID -> PLAIN_TEXT;
      default -> throw new IllegalArgumentException("Invalid gif extension id: " + id);
    };
  }
}
