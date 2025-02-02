package com.yaetoti.gif.io;

import java.io.IOException;

public class GifInvalidFormatException extends IOException {
  public GifInvalidFormatException() {
    super();
  }

  public GifInvalidFormatException(String s) {
    super(s);
  }
}
