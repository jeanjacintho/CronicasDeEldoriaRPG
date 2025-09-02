package br.com.cronicasdeeldoria.entity.character.npc;

public class SkeletonMonster extends Npc {
  private int xpReward = 30;

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

  public SkeletonMonster(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize, boolean interactive, boolean autoInteraction) {
    super(name, isStatic, dialog, x, y, skin, playerSize, interactive, autoInteraction);
  }

  public int getXpReward() {
    return xpReward;
  }

  public void setXpReward(int xpReward) {
    this.xpReward = xpReward;
  }
}
