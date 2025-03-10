package com.yaetoti.gif.utils;

import com.yaetoti.io.BitInputStreamBE;
import com.yaetoti.io.BitOutputStreamBE;
import com.yaetoti.utils.BitUtils;
import com.yaetoti.bytes.ByteSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class GifLzwUtils {
  private GifLzwUtils() {
    assert false : "Utility class.";
  }

  public static final int MAXIMUM_CODE_SIZE = 12;

  public static byte[] Encode(@Range(from = 1, to = MAXIMUM_CODE_SIZE - 1) int minimumCodeSize, byte @NotNull [] data) throws GifLzwCodeOutOfBoundsException {
    final short clearCode = (short) (1 << minimumCodeSize);
    final short eoiCode = (short) (clearCode + 1);
    short nextUnusedCode = (short) (eoiCode + 1);
    int codeBits = minimumCodeSize + 1;

    // Send clear code
    BitOutputStreamBE out = new BitOutputStreamBE();
    out.PutShort(clearCode, codeBits);

    // Early end
    if (data.length == 0) {
      out.PutShort(eoiCode, codeBits);
      return out.ToByteArray();
    }

    // Init code table
    HashMap<ByteSequence, Short> codeTable = new HashMap<>();
    for (short index = 0; index < clearCode; ++index) {
      codeTable.put(ByteSequence.Of(index), index);
    }

    // Start reading
    ByteSequence indexBuffer = ByteSequence.Of(data[0]);

    for (int nextByteIndex = 1; nextByteIndex < data.length; ++nextByteIndex) {
      // break: out.m_bytes == 6225, nextByteIndex == 11889
      byte nextByte = data[nextByteIndex];
      ByteSequence nextIndexBuffer = indexBuffer.Append(nextByte);

      if (codeTable.containsKey(nextIndexBuffer)) {
        // Sequence already exists
        indexBuffer = nextIndexBuffer;
        continue;
      }

      // Add new code, output code for indexBuffer
      codeTable.put(nextIndexBuffer, nextUnusedCode);
      out.PutShort(codeTable.get(indexBuffer), codeBits);
      indexBuffer = ByteSequence.Of(nextByte);

      // Check for nextUnusedCode overflow
      int nextCodeBits = BitUtils.GetBitLength(nextUnusedCode);
      nextUnusedCode += 1;

      if (nextCodeBits != codeBits) {
        if (BitUtils.GetBitLength(nextUnusedCode) > MAXIMUM_CODE_SIZE) {
          // If overflow - send clear code
          out.PutShort(clearCode, codeBits);

          // Reinit table
          codeBits = minimumCodeSize + 1;
          nextUnusedCode = (short) (eoiCode + 1);

          codeTable.clear();
          for (short index = 0; index < clearCode; ++index) {
            codeTable.put(ByteSequence.Of(index), index);
          }

          continue;
        }

        codeBits = nextCodeBits;
      }
    }

    // Send code for index buffer and EOI code
    out.PutShort(codeTable.get(indexBuffer), codeBits);
    out.PutShort(eoiCode, codeBits);
    return out.ToByteArray();
  }

  public static byte[] Decode(@Range(from = 1, to = MAXIMUM_CODE_SIZE - 1) int minimumCodeSize, byte @NotNull [] data) throws IOException, GifLzwCodeOutOfBoundsException, GifLzwMalformedDataException {
    final short clearCode = (short) (1 << minimumCodeSize);
    final short eoiCode = (short) (clearCode + 1);
    short nextUnusedCode = (short) (eoiCode + 1);
    int codeBits = minimumCodeSize + 1;

    // Prepare for decoding
    HashMap<Short, ByteSequence> table = new HashMap<>();
    BitInputStreamBE in = new BitInputStreamBE(data);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    // Decoding
    short currCode = 0;
    short prevCode = Short.MIN_VALUE;

    while (true) {
      // Some encoders really like to omit last 0 byte (Premiere)
      try {
        currCode = in.GetShort(codeBits);
      } catch (IndexOutOfBoundsException e) {
        break;
      }

      // End of information
      if (currCode == eoiCode) {
        break;
      }

      // Reinit table
      if (currCode == clearCode) {
        table.clear();
        for (int index = 0; index < clearCode; ++index) {
          table.put((short)index, ByteSequence.Of((byte)index));
        }

        prevCode = Short.MIN_VALUE;
        codeBits = minimumCodeSize + 1;
        nextUnusedCode = (short) (eoiCode + 1);
        continue;
      }

      // First code was read
      if (prevCode == Short.MIN_VALUE) {
        var currSequence = table.get(currCode);
        if (currSequence == null) {
          throw new GifLzwMalformedDataException("No sequence found for code " + currCode);
        }

        out.write(currSequence.ToByteArray());
        prevCode = currCode;
        continue;
      }

      // After both branches we should increment nextUnusedIndex

      var currSequence = table.get(currCode);
      if (currSequence != null) {
        var prevSequence = table.get(prevCode);
        if (prevSequence == null) {
          throw new GifLzwMalformedDataException("No sequence found for code " + currCode);
        }

        var nextSequence = prevSequence.Append(currSequence.GetByte(0));
        out.write(currSequence.ToByteArray());

        // Add new table entry
        table.put(nextUnusedCode, nextSequence);
      }
      else {
        var prevSequence = table.get(prevCode);
        if (prevSequence == null) {
          throw new GifLzwMalformedDataException("No sequence found for prev code " + prevCode);
        }

        var nextSequence = prevSequence.Append(prevSequence.GetByte(0));
        out.write(nextSequence.ToByteArray());

        // Add new table entry
        table.put(nextUnusedCode, nextSequence);
      }

      // Check for added code overflow
      if (nextUnusedCode < (1 << MAXIMUM_CODE_SIZE)) {
        ++nextUnusedCode;
        if (nextUnusedCode == (1 << codeBits) && nextUnusedCode < (1 << MAXIMUM_CODE_SIZE)) {
          ++codeBits;
        }
      }

      // Swap codes
      prevCode = currCode;
    }

    return out.toByteArray();
  }
}
