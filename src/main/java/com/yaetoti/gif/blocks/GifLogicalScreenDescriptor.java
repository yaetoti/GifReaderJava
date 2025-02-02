package com.yaetoti.gif.blocks;

public final class GifLogicalScreenDescriptor extends GifElement {
  /// (unsigned word)
  public int logicalScreenWidth;
  /// (unsigned word)
  public int logicalScreenHeight;
  /// 2^(N + 1) colors
  public int globalColorTableSize;
  /// <p>(unsigned byte)</p>
  /// N + 1 = Number of bits per color of original image
  public int colorResolution;
  public int backgroundColorIndex;
  /// Aspect ratio = W / H = (N + 15) / 64 or 0 if no information is given
  public int pixelAspectRatio;
  public boolean isGlobalColorTableSorted;
  public boolean isGlobalColorTablePresent;

  public GifLogicalScreenDescriptor() {
    super(GifElementType.LOGICAL_SCREEN_DESCRIPTOR);
  }

  @Override
  public String toString() {
    return "GifLogicalScreenDescriptor{" +
      "logicalScreenWidth=" + logicalScreenWidth +
      ", logicalScreenHeight=" + logicalScreenHeight +
      ", globalColorTableSize=" + globalColorTableSize +
      ", colorResolution=" + colorResolution +
      ", backgroundColorIndex=" + backgroundColorIndex +
      ", pixelAspectRatio=" + pixelAspectRatio +
      ", isGlobalColorTableSorted=" + isGlobalColorTableSorted +
      ", isGlobalColorTablePresent=" + isGlobalColorTablePresent +
      '}';
  }
}
