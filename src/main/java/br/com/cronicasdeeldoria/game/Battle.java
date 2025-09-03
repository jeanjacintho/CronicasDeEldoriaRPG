package br.com.cronicasdeeldoria.game;

import br.com.cronicasdeeldoria.entity.Entity;
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
    // Cura uma pequena quantidade e aumenta defesa temporariamente
    int healAmount = character.getAttributeMaxHealth() / 20; // 5% do HP máximo
    int newHealth = Math.min(character.getAttributeMaxHealth(), character.getAttributeHealth() + healAmount);
    character.setAttributeHealth(newHealth);

    System.out.println(character.getName() + " defended and recovered " + healAmount + " HP!");
    System.out.println(character.getName() + " HP: " + character.getAttributeHealth() + "/" + character.getAttributeMaxHealth());
  }

  private boolean flee(Character character) {
    if (character instanceof Player) {
      int fleeChance = 50; // 50% chance base

      if (Math.random() * 100 < fleeChance) {
        System.out.println("You successfully fled from battle!");
        return true;
      } else {
        System.out.println("Failed to flee!");
        return false;
      }
    }
    return false;
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
