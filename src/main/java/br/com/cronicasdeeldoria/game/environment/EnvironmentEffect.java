package br.com.cronicasdeeldoria.game.environment;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;

public class EnvironmentEffect {
    private final String id;
    private final ImageIcon imageIcon;
    private final DrawMode drawMode;
    private final float alpha;

    public enum DrawMode {
        STRETCH,
        CENTER,
        TILE
    }

    public EnvironmentEffect(String id, ImageIcon imageIcon, DrawMode drawMode, float alpha) {
        this.id = id;
        this.imageIcon = imageIcon;
        this.drawMode = drawMode;
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
    }

    public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
        if (imageIcon == null) return;

        var oldAlpha = g2.getComposite();
        if (alpha < 1.0f) {
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        }

        Image img = imageIcon.getImage();
        int imgW = imageIcon.getIconWidth();
        int imgH = imageIcon.getIconHeight();

        switch (drawMode) {
            case CENTER -> {
                int x = (screenWidth - imgW) / 2;
                int y = (screenHeight - imgH) / 2;
                g2.drawImage(img, x, y, null);
            }
            case TILE -> {
                for (int y = 0; y < screenHeight; y += imgH) {
                    for (int x = 0; x < screenWidth; x += imgW) {
                        g2.drawImage(img, x, y, null);
                    }
                }
            }
            case STRETCH -> {
                g2.drawImage(img, 0, 0, screenWidth, screenHeight, null);
            }
        }

        g2.setComposite(oldAlpha);
    }

    public String getId() { return id; }

    public void drawWorldRelative(Graphics2D g2, int worldOriginX, int worldOriginY, int screenWidth, int screenHeight) {
        if (imageIcon == null) return;

        var oldAlpha = g2.getComposite();
        if (alpha < 1.0f) {
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        }

        Image img = imageIcon.getImage();
        int imgW = Math.max(1, imageIcon.getIconWidth());
        int imgH = Math.max(1, imageIcon.getIconHeight());

        switch (drawMode) {
            case TILE -> {
                int offsetX = -Math.floorMod(worldOriginX, imgW);
                int offsetY = -Math.floorMod(worldOriginY, imgH);
                for (int y = offsetY; y < screenHeight; y += imgH) {
                    for (int x = offsetX; x < screenWidth; x += imgW) {
                        g2.drawImage(img, x, y, null);
                    }
                }
            }
            case STRETCH -> {
                int tileW = screenWidth;
                int tileH = screenHeight;
                int offsetX = -Math.floorMod(worldOriginX, tileW);
                int offsetY = -Math.floorMod(worldOriginY, tileH);
                for (int y = offsetY; y < screenHeight; y += tileH) {
                    for (int x = offsetX; x < screenWidth; x += tileW) {
                        g2.drawImage(img, x, y, tileW, tileH, null);
                    }
                }
            }
            case CENTER -> {
                int x = -worldOriginX + (screenWidth - imgW) / 2;
                int y = -worldOriginY + (screenHeight - imgH) / 2;
                g2.drawImage(img, x, y, null);
            }
        }

        g2.setComposite(oldAlpha);
    }
}


