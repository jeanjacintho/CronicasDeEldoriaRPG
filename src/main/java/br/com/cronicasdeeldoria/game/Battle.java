package br.com.cronicasdeeldoria.game;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.character.npc.Npc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Battle {
  private final GamePanel gp;
  private final ArrayList<Character> turnOrder; //
  private int currentTurn;
  private boolean inBattle;
  private Player player;
  private Npc monster;
  private boolean waitingForPlayerInput;

  public Battle(GamePanel gp) {
    this.gp = gp;
    this.turnOrder = new ArrayList<>();
    this.inBattle = false;
    this.waitingForPlayerInput = false;
  }

  public void startBattle(Player player, Npc monster) {
    this.player = player;
    this.monster = monster;
    this.inBattle = true;
    this.currentTurn = 0;
    this.waitingForPlayerInput = true;

    // Popula a ordem dos turnos
    determineTurnOrder();

    System.out.println("Battle started: " + player.getName() + " vs " + monster.getName());
    System.out.println("Turn order: ");
    for (Character c : turnOrder) {
      System.out.println("- " + c.getName());
    }

    // Se o primeiro turno for do monstro, processa automaticamente
    if (getCurrentCharacter() instanceof Npc) {
      processMonsterTurn();
    }
  }

  private void determineTurnOrder() {
    turnOrder.clear();
    turnOrder.add(player);
    turnOrder.add(monster);

    // Ordenar por agilidade se você tiver esse atributo
    Collections.sort(turnOrder, (c1, c2) -> Integer.compare(c2.getAttributeAgility(), c1.getAttributeAgility()));

    System.out.println("Turn order determined by agility:");
    for (int i = 0; i < turnOrder.size(); i++) {
      Character c = turnOrder.get(i);
      System.out.println((i+1) + ". " + c.getName() + " (Agility: " + c.getAttributeAgility() + ")");
    }
  }

  public void processPlayerAction(String action) {
    if (!inBattle || !waitingForPlayerInput) return;
    //if (!inBattle ) return;

    Character currentCharacter = getCurrentCharacter();
    if (!(currentCharacter instanceof Player)) {
      System.out.println("Not player's turn!");
      return;
    }

    waitingForPlayerInput = false;

    switch (action) {
      case "ATTACK": attack(player, monster); break;
      case "DEFEND": defend(player); break;
      case "FLEE":
        if (flee(player)) {
          gp.endBattle(false);
          return;
        }
        break;
      case "MAGIC": useMagic(player, monster); break;
      case "HEALTH": healthPotion(player); break;
      case "MANA": manaPotion(player); break;
      default:
        System.out.println("Invalid action!");
        //waitingForPlayerInput = true; // Permite tentar novamente
        return;
    }

    // Verificar se a batalha terminou
    if (checkBattleEnd()) return;

    // Próximo turno
    nextTurn();

    // Se for turno do monstro, processar automaticamente
    if (getCurrentCharacter() instanceof Npc) {
      processMonsterTurn();
    } else {
      waitingForPlayerInput = true;
    }
  }

  private void processMonsterTurn() {
    if (!inBattle) return;


    Character currentCharacter = getCurrentCharacter();
    if (!(currentCharacter instanceof Npc)) return;

    Npc currentMonster = (Npc) currentCharacter;

    // IA do monstro
    Random random = new Random();
    int choice = random.nextInt(100);

    if (choice < 69) { // 70% chance de ataque
      attack(currentMonster, player);
    } else if (choice < 99) { // 20% chance de defesa
      defend(currentMonster);
    }
    // Caso monstro poder usar magia
//    else { // 20% chance de magia (se tiver mana)
//      if (currentMonster.getAttributeMana() >= 10) {
//        useMagic(currentMonster, player);
//      }
//    }

    // Verificar se a batalha terminou
    if (checkBattleEnd()) return;

    // Próximo turno
    nextTurn();

    // Se próximo turno for do jogador
    if (getCurrentCharacter() instanceof Player) {
      waitingForPlayerInput = true;
    } else {
      // Se há outro monstro (improvável neste caso), processar também
      processMonsterTurn();
    }
  }

  private boolean checkBattleEnd() {
    if (player.getAttributeHealth() <= 0) {
      System.out.println("You were defeated!");
      gp.endBattle(false);
      return true;
    }

    if (monster.getAttributeHealth() <= 0) {
      System.out.println("You won the battle!");
      gp.endBattle(true);
      return true;
    }

    return false;
  }

  private void nextTurn() {
    currentTurn = (currentTurn + 1) % turnOrder.size();
    System.out.println("\n--- " + getCurrentCharacter().getName() + "'s turn ---");
  }

  private void attack(Character attacker, Character target) {
    int damage = calculateDamage(attacker, target);
    int newHealth = Math.max(0, target.getAttributeHealth() - damage);
    target.setAttributeHealth(newHealth);

    System.out.println(attacker.getName() + " attacks " + target.getName() + " causing " + damage + " damage!");
    System.out.println(target.getName() + " HP: " + target.getAttributeHealth() + "/" + target.getAttributeMaxHealth());
  }

  private int calculateDamage(Character attacker, Character target) {
    int baseDamage = Math.max(1, attacker.getAttributeStrength() - (target.getAttributeDefence() / 2));
    int variation = Math.max(1, (int)(baseDamage * 0.2)); // 20% variation
    int finalDamage = baseDamage + (int)(Math.random() * variation * 2) - variation;

    return Math.max(1, finalDamage); // Minimum 1 damage
  }

  private void defend(Character character) {
    // Aumenta defesa temporariamente
    System.out.println(character.getName() + " defended ");
    System.out.println(character.getName() + " HP: " + character.getAttributeHealth() + "/" + character.getAttributeMaxHealth());
  }

  private boolean flee(Character character) {
    if (character instanceof Player) {
      int fleeChance = 50; // 50% chance base

      if (Math.random() * 100 < fleeChance) {
        System.out.println("You successfully fled from battle!");

        // Mover o jogador para fora da área de detecção (6 tiles de distância)
        movePlayerAwayFromMonster();

        return true;
      } else {
        System.out.println("Failed to flee!");
        return false;
      }
    }
    return false;
  }

  private void healthPotion(Character character) {
    int baseHeal = 25;
    int variation = (int) (baseHeal * 0.4); // 40% de variação
    int finalHeal = baseHeal + (int)(Math.random() * variation * 2) - variation;
    character.setAttributeHealth(character.getAttributeHealth() + finalHeal);

    System.out.println(character.getName() + "Recuperou " + finalHeal + " de Vida");
    //System.out.println(character.getName() + " HP: " + character.getAttributeHealth() + "/" + character.getAttributeMaxHealth());
  }

  private void manaPotion(Character character) {
    int baseManaRecover = 20;
    int variation = (int) (baseManaRecover * 0.3); // 30% de variação
    int finalManaRecover = baseManaRecover + (int)(Math.random() * variation * 2) - variation;
    character.setAttributeMana(character.getAttributeMana() + finalManaRecover);

    System.out.println(character.getName() + "Recuperou " + finalManaRecover + " de Mana");
    //System.out.println(character.getName() + " MP: " + character.getAttributeMana() + "/" + character.getAttributeMaxMana());
  }

  private void movePlayerAwayFromMonster() {
    if (player == null || monster == null) return;

    int tileSize = gp.getTileSize();
    int moveDistance = tileSize * 6; // 6 tiles de distância

    // Calcular diferença de posição
    int deltaX = player.getWorldX() - monster.getWorldX();
    int deltaY = player.getWorldY() - monster.getWorldY();

    // Determinar direção de movimento baseada na posição relativa
    int newX = player.getWorldX();
    int newY = player.getWorldY();

    if (Math.abs(deltaX) >= Math.abs(deltaY)) {
      // Mover horizontalmente
      if (deltaX >= 0) {
        newX = monster.getWorldX() + moveDistance; // Mover para direita
      } else {
        newX = monster.getWorldX() - moveDistance; // Mover para esquerda
      }
    } else {
      // Mover verticalmente
      if (deltaY >= 0) {
        newY = monster.getWorldY() + moveDistance; // Mover para baixo
      } else {
        newY = monster.getWorldY() - moveDistance; // Mover para cima
      }
    }

    // Se player e monster estão na mesma posição, mover para direita
    if (deltaX == 0 && deltaY == 0) {
      newX = monster.getWorldX() + moveDistance;
    }

    // Verificar limites do mundo
    int maxWorldX = gp.maxWorldCol * tileSize - player.getPlayerSize();
    int maxWorldY = gp.maxWorldRow * tileSize - player.getPlayerSize();

    newX = Math.max(0, Math.min(newX, maxWorldX));
    newY = Math.max(0, Math.min(newY, maxWorldY));

    // Aplicar o movimento diretamente
    player.setWorldX(newX);
    player.setWorldY(newY);
  }

  private void useMagic(Character attacker, Character target) {
    int manaCost = 10;

    if (attacker.getAttributeMana() >= manaCost) {
      attacker.setAttributeMana(attacker.getAttributeMana() - manaCost);
      int magicDamage = (int)(calculateDamage(attacker, target) * 1.5); // 50% mais dano
      int newHealth = Math.max(0, target.getAttributeHealth() - magicDamage);
      target.setAttributeHealth(newHealth);

      System.out.println(attacker.getName() + " uses magic on " + target.getName() +
        " causing " + magicDamage + " magic damage!");
      System.out.println(target.getName() + " HP: " + target.getAttributeHealth() +
        "/" + target.getAttributeMaxHealth());
      System.out.println(attacker.getName() + " Mana: " + attacker.getAttributeMana() +
        "/" + attacker.getAttributeMaxMana());
    } else {
      System.out.println(attacker.getName() + " doesn't have enough mana!");
      // Se for o jogador, permite tentar outra ação
      if (attacker instanceof Player) {
        waitingForPlayerInput = true;
      }
    }
  }

  // Getters e métodos de utilidade
  public Character getCurrentCharacter() {
    if (turnOrder.isEmpty()) return null;
    return turnOrder.get(currentTurn);
  }

  public boolean isPlayerTurn() {
    return inBattle && getCurrentCharacter() instanceof Player;
  }

  public boolean isWaitingForPlayerInput() {
    return waitingForPlayerInput;
  }

  public Player getPlayer() {
    return player;
  }

  public Npc getMonster() {
    return monster;
  }

  public boolean isInBattle() {
    return inBattle;
  }

  public void endBattle() {
    inBattle = false;
    waitingForPlayerInput = false;
    turnOrder.clear();
    currentTurn = 0;
    player = null;
    monster = null;
  }
}
