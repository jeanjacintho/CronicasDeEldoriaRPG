package br.com.cronicasdeeldoria.entity.character.classes;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.Battle;
import br.com.cronicasdeeldoria.game.GamePanel;

/**
 * Representa a raça Mage, cujo atributo especial é magicPower.
 */
public class Mage implements CharacterClass {
  private int magicPower;

  /**
   * Cria um Mage com magicPower definido.
   * @param magicPower Poder mágico.
   */
  public Mage(int magicPower) {
    this.magicPower = magicPower;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getCharacterClassName() {
    return "Mage";
  }

  public int getMagicPower() {
    return magicPower;
  }

  public void setMagicPower(int magicPower) {
    this.magicPower = magicPower;
  }

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "magicPower";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
  @Override
  public int getSpecialAttributeValue() {
    return magicPower;
  }

  @Override
  public String getSpecialAbilityName() {
    return "Ataque Mágico";
  }

  @Override
  public void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp) {
    int manaCost = 15;

    if (attacker.getAttributeMana() >= manaCost) {
      attacker.setAttributeMana(attacker.getAttributeMana() - manaCost);
      int magicDamage = (int) (Battle.calculateDamage(attacker, target) * 2.8);
      int newHealth = Math.max(0, target.getAttributeHealth() - magicDamage);
      target.setAttributeHealth(newHealth);
      gp.getGameUI().showDamage(target, magicDamage, null);

      System.out.println(attacker.getName() + " uses Special Ability on " + target.getName() +
        " causing " + magicDamage + " Fire damage!");
      System.out.println("-----------------------------");
    }
  }
}
