package com.yaetoti.gif.blocks;

import com.yaetoti.gif.utils.GifVersion;

public final class GifHeader extends GifElement {
  public GifVersion version;

  public GifHeader() {
    super(GifElementType.HEADER);
  }
}
