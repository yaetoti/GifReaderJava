package com.yaetoti.gif.utils;

import com.yaetoti.gif.io.BitInputStream;
import com.yaetoti.gif.io.BitOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class LzwUtils {
  public static final int MAXIMUM_CODE_SIZE = 12;

  public static byte[] encode(@Range(from = 1, to = MAXIMUM_CODE_SIZE - 1) int minimumCodeSize, byte @NotNull [] data) {
    final int clearCode = 1 << minimumCodeSize;
    final int eoiCode = clearCode + 1;
    int nextUnusedCode = eoiCode + 1;
    int codeBits = minimumCodeSize + 1;

    // Send clear code
    BitOutputStream out = new BitOutputStream();
    out.PutInt(clearCode, codeBits);

    // Early end
    if (data.length == 0) {
      out.PutInt(eoiCode, codeBits);
      return out.ToByteArray();
    }

    // Init code table
    HashMap<ByteSequence, Integer> codeTable = new HashMap<>();
    for (int i = 0; i < clearCode; ++i) {
      codeTable.put(ByteSequence.Of((byte)i), i);
    }

    // Start reading
    ByteSequence indexBuffer = ByteSequence.Of(data[0]);

    for (int nextByteIndex = 1; nextByteIndex < data.length; ++nextByteIndex) {
      byte nextByte = data[nextByteIndex];
      ByteSequence nextIndexBuffer = indexBuffer.Append(nextByte);

      if (codeTable.containsKey(nextIndexBuffer)) {
        // Sequence already exists
        indexBuffer = nextIndexBuffer;
      } else {
        // Add new code, output code for indexBuffer
        codeTable.put(nextIndexBuffer, nextUnusedCode);
        out.PutInt(codeTable.get(indexBuffer), codeBits);
        indexBuffer = ByteSequence.Of(nextByte);

        // Check for nextUnusedCode overflow
        int nextCodeBits = BitUtils.GetBitLength(nextUnusedCode);
        if (nextCodeBits != codeBits) {
          if (nextCodeBits > MAXIMUM_CODE_SIZE) {
            break;
          }

          codeBits = nextCodeBits;
        }

        nextUnusedCode += 1;
      }
    }

    // Send code for index buffer and EOI code
    out.PutInt(codeTable.get(indexBuffer), codeBits);
    out.PutInt(eoiCode, codeBits);
    return out.ToByteArray();
  }

  public static byte[] decode(@Range(from = 1, to = MAXIMUM_CODE_SIZE - 1) int minimumCodeSize, byte @NotNull [] data) throws IOException {
    final short clearCode = (short) (1 << minimumCodeSize);
    final short eoiCode = (short) (clearCode + 1);
    int nextUnusedCode = eoiCode + 1;
    int codeBits = minimumCodeSize + 1;

    // Prepare for decoding
    HashMap<Short, ByteSequence> table = new HashMap<>();
    BitInputStream in = new BitInputStream(data);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    // Decoding
    short currCode = 0;
    short prevCode = Short.MIN_VALUE;

    while (true) {
      currCode = in.GetShort(codeBits);

      // End of information
      if (currCode == eoiCode) {
        break;
      }

      // Reinit table
      if (currCode == clearCode) {
        table.clear();
        for (int i = 0; i < clearCode; ++i) {
          table.put((short)i, ByteSequence.Of((byte)i));
        }

        prevCode = Short.MIN_VALUE;
        continue;
      }

      // First code was read
      if (prevCode == Short.MIN_VALUE) {
        out.write(table.get(currCode).ToByteArray());
        prevCode = currCode;
        continue;
      }

      // After both branches we should increment nextUnusedIndex

      var currSequence = table.get(currCode);
      if (currSequence != null) {
        var nextSequence = table.get(prevCode).Append(currSequence.At(0));
        out.write(currSequence.ToByteArray());

        // Add new table entry
        table.put((short) nextUnusedCode, nextSequence);
      }
      else {
        var prevSequence = table.get(prevCode);
        var nextSequence = prevSequence.Append(prevSequence.At(0));
        out.write(nextSequence.ToByteArray());

        // Add new table entry
        table.put((short) nextUnusedCode, nextSequence);
      }

      // Swap codes
      prevCode = currCode;

      // Check for nextUnusedCode overflow
      int nextCodeBits = BitUtils.GetBitLength(nextUnusedCode + 1);
      if (nextCodeBits != codeBits) {
        if (nextCodeBits > MAXIMUM_CODE_SIZE) {
          break;
        }

        codeBits = nextCodeBits;
      }

      nextUnusedCode += 1;
    }

    return out.toByteArray();
  }
}
