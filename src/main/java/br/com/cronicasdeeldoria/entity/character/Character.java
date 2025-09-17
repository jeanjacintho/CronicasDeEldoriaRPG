package br.com.cronicasdeeldoria.entity.character;

import br.com.cronicasdeeldoria.entity.Entity;
import br.com.cronicasdeeldoria.entity.character.classes.CharacterClass;
import br.com.cronicasdeeldoria.game.Buff;
import br.com.cronicasdeeldoria.game.inventory.Equipment;
import br.com.cronicasdeeldoria.game.GamePanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe base para personagens jogáveis e NPCs, contendo atributos de raça e status.
 */
public class Character extends Entity {
  private CharacterClass characterClass;
  private int attributeHealth;
  private int attributeMaxHealth;
  private int attributeMana;
  private int attributeMaxMana;
  private int attributeForce;
  private int attributeDefence;
  private int attributeStamina;
  private int attributeStrength;
  private int attributeAgility;
  private int attributeArmor;
  private List<Buff> activeBuffs = new ArrayList<>();
  private Equipment equipment;

  /**
   * Cria um novo personagem.
   * @param x Posição X no mundo.
   * @param y Posição Y no mundo.
   * @param speed Velocidade do personagem.
   * @param direction Direção inicial.
   * @param name Nome do personagem.
   * @param characterClass Raça do personagem.
   * @param attributeHealth Vida.
   * @param attributeMaxHealth Vida máxima.
   * @param attributeMana Mana.
   * @param attributeMaxMana Mana máxima.
   * @param attributeStrength Força.
   * @param attributeAgility Agilidade.
   */
  public Character(int x, int y, int speed, String direction, String name, CharacterClass characterClass, int attributeHealth, int attributeMaxHealth, int attributeMana, int attributeMaxMana, int attributeStrength, int attributeAgility, int attributeArmor) {
    super(x, y, speed, direction, name);
    this.characterClass = characterClass;
    this.attributeHealth = attributeHealth;
    this.attributeMaxHealth = attributeMaxHealth;
    this.attributeMana = attributeMana;
    this.attributeMaxMana = attributeMaxMana;
    this.attributeStrength = attributeStrength;
    this.attributeAgility = attributeAgility;
    this.attributeArmor = attributeArmor;
    //this.equipment = new Equipment( this);
  }

  public boolean hasActiveBuff(String type) {
    return activeBuffs.stream()
      .anyMatch(b -> b.getType().equals(type) && b.isActive());
  }

  public boolean canApplyBuff(String type) {
    // até 3 buffs diferentes ativos
    if (activeBuffs.size() >= 3) return false;

    // impede reaplicar mesmo tipo de buff ativo ou em cooldown
    for (Buff b : activeBuffs) {

      // Qualquer outro buff, não pode aplicar de novo
      if (b.getType().equals(type) && (b.isActive() || b.isOnCooldown())) {
        return false;
      }
    }
    return true;
  }

  public void applyBuff(Buff buff) {
    if (canApplyBuff(buff.getType())) {
      activeBuffs.add(buff);
    }
  }

  public void updateBuffs(int countTurn, GamePanel gp) {
    activeBuffs.removeIf(b -> !b.isActive() && !b.isOnCooldown()); // limpa buffs que terminaram

    for (Buff b : activeBuffs) {
      switch (b.getType()) {
        case "HOT":
          if (countTurn % 2 == 0) {
            System.out.println("Curei no turno: " + countTurn);
            int missingHp = Math.max(0, (attributeMaxHealth - attributeHealth));
            int healAmount = Math.max(0, Math.min(b.getBonus(), missingHp));
            if (healAmount > 0) {
              attributeHealth += healAmount;

              gp.getGameUI().showHeal(this, healAmount, "REGEN");
            }
          }
          break;
        case "DOT":
          if (countTurn % 2 == 0) {
            System.out.println("Dano no turno: " + countTurn);
            attributeHealth -= b.getBonus();

            gp.getGameUI().showDamage(this, b.getBonus(), "DAMAGEOVERTIME");
          }
          break;
      }
      b.decrementDuration(this);
    }
  }

  public int getEffectiveArmor() {
    int bonus = activeBuffs.stream()
      .filter(b -> b.getType().equals("ARMOR") && b.isActive())
      .mapToInt(Buff::getBonus)
      .sum();
    return attributeArmor + bonus;
  }

  public int getEffectiveStrength() {
    int bonus = activeBuffs.stream()
      .filter(b -> b.getType().equals("STRENGTH") && b.isActive())
      .mapToInt(Buff::getBonus)
      .sum();
    return attributeStrength + bonus;
  }

  public CharacterClass getCharacterClass() {
    return characterClass;
  }
  public void setCharacterClass(CharacterClass characterClass) {
    this.characterClass = characterClass;
  }
  public int getAttributeHealth() {
    return attributeHealth;
  }
  public void setAttributeHealth(int attributeHealth) {
    this.attributeHealth = attributeHealth;
  }
  public int getAttributeMaxHealth() {
     return attributeMaxHealth;
  }

  public void setAttributeMaxHealth(int attributeMaxHealth) {
    this.attributeMaxHealth = attributeMaxHealth;
  }

  public int getAttribueForce() {
    return attributeForce;
  }

  public void setAttribueForce(int attribueForce) {
    this.attributeForce = attribueForce;
  }

  public int getAttributeDefence() {
    return attributeDefence;
  }

  public void setAttributeDefence(int attributeDefence) {
    this.attributeDefence = attributeDefence;
  }

  public int getAttributeStamina() {
    return attributeStamina;
  }

  public void setAttributeStamina(int attributeStamina) {
    this.attributeStamina = attributeStamina;
  }

  public int getAttributeMana() {
    return attributeMana;
  }

  public void setAttributeMana(int attributeMana) {
    this.attributeMana = attributeMana;
  }

  public int getAttributeMaxMana() {
    return attributeMaxMana;
  }

  public void setAttributeMaxMana(int attributeMaxMana) {
    this.attributeMaxMana = attributeMaxMana;
  }

  public int getAttributeStrength() {
    return attributeStrength;
  }

  public void setAttributeStrength(int attributeStrength) {
    this.attributeStrength = attributeStrength;
  }

  public int getAttributeAgility() {
    return attributeAgility;
  }

  public void setAttributeAgility(int attributeAgility) {
    this.attributeAgility = attributeAgility;
  }

  public int getAttributeArmor() { return attributeArmor; }

  public void setAttributeArmor(int attributeArmor) { this.attributeArmor = attributeArmor; }

  public List<Buff> getActiveBuffs() { return activeBuffs; }

  public void setActiveBuffs(List<Buff> activeBuffs) { this.activeBuffs = activeBuffs; }

  public void setEquipment(Equipment equipment) { this.equipment = equipment; }
  public Equipment getEquipment() { return equipment; }
}
