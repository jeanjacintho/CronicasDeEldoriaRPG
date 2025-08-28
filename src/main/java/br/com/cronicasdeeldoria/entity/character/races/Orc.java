package br.com.cronicasdeeldoria.entity.character.races;

public class Orc implements Race {
  private int rage;

  public Orc(int rage) {
    this.rage = rage;
  }

  @Override
  public String getRaceName() {
    return "Orc";
  }

  public int getRage() {
    return rage;
  }

  public void setRage(int rage) {
    this.rage = rage;
  }

  @Override
  public String getSpecialAttributeName() {
    return "rage";
  }
  @Override
  public int getSpecialAttributeValue() {
    return rage;
  }
}
