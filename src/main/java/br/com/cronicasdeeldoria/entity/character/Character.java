package br.com.cronicasdeeldoria.entity.character;

import br.com.cronicasdeeldoria.entity.Entity;
import br.com.cronicasdeeldoria.entity.character.races.Race;

public class Character extends Entity {
  private Race race;
  private int attributeHealth;
  private int attributeForce;
  private int attributeDefence;
  private int attributeStamina;

  public Character(int x, int y, int speed, String direction, String name, Race race, int attributeHealth, int attributeForce, int attributeDefence, int attributeStamina) {
    super(x, y, speed, direction, name);
    this.race = race;
    this.attributeHealth = attributeHealth;
    this.attributeForce = attributeForce;
    this.attributeDefence = attributeDefence;
    this.attributeStamina = attributeStamina;
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
}
