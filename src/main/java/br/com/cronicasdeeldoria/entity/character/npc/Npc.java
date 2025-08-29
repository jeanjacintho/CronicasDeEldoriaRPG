package br.com.cronicasdeeldoria.entity.character.npc;

import br.com.cronicasdeeldoria.entity.character.Character;
import java.awt.Graphics2D;
import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.game.GamePanel;

public class Npc extends Character {
    protected boolean isStatic;
    protected String dialog;
    protected String skin;
    private int actionCounter = 0;
    private int actionInterval = 120;
    private int spriteCounter = 0;
    private int spriteNum = 1;
    private boolean isMoving = false;

    public Npc(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize) {
        super(x, y, 1, "down", name, null, 0, 0, 0, 0);
        this.isStatic = isStatic;
        this.dialog = dialog;
        this.skin = skin;
        int hitboxWidth = 32;
        int hitboxHeight = 36;
        int hitboxX = (playerSize - hitboxWidth) / 2;
        int hitboxY = playerSize / 2;
        this.setHitbox(new java.awt.Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight));
    }

    public void walk() {
        if (!isStatic) {
            isMoving = false;
            actionCounter++;
            java.util.Random random = new java.util.Random();
            if (actionCounter >= actionInterval) {
                actionCounter = 0;
                if (random.nextInt(100) < 80) {
                    String[] directions = {"up", "down", "left", "right"};
                    java.util.List<String> dirList = java.util.Arrays.asList(directions);
                    java.util.Collections.shuffle(dirList, random);
                    boolean moved = false;
                    for (String dir : dirList) {
                        setDirection(dir);
                        setCollisionOn(false);
                        if (!isCollisionOn()) {
                            isMoving = true;
                            switch (getDirection()) {
                                case "up":
                                    setWorldY(getWorldY() - getSpeed());
                                    break;
                                case "down":
                                    setWorldY(getWorldY() + getSpeed());
                                    break;
                                case "left":
                                    setWorldX(getWorldX() - getSpeed());
                                    break;
                                case "right":
                                    setWorldX(getWorldX() + getSpeed());
                                    break;
                            }
                            moved = true;
                            break;
                        }
                    }
                    if (!moved) {
                        isMoving = false;
                        spriteNum = 1;
                        spriteCounter = 0;
                        return;
                    }
                } else {
                    actionInterval = 60;
                    return;
                }
                actionInterval = 120 + random.nextInt(120);
            }
            if (isMoving) {
                spriteCounter++;
                if (spriteCounter > 15 - getSpeed()) {
                    spriteNum = (spriteNum == 1) ? 2 : 1;
                    spriteCounter = 0;
                }
            } else {
                spriteNum = 1;
                spriteCounter = 0;
            }
        }
    }

    public void interact() {
        System.out.println("Interagindo com NPC: " + getName() + " - " + getDialog());
    }

    public void update(GamePanel gamePanel, Player player) {
        if (!isStatic) {
            walk(gamePanel, player);
        }
    }

    public void walk(GamePanel gamePanel, Player player) {
        if (!isStatic) {
            actionCounter++;
            java.util.Random random = new java.util.Random();
            
            if (actionCounter >= actionInterval) {
                actionCounter = 0;
                isMoving = false;
                
                if (random.nextInt(100) < 80) {
                    String[] directions = {"up", "down", "left", "right"};
                    java.util.List<String> dirList = java.util.Arrays.asList(directions);
                    java.util.Collections.shuffle(dirList, random);
                    
                    for (String dir : dirList) {
                        if (canMove(dir, gamePanel, player)) {
                            setDirection(dir);
                            break;
                        }
                    }
                }
                
                actionInterval = 120 + random.nextInt(120);
            }
            
            if (canMove(getDirection(), gamePanel, player)) {
                isMoving = true;
                switch (getDirection()) {
                    case "up":
                        setWorldY(getWorldY() - getSpeed());
                        break;
                    case "down":
                        setWorldY(getWorldY() + getSpeed());
                        break;
                    case "left":
                        setWorldX(getWorldX() - getSpeed());
                        break;
                    case "right":
                        setWorldX(getWorldX() + getSpeed());
                        break;
                }
            } else {
                isMoving = false;
            }
            
            if (isMoving) {
                spriteCounter++;
                if (spriteCounter > 15 - getSpeed()) {
                    spriteNum = (spriteNum == 1) ? 2 : 1;
                    spriteCounter = 0;
                }
            } else {
                spriteNum = 1;
                spriteCounter = 0;
            }
        }
    }
    
    private boolean canMove(String direction, GamePanel gamePanel, Player player) {
        int newX = getWorldX();
        int newY = getWorldY();
        switch (direction) {
            case "up":
                newY -= getSpeed();
                break;
            case "down":
                newY += getSpeed();
                break;
            case "left":
                newX -= getSpeed();
                break;
            case "right":
                newX += getSpeed();
                break;
        }
        
        String originalDirection = getDirection();
        setDirection(direction);
        setCollisionOn(false);
        gamePanel.getColisionChecker().checkTile(this);
        boolean tileCollision = isCollisionOn();
        setDirection(originalDirection);
        
        if (tileCollision) {
            return false;
        }
        
        if (this.getHitbox() != null && player.getHitbox() != null) {
            java.awt.Rectangle npcFutureBox = new java.awt.Rectangle(
                newX + getHitbox().x, 
                newY + getHitbox().y, 
                getHitbox().width, 
                getHitbox().height
            );
            java.awt.Rectangle playerBox = new java.awt.Rectangle(
                player.getWorldX() + player.getHitbox().x, 
                player.getWorldY() + player.getHitbox().y, 
                player.getHitbox().width, 
                player.getHitbox().height
            );
            if (npcFutureBox.intersects(playerBox)) {
                return false;
            }
        }
        
        return true;
    }

    public void draw(Graphics2D g, NpcSpriteLoader spriteLoader, int tileSize, Player player, int playerScreenX, int playerScreenY) {
        String direction = getDirection();
        java.util.List<String> sprites = spriteLoader.getSprites(skin, direction);
        int screenX = getWorldX() - player.getWorldX() + playerScreenX;
        int screenY = getWorldY() - player.getWorldY() + playerScreenY;
        int npcSize = player.getPlayerSize();
        int spriteIdx = 0;
        if (isMoving && sprites != null && sprites.size() > 2) {
            spriteIdx = (spriteNum == 1) ? 1 : 2;
        }
        if (sprites != null && !sprites.isEmpty()) {
            try {
                java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(new java.io.File("src/main/resources/sprites/" + sprites.get(spriteIdx)));
                g.drawImage(img, screenX, screenY, npcSize, npcSize, null);
            } catch (java.io.IOException e) {
                g.setColor(java.awt.Color.RED);
                g.fillRect(screenX, screenY, npcSize, npcSize);
            }
        } else {
            g.setColor(java.awt.Color.RED);
            g.fillRect(screenX, screenY, npcSize, npcSize);
        }
    }

    public String getSkin() { return skin; }
    public void setSkin(String skin) { this.skin = skin; }
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    public String getDialog() { return dialog; }
    public void setDialog(String dialog) { this.dialog = dialog; }
}
