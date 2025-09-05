package br.com.cronicasdeeldoria.entity.character.classes;

import br.com.cronicasdeeldoria.entity.character.Character;

/**
 * Interface que representa uma raça de personagem, com nome e atributo especial.
 */
public interface CharacterClass {
  /**
   * Retorna o nome da raça.
   * @return Nome da raça.
   */
  String getCharacterClassName();
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

  /**
   * Retorna o nome da habilide especial da raça.
   * @return Nome da habilide especial da raça.
   */
  String getSpecialAbilityName();

  /**
   * Retorna o efeito da habiliade especial.
   *
   * @return Efeito da habilide especial da raça.
   */
  boolean getSpecialAbility(Character attacker, Character target, int countTurn);
}
