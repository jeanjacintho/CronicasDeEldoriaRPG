package br.com.cronicasdeeldoria.entity.character.classes;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.GamePanel;

/**
 * Interface que representa uma classe de personagem, com nome e atributo especial.
 */
public interface CharacterClass {
  /**
   * Retorna o nome da classe.
   * @return Nome da classe.
   */
  String getCharacterClassName();
  /**
   * Retorna o nome do atributo especial da classe.
   * @return Nome do atributo especial.
   */
  String getSpecialAttributeName();
  /**
   * Retorna o valor do atributo especial da classe.
   * @return Valor do atributo especial.
   */
  int getSpecialAttributeValue();

  /**
   * Retorna o nome da habilide especial da classe.
   * @return Nome da habilide especial da classe.
   */
  String getSpecialAbilityName();

  /**
   * Retorna o efeito da habiliade especial.
   */
  void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp);

}
