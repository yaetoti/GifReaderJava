package com.yaetoti.gif.blocks;

public final class GifCommentExtension extends GifElement {
  public String commentData;

  public GifCommentExtension() {
    super(GifElementType.COMMENT_EXTENSION);
  }

  @Override
  public String toString() {
    return "GifCommentExtension{" +
      "commentData='" + commentData + '\'' +
      '}';
  }
}
