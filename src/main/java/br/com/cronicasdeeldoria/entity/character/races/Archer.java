package br.com.cronicasdeeldoria.entity.character.races;

public class Archer implements Race {
  private int dexterity;

  public Archer(int dexterity) {
    this.dexterity = dexterity;
  }

  @Override
  public String getRaceName() {
    return "Archer";
  }

  public int getDexterity() {
    return dexterity;
  }

  public void setDexterity(int dexterity) {
    this.dexterity = dexterity;
  }
}
