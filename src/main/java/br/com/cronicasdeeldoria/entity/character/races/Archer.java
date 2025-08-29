package br.com.cronicasdeeldoria.entity.character.races;

/**
 * Representa a raça Archer, cujo atributo especial é destreza (dexterity).
 */
public class Archer implements Race {
  private int dexterity;

  /**
   * Cria um Archer com destreza definida.
   * @param dexterity Destreza do Archer.
   */
  public Archer(int dexterity) {
    this.dexterity = dexterity;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getRaceName() {
    return "Archer";
  }

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "dexterity";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
  @Override
  public int getSpecialAttributeValue() {
    return dexterity;
  }

  public int getDexterity() {
    return dexterity;
  }

  public void setDexterity(int dexterity) {
    this.dexterity = dexterity;
  }
}
