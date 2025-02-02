package com.yaetoti.gif.utils;

public enum GifVersion {
  GIF87a("GIF87a"),
  GIF89a("GIF89a");

  private final String m_signature;

  GifVersion(String signature) {
    m_signature = signature;
  }

  public String GetSignature() {
    return m_signature;
  }

  public static GifVersion FromSignature(String signature) {
    return switch (signature) {
      case "GIF87a" -> GIF87a;
      case "GIF89a" -> GIF89a;
      default -> throw new IllegalArgumentException("Unknown GIF version: " + signature);
    };
  }
}
