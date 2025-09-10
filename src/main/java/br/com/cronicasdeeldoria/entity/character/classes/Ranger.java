package br.com.cronicasdeeldoria.entity.character.classes;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.GamePanel;

/**
 * Representa a raça Ranger, cujo atributo especial é destreza (dexterity).
 */
public class Ranger implements CharacterClass {
  private int dexterity;
  private String specialAbilityName;
  private String specialAbility;

  /**
   * Cria um Ranger com destreza definida.
   * @param dexterity Destreza do Ranger.
   */
  public Ranger(int dexterity) {
    this.dexterity = dexterity;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getCharacterClassName() {
    return "Ranger";
  }

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "dexterity";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
  @Override
  public int getSpecialAttributeValue() {
    return dexterity;
  }

  @Override
  public String getSpecialAbilityName() {
    return "Flecha Certeira";
  }

  @Override
  public void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp) {
    int manaCost = 15;

    // Variação de dano e ignora armadura do alvo
    int baseDamage = Math.max(1, attacker.getEffectiveStrength());
    int variation = Math.max(1, (int)(baseDamage * 0.4)); // 40% variation
    int finalDamage = baseDamage + (int)(Math.random() * variation * 2) - variation;

    if (attacker.getAttributeMana() >= manaCost) {
      attacker.setAttributeMana(attacker.getAttributeMana() - manaCost);
      int damage = (int)((Math.max(1, finalDamage)) * 2);
      int newHealth = Math.max(0, target.getAttributeHealth() - damage);
      target.setAttributeHealth(newHealth);
      gp.getGameUI().showDamage(target, damage);

      System.out.println(attacker.getName() + " uses Special Ability on " + target.getName() +
        " causing " + damage + " Physical damage!");
      System.out.println("-----------------------------");
    }
  }

  public int getDexterity() {
    return dexterity;
  }

  public void setDexterity(int dexterity) {
    this.dexterity = dexterity;
  }
}
