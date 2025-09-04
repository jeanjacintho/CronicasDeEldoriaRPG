package br.com.cronicasdeeldoria.entity.character.races;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.Battle;

/**
 * Representa a raça Dwarf, cujo atributo especial é endurance.
 */
public class Dwarf implements Race {
  private int endurance;
  private String specialAbilityName;
  private String specialAbility;


  /**
   * Cria um Dwarf com endurance definida.
   * @param endurance Endurance do Dwarf.
   */
  public Dwarf(int endurance) {
    this.endurance = endurance;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getRaceName() {
    return "Dwarf";
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
    return "Endurecer";
  }

  @Override
  public boolean getSpecialAbility(Character attacker, Character target, int countTurn) {
    int manaCost = 15;

    if (attacker.getAttributeMana() >= manaCost) {
      attacker.setAttributeMana(attacker.getAttributeMana() - manaCost);
      int damage = (int) (Battle.calculateDamage(attacker, target) * 1.5); // 50% mais dano
      int newHealth = Math.max(0, target.getAttributeHealth() - damage);
      target.setAttributeHealth(newHealth);

      // Cura o atacante com 60% do dano causado
      int finalHeal = (int) (damage * 0.6);
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
