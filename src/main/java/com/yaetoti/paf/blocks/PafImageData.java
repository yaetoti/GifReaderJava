package com.yaetoti.paf.blocks;

import com.yaetoti.paf.utils.PafElementType;

public final class PafImageData extends PafElement {
  /// Deflate-encoded image data
  public byte[] data = null;

  public PafImageData() {
    super(PafElementType.IMAGE_DATA);
  }
}
