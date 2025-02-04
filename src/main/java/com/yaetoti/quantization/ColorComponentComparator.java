package com.yaetoti.quantization;

import com.yaetoti.utils.ByteSequence;

import java.util.Comparator;

public class ColorComponentComparator implements Comparator<ByteSequence> {
  private final int m_index;
  private final boolean m_reverse;

  public ColorComponentComparator(int index, boolean reverse) {
    m_index = index;
    m_reverse = reverse;
  }

  @Override
  public int compare(ByteSequence lhs, ByteSequence rhs) {
    int lhsColor = lhs.At(m_index) & 0xFF;
    int rhsColor = rhs.At(m_index) & 0xFF;
    return (m_reverse ? -1 : 1) * Integer.compare(lhsColor, rhsColor);
  }
}
