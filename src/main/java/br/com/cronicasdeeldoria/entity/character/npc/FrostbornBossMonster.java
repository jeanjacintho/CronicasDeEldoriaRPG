package br.com.cronicasdeeldoria.entity.character.npc;

public class FrostbornBossMonster extends Npc {
  private int xpReward = 100;
  /**
   * Cria um novo FrostbornBossMonster.
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
  public FrostbornBossMonster(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize, boolean interactive, boolean autoInteraction) {
    super(name, isStatic, dialog, x, y, skin, playerSize, interactive, autoInteraction);
    setAttributeHealth(200);
    setAttributeMaxHealth(200);
    setAttributeStrength(50);
    setAttributeDefence(45);
    setAttributeAgility(25);
  }

  public int getXpReward() { return xpReward; }

  public void setXpReward(int xpReward) { this.xpReward = xpReward; }
}
