package br.com.cronicasdeeldoria.entity.character.races;

public class Mage implements Race {
  private int magicPower;

  public Mage(int magicPower) {
    this.magicPower = magicPower;
  }

  @Override
  public String getRaceName() {
    return "Mage";
  }

  public int getMagicPower() {
    return magicPower;
  }

  public void setMagicPower(int magicPower) {
    this.magicPower = magicPower;
  }
}
