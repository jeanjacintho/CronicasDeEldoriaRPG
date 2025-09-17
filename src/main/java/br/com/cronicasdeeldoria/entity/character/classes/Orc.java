package br.com.cronicasdeeldoria.entity.character.classes;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.Battle;
import br.com.cronicasdeeldoria.game.GamePanel;

/**
 * Representa a raça Orc, cujo atributo especial é rage.
 */
public class Orc implements CharacterClass {
  private int rage;

  /**
   * Cria um Orc com rage definido.
   * @param rage Raiva do Orc.
   */
  public Orc(int rage) {
    this.rage = rage;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getCharacterClassName() {
    return "Orc";
  }

  public int getRage() {
    return rage;
  }

  public void setRage(int rage) {
    this.rage = rage;
  }

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "rage";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
  @Override
  public int getSpecialAttributeValue() {
    return rage;
  }

  @Override
  public String getSpecialAbilityName() {
    return "Golpe Duplo";
  }

  @Override
  public void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp) {
    int manaCost = 10;


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
      } else {
        attacker.setAttributeHealth(attacker.getAttributeHealth() + diffCurrentHpAndMaxHp);
      }
    }
  }
}
