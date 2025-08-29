package br.com.cronicasdeeldoria.entity.object;

import java.awt.image.BufferedImage;
import br.com.cronicasdeeldoria.entity.Entity;

public class Key extends GameObject {
    public Key(int worldX, int worldY, BufferedImage[][] sprites) {
        super(worldX, worldY, 1, 1, false, sprites, "key");
    }

    @Override
    public void interact(Entity interactor) {
        System.out.println("Pegou a chave");
        setActive(false);
    }
}
