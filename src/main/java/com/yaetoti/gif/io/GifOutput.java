package com.yaetoti.gif.io;

import com.yaetoti.gif.blocks.*;
import com.yaetoti.gif.utils.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class GifOutput {
  @NotNull
  private DataOutputLE m_output;

  public GifOutput(@NotNull DataOutputLE output) {
    m_output = output;
  }

  public void SetOutput(@NotNull DataOutputLE output) {
    m_output = output;
  }

  public void WriteElement(@NotNull GifElement element) throws IOException {
    switch (element.GetElementType()) {
    case HEADER -> {
      WriteHeader(element.As());
    }
    case LOGICAL_SCREEN_DESCRIPTOR -> {
      WriteLogicalScreenDescriptor(element.As());
    }
    case COLOR_TABLE -> {
      WriteColorTable(element.As());
    }
    case IMAGE_DESCRIPTOR -> {
      WriteImageDescriptor(element.As());
    }
    case TABLE_BASED_IMAGE_DATA -> {
      WriteTableBasedImageData(element.As());
    }
    case TRAILER -> {
      WriteTrailer(element.As());
    }
    case APPLICATION_EXTENSION -> {
      WriteApplicationExtension(element.As());
    }
    case GRAPHIC_CONTROL_EXTENSION -> {
      WriteGraphicControlExtension(element.As());
    }
    case COMMENT_EXTENSION -> {
      WriteCommentExtension(element.As());
    }
    case PLAIN_TEXT_EXTENSION -> {
      WritePlainTextExtension(element.As());
    }
    default -> {
      throw new IllegalStateException("Unexpected value: " + element.GetElementType());
    }
    }
  }

  public void WriteHeader(@NotNull GifHeader element) throws IOException {
    m_output.write(element.version.GetSignature().getBytes(StandardCharsets.US_ASCII));
  }

  public void WriteTrailer(@NotNull GifTrailer element) throws IOException {
    m_output.writeByte(GifBlockLabel.TRAILER_ID);
  }

  public void WriteLogicalScreenDescriptor(@NotNull GifLogicalScreenDescriptor element) throws IOException {
    m_output.writeShort(element.logicalScreenWidth);
    m_output.writeShort(element.logicalScreenHeight);
    int packed = 0;
    packed |= (BitUtils.GetBitLength(element.globalColorTableSize) - 1) & 0x7;
    packed |= element.isGlobalColorTableSorted ? (1 << 3) : 0;
    packed |= ((element.colorResolution - 1) & 0x7) << 4;
    packed |= element.isGlobalColorTablePresent ? (1 << 7) : 0;
    m_output.writeByte(packed);
    m_output.writeByte(element.backgroundColorIndex);
    m_output.writeByte(element.pixelAspectRatio);
  }

  public void WriteColorTable(@NotNull GifColorTable element) throws IOException {
    m_output.write(element.table);
  }

  public void WriteGraphicControlExtension(@NotNull GifGraphicsControlExtension element) throws IOException {
    m_output.writeByte(GifBlockLabel.EXTENSION_ID);
    m_output.writeByte(GifExtensionLabel.GRAPHICS_CONTROL_EXTENSION_ID);

    // Start of a sub-block
    m_output.writeByte(4);
    int packed = 0;
    packed |= element.transparentColorFlag ? 1 : 0;
    packed |= element.userInputFlag ? 0x2 : 0;
    packed |= (element.disposalMethod.GetId() & 0x7) << 2;
    // 3 Reserved
    m_output.writeByte(packed);
    m_output.writeShort(element.delayTime);
    m_output.writeByte(element.transparentColorIndex);
    // end of a sub-block
    m_output.writeByte(0);
  }

  public void WritePlainTextExtension(@NotNull GifPlainTextExtension element) throws IOException {
    m_output.writeByte(GifBlockLabel.EXTENSION_ID);
    m_output.writeByte(GifExtensionLabel.PLAIN_TEXT_EXTENSION_ID);

    // Start of the first sub-block
    m_output.writeByte(12);
    m_output.writeShort(element.textGridLeftPosition);
    m_output.writeShort(element.textGridTopPosition);
    m_output.writeShort(element.textGridWidth);
    m_output.writeShort(element.textGridHeight);
    m_output.writeByte(element.characterCellWidth);
    m_output.writeByte(element.characterCellHeight);
    m_output.writeByte(element.textForegroundColorIndex);
    m_output.writeByte(element.textBackgroundColorIndex);
    WriteSubBlocks(element.plainTextData);
  }

  public void WriteImageDescriptor(@NotNull GifImageDescriptor element) throws IOException {
    m_output.writeByte(GifBlockLabel.IMAGE_DESCRIPTOR_ID);

    m_output.writeShort(element.imageLeftPosition);
    m_output.writeShort(element.imageTopPosition);
    m_output.writeShort(element.imageWidth);
    m_output.writeShort(element.imageHeight);
    int packed = 0;
    packed |= (BitUtils.GetBitLength(element.localColorTableSize) - 1) & 0x7;
    // 2 Reserved
    packed |= element.isLocalColorTableSorted ? (1 << 5) : 0;
    packed |= element.isInterlaced ? (1 << 6) : 0;
    packed |= element.isLocalColorTablePresent ? (1 << 7) : 0;
    m_output.writeByte(packed);
  }

  public void WriteTableBasedImageData(@NotNull GifTableBasedImageData element) throws IOException {
    m_output.writeByte(element.lzwMinimumCodeSize);
    WriteSubBlocks(element.imageData);
  }

  public void WriteApplicationExtension(@NotNull GifApplicationExtension element) throws IOException {
    m_output.writeByte(GifBlockLabel.EXTENSION_ID);
    m_output.writeByte(GifExtensionLabel.APPLICATION_EXTENSION_ID);

    m_output.writeByte(11);
    m_output.write(element.applicationIdentifier.getBytes(StandardCharsets.US_ASCII));
    m_output.write(element.authenticationCode);
    WriteSubBlocks(element.applicationData);
  }

  public void WriteCommentExtension(@NotNull GifCommentExtension element) throws IOException {
    m_output.writeByte(GifBlockLabel.EXTENSION_ID);
    m_output.writeByte(GifExtensionLabel.COMMENT_EXTENSION_ID);

    WriteSubBlocks(element.commentData.getBytes(StandardCharsets.US_ASCII));
  }

  public void WriteTrailer() throws IOException {
    m_output.writeByte(GifBlockLabel.TRAILER_ID);
  }

  public void WriteSubBlocks(byte @Nullable [] data) throws IOException {
    if (data == null) {
      m_output.writeByte(0);
      return;
    }

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
