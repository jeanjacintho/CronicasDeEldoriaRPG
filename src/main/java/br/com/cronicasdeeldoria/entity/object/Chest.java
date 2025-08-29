package br.com.cronicasdeeldoria.entity.object;

import java.awt.image.BufferedImage;
import br.com.cronicasdeeldoria.entity.Entity;

/**
 * Representa um baú que pode ser aberto no jogo.
 */
public class Chest extends GameObject {
    private boolean opened = false;

    /**
     * Cria um novo baú.
     * @param worldX Posição X no mundo.
     * @param worldY Posição Y no mundo.
     * @param sprites Sprites do baú.
     */
    public Chest(int worldX, int worldY, BufferedImage[][] sprites) {
        super(worldX, worldY, 2, 2, true, sprites, "chest");
    }

    /**
     * Interage com o baú, abrindo-o se ainda não estiver aberto.
     * @param interactor Entidade que interage com o baú.
     */
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
