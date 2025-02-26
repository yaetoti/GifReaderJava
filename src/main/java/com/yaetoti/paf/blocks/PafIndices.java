package com.yaetoti.paf.blocks;

import com.yaetoti.paf.utils.PafElementType;
import com.yaetoti.paf.utils.PafIndex;

import java.util.ArrayList;

public final class PafIndices extends PafElement {
  public ArrayList<PafIndex> indices = new ArrayList<>();

  public PafIndices() {
    super(PafElementType.INDICES);
  }
}
