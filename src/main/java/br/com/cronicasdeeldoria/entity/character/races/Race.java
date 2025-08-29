package br.com.cronicasdeeldoria.entity.character.races;

/**
 * Interface que representa uma raça de personagem, com nome e atributo especial.
 */
public interface Race {
  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  String getRaceName();
  /**
   * Retorna o nome do atributo especial da raça.
   * @return Nome do atributo especial.
   */
  String getSpecialAttributeName();
  /**
   * Retorna o valor do atributo especial da raça.
   * @return Valor do atributo especial.
   */
  int getSpecialAttributeValue();
}
