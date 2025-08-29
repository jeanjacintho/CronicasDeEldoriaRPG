package br.com.cronicasdeeldoria.entity.object;

import java.awt.image.BufferedImage;
import br.com.cronicasdeeldoria.entity.Entity;

public class Chest extends GameObject {
    private boolean opened = false;

    public Chest(int worldX, int worldY, BufferedImage[][] sprites) {
        super(worldX, worldY, 2, 2, true, sprites, "chest");
    }

    @Override
    public void interact(Entity interactor) {
        if (!opened) {
            opened = true;
            System.out.println("Abrindo baú");
        }
    }

    public boolean isOpened() {
        return opened;
    }
}
