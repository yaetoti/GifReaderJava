package com.yaetoti.bytes;

import java.util.Comparator;

public class ByteSequenceUnsignedComponentComparator implements Comparator<ByteSequence> {
  private final int m_index;
  private final boolean m_reverse;

  public ByteSequenceUnsignedComponentComparator(int index, boolean reverse) {
    m_index = index;
    m_reverse = reverse;
  }

  @Override
  public int compare(ByteSequence lhs, ByteSequence rhs) {
    int lhsColor = lhs.Get(m_index) & 0xFF;
    int rhsColor = rhs.Get(m_index) & 0xFF;
    return (m_reverse ? -1 : 1) * Integer.compare(lhsColor, rhsColor);
  }
}
