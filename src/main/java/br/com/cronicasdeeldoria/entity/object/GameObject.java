package br.com.cronicasdeeldoria.entity.object;

import br.com.cronicasdeeldoria.entity.Entity;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class GameObject extends Entity {
    private int width;
    private int height;
    protected boolean collision;
    protected BufferedImage[][] sprites;
    protected boolean active = true;

    public GameObject(int worldX, int worldY, int width, int height, boolean collision, BufferedImage[][] sprites, String objectId) {
        super(worldX, worldY, 0, "none", objectId);
        this.width = width;
        this.height = height;
        this.collision = collision;
        this.sprites = sprites;
        this.setHitbox(new Rectangle(0, 0, width, height));
        this.setCollisionOn(collision);
    }

    public abstract void interact(Entity interactor);

    public void draw(Graphics2D g2, int offsetX, int offsetY, int tileSize) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                g2.drawImage(sprites[row][col],
                    (getWorldX() + col * tileSize) - offsetX,
                    (getWorldY() + row * tileSize) - offsetY,
                    tileSize, tileSize, null);
            }
        }
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}
