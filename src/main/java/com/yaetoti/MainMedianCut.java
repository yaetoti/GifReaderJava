package com.yaetoti;
import com.yaetoti.quantization.MedianCut;
import com.yaetoti.bytes.ByteSequence;
import java.util.*;

public class MainMedianCut {
  public static void main(String[] args) {
    var set = Set.of(
      ByteSequence.Of(255, 255, 255),
      ByteSequence.Of(0, 0, 0),
      ByteSequence.Of(255, 0, 0),
      ByteSequence.Of(0, 255, 0),
      ByteSequence.Of(0, 0, 255),
      ByteSequence.Of(0, 255, 255),
      ByteSequence.Of(255, 0, 255),
      ByteSequence.Of(255, 255, 0)
    );

    System.out.println("Before quantization:");
    for (ByteSequence color : set) {
      System.out.println(color);
    }

    var result = MedianCut.Reduce(set.toArray(new ByteSequence[0]), 3, 256);
    System.out.println("After quantization:");
    for (ByteSequence color : result) {
      System.out.println(color);
    }
  }
}
