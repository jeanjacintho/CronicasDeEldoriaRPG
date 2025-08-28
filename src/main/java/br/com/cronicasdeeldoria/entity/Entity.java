package br.com.cronicasdeeldoria.entity;

import java.awt.image.BufferedImage;
import java.util.UUID;

public class Entity {
  private final UUID id;
  private int worldX, worldY;
  private int speed;
  private String direction;
  private String name;
  protected BufferedImage up, down, left, right, up1, up2, down1, down2, left1, left2, right1, right2;

  public Entity(int x, int y, int speed, String direction, String name) {
    this.id = UUID.randomUUID();
    this.worldX = x;
    this.worldY = y;
    this.speed = speed;
    this.direction = direction;
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public int getWorldX() {
    return worldX;
  }

  public void setWorldX(int x) {
    this.worldX = x;
  }

  public int getWorldY() {
    return worldY;
  }

  public void setWorldY(int y) {
    this.worldY = y;
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
