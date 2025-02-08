package com.yaetoti.gif.blocks;

import com.yaetoti.gif.utils.GifColorTableType;

public final class GifColorTable extends GifElement {
  public byte[] table;
  public GifColorTableType type;

  public GifColorTable() {
    super(GifElementType.COLOR_TABLE);
  }
}
