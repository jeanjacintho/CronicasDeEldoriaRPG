package br.com.cronicasdeeldoria.entity.character.races;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.Buff;

/**
 * Representa a raça Barbarian, cujo atributo especial é força de vontade (willpower).
 */
public class Barbarian implements Race {
  private int willpower;
  private String specialAbilityName;
  private String specialAbility;


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
  public String getRaceName() {
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
  public boolean getSpecialAbility(Character attacker, Character target, int countTurn) {
    int manaCost = 15;

    if (attacker.getAttributeMana() >= manaCost) {
      int bonus = (int)(attacker.getAttributeStrength() * 1.5);
      attacker.setAttributeMana(attacker.getAttributeMana() - manaCost);

      // 50% de buff por 2 turnos atacando e 4 de cooldown
      Buff strBuff = new Buff("STRENGTH", bonus, 3 * 2, 3 * 2);
      attacker.applyBuff(strBuff);
    }
    return false;
  }
}
