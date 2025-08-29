package br.com.cronicasdeeldoria.entity.character.races;

/**
 * Representa a raça Dwarf, cujo atributo especial é endurance.
 */
public class Dwarf implements Race {
  private int endurance;

  /**
   * Cria um Dwarf com endurance definida.
   * @param endurance Endurance do Dwarf.
   */
  public Dwarf(int endurance) {
    this.endurance = endurance;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getRaceName() {
    return "Dwarf";
  }

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "endurance";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
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
