package com.yaetoti.gif;

import com.yaetoti.gif.io.BitInputStream;
import com.yaetoti.gif.io.BitOutputStream;
import com.yaetoti.gif.utils.IoUtils;
import com.yaetoti.gif.utils.LzwUtils;

public class LzwMain {
  public static void main(String[] args) {
//    BitOutputStream out = new BitOutputStream();
//    out.PutInt(4, 3);
//    out.PutInt(1, 3);
//    out.PutInt(6, 3);
//    out.PutInt(6, 3);
//    out.PutInt(6, 3);
//    byte[] array0 = out.ToByteArray();
//    IoUtils.WriteByteArrayBin(System.out, array0);
//
//    byte[] array1 = LzwUtils.encode(2, new byte[] { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1 });
//    IoUtils.WriteByteArrayBin(System.out, array1);

    BitInputStream in = new BitInputStream(new byte[] {
      (byte) 0b11100011,
      (byte) 0b10001110
    });
    System.out.println(in.GetRemainingBits());

    BitOutputStream out = new BitOutputStream();
    out.PutLong(in.GetLong(12));
    out.PutByte(in.GetByte(4));

    IoUtils.WriteByteArrayBin(System.out, out.ToByteArray());
  }
}