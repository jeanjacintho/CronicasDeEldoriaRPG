package br.com.cronicasdeeldoria.entity.character.npc;

public class SkeletonMonster extends Npc {
  private int xpReward = 30;

  /**
   * Cria um novo SkeletonMonster.
   * @param name Nome do Monster.
   * @param isStatic Indica se o Monster é estático.
   * @param dialog Diálogo do Monster.
   * @param x Posição X.
   * @param y Posição Y.
   * @param skin Skin do Monster.
   * @param playerSize Tamanho do jogador (para hitbox).
   * @param interactive Indica se o Monster é interativo.
   * @param autoInteraction Indica se a interação é automática.
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
