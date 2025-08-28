package br.com.cronicasdeeldoria.entity.character.races;

public class Breton implements Race {
  private int strength;

  public Breton(int strength) {
    this.strength = strength;
  }

  @Override
  public String getRaceName() {
    return "Breton";
  }

  public int getStrength() {
    return strength;
  }

  public void setStrength(int strength) {
    this.strength = strength;
  }
}
