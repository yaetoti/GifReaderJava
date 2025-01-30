package com.yaetoti.gif.blocks;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class GifApplicationExtension {
  public String applicationIdentifier;
  public byte[] authenticationCode;
  public byte @Nullable [] applicationData;

  @Override
  public String toString() {
    return "GifApplicationExtension{" +
      "applicationIdentifier='" + applicationIdentifier + '\'' +
      ", authenticationCode=" + Arrays.toString(authenticationCode) +
      ", applicationData=" + Arrays.toString(applicationData) +
      '}';
  }
}
