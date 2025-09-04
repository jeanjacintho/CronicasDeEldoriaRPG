package br.com.cronicasdeeldoria.entity.character.races;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.Battle;

/**
 * Representa a raça Breton, cujo atributo especial é força de vontade (willpower).
 */
public class Breton implements Race {
  private int willpower;
  private String specialAbilityName;
  private String specialAbility;


  /**
   * Cria um Breton com força de vontade definida.
   * @param willpower Força de vontade.
   */
  public Breton(int willpower) {
    this.willpower = willpower;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getRaceName() {
    return "Breton";
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
    return "Ataque Sagrado";
  }

  @Override
  public boolean getSpecialAbility(Character attacker, Character target, int countTurn) {
    int manaCost = 15;

    if (attacker.getAttributeMana() >= manaCost) {
      attacker.setAttributeMana(attacker.getAttributeMana() - manaCost);
      int magicDamage = (int) (Battle.calculateDamage(attacker, target) * 1.3); // 30% mais dano
      int newHealth = Math.max(0, target.getAttributeHealth() - magicDamage);
      target.setAttributeHealth(newHealth);

      // Cura o atacante com 60% do dano causado
      int finalHeal = (int) (magicDamage * 0.6);
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

      System.out.println(attacker.getName() + " uses magic on " + target.getName() +
        " causing " + magicDamage + " Fisical damage!");
      System.out.println("-----------------------------");
    }
    return false;
  }
}
