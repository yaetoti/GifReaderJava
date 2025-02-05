package com.yaetoti.quantization;

import com.yaetoti.bytes.ByteSequenceUnsignedComponentComparator;
import com.yaetoti.bytes.ByteSequenceUtils;
import com.yaetoti.utils.ArrayUtils;
import com.yaetoti.bytes.ByteSequence;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class MedianCut {
  public static ByteSequence[] Reduce(@NotNull ByteSequence[] colors, int elementSize, int targetCount) {
    if (colors.length <= targetCount) {
      return colors;
    }

    // Sort by sum of color ranges
    PriorityQueue<ByteSequence[]> buckets = new PriorityQueue<>((o1, o2) -> {
      int diff1 = ArrayUtils.Sum(ByteSequenceUtils.GetRangesUnsigned(o1, elementSize));
      int diff2 = ArrayUtils.Sum(ByteSequenceUtils.GetRangesUnsigned(o2, elementSize));
      if (diff1 == diff2) {
        // With the highest length on top
        return -Integer.compare(o1.length, o2.length);
      }

      // With the highest range on top
      return -Integer.compare(diff1, diff2);
    });

    buckets.add(Arrays.copyOf(colors, colors.length));

    // Prepare N buckets
    while (buckets.size() < targetCount) {
      ByteSequence[] largestBucket = buckets.poll();
      assert largestBucket != null;

      List<ByteSequence[]> newBuckets = SplitBucket(largestBucket);
      buckets.addAll(newBuckets);
    }

    ByteSequence[] result = new ByteSequence[targetCount];

    // Calculate average for buckets
    for (int i = 0; i < targetCount; i++) {
      ByteSequence[] bucket = buckets.poll();
      assert bucket != null;

      if (bucket.length == 1) {
        result[i] = bucket[0];
        continue;
      }

      int[] components = new int[elementSize];
      Arrays.fill(components, 0);

      for (ByteSequence color : bucket) {
        for (int index = 0; index < elementSize; index++) {
          components[index] += color.GetUnsigned(index);
        }
      }

      for (int index = 0; index < elementSize; index++) {
        components[index] /= bucket.length;
      }

      result[i] = ByteSequence.Of(components);
    }

    return result;
  }

  private static List<ByteSequence[]> SplitBucket(@NotNull ByteSequence[] bucket) {
    assert bucket.length > 1;

    int index = ByteSequenceUtils.GetIndexWithHighestRangeUnsigned(bucket, 3);
    Arrays.sort(bucket, new ByteSequenceUnsignedComponentComparator(index, true));
    int middle = bucket.length / 2;
    return List.of(
      Arrays.copyOfRange(bucket, 0, middle),
      Arrays.copyOfRange(bucket, middle, bucket.length)
    );
  }
}
