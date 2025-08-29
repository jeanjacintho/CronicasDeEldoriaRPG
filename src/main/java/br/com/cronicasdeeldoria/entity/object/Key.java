package br.com.cronicasdeeldoria.entity.object;

import java.awt.image.BufferedImage;
import br.com.cronicasdeeldoria.entity.Entity;

public class Key extends GameObject {
    public Key(int worldX, int worldY, BufferedImage[][] sprites) {
        super(worldX, worldY, 1, 1, false, sprites, "key");
    }

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
