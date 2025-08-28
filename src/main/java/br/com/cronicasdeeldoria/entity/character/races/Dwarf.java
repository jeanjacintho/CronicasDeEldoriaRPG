package br.com.cronicasdeeldoria.entity.character.races;

public class Dwarf implements Race {
  private int endurance;

  public Dwarf(int endurance) {
    this.endurance = endurance;
  }

  @Override
  public String getRaceName() {
    return "Dwarf";
  }

  @Override
  public String getSpecialAttributeName() {
    return "endurance";
  }
  @Override
  public int getSpecialAttributeValue() {
    return endurance;
  }

  public int getEndurance() {
    return endurance;
  }

  public void setEndurance(int endurance) {
    this.endurance = endurance;
  }
}
