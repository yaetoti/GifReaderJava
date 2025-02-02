package com.yaetoti.gif.blocks;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class GifTableBasedImageData extends GifElement {
  public int lzwMinimumCodeSize;
  public byte @Nullable[] imageData;

  public GifTableBasedImageData() {
    super(GifElementType.TABLE_BASED_IMAGE_DATA);
  }

  @Override
  public String toString() {
    return "GifTableBasedImageData{" +
      "lzwMinimumCodeSize=" + lzwMinimumCodeSize +
      ", imageData=" + Arrays.toString(imageData) +
      '}';
  }
}
