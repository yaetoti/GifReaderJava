package com.yaetoti.gif.utils;

import org.jetbrains.annotations.Range;

public enum DisposalMethod {
  NO_DISPOSAL(DisposalMethod.NO_DISPOSAL_ID),
  DO_NOT_DISPOSE(DisposalMethod.DO_NOT_DISPOSE_ID),
  RESTORE_TO_BACKGROUND(DisposalMethod.RESTORE_TO_BACKGROUND_ID),
  RESTORE_TO_PREVIOUS(DisposalMethod.RESTORE_TO_PREVIOUS_ID);

  public static final int NO_DISPOSAL_ID = 0;
  public static final int DO_NOT_DISPOSE_ID = 1;
  public static final int RESTORE_TO_BACKGROUND_ID = 2;
  public static final int RESTORE_TO_PREVIOUS_ID = 3;

  @Range(from = 0, to = 7)
  private final int m_id;

  DisposalMethod(int id) {
    m_id = id;
  }

  @Range(from = 0, to = 7)
  public int GetId() {
    return m_id;
  }

  public static DisposalMethod FromId(int id) {
    return switch (id) {
      case NO_DISPOSAL_ID -> DisposalMethod.NO_DISPOSAL;
      case DO_NOT_DISPOSE_ID -> DisposalMethod.DO_NOT_DISPOSE;
      case RESTORE_TO_BACKGROUND_ID -> DisposalMethod.RESTORE_TO_PREVIOUS;
      case RESTORE_TO_PREVIOUS_ID -> DisposalMethod.RESTORE_TO_BACKGROUND;
      default -> throw new IllegalArgumentException("Invalid disposal method id: " + id);
    };
  }
}
