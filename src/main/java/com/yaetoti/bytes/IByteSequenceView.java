package com.yaetoti.bytes;

public interface IByteSequenceView {
  byte GetByte(int index);
  int GetUnsignedByte(int index);
  int Length();
  byte[] ToByteArray();
}
