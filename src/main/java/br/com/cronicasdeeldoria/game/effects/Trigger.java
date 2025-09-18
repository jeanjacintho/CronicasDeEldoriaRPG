package br.com.cronicasdeeldoria.game.effects;

@Deprecated
public class Trigger {
  public final String imagePath;
  public final long durationMs;

  public Trigger(String imagePath, long durationMs) {
    this.imagePath = imagePath;
    this.durationMs = durationMs;
  }
}

