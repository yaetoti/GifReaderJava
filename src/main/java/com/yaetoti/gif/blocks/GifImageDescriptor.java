package com.yaetoti.gif.blocks;

public class GifImageDescriptor extends GifElement {
  public int imageLeftPosition;
  public int imageTopPosition;
  public int imageWidth;
  public int imageHeight;
  public int localColorTableSize;
  public boolean isInterlaced;
  public boolean isLocalColorTablePresent;
  public boolean isLocalColorTableSorted;

  public GifImageDescriptor() {
    super(GifElementType.IMAGE_DESCRIPTOR);
  }

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
