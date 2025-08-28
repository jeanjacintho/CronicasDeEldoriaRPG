package br.com.cronicasdeeldoria.entity.character.player;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.KeyHandler;
import br.com.cronicasdeeldoria.entity.character.races.Race;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Player extends Character {
  private int luck;
  GamePanel gamePanel;
  KeyHandler keyHandler;
  private int spriteCounter = 0;
  private int spriteNum = 1;
  private boolean isMoving = false;

  public Player(GamePanel gamePanel, KeyHandler keyHandler, Race race, int x, int y, int speed, String direction, String name, int health, int mana, int strength, int agility, int luck) {
    super(x, y, speed, direction, name, race, health, mana, strength, agility);
    this.luck = luck;
    this.gamePanel = gamePanel;
    this.keyHandler = keyHandler;
    this.getPlayerImage();
  }

  public void getPlayerImage() {
    try {
      String classFolder = getRace().getRaceName().toLowerCase();
      this.up = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_back.png"));
      this.down = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_front.png"));
      this.left = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_left.png"));
      this.right = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_right.png"));
      this.up1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_back_walk1.png"));
      this.up2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_back_walk2.png"));
      this.down1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_front_walk1.png"));
      this.down2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_front_walk2.png"));
      this.left1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_left_walk1.png"));
      this.left2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_left_walk2.png"));
      this.right1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_right_walk1.png"));
      this.right2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_right_walk2.png"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void update() {
    if(keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed) {
      isMoving = true;
      if(keyHandler.upPressed) {
        setDirection("up");
        setY(getY() - getSpeed());
      } else if(keyHandler.downPressed) {
        setDirection("down");
        setY(getY() + getSpeed());
      } else if(keyHandler.leftPressed) {
        setDirection("left");
        setX(getX() - getSpeed());
      } else if(keyHandler.rightPressed) {
        setDirection("right");
        setX(getX() + getSpeed());
      }

      spriteCounter++;

      if(spriteCounter > 15 - getSpeed()) {
        spriteNum = (spriteNum == 1) ? 2 : 1;
        spriteCounter = 0;
      }
    } else {
      isMoving = false;
      spriteNum = 1;
      spriteCounter = 0;
    }
  }

  public void draw(Graphics2D graphics2d) {
    BufferedImage image = null;

    switch(getDirection()) {
      case "up":
        if (isMoving) {
            image = (spriteNum == 1) ? up1 : up2;
        } else {
            image = up;
        }
        break;
      case "down":
        if (isMoving) {
            image = (spriteNum == 1) ? down1 : down2;
        } else {
            image = down;
        }
        break;
      case "left":
        if (isMoving) {
            image = (spriteNum == 1) ? left1 : left2;
        } else {
            image = left;
        }
        break;
      case "right":
        if (isMoving) {
            image = (spriteNum == 1) ? right1 : right2;
        } else {
            image = right;
        }
        break;
    }

    graphics2d.drawImage(image, getX(), getY(), gamePanel.getPlayerSize(), gamePanel.getPlayerSize(), null);
  }
 
  public int getLuck() {
    return luck;
  }

  public void setLuck(int luck) {
    this.luck = luck;
  }
}
