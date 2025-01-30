package com.yaetoti.gif.blocks;

import com.yaetoti.gif.utils.GifExtensionLabel;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class GifPlainTextExtension {
  public int textGridLeftPosition;
  public int textGridTopPosition;
  public int textGridWidth;
  public int textGridHeight;
  public int characterCellWidth;
  public int characterCellHeight;
  public int textForegroundColorIndex;
  public int textBackgroundColorIndex;
  public byte @Nullable[] plainTextData;

  @Override
  public String toString() {
    return "GifPlainTextExtension{" +
      "textGridLeftPosition=" + textGridLeftPosition +
      ", textGridTopPosition=" + textGridTopPosition +
      ", textGridWidth=" + textGridWidth +
      ", textGridHeight=" + textGridHeight +
      ", characterCellWidth=" + characterCellWidth +
      ", characterCellHeight=" + characterCellHeight +
      ", textForegroundColorIndex=" + textForegroundColorIndex +
      ", textBackgroundColorIndex=" + textBackgroundColorIndex +
      ", plainTextData=" + Arrays.toString(plainTextData) +
      '}';
  }
}
