package com.yaetoti.gif.blocks;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class GifTableBasedImageData {
  public int lzwMinimumCodeSize;
  public byte @Nullable[] imageData;

  @Override
  public String toString() {
    return "GifTableBasedImageData{" +
      "lzwMinimumCodeSize=" + lzwMinimumCodeSize +
      ", imageData=" + Arrays.toString(imageData) +
      '}';
  }
}
