package com.yaetoti.gif.io;

import com.yaetoti.gif.blocks.*;
import com.yaetoti.gif.utils.GifBlockLabel;
import com.yaetoti.gif.utils.GifExtensionLabel;
import com.yaetoti.io.DataInputLE;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class GifReader {
  private final GifInput m_input;
  private State m_state;
  private int m_nextColorTableSize;

  public GifReader(@NotNull DataInputLE in) {
    m_input = new GifInput(in);
    m_state = State.NEXT_HEADER;
    m_nextColorTableSize = 0;
  }

  void SetInput(@NotNull DataInputLE input) {
    m_input.SetInput(input);
  }

  public void Reset() {
    m_state = State.NEXT_HEADER;
  }

  public void Reset(@NotNull DataInputLE input) {
    m_state = State.NEXT_HEADER;
    m_input.SetInput(input);
  }

  public State GetState() {
    return m_state;
  }

  @NotNull
  public GifElement ReadElement() throws IOException {
    return switch (m_state) {
    case NEXT_HEADER -> HandleNextHeader();
    case NEXT_LOGICAL_SCREEN_DESCRIPTOR -> HandleNextLogicalScreenDescriptor();
    case NEXT_GLOBAL_COLOR_TABLE -> HandleNextGlobalColorTable();
    case NEXT_BLOCK -> HandleNextBlock();
    case NEXT_GRAPHICS_BLOCK -> HandleNextGraphicsBlock();
    case NEXT_LOCAL_COLOR_TABLE -> HandleNextLocalColorTable();
    case NEXT_TABLE_BASED_IMAGE_DATA -> HandleNextTableBasedImageData();
    default -> throw new IllegalStateException("Unexpected value: " + m_state);
    };
  }

  @NotNull
  private GifElement HandleNextHeader() throws IOException {
    GifHeader element = m_input.ReadHeader();
    m_state = State.NEXT_LOGICAL_SCREEN_DESCRIPTOR;
    return element;
  }

  @NotNull
  private GifElement HandleNextLogicalScreenDescriptor() throws IOException {
    GifLogicalScreenDescriptor element = m_input.ReadLogicalScreenDescriptor();
    if (element.isGlobalColorTablePresent) {
      m_nextColorTableSize = element.globalColorTableSize;
      m_state = State.NEXT_GLOBAL_COLOR_TABLE;
    }
    else {
      m_state = State.NEXT_BLOCK;
    }

    return element;
  }

  @NotNull
  private GifElement HandleNextGlobalColorTable() throws IOException {
    GifColorTable element = m_input.ReadColorTable(m_nextColorTableSize, GifColorTable.Type.GLOBAL);
    m_state = State.NEXT_BLOCK;
    return element;
  }

  @NotNull
  private GifElement HandleNextBlock() throws IOException {
    GifBlockLabel blockLabel = m_input.ReadBlockLabel();
    if (blockLabel == GifBlockLabel.TRAILER) {
      m_state = State.NEXT_HEADER;
      return new GifTrailer();
    }

    if (blockLabel == GifBlockLabel.EXTENSION) {
      GifExtensionLabel extensionLabel = m_input.ReadExtensionLabel();
      if (extensionLabel == GifExtensionLabel.GRAPHICS_CONTROL) {
        GifGraphicsControlExtension element = m_input.ReadGraphicControlExtension();
        m_state = State.NEXT_GRAPHICS_BLOCK;
        return element;
      }

      if (extensionLabel == GifExtensionLabel.APPLICATION) {
        GifApplicationExtension element = m_input.ReadApplicationExtension();
        m_state = State.NEXT_BLOCK;
        return element;
      }

      if (extensionLabel == GifExtensionLabel.COMMENT) {
        GifCommentExtension element = m_input.ReadCommentExtension();
        m_state = State.NEXT_BLOCK;
        return element;
      }

      throw new GifInvalidFormatException("Unexpected extension label: " + extensionLabel);
    }

    throw new GifInvalidFormatException("Unexpected block label: " + blockLabel);
  }

  @NotNull
  private GifElement HandleNextGraphicsBlock() throws IOException {
    GifBlockLabel blockLabel = m_input.ReadBlockLabel();
    if (blockLabel == GifBlockLabel.IMAGE_DESCRIPTOR) {
      GifImageDescriptor element = m_input.ReadImageDescriptor();
      if (element.isLocalColorTablePresent) {
        m_nextColorTableSize = element.localColorTableSize;
        m_state = State.NEXT_LOCAL_COLOR_TABLE;
      }
      else {
        m_state = State.NEXT_TABLE_BASED_IMAGE_DATA;
      }

      return element;
    }

    if (blockLabel == GifBlockLabel.EXTENSION) {
      GifExtensionLabel extensionLabel = m_input.ReadExtensionLabel();
      if (extensionLabel == GifExtensionLabel.PLAIN_TEXT) {
        GifPlainTextExtension element = m_input.ReadPlainTextExtension();
        m_state = State.NEXT_BLOCK;
        return element;
      }

      throw new GifInvalidFormatException("Unexpected extension label: " + extensionLabel);
    }

    throw new GifInvalidFormatException("Unexpected block label: " + blockLabel);
  }

  @NotNull
  private GifElement HandleNextLocalColorTable() throws IOException {
    GifColorTable element = m_input.ReadColorTable(m_nextColorTableSize, GifColorTable.Type.LOCAL);
    m_state = State.NEXT_TABLE_BASED_IMAGE_DATA;
    return element;
  }

  @NotNull
  private GifElement HandleNextTableBasedImageData() throws IOException {
    GifTableBasedImageData element = m_input.ReadTableBasedImageData();
    m_state = State.NEXT_BLOCK;
    return element;
  }

  public enum State {
    NEXT_HEADER,
    NEXT_LOGICAL_SCREEN_DESCRIPTOR,
    NEXT_GLOBAL_COLOR_TABLE,
    NEXT_BLOCK,
    NEXT_GRAPHICS_BLOCK, // After GCE goes plain text extension or image descriptor
    NEXT_LOCAL_COLOR_TABLE,
    NEXT_TABLE_BASED_IMAGE_DATA
  }
}
