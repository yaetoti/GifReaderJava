package com.yaetoti.gif.blocks;

import com.yaetoti.gif.utils.DisposalMethod;

public final class GifGraphicsControlExtension extends GifElement {
  public boolean transparentColorFlag;
  public boolean userInputFlag;
  public DisposalMethod disposalMethod;
  /// 1/100 of a second
  public int delayTime;
  public int transparentColorIndex;

  public GifGraphicsControlExtension() {
    super(GifElementType.GRAPHIC_CONTROL_EXTENSION);
  }

  @Override
  public String toString() {
    return "GifGraphicsControlExtension{" +
      "transparentColorFlag=" + transparentColorFlag +
      ", userInputFlag=" + userInputFlag +
      ", disposalMethod=" + disposalMethod +
      ", delayTime=" + delayTime +
      ", transparentColorIndex=" + transparentColorIndex +
      '}';
  }
}
