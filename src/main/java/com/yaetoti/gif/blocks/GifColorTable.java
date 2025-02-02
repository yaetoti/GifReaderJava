package com.yaetoti.gif.blocks;

public final class GifColorTable extends GifElement {
  public byte[] table;

  public GifColorTable() {
    super(GifElementType.COLOR_TABLE);
  }
}
