package br.com.cronicasdeeldoria.entity.object;

import java.awt.image.BufferedImage;
import br.com.cronicasdeeldoria.entity.Entity;

/**
 * Representa uma chave coletável no jogo.
 */
public class Key extends GameObject {
    /**
     * Cria uma nova chave.
     * @param worldX Posição X no mundo.
     * @param worldY Posição Y no mundo.
     * @param sprites Sprites da chave.
     */
    public Key(int worldX, int worldY, BufferedImage[][] sprites) {
        super(worldX, worldY, 1, 1, false, sprites, "key");
    }

    /**
     * Interage com a chave, tornando-a inativa e notificando o jogador.
     * @param interactor Entidade que interage com a chave.
     */
    @Override
    public void interact(Entity interactor) {
        setActive(false);
        if (interactor instanceof br.com.cronicasdeeldoria.entity.character.player.Player) {
            br.com.cronicasdeeldoria.entity.character.player.Player player =
                (br.com.cronicasdeeldoria.entity.character.player.Player) interactor;
            player.getGamePanel().ui.addMessage(
                "Você pegou uma chave!",
                sprites[0][0],
                3000
            );
        }
    }
}
