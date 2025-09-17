package br.com.cronicasdeeldoria.game;

import br.com.cronicasdeeldoria.entity.character.Character;

public class Buff {
  private String type;        // "ARMOR", "STRENGTH", etc.
  private int bonus;          // valor do buff
  private int turnsLeft;      // duração
  private int cooldownLeft;   // cooldown
  private int cooldownTime;   // cooldown padrão
  private Character caster;

  public Buff(String type, int bonus, int duration, int cooldownTime, Character caster) {
    this.type = type;
    this.bonus = bonus;
    this.turnsLeft = duration;
    this.cooldownTime = cooldownTime;
    this.cooldownLeft = 0;
    this.caster = caster;
  }

  public String getType() {
    return type;
  }

  public int getBonus() {
    return bonus;
  }

  public int getTurnsLeft() {
    return turnsLeft;
  }

  public boolean isActive() {
    return turnsLeft > 0;
  }

  public boolean isOnCooldown() {
    return cooldownLeft > 0;
  }

  public Character getCaster() { return caster; }

  public void decrementDuration(Character character) {
    if (turnsLeft > 0) {
      turnsLeft--;
      if (turnsLeft == 0) {
        cooldownLeft = cooldownTime; // inicia cooldown
        System.out.println(character.getName() + "'s " + type + " Buff has expired!");
      }
    }
    if (cooldownLeft > 0) {
      cooldownLeft--;
    }
  }
}
