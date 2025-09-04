package br.com.cronicasdeeldoria.entity.character;

import br.com.cronicasdeeldoria.entity.Entity;
import br.com.cronicasdeeldoria.entity.character.races.Race;

/**
 * Classe base para personagens jogáveis e NPCs, contendo atributos de raça e status.
 */
public class Character extends Entity {
  private Race race;
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
  private int temporaryArmorBonus = 0;
  private int armorBuffTurnsLeft = 0;
  private int armorBuffCooldown = 0;

  /**
   * Cria um novo personagem.
   * @param x Posição X no mundo.
   * @param y Posição Y no mundo.
   * @param speed Velocidade do personagem.
   * @param direction Direção inicial.
   * @param name Nome do personagem.
   * @param race Raça do personagem.
   * @param attributeHealth Vida.
   * @param attributeMaxHealth Vida máxima.
   * @param attributeMana Mana.
   * @param attributeMaxMana Mana máxima.
   * @param attributeStrength Força.
   * @param attributeAgility Agilidade.
   */
  public Character(int x, int y, int speed, String direction, String name, Race race, int attributeHealth, int attributeMaxHealth, int attributeMana, int attributeMaxMana, int attributeStrength, int attributeAgility, int attributeArmor) {
    super(x, y, speed, direction, name);
    this.race = race;
    this.attributeHealth = attributeHealth;
    this.attributeMaxHealth = attributeMaxHealth;
    this.attributeMana = attributeMana;
    this.attributeMaxMana = attributeMaxMana;
    this.attributeStrength = attributeStrength;
    this.attributeAgility = attributeAgility;
    this.attributeArmor = attributeArmor;
  }

  public boolean canUseArmorBuff() {
    return armorBuffCooldown == 0 && armorBuffTurnsLeft == 0;
  }

  public void applyArmorBuff(int bonus, int duration, int cooldown) {
    this.temporaryArmorBonus = bonus;
    this.armorBuffTurnsLeft = duration;
    this.armorBuffCooldown = cooldown;
  }

  public int getEffectiveArmor() {
    return attributeArmor + temporaryArmorBonus;
  }

  public void decrementBuffDuration() {
    if (armorBuffTurnsLeft > 0) {
      armorBuffTurnsLeft--;
      if (armorBuffTurnsLeft == 0) {
        temporaryArmorBonus = 0;
        System.out.println(getName() + "'s Armor Buff has expired!");
      }
    }
    if (armorBuffCooldown > 0) {
      armorBuffCooldown--;
    }
  }

  public Race getRace() {
    return race;
  }
  public void setRace(Race race) {
    this.race = race;
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
}
