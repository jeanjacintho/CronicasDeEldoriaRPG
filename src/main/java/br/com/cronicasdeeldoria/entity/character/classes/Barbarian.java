package br.com.cronicasdeeldoria.entity.character.classes;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.Buff;
import br.com.cronicasdeeldoria.game.GamePanel;

/**
 * Representa a raça Barbarian, cujo atributo especial é força de vontade (willpower).
 */
public class Barbarian implements CharacterClass {
  private int willpower;

  /**
   * Cria um Barbarian com força de vontade definida.
   * @param willpower Força de vontade.
   */
  public Barbarian(int willpower) {
    this.willpower = willpower;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getCharacterClassName() {
    return "Barbarian";
  }

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "willpower";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
  @Override
  public int getSpecialAttributeValue() {
    return willpower;
  }
  public int getWillpower() {
    return willpower;
  }
  public void setWillpower(int willpower) {
    this.willpower = willpower;
  }

  @Override
  public String getSpecialAbilityName() {
    return "Furia de Batalha";
  }

  @Override
  public void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp) {
    int manaCost = 15;

    if (attacker.getAttributeMana() >= manaCost) {
      int bonus = (int)(attacker.getAttributeStrength() * 1.2);
      attacker.setAttributeMana(attacker.getAttributeMana() - manaCost);

      // 50% de buff por 2 turnos atacando e 4 de cooldown
      Buff strBuff = new Buff("STRENGTH", bonus, 3 * 2, 3 * 2);
      attacker.applyBuff(strBuff);
    }
  }
}
