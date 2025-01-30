package com.yaetoti.gif.blocks;

public class GifImageDescriptor {
  public int imageLeftPosition;
  public int imageTopPosition;
  public int imageWidth;
  public int imageHeight;
  public int localColorTableSize;
  public boolean isInterlaced;
  public boolean isLocalColorTablePresent;
  public boolean isLocalColorTableSorted;

  @Override
  public String toString() {
    return "GifImageDescriptor{" +
      "imageLeftPosition=" + imageLeftPosition +
      ", imageTopPosition=" + imageTopPosition +
      ", imageWidth=" + imageWidth +
      ", imageHeight=" + imageHeight +
      ", localColorTableSize=" + localColorTableSize +
      ", isInterlaced=" + isInterlaced +
      ", isLocalColorTablePresent=" + isLocalColorTablePresent +
      ", isLocalColorTableSorted=" + isLocalColorTableSorted +
      '}';
  }
}
