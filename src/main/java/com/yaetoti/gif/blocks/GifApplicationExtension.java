package com.yaetoti.gif.blocks;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class GifApplicationExtension extends GifElement {
  public String applicationIdentifier;
  public byte[] authenticationCode;
  public byte @Nullable [] applicationData;

  public GifApplicationExtension() {
    super(GifElementType.APPLICATION_EXTENSION);
  }

  @Override
  public String toString() {
    return "GifApplicationExtension{" +
      "applicationIdentifier='" + applicationIdentifier + '\'' +
      ", authenticationCode=" + Arrays.toString(authenticationCode) +
      ", applicationData=" + Arrays.toString(applicationData) +
      '}';
  }
}
