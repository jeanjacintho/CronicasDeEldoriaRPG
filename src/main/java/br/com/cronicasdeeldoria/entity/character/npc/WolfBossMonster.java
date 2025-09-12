package br.com.cronicasdeeldoria.entity.character.npc;

public class WolfBossMonster extends Npc {
  private int xpReward = 250;
  /**
   * Cria um novo WolfBossMonster.
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
  public WolfBossMonster(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize, boolean interactive, boolean autoInteraction) {
    super(name, isStatic, dialog, x, y, skin, playerSize, interactive, autoInteraction);
    setAttributeHealth(5);
    setAttributeMaxHealth(150);
    setAttributeStrength(25);
    setAttributeDefence(15);
    setAttributeAgility(15);
  }

  public int getXpReward() { return xpReward; }

  public void setXpReward(int xpReward) { this.xpReward = xpReward; }
}
