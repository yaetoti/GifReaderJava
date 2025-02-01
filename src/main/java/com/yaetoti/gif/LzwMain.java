package com.yaetoti.gif;

import com.yaetoti.gif.io.BitInputStreamBE;
import com.yaetoti.gif.io.BitOutputStreamBE;
import com.yaetoti.gif.utils.GifLzwUtils;
import com.yaetoti.gif.utils.IoUtils;

import java.io.IOException;

public class LzwMain {
  public static void main(String[] args) throws IOException {
//    BitOutputStreamBE out = new BitOutputStreamBE();
//    out.PutInt(4, 3);
//    out.PutInt(1, 3);
//    out.PutInt(6, 3);
//    out.PutInt(6, 3);
//    out.PutInt(6, 3);
//    out.PutInt(7, 3);
//    byte[] array0 = out.ToByteArray();
//    IoUtils.WriteByteArrayBin(System.out, array0);
//
//    BitInputStreamBE in = new BitInputStreamBE(array0, 0, 18);
//    System.out.println(in.GetShort(3));
//    System.out.println(in.GetShort(3));
//    System.out.println(in.GetShort(3));
//    System.out.println(in.GetShort(3));
//    System.out.println(in.GetShort(3));
//    System.out.println(in.GetShort(3));


    byte[] array0 = new byte[] { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1 };
    System.out.println("Original array:");
    IoUtils.WriteByteArrayHex(System.out, array0);

    byte[] array1 = GifLzwUtils.encode(2, array0);
    System.out.println("Encoded array:");
    IoUtils.WriteByteArrayBin(System.out, array1);

    byte[] array2 = GifLzwUtils.decode(2, array1);
    System.out.println("Decoded array:");
    IoUtils.WriteByteArrayHex(System.out, array2);

//    BitInputStream in = new BitInputStream(new byte[] {
//      (byte) 0b11100011,
//      (byte) 0b10001110
//    });
//    System.out.println(in.GetRemainingBits());
//
//    BitOutputStream out = new BitOutputStream();
//    out.PutLong(in.GetLong(12));
//    out.PutByte(in.GetByte(4));
//
//    IoUtils.WriteByteArrayBin(System.out, out.ToByteArray());
  }
}