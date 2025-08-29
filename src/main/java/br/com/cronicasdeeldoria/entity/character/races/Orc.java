package br.com.cronicasdeeldoria.entity.character.races;

/**
 * Representa a raça Orc, cujo atributo especial é rage.
 */
public class Orc implements Race {
  private int rage;

  /**
   * Cria um Orc com rage definido.
   * @param rage Raiva do Orc.
   */
  public Orc(int rage) {
    this.rage = rage;
  }

  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
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

  /**
   * Retorna o nome do atributo especial.
   * @return Nome do atributo especial.
   */
  @Override
  public String getSpecialAttributeName() {
    return "rage";
  }
  /**
   * Retorna o valor do atributo especial.
   * @return Valor do atributo especial.
   */
  @Override
  public int getSpecialAttributeValue() {
    return rage;
  }
}
