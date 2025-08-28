package br.com.cronicasdeeldoria.entity;

import java.awt.image.BufferedImage;
import java.util.UUID;

public class Entity {
  private final UUID id;
  private int x, y;
  private int speed;
  private String direction;
  private String name;
  protected BufferedImage up, down, left, right, up1, up2, down1, down2, left1, left2, right1, right2;

  public Entity(int x, int y, int speed, String direction, String name) {
    this.id = UUID.randomUUID();
    this.x = x;
    this.y = y;
    this.speed = speed;
    this.direction = direction;
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
