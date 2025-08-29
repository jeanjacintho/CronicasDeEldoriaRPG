package br.com.cronicasdeeldoria.entity.character;

import br.com.cronicasdeeldoria.entity.Entity;
import br.com.cronicasdeeldoria.entity.character.races.Race;

/**
 * Classe base para personagens jogáveis e NPCs, contendo atributos de raça e status.
 */
public class Character extends Entity {
  private Race race;
  private int attributeHealth;
  private int attributeForce;
  private int attributeDefence;
  private int attributeStamina;
  private int attributeMana;
  private int attributeStrength;
  private int attributeAgility;

  /**
   * Cria um novo personagem.
   * @param x Posição X no mundo.
   * @param y Posição Y no mundo.
   * @param speed Velocidade do personagem.
   * @param direction Direção inicial.
   * @param name Nome do personagem.
   * @param race Raça do personagem.
   * @param attributeHealth Vida.
   * @param attributeMana Mana.
   * @param attributeStrength Força.
   * @param attributeAgility Agilidade.
   */
  public Character(int x, int y, int speed, String direction, String name, Race race, int attributeHealth, int attributeMana, int attributeStrength, int attributeAgility) {
    super(x, y, speed, direction, name);
    this.race = race;
    this.attributeHealth = attributeHealth;
    this.attributeMana = attributeMana;
    this.attributeStrength = attributeStrength;
    this.attributeAgility = attributeAgility;
  }

  public Race getRace() {
    return race;
  }

  public void setRace(Race race) {
    this.race = race;
  }

  public int getAttributeLife() {
    return attributeHealth;
  }

  public void setAttributeHealth(int attributeHealth) {
    this.attributeHealth = attributeHealth;
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
}
