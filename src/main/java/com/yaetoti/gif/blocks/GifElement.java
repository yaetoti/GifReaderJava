package com.yaetoti.gif.blocks;

/**
 * Not all parts of GIF image are considered blocks and some does not have corresponding block label, so the base class is called element instead
 */
public abstract class GifElement {
  private final GifElementType m_type;

  public GifElement(GifElementType type) {
    m_type = type;
  }

  public GifElementType GetElementType() {
    return m_type;
  }

  @SuppressWarnings("unchecked")
  public <T extends GifElement> T As() {
    return (T) this;
  }
}
