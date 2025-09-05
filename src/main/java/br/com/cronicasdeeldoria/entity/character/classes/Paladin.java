package br.com.cronicasdeeldoria.entity.character.classes;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.Battle;

/**
 * Representa a raça Paladin, cujo atributo especial é endurance.
 */
public class Paladin implements CharacterClass {
  private int endurance;
  private String specialAbilityName;
  private String specialAbility;


  /**
   * Cria um Paladin com endurance definida.
   * @param endurance Endurance do Paladin.
   */
  public Paladin(int endurance) {
    this.endurance = endurance;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getCharacterClassName() {
    return "Paladin";
  }

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "endurance";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
  @Override
  public int getSpecialAttributeValue() {
    return endurance;
  }

  public int getEndurance() {
    return endurance;
  }

  public void setEndurance(int endurance) {
    this.endurance = endurance;
  }

  @Override
  public String getSpecialAbilityName() {
    return "Ataque Sagrado";
  }

  @Override
  public boolean getSpecialAbility(Character attacker, Character target, int countTurn) {
    int manaCost = 15;

    if (attacker.getAttributeMana() >= manaCost) {
      attacker.setAttributeMana(attacker.getAttributeMana() - manaCost);
      int damage = (int) (Battle.calculateDamage(attacker, target) * 2); // 100% mais dano
      int newHealth = Math.max(0, target.getAttributeHealth() - damage);
      target.setAttributeHealth(newHealth);

      // Cura o atacante com 80% do dano causado
      int finalHeal = (int) (damage * 0.8);
      int diffCurrentHpAndMaxHp = attacker.getAttributeMaxHealth() - attacker.getAttributeHealth();

      if (diffCurrentHpAndMaxHp > finalHeal) {
        attacker.setAttributeHealth(attacker.getAttributeHealth() + finalHeal);
        System.out.println(attacker.getName() + " recuperou " + finalHeal + " de Vida");
        System.out.println("-----------------------------");
      } else {
        attacker.setAttributeHealth(attacker.getAttributeHealth() + diffCurrentHpAndMaxHp);
        System.out.println(attacker.getName() + " recuperou " + diffCurrentHpAndMaxHp + " de Vida");
        System.out.println("-----------------------------");
      }

      System.out.println(attacker.getName() + " uses Special Ability on " + target.getName() +
        " causing " + damage + " Holy damage!");
      System.out.println("-----------------------------");
    }
    return false;
  }
}
