package com.yaetoti.paf.blocks;

import com.yaetoti.paf.utils.PafElementType;

public class PafElement {
  private PafElementType m_type;

  protected PafElement(PafElementType type) {
    m_type = type;
  }

  public PafElementType GetElementType() {
    return m_type;
  }

  @SuppressWarnings("unchecked")
  public <T extends PafElement> T As() {
    return (T) this;
  }
}
