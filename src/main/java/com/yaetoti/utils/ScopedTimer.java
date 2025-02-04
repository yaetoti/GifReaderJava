package com.yaetoti.utils;

import java.util.concurrent.TimeUnit;

public class ScopedTimer implements AutoCloseable {
  private final long startTime;
  private final String taskName;

  public ScopedTimer(String taskName) {
    this.taskName = taskName;
    this.startTime = System.nanoTime();
  }

  @Override
  public void close() {
    long elapsedTime = System.nanoTime() - startTime;
    long millis = TimeUnit.NANOSECONDS.toMillis(elapsedTime);
    System. out.println("Task '" + taskName + "' completed in " + millis + " ms");
  }
}
