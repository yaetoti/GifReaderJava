package com.yaetoti.gif.io;

import com.yaetoti.gif.InvalidFormatException;
import com.yaetoti.gif.blocks.*;
import com.yaetoti.gif.utils.DisposalMethod;
import com.yaetoti.gif.utils.GifBlockLabel;
import com.yaetoti.gif.utils.GifExtensionLabel;
import com.yaetoti.gif.utils.GifVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedList;

public final class GifInput {
  @NotNull
  private DataInput m_input;

  public GifInput(@NotNull DataInput input) {
    m_input = input;
  }

  public void SetInput(@NotNull DataInput input) {
    m_input = input;
  }

  /**
   * Reads GIF header (signature)
   * @return Gif version
   * @throws IOException if IO error occurs
   * @throws InvalidFormatException if signature is invalid
   */
  @NotNull
  public GifVersion ReadHeader() throws IOException {
    byte[] signature = new byte[6];
    m_input.readFully(signature);
    return GifVersion.FromSignature(new String(signature));
  }

  @NotNull
  public GifLogicalScreenDescriptor ReadLogicalScreenDescriptor() throws IOException {
    GifLogicalScreenDescriptor descriptor = new GifLogicalScreenDescriptor();
    descriptor.logicalScreenWidth = m_input.readUnsignedShort();
    descriptor.logicalScreenHeight = m_input.readUnsignedShort();

    int packed = m_input.readUnsignedByte();
    descriptor.globalColorTableSize = 1 << ((packed & 0b00000111) + 1);
    descriptor.isGlobalColorTableSorted = (packed & 0b00001000) != 0;
    descriptor.colorResolution = ((packed & 0b01110000) >> 4) + 1;
    descriptor.isGlobalColorTablePresent = (packed & 0b10000000) != 0;

    descriptor.backgroundColorIndex = m_input.readUnsignedByte();
    descriptor.pixelAspectRatio = m_input.readUnsignedByte();

    return descriptor;
  }

  public byte @NotNull[] ReadColorTable(int size) throws IOException {
    byte[] colorTable = new byte[3 * size];
    m_input.readFully(colorTable);
    return colorTable;
  }

  @NotNull
  public GifGraphicsControlExtension ReadGraphicControlExtension() throws IOException {
    int blockSize = m_input.readUnsignedByte();
    if (blockSize != 4) {
      throw new InvalidFormatException("Invalid Graphics Control Extension sub-block size");
    }

    int packed = m_input.readUnsignedByte();

    GifGraphicsControlExtension extension = new GifGraphicsControlExtension();
    extension.transparentColorFlag = (packed & 0b00000001) != 0;
    extension.userInputFlag = (packed & 0b00000010) != 0;
    extension.disposalMethod = DisposalMethod.FromId((packed & 0b00011100) >> 2);
    extension.delayTime = m_input.readUnsignedShort();
    extension.transparentColorIndex = m_input.readUnsignedByte();

    if (m_input.readUnsignedByte() != 0) {
      throw new InvalidFormatException("Invalid Graphics Control Extension terminator");
    }

    return extension;
  }

  @NotNull
  public GifPlainTextExtension ReadPlainTextExtension() throws IOException {
    int blockSize = m_input.readUnsignedByte();
    if (blockSize != 12) {
      throw new InvalidFormatException("Invalid Plain Text Extension sub-block size");
    }

    GifPlainTextExtension extension = new GifPlainTextExtension();
    extension.textGridLeftPosition = m_input.readUnsignedShort();
    extension.textGridTopPosition = m_input.readUnsignedShort();
    extension.textGridWidth = m_input.readUnsignedShort();
    extension.textGridHeight = m_input.readUnsignedShort();
    extension.characterCellWidth = m_input.readUnsignedByte();
    extension.characterCellHeight = m_input.readUnsignedByte();
    extension.textForegroundColorIndex = m_input.readUnsignedByte();
    extension.textBackgroundColorIndex = m_input.readUnsignedByte();
    extension.plainTextData = ReadSubBlocks();

    return extension;
  }

  @NotNull
  public GifImageDescriptor ReadImageDescriptor() throws IOException {
    GifImageDescriptor descriptor = new GifImageDescriptor();
    descriptor.imageLeftPosition = m_input.readUnsignedShort();
    descriptor.imageTopPosition = m_input.readUnsignedShort();
    descriptor.imageWidth = m_input.readUnsignedShort();
    descriptor.imageHeight = m_input.readUnsignedShort();

    int packed = m_input.readUnsignedByte();
    descriptor.localColorTableSize = 1 << ((packed & 0b00000111) + 1);
    descriptor.isLocalColorTableSorted = (packed & 0b00100000) != 0;
    descriptor.isInterlaced = (packed & 0b01000000) != 0;
    descriptor.isLocalColorTablePresent = (packed & 0b10000000) != 0;

    return descriptor;
  }

  @NotNull
  public GifTableBasedImageData ReadTableBasedImageData() throws IOException {
    GifTableBasedImageData imageData = new GifTableBasedImageData();
    imageData.lzwMinimumCodeSize = m_input.readUnsignedByte();
    imageData.imageData = ReadSubBlocks();
    return imageData;
  }

  @NotNull
  public GifApplicationExtension ReadApplicationExtension() throws IOException {
    int blockSize = m_input.readUnsignedByte();
    if (blockSize != 11) {
      throw new InvalidFormatException("Invalid Application Extension sub-block size");
    }

    byte[] identifierBytes = new byte[8];
    m_input.readFully(identifierBytes);
    String applicationIdentifier = new String(identifierBytes);

    byte[] authenticationCode = new byte[3];
    m_input.readFully(authenticationCode);

    byte @Nullable[] applicationData = ReadSubBlocks();

    GifApplicationExtension extension = new GifApplicationExtension();
    extension.applicationIdentifier = applicationIdentifier;
    extension.authenticationCode = authenticationCode;
    extension.applicationData = applicationData;

    return extension;
  }

  @NotNull
  public GifCommentExtension ReadCommentExtension() throws IOException {
    byte @Nullable[] commentBytes = ReadSubBlocks();
    String comment = commentBytes == null ? "" : new String(commentBytes);

    GifCommentExtension extension = new GifCommentExtension();
    extension.commentData = comment;
    return extension;
  }


  /**
   * Reads block label byte
   * @return block label
   * @throws IOException if IO error occurs
   */
  @NotNull
  public GifBlockLabel ReadBlockLabel() throws IOException {
    return GifBlockLabel.FromId(m_input.readByte());
  }

  /**
   * Reads extension ID byte
   * @return extension ID
   * @throws IOException if IO error occurs
   */
  @NotNull
  public GifExtensionLabel ReadExtensionLabel() throws IOException {
    return GifExtensionLabel.FromId(m_input.readUnsignedByte());
  }

  /**
   * Reads data of all subsequent sub-blocks before 0-length sub-block is met.
   * <p> ([3 1 2 3] [2 1 2] [0]) [3 1 2 3] = byte[] { 1 2 3 1 2 } </p>
   * <p> ([0]) [3 1 2 3] = null </p>
   * @return data of all sub-blocks before first 0-length sub-block / null if 0-length sub-block is the first to read
   * @throws IOException if IO error occurs / if stream ends before able to read all the data
   */
  public byte @Nullable [] ReadSubBlocks() throws IOException {
    LinkedList<byte[]> subBlocks = new LinkedList<>();
    while (true) {
      byte[] subBlock = ReadSubBlock();
      // Stop at 0-length sub-block
      if (subBlock == null) {
        break;
      }

      subBlocks.add(subBlock);
    }

    // 0-length sub-block was read
    if (subBlocks.isEmpty()) {
      return null;
    }

    // Combine and return data
    int size = 0;
    for (byte[] subBlock : subBlocks) {
      size += subBlock.length;
    }

    byte[] result = new byte[size];
    int offset = 0;
    for (byte[] subBlock : subBlocks) {
      System.arraycopy(subBlock, 0, result, offset, subBlock.length);
      offset += subBlock.length;
    }

    return result;
  }

  /**
   * @return data sub-block / null if 0-length block was read
   * @throws IOException if IO error occurs / if stream ends before able to read all the data
   * @throws EOFException if no data in the data stream
   */
  public byte @Nullable[] ReadSubBlock() throws IOException {
    int subBlockSize = m_input.readUnsignedByte();
    if (subBlockSize == 0) {
      return null;
    }

    byte[] data = new byte[subBlockSize];
    // Contract tells that there should be the required amount of data. So, its absence = error
    try {
      m_input.readFully(data);
    } catch (EOFException e) {
      throw new IOException(e.getMessage());
    }

    return data;
  }
}
