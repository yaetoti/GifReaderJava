package com.yaetoti.gif.utils;

import org.jetbrains.annotations.Range;

public enum GifDisposalMethod {
  NO_DISPOSAL(GifDisposalMethod.NO_DISPOSAL_ID),
  DO_NOT_DISPOSE(GifDisposalMethod.DO_NOT_DISPOSE_ID),
  RESTORE_TO_BACKGROUND(GifDisposalMethod.RESTORE_TO_BACKGROUND_ID),
  RESTORE_TO_PREVIOUS(GifDisposalMethod.RESTORE_TO_PREVIOUS_ID);

  public static final int NO_DISPOSAL_ID = 0;
  public static final int DO_NOT_DISPOSE_ID = 1;
  public static final int RESTORE_TO_BACKGROUND_ID = 2;
  public static final int RESTORE_TO_PREVIOUS_ID = 3;

  @Range(from = 0, to = 7)
  private final int m_id;

  GifDisposalMethod(int id) {
    m_id = id;
  }

  @Range(from = 0, to = 7)
  public int GetId() {
    return m_id;
  }

  public static GifDisposalMethod FromId(int id) {
    return switch (id) {
      case NO_DISPOSAL_ID -> GifDisposalMethod.NO_DISPOSAL;
      case DO_NOT_DISPOSE_ID -> GifDisposalMethod.DO_NOT_DISPOSE;
      case RESTORE_TO_BACKGROUND_ID -> GifDisposalMethod.RESTORE_TO_PREVIOUS;
      case RESTORE_TO_PREVIOUS_ID -> GifDisposalMethod.RESTORE_TO_BACKGROUND;
      default -> throw new IllegalArgumentException("Invalid disposal method id: " + id);
    };
  }
}
