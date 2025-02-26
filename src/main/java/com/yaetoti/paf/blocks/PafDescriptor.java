package com.yaetoti.paf.blocks;

import com.yaetoti.paf.utils.PafElementType;

public final class PafDescriptor extends PafElement {
  public int width;
  public int height;
  public int frames;

  public PafDescriptor() {
    super(PafElementType.DESCRIPTOR);
  }
}
