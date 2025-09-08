package br.com.cronicasdeeldoria.game;

import java.awt.*;

public class FloatingText {
  public String text;
  public int x, y;
  public Color color;
  public long startTime;
  public long duration = 1000; // 1 segundo

  public FloatingText(String text, int x, int y, Color color) {
    this.text = text;
    this.x = x;
    this.y = y;
    this.color = color;
    this.startTime = System.currentTimeMillis();
  }

  public boolean isExpired() {
    return System.currentTimeMillis() - startTime > duration;
  }

  public String getText() {
    return text;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public Color getColor() {
    return color;
  }

  public void update() {
  }
}
