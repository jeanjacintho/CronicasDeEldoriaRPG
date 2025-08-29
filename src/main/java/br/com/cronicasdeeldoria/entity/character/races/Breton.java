package br.com.cronicasdeeldoria.entity.character.races;

/**
 * Representa a raça Breton, cujo atributo especial é força de vontade (willpower).
 */
public class Breton implements Race {
  private int willpower;

  /**
   * Cria um Breton com força de vontade definida.
   * @param willpower Força de vontade.
   */
  public Breton(int willpower) {
    this.willpower = willpower;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  @Override
  public String getRaceName() {
    return "Breton";
  }

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "willpower";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
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
