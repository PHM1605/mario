package util;

public class Time {
  private static float startTime = System.nanoTime();

  public static float getTime() {
    return (float)((System.nanoTime()-startTime) * 1E-9);
  }
}
