package com.yaetoti.gif;

import java.io.IOException;

public class InvalidFormatException extends IOException {
  public InvalidFormatException() {
    super();
  }

  public InvalidFormatException(String s) {
    super(s);
  }
}
