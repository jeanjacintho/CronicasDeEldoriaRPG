package br.com.cronicasdeeldoria.entity.character.races;

public class Breton implements Race {
  private int willpower;

  public Breton(int willpower) {
    this.willpower = willpower;
  }

  @Override
  public String getRaceName() {
    return "Breton";
  }

  @Override
  public String getSpecialAttributeName() {
    return "willpower";
  }
  @Override
  public int getSpecialAttributeValue() {
    return willpower;
  }
  public int getWillpower() {
    return willpower;
  }
  public void setWillpower(int willpower) {
    this.willpower = willpower;
  }
}
