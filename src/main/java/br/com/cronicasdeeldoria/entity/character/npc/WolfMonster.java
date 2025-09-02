package br.com.cronicasdeeldoria.entity.character.npc;

public class WolfMonster extends Npc {

  /**
   * Cria um novo OrcMonster.
   * @param name Nome do NPC.
   * @param isStatic Indica se o NPC é estático.
   * @param dialog Diálogo do NPC.
   * @param x Posição X.
   * @param y Posição Y.
   * @param skin Skin do NPC.
   * @param playerSize Tamanho do jogador (para hitbox).
   * @param interactive Indica se o NPC é interativo.
   * @param autoInteraction Indica se a interação é automática.
   */
  public WolfMonster(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize, boolean interactive, boolean autoInteraction) {
    super(name, isStatic, dialog, x, y, skin, playerSize, interactive, autoInteraction);
  }
}
