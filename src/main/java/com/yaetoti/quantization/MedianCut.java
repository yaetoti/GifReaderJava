package com.yaetoti.quantization;

import com.yaetoti.utils.ArrayUtils;
import com.yaetoti.utils.ByteSequence;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class MedianCut {
  public static int[] GetRanges(Collection<ByteSequence> sequences, int indexCount) {
    int[] lowest = new int[indexCount];
    int[] highest = new int[indexCount];
    Arrays.fill(lowest, Integer.MAX_VALUE);
    Arrays.fill(highest, Integer.MIN_VALUE);

    for (ByteSequence s : sequences) {
      for (int index = 0; index < indexCount; index++) {
        int value = s.AtUnsigned(index);

        if (value < lowest[index]) {
          lowest[index] = value;
        }
        else if (value > highest[index]) {
          highest[index] = value;
        }
      }
    }

    int[] ranges = new int[indexCount];
    for (int index = 0; index < indexCount; index++) {
      ranges[index] = highest[index] - lowest[index];
    }

    return ranges;
  }

  public static int GetIndexWithHighestDifference(Collection<ByteSequence> sequences, int indexCount) {
    int[] ranges = GetRanges(sequences, indexCount);
    int index = 0;
    int range = ranges[index];

    for (int i = 1; i < indexCount; i++) {
      int nextRange = ranges[i];
      if (range < nextRange) {
        range = nextRange;
        index = i;
      }
    }

    return index;
  }

  public static ArrayList<ByteSequence> Cut(@NotNull ArrayList<ByteSequence> colors, int targetCount) {
    if (colors.size() <= targetCount) {
      return colors;
    }

    // Sort by sum of color ranges
    PriorityQueue<List<ByteSequence>> buckets = new PriorityQueue<>((o1, o2) -> {
      int diff1 = ArrayUtils.Sum(GetRanges(o1, 3));
      int diff2 = ArrayUtils.Sum(GetRanges(o2, 3));
      if (diff1 == diff2) {
        return Integer.compare(o1.size(), o2.size());
      }

      return Integer.compare(diff1, diff2);
    });
    buckets.add(new ArrayList<>(colors));

    // Prepare N buckets
    while (buckets.size() < targetCount) {
      List<ByteSequence> largestBucket = buckets.poll();
      assert largestBucket != null;

      var newBuckets = SplitBucket(largestBucket);
      buckets.addAll(newBuckets);
    }

    // Calculate average for buckets
    ArrayList<ByteSequence> result = new ArrayList<>();
    for (var bucket : buckets) {
      if (bucket.size() == 1) {
        result.add(bucket.getFirst());
        continue;
      }

      int[] components = new int[3];
      Arrays.fill(components, 0);

      for (ByteSequence color : bucket) {
        for (int index = 0; index < 3; index++) {
          components[index] += color.AtUnsigned(index);
        }
      }

      for (int index = 0; index < 3; index++) {
        components[index] /= bucket.size();
      }

      result.add(ByteSequence.Of(components));
    }

    return result;
  }

  static List<List<ByteSequence>> SplitBucket(@NotNull List<ByteSequence> bucket) {
    int index = GetIndexWithHighestDifference(bucket, 3);
    bucket.sort(new ColorComponentComparator(index, true));
    int middle = bucket.size() / 2;
    return Arrays.asList(bucket.subList(0, middle), bucket.subList(middle, bucket.size()));
  }
}
