package br.com.cronicasdeeldoria.entity.character.monster;

import br.com.cronicasdeeldoria.entity.character.monster.Monster;

/**
 * Representa um NPC guerreiro, que pode ser contratado ou dispensado.
 */
public class OrcMonster extends Monster {

  /**
   * Cria um novo OrcMonster.
   * @param name Nome do NPC.
   * @param isStatic Indica se o NPC é estático.
   * @param dialog Diálogo do NPC.
   * @param x Posição X.
   * @param y Posição Y.
   * @param skin Skin do NPC.
   * @param playerSize Tamanho do jogador (para hitbox).
   */
  public OrcMonster(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize) {
    super(x, y, 1, "down", name, null, 20, 20, 0, 0, 5, 5, 15, skin,"teste");
  }

}
