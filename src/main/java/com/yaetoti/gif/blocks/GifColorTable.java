package com.yaetoti.gif.blocks;

public final class GifColorTable extends GifElement {
  public byte[] table;
  public Type type;

  public GifColorTable() {
    super(GifElementType.COLOR_TABLE);
  }

  public enum Type {
    GLOBAL,
    LOCAL
  }
}
