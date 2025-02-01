package com.yaetoti.gif.utils;

import com.yaetoti.gif.io.BitOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;

public class LzwUtils {
  public static byte[] encode(@Range(from = 1, to = 11) int minimumCodeSize, byte @NotNull [] bytes) {
    final int clearCode = 1 << minimumCodeSize;
    final int eoiCode = clearCode + 1;
    int nextUnusedCode = eoiCode + 1;
    int codeBits = minimumCodeSize + 1;

    // Send clear code
    BitOutputStream out = new BitOutputStream();
    out.PutInt(clearCode, codeBits);

    // Early end
    if (bytes.length == 0) {
      out.PutInt(eoiCode, codeBits);
      return out.ToByteArray();
    }

    // Init code table
    HashMap<ByteSequence, Integer> codeTable = new HashMap<>();
    for (int i = 0; i < clearCode; ++i) {
      codeTable.put(ByteSequence.Of((byte)i), i);
    }

    // Start reading
    ByteSequence indexBuffer = ByteSequence.Of(bytes[0]);

    for (int nextByteIndex = 1; nextByteIndex < bytes.length; ++nextByteIndex) {
      byte nextByte = bytes[nextByteIndex];
      ByteSequence nextIndexBuffer = indexBuffer.Append(nextByte);

      if (codeTable.containsKey(nextIndexBuffer)) {
        // Sequence already exists
        indexBuffer = nextIndexBuffer;
      } else {
        // Add new code, output code for indexBuffer
        codeTable.put(nextIndexBuffer, nextUnusedCode);
        out.PutInt(codeTable.get(indexBuffer), codeBits);
        indexBuffer = ByteSequence.Of(nextByte);

        // Check for nextCode overflow
        int nextCodeBits = BitUtils.GetBitLength(nextUnusedCode);
        // TODO replace magic literal
        if (nextCodeBits != codeBits) {
          if (nextCodeBits > 12) {
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
}
