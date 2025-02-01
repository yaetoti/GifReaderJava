package com.yaetoti.gif;

import com.yaetoti.gif.io.BitOutputStream;
import com.yaetoti.gif.io.GifInput;
import com.yaetoti.gif.io.LittleEndianDataInput;
import com.yaetoti.gif.utils.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

class LzwMain {
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
        int nextCodeBits = BitUtils.GetBitsRequired(nextUnusedCode);
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

  public static void main(String[] args) {
    BitOutputStream out = new BitOutputStream();
    out.PutInt(4, 3);
    out.PutInt(1, 3);
    out.PutInt(6, 3);
    out.PutInt(6, 3);
    out.PutInt(6, 3);
    byte[] array0 = out.ToByteArray();
    IoUtils.WriteByteArrayBin(System.out, array0);

    byte[] array1 = encode(2, new byte[] { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1 });
    IoUtils.WriteByteArrayBin(System.out, array1);
  }
}

public class Main {
  public static void main(String[] args) throws IOException {
    //WriteByteArray(new byte[] { 0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18 });


    RandomAccessFile file = new RandomAccessFile("image.gif", "r");
    //RandomAccessFile file = new RandomAccessFile("E:\\PremiereExport\\Miraculous-london-Full-HD.gif", "r");
    GifInput input = new GifInput(new LittleEndianDataInput(file));

    int frames = 0;

    try (ScopedTimer timer = new ScopedTimer("GIF reading")) {


      var version = input.ReadHeader();
      System.out.println(version);
      var lsd = input.ReadLogicalScreenDescriptor();
      System.out.println(lsd);
      if (lsd.isGlobalColorTablePresent) {
        var colorTable = input.ReadColorTable(lsd.globalColorTableSize);
        System.out.println(Arrays.toString(colorTable));
      }

      while (true) {
        var label = input.ReadBlockLabel();
        System.out.println(label);

        if (label == GifBlockLabel.TRAILER) {
          break;
        }

        if (label == GifBlockLabel.IMAGE_DESCRIPTOR) {
          var descriptor = input.ReadImageDescriptor();
          System.out.println(descriptor);

          if (descriptor.isLocalColorTablePresent) {
            var colorTable = input.ReadColorTable(descriptor.localColorTableSize);
            System.out.println(Arrays.toString(colorTable));
          }

          var imageData = input.ReadTableBasedImageData();
          System.out.println(imageData);

          // TODO decoding



          // TODO debug
          ++frames;
          if (frames == 5) {
            break;
          }

          continue;
        }

        if (label == GifBlockLabel.EXTENSION) {
          var extensionLabel  = input.ReadExtensionLabel();
          System.out.println(extensionLabel);

          if (extensionLabel == GifExtensionLabel.COMMENT) {
            var extension = input.ReadCommentExtension();
            System.out.println(extension);
            continue;
          }

          if (extensionLabel == GifExtensionLabel.APPLICATION) {
            var extension = input.ReadApplicationExtension();
            System.out.println(extension);
            continue;
          }

          if (extensionLabel == GifExtensionLabel.PLAIN_TEXT) {
            var extension = input.ReadPlainTextExtension();
            System.out.println(extension);
            continue;
          }

          if (extensionLabel == GifExtensionLabel.GRAPHICS_CONTROL) {
            var extension = input.ReadGraphicControlExtension();
            System.out.println(extension);
            continue;
          }
        }
      }
    }

    file.close();

//
//    for (int i = 0; i < result.length; i++) {
//      ////System.out.println("[" + i + "]: " + result[i]);
//    }


    // Read from any stream
    // Read from memory
    // Read from file

    // Read next block
    // Read all blocks
    // Read block #N

    // Index blocks in file


    // Indexing:
    // Frame:
    // - [GraphicsControlExtension]
    // - Image Descriptor
    // - [Local Color Table]
    // - Image Table Data


    // Get how many bytes we consumed on error to rewind


    // Determinism concept
    // - Skip plain text frames because we don't need them

    // Problems:
    // - How do we analyze before writing? - Read into buffer - Write buffer - Use stream-wrapper over buffer


//    RandomAccessFile file = new RandomAccessFile("image.gif", "r");
//    file.close();
//
//    new GifLexer().Parse(new DataInputStream(new FileInputStream("image.gif")));
  }
}
