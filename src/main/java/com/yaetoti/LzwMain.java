package com.yaetoti;

import com.yaetoti.gif.utils.GifLzwUtils;

import java.io.IOException;
import java.util.Arrays;

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


//    byte[] array0 = new byte[] { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1 };
//    System.out.println("Original array:");
//    IoUtils.WriteByteArrayHex(System.out, array0);
//
//    byte[] array1 = GifLzwUtils.encode(2, array0);
//    System.out.println("Encoded array:");
//    IoUtils.WriteByteArrayBin(System.out, array1);
//
//    byte[] array2 = GifLzwUtils.decode(2, array1);
//    System.out.println("Decoded array:");
//    IoUtils.WriteByteArrayHex(System.out, array2);


    // Long sequence
    //for (int i = 0; i <= 255; ++i) {
      byte[] array0 = new byte[16384];
      int value = 0;
      for (int i = 0; i < array0.length; ++i) {
        array0[i] = (byte) value;

        if (++value >= 10) {
          value = 0;
        }
      }
      //Arrays.fill(array0, (byte)i);

      //System.out.println("Original array:");
      //IoUtils.WriteByteArrayHex(System.out, array0);

      byte[] array1 = GifLzwUtils.Encode(8, array0);
      //System.out.println("Encoded array:");
      //IoUtils.WriteByteArrayHex(System.out, array1);

      byte[] array2 = GifLzwUtils.Decode(8, array1);
      //System.out.println("Decoded array:");
      //IoUtils.WriteByteArrayHex(System.out, array2);

      assert Arrays.equals(array0, array2);
    //}



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