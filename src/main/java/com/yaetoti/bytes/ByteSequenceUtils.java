package com.yaetoti.bytes;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ByteSequenceUtils {
  private ByteSequenceUtils() {
    throw new AssertionError();
  }

  public static int[] GetRangesUnsigned(@NotNull ByteSequence[] sequences, int elementSize) {
    if (sequences.length <= 1) {
      int[] ranges = new int[elementSize];
      Arrays.fill(ranges, 0);
      return ranges;
    }

    int[] lowest = new int[elementSize];
    int[] highest = new int[elementSize];
    Arrays.fill(lowest, Integer.MAX_VALUE);
    Arrays.fill(highest, Integer.MIN_VALUE);

    for (ByteSequence s : sequences) {
      assert s.Length() == elementSize;

      for (int index = 0; index < elementSize; index++) {
        int value = s.GetUnsignedByte(index);

        if (value < lowest[index]) {
          lowest[index] = value;
        }

        if (value > highest[index]) {
          highest[index] = value;
        }
      }
    }

    int[] ranges = new int[elementSize];
    for (int index = 0; index < elementSize; index++) {
      ranges[index] = highest[index] - lowest[index];
    }

    return ranges;
  }

  public static int GetIndexWithHighestRangeUnsigned(@NotNull ByteSequence[] sequences, int elementSize) {
    int[] ranges = GetRangesUnsigned(sequences, elementSize);
    int index = 0;
    int range = ranges[index];

    for (int i = 1; i < elementSize; i++) {
      int nextRange = ranges[i];
      if (range < nextRange) {
        range = nextRange;
        index = i;
      }
    }

    return index;
  }

  public static int DistanceSquareUnsigned(ByteSequence c1, ByteSequence c2) {
    assert c1.Length() == c2.Length();

    int result = 0;
    for (int i = 0; i < c1.Length(); i++) {
      int difference = c1.GetUnsignedByte(i) - c2.GetUnsignedByte(i);
      result += difference * difference;
    }

    return result;
  }

  public static int FindClosestSequenceUnsigned(ByteSequence target, ByteSequence[] dict) {
    int bestIndex = 0;
    int bestDistance = Integer.MAX_VALUE;

    for (int i = 0; i < dict.length; i++) {
      ByteSequence color = dict[i];
      int distance = DistanceSquareUnsigned(target, color);
      if (distance < bestDistance) {
        bestDistance = distance;
        bestIndex = i;
      }
    }

    return bestIndex;
  }

  public static Map<Integer, Integer> MapClosestIndices(ByteSequence[] dict1, ByteSequence[] dict2) {
    HashMap<Integer, Integer> colorMap = new HashMap<>();
    for (int i = 0; i < dict1.length; i++) {
      ByteSequence color = dict1[i];
      int newIndex = FindClosestSequenceUnsigned(color, dict2);
      colorMap.put(i, newIndex);
    }

    return colorMap;
  }
}
