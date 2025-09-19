package br.com.cronicasdeeldoria.game;

import br.com.cronicasdeeldoria.audio.AudioManager;
import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.character.npc.Npc;
import br.com.cronicasdeeldoria.game.dialog.DialogManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Battle {
  private final GamePanel gp;
  private final ArrayList<Character> turnOrder; //
  private int currentTurn;
  private boolean inBattle;
  private Player player;
  private Npc monster;
  private boolean waitingForPlayerInput;
  private int countTurn = 0;
  private int timeOutCounter = 0;
  private final AudioManager audioManager;
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public Battle(GamePanel gp) {
    this.gp = gp;
    this.turnOrder = new ArrayList<>();
    this.inBattle = false;
    this.waitingForPlayerInput = false;
    this.audioManager = AudioManager.getInstance();
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
        } break;
      case "SPECIAL": specialAttack(player, monster, countTurn); break;
      case "REGEN": waterOrb(player); break;
      case "DAMAGEOVERTIME": fireOrb(monster); break;
      case "HEALTH": healthPotion(player); break;
      case "MANA": manaPotion(player); break;
      default:
        //waitingForPlayerInput = true; // Permite tentar novamente
        return;
    }

    // Verificar se a batalha terminou
    if (checkBattleEnd()) return;

    // Próximo turno
    nextTurn();

    // Se for turno do monstro, processar automaticamente com atraso
    if (getCurrentCharacter() instanceof Npc) {
      // Delay para processar o turno do monstro
      scheduler.schedule(() -> {
        SwingUtilities.invokeLater(() -> {
          processMonsterTurn();
        });
      }, 1200, TimeUnit.MILLISECONDS);
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

    if (choice < 79) { // 80% chance de ataque
      attack(currentMonster, player);
    } else if (choice < 99) { // 20% chance de defesa
      defend(currentMonster);
    }

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
      countTurn = 0;
      gp.endBattle(false);
      return true;
    }

    if (monster.getAttributeHealth() <= 0) {
      countTurn = 0;

      // Quando o monstro é derrotado, os buffs do player são retirados
      player.cleanActiveBuffs();
      gp.endBattle(true);
      return true;
    }
    return false;
  }

  private void nextTurn() {
    countTurn += 1;
    currentTurn = (currentTurn + 1) % turnOrder.size();

    // Atualiza buffs de todos
    for (Character c : turnOrder) {
      c.updateBuffs(countTurn, gp);
    }

    //System.out.println("\n---------- Turn: " + countTurn + " ----------");
  }

  public static int calculateDamage(Character attacker, Character target) {
    int baseDamage = Math.max(1, attacker.getEffectiveStrength() - (target.getEffectiveArmor() / 2));
    int variation = Math.max(1, (int)(baseDamage * 0.4)); // 40% variation
    int finalDamage = baseDamage + (int)(Math.random() * variation * 2) - variation;

    return Math.max(1, finalDamage); // Minimum 1 damage
  }

  private void attack(Character attacker, Character target) {
    int damage = calculateDamage(attacker, target);
    int newHealth = Math.max(0, target.getAttributeHealth() - damage);
    target.setAttributeHealth(newHealth);

    // Reproduzir som de ataque apenas quando o jogador atacar
    if (attacker instanceof Player) {
      audioManager.playSoundEffect("player_attack");
    }

    // Reproduzir som de bloqueio quando o jogador está defendendo e sendo atacado
    if (target instanceof Player && target.hasActiveBuff("ARMOR")) {
      audioManager.playSoundEffect("player_block");
    }

    System.out.println(attacker.getName() + " attacks " + target.getName() + " causing " + damage + " damage!");
    System.out.println("-----------------------------");

    gp.getGameUI().showDamage(target, damage, null);

    // Acionado a animação de sobreposição SOMENTE no alvo, usando o lado de configuração correto
    try {
      if (attacker instanceof Player) {
        // Ataques do jogador -> usa a configuração da classe do jogador, desenha sobre o monstro
        String playerClass = attacker.getCharacterClass().getCharacterClassName();
        gp.getBattleEffectManager().triggerForMonsterFromPlayer(playerClass, "attack");
      } else {
        // Monster attacks -> use monster-type config, draw over player
        String monsterKey = deriveMonsterKey((Npc) attacker);
        gp.getBattleEffectManager().triggerForPlayerFromMonster(monsterKey, "attack");
      }
    } catch (Exception ignored) {}
  }

  private String deriveMonsterKey(Npc npc) {
    if (npc == null) return "default";
    String name = npc.getName();
    if (name == null) name = "";
    name = name.toLowerCase();
    if (name.contains("orcboss")) return "orcboss";
    if (name.contains("orc")) return "orc";
    if (name.contains("frostbornboss")) return "frostbornboss";
    if (name.contains("frostborn")) return "frostborn";
    if (name.contains("wolfboss")) return "wolfboss";
    if (name.contains("wolf")) return "wolf";
    if (name.contains("skeletonboss")) return "skeletonboss";
    if (name.contains("skeleton")) return "skeleton";
    if (name.contains("suprememage") || name.contains("supreme") || name.contains("mage")) return "suprememage";
    return "default";
  }

  private void defend(Character character) {
    int bonus = (int)(character.getAttributeArmor() * 1.2);

    // 20% de buff por 2 turnos defendendo e 3 de cooldown
    Buff armorBuff = new Buff("ARMOR", bonus, 2 * 2, 2 * 2, character); //

    // Acionar animação somente se o buff foi realmente aplicado
    boolean buffWasApplied = character.canApplyBuff("ARMOR");
    character.applyBuff(armorBuff);

    try {
      if (buffWasApplied) {
        if (character instanceof Player) {
          String cls = character.getCharacterClass().getCharacterClassName();
          gp.getBattleEffectManager().triggerForPlayer(cls, "shield");
        } else if (character instanceof Npc) {
          String monsterKey = deriveMonsterKey((Npc) character);
          gp.getBattleEffectManager().triggerForMonster(monsterKey, "shield");
        }
      }
    } catch (Exception ignored) {}
  }

  private void fireOrb(Character monster) {
    int effectiveDamage = 0;

    // Se o monstro enfrentado é o mago supremo o dot é menor.
    if (Objects.equals(monster.getName(), "Mago Supremo")) {
      effectiveDamage = (int) (monster.getAttributeMaxHealth() * 0.03);
    } else {
      effectiveDamage = (int) (monster.getAttributeMaxHealth() * 0.04);
    }
    Buff damageOverTime = new Buff("DOT", effectiveDamage, 99, 0, player);

    monster.applyBuff(damageOverTime);

    try {
      String monsterKey = deriveMonsterKey((Npc) monster);
      gp.getBattleEffectManager().triggerForMonster(monsterKey, "dot");
    } catch (Exception ignored) {}
  }

  private void waterOrb(Character character) {
    int effectiveHeal = (int) (character.getAttributeMaxHealth() * 0.035);
    Buff healingOverTime = new Buff("HOT", effectiveHeal, 99, 0, character);

    character.applyBuff(healingOverTime);

    try {
      if (character instanceof Player) {
        String cls = character.getCharacterClass().getCharacterClassName();
        gp.getBattleEffectManager().triggerForPlayer(cls, "heal");
      } else if (character instanceof Npc) {
        String monsterKey = deriveMonsterKey((Npc) character);
        gp.getBattleEffectManager().triggerForMonster(monsterKey, "heal");
      }
    } catch (Exception ignored) {}
  }

  private boolean flee(Character character) {
    if (character instanceof Player) {
      // Reproduzir som de tentativa de fuga sempre que o jogador tentar fugir
      audioManager.playSoundEffect("player_flee");

      int fleeChance = 50; // 50% chance base

      // Player não pode fugir da batalha do Mago Supremo
      if (Math.random() * 100 < fleeChance && !Objects.equals(monster.getName(), "Mago Supremo")) {
        System.out.println("You successfully fled from battle!");
        System.out.println("-----------------------------");

        // Mover o jogador para fora da área de detecção (2 tiles de distância)
        //movePlayerAwayFromMonster();

        return true;
      } else {
        if (Objects.equals(monster.getName(), "Mago Supremo")) {
          System.out.println("You can't flee from Supreme Mage!");
        } else {
          System.out.println("Failed to flee!");
          System.out.println("-----------------------------");
          return false;
        }
      }
    }
    return false;
  }

  private void specialAttack(Character attacker, Character target, int countTurn) {
    attacker.getCharacterClass().getSpecialAbility(attacker, target, countTurn, gp);
    try {
      if (attacker instanceof Player) {
        String cls = attacker.getCharacterClass().getCharacterClassName();
        gp.getBattleEffectManager().triggerForPlayer(cls, "special");
      } else if (attacker instanceof Npc) {
        String monsterKey = deriveMonsterKey((Npc) attacker);
        gp.getBattleEffectManager().triggerForMonster(monsterKey, "special");
      }
    } catch (Exception ignored) {}
  }

  // Health Potion
  private void healthPotion(Character character) {
    // Reproduzir som de cura quando poção de vida for consumida
    audioManager.playSoundEffect("potion_heal");

    int baseHeal = 50;
    int variation = (int) (baseHeal * 0.4); // 40% de variação
    int finalHeal = baseHeal + (int)(Math.random() * variation * 2) - variation;
    int diffCurrentHpAndMaxHp = character.getAttributeMaxHealth() - character.getAttributeHealth();

    if (diffCurrentHpAndMaxHp > finalHeal) {
      character.setAttributeHealth(character.getAttributeHealth() + finalHeal);

    } else {
      character.setAttributeHealth(character.getAttributeHealth() + diffCurrentHpAndMaxHp);
    }
    gp.getGameUI().showHeal(character, finalHeal, "POTION");

    // Acionar animação de cura
    try {
      if (character instanceof Player) {
        String cls = character.getCharacterClass().getCharacterClassName();
        gp.getBattleEffectManager().triggerForPlayer(cls, "heal");
      } else if (character instanceof Npc) {
        String monsterKey = deriveMonsterKey((Npc) character);
        gp.getBattleEffectManager().triggerForMonster(monsterKey, "heal");
      }
    } catch (Exception ignored) {}
  }

  // Mana Potion
  private void manaPotion(Character character) {
    // Reproduzir som de cura quando poção de mana for consumida
    audioManager.playSoundEffect("potion_heal");

    int baseManaRecover = 35;
    int variation = (int) (baseManaRecover * 0.3); // 30% de variação
    int finalManaRecover = baseManaRecover + (int)(Math.random() * variation * 2) - variation;
    int diffCurrentMpAndMaxMp = character.getAttributeMaxMana() - character.getAttributeMana();

    if (diffCurrentMpAndMaxMp > finalManaRecover) {
      character.setAttributeMana(character.getAttributeMana() + finalManaRecover);
    } else {
      character.setAttributeMana(character.getAttributeMana() + diffCurrentMpAndMaxMp);
    }
    gp.getGameUI().showMana(character, finalManaRecover);

    // Acionar animação de cura
    try {
      if (character instanceof Player) {
        String cls = character.getCharacterClass().getCharacterClassName();
        gp.getBattleEffectManager().triggerForPlayer(cls, "heal");
      } else if (character instanceof Npc) {
        String monsterKey = deriveMonsterKey((Npc) character);
        gp.getBattleEffectManager().triggerForMonster(monsterKey, "heal");
      }
    } catch (Exception ignored) {}
  }

  private void movePlayerAwayFromMonster() {
    if (player == null || monster == null) return;

    int tileSize = gp.getTileSize();
    int moveDistance = tileSize * 1; // 6 tiles de distância

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
