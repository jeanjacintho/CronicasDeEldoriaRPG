package br.com.cronicasdeeldoria.entity.character.races;

/**
 * Representa a raça Mage, cujo atributo especial é magicPower.
 */
public class Mage implements Race {
  private int magicPower;

  /**
   * Cria um Mage com magicPower definido.
   * @param magicPower Poder mágico.
   */
  public Mage(int magicPower) {
    this.magicPower = magicPower;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
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

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "magicPower";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
  @Override
  public int getSpecialAttributeValue() {
    return magicPower;
  }
}
