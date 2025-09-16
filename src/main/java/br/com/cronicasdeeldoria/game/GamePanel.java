package br.com.cronicasdeeldoria.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.BasicStroke;

import javax.swing.JPanel;

import br.com.cronicasdeeldoria.entity.character.npc.*;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.character.classes.*;
import br.com.cronicasdeeldoria.entity.character.classes.Barbarian;
import br.com.cronicasdeeldoria.entity.object.MapObject;
import br.com.cronicasdeeldoria.entity.object.ObjectManager;
import br.com.cronicasdeeldoria.entity.object.ObjectSpriteLoader;
import br.com.cronicasdeeldoria.game.ui.GameUI;
import br.com.cronicasdeeldoria.game.ui.KeyboardMapper;
import br.com.cronicasdeeldoria.game.font.FontManager;
import br.com.cronicasdeeldoria.game.ui.InteractionManager;
import br.com.cronicasdeeldoria.game.ui.SimpleInteractionManager;
import br.com.cronicasdeeldoria.tile.TileManager;
import br.com.cronicasdeeldoria.tile.TileManager.MapTile;
import br.com.cronicasdeeldoria.config.CharacterConfigLoader;
import java.util.List;
import java.util.ArrayList;

import br.com.cronicasdeeldoria.entity.Entity;
import br.com.cronicasdeeldoria.game.inventory.InventoryManager;
import br.com.cronicasdeeldoria.game.merchant.MerchantManager;
import br.com.cronicasdeeldoria.game.merchant.MerchantUI;
import br.com.cronicasdeeldoria.game.dialog.DialogManager;
import br.com.cronicasdeeldoria.game.dialog.DialogUI;
import br.com.cronicasdeeldoria.game.teleport.TeleportManager;
import br.com.cronicasdeeldoria.game.quest.QuestManager;
import br.com.cronicasdeeldoria.audio.AudioManager;
import br.com.cronicasdeeldoria.audio.AudioContext;
/**
 * Painel principal do jogo, responsável pelo loop de atualização, renderização e gerenciamento dos elementos do jogo.
 */
public class GamePanel extends JPanel implements Runnable{
  private static final int FPS = 60;

  public int maxWorldCol;
  public int maxWorldRow;
  public final int worldWidth = getTileSize() * maxWorldCol;
  public final int worldHeight = getTileSize() * maxWorldRow;

  KeyHandler keyHandler = new KeyHandler();
  Thread gameThread;
  private ColisionChecker colisionChecker = new ColisionChecker(this);

  Player player;
  private int tileSize;
  private int maxScreenRow;
  private int maxScreenCol;
  private TileManager tileManager;
  private List<Npc> npcs = new ArrayList<>();
  private NpcSpriteLoader npcSpriteLoader;
  private ObjectManager objectManager;
  private GameUI gameUI;
  private KeyboardMapper keyboardMapper;
  private InteractionManager interactionManager;
  private SimpleInteractionManager simpleInteractionManager;
  private InventoryManager inventoryManager;
  private MerchantManager merchantManager;
  private MerchantUI merchantUI;
  private DialogManager dialogManager;
  private DialogUI dialogUI;
  private TeleportManager teleportManager;
  private QuestManager questManager;
  private AudioManager audioManager;
  private String playerClassName;
  private String currentMapName;

  public int ancianDialogId = 30;

  // Estados do jogo
  public int gameState;
  public final int playState = 1;
  public final int battleState = 2;
  public final int inventoryState = 3;
  public final int merchantState = 4;
  public final int dialogState = 5;
  public final int pauseState = 6;
  public final int victoryState = 7;

  public Npc battleMonster = null;
  public Battle battle;

  // Cooldown para evitar re-engajamento imediato
  private long lastBattleEndTime = 0;
  private final long BATTLE_COOLDOWN = 1000; // 1 segundo de cooldown

  /**
   * Inicializa o painel do jogo com as configurações fornecidas.
   * @param screenWidth Largura da tela.
   * @param screenHeight Altura da tela.
   * @param playerName Nome do jogador.
   * @param characterClass Classe do jogador.
   * @param tileSize Tamanho do tile.
   * @param maxScreenRow Máximo de linhas na tela.
   * @param maxScreenCol Máximo de colunas na tela.
   */
  public GamePanel(int screenWidth, int screenHeight, String playerName, CharacterClass characterClass, int tileSize, int maxScreenRow, int maxScreenCol) {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.maxScreenRow = maxScreenRow;
    this.maxScreenCol = maxScreenCol;
    this.setBackground(Color.BLACK);
    this.tileSize = tileSize;
    this.setDoubleBuffered(true);
    this.addKeyListener(keyHandler);
    this.setFocusable(true);
    this.playerClassName = characterClass.getCharacterClassName();

    this.battle = new Battle(this);
    gameState = playState;

    CharacterConfigLoader configLoader = CharacterConfigLoader.getInstance();
    String characterClassName = characterClass.getCharacterClassName().toLowerCase();
    int playerSize = getPlayerSize();

    this.tileManager = new TileManager(this);
    this.maxWorldCol = tileManager.getMapWidth();
    this.maxWorldRow = tileManager.getMapHeight();

    int x = (maxWorldCol * tileSize) / 2 - (playerSize / 2);
    int y = (maxWorldRow * tileSize) / 2 - (playerSize / 2);
    int speed = configLoader.getIntAttribute(characterClassName, "speed", 4);
    String direction = configLoader.getStringAttribute(characterClassName, "direction", "down");
    int health = configLoader.getIntAttribute(characterClassName, "health", 100);
    int maxHealth = health;
    int mana = configLoader.getIntAttribute(characterClassName, "mana", 100);
    int maxMana = mana;
    int strength = configLoader.getIntAttribute(characterClassName, "strength", 10);
    int agility = configLoader.getIntAttribute(characterClassName, "agility", 10);
    int luck = configLoader.getIntAttribute(characterClassName, "luck", 0);
    int armor = configLoader.getIntAttribute(characterClassName, "armor", 0);

    int special = 0;
    switch (characterClassName) {
      case "paladin":
        special = configLoader.getIntAttribute(characterClassName, "endurance", 0);
        break;
      case "mage":
        special = configLoader.getIntAttribute(characterClassName, "magicPower", 0);
        break;
      case "orc":
        special = configLoader.getIntAttribute(characterClassName, "rage", 0);
        break;
      case "barbarian":
        special = configLoader.getIntAttribute(characterClassName, "willpower", 0);
        break;
      case "ranger":
        special = configLoader.getIntAttribute(characterClassName, "dexterity", 0);
        break;
      default:
        special = 0;
    }
    CharacterClass characterClassInstance;
    switch (characterClassName) {
      case "paladin": characterClassInstance = new Paladin(special); break;
      case "mage": characterClassInstance = new Mage(special); break;
      case "orc": characterClassInstance = new Orc(special); break;
      case "barbarian": characterClassInstance = new Barbarian(special); break;
      case "archer": characterClassInstance = new Ranger(special); break;
      default: characterClassInstance = characterClass;
    }

    player = new Player(this, keyHandler, characterClassInstance, x, y, speed, direction, playerName, health, maxHealth, mana, maxMana, strength, agility, luck, armor);

    initializeGameComponents(player);
    // Inicializar sistema de comerciante após a criação do player
    this.merchantManager = new MerchantManager(inventoryManager, player.getPlayerMoney());
    this.merchantUI = new MerchantUI(this);

    // Inicializar sistema de diálogo
    this.dialogManager = new DialogManager(this, player);
    this.dialogUI = new DialogUI(this);

    // Inicializar sistema de teleporte
    this.teleportManager = TeleportManager.getInstance();

    // Inicializar sistema de quests
    this.questManager = QuestManager.getInstance();
    this.questManager.initialize(this);

    // Inicializar sistema de áudio
    this.audioManager = AudioManager.getInstance();
    this.currentMapName = "houses/player_house"; // Mapa inicial padrão

    // Configurar contexto inicial de áudio
    AudioContext initialContext = AudioContext.fromMapName(currentMapName);
    audioManager.changeContext(initialContext);

    // Iniciar diálogo inicial ao começar um novo jogo
    if (this.dialogManager != null) {
      boolean started = this.dialogManager.startDialog(37);
      if (started) {
        gameState = dialogState;
      }
    }
  }

  /**
   * Método de teste para forçar a abertura de um diálogo
   */
  public void testDialog() {
    if (dialogManager != null) {
      boolean success = dialogManager.startDialog(1);
      if (success) {
        gameState = dialogState;
        System.out.println("TESTE: Diálogo forçado com sucesso!");
      } else {
        System.out.println("TESTE: Falha ao forçar diálogo!");
      }
    } else {
      System.out.println("TESTE: DialogManager é null!");
    }
  }

  /**
   * Método de teste para verificar NPCs carregados
   */
  public void testNpcs() {
    System.out.println("TESTE: Verificando NPCs carregados...");
    if (npcs != null) {
      System.out.println("Total de NPCs: " + npcs.size());

    } else {
      System.out.println("TESTE: Lista de NPCs é null!");
    }
  }

  /**
   * Inicia a thread principal do jogo.
   */
  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
    this.requestFocusInWindow(); // Garantir que o GamePanel tenha foco

    // Ao iniciar o jogo da 3 poções ao player
    dialogManager.giveItemToPlayer("health_potion", 3);
    dialogManager.giveItemToPlayer("mana_potion", 2);

    dialogManager.giveItemToPlayer("mana_potion", 5);
    dialogManager.giveItemToPlayer("health_potion", 5);

    //Itens para testar Dungeon3
    dialogManager.giveItemToPlayer("ring_rare", 1);
    dialogManager.giveItemToPlayer("shield_common", 1);
    dialogManager.giveItemToPlayer("armor_rare", 1);
    dialogManager.giveItemToPlayer("axe_rare", 1);
    dialogManager.giveItemToPlayer("bow_rare", 1);

    //Itens para testar Dungeon4
    dialogManager.giveItemToPlayer("ring_legendary", 1);
    dialogManager.giveItemToPlayer("shield_legendary", 1);
    dialogManager.giveItemToPlayer("armor_legendary", 1);
    dialogManager.giveItemToPlayer("axe_legendary", 1);
    dialogManager.giveItemToPlayer("bow_legendary", 1);
  }

  @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
            if(timer >= 1000000000) {
                timer = 0;
            }
        }
    }

    /**
     * Atualiza o estado do jogo.
     */
    public void update() {

      if (gameState == playState) {
        player.update();

        // Atualizar NPCs apenas se houver NPCs no mapa
        if (npcs != null && !npcs.isEmpty()) {
          for (Npc npc : npcs) {
            npc.update(this, player);
          }
        }

        // Atualizar objetos
        if (objectManager != null) {
          objectManager.updateActiveObjects(player.getWorldX() / tileSize, player.getWorldY() / tileSize);
        }

        // Atualizar pontos de interação
        updateInteractionPoints();

        // Verificar teleportes automáticos
        checkAutomaticTeleports();

        // Atualizar sistema de quests
        updateQuests();

        // Verificar se o GamePanel perdeu o foco e restaurá-lo
        if (!this.hasFocus()) {
          this.requestFocusInWindow();
        }

        // Controle do inventário
        if (keyHandler.inventoryPressed) {
          inventoryManager.toggleVisibility();
          if (inventoryManager.isVisible()) {
            gameState = inventoryState;
          }
          keyHandler.inventoryPressed = false;
        }

        // Controle de pausa
        if (keyHandler.escapeKeyPressed) {
          gameState = pauseState;
          keyHandler.escapeKeyPressed = false;
        }
      }

      if (gameState == battleState) {
        updateBattle();
      }

      if (gameState == inventoryState) {
        updateInventory();
      }

      if (gameState == merchantState) {
        updateMerchant();
      }

      if (gameState == dialogState) {
        updateDialog();
      }

      if (gameState == pauseState) {
        updatePause();
      }

      if (gameState == victoryState) {
        updateVictory();
      }
    }

  public void startBattle(Entity targetEntity) {
    if (targetEntity instanceof Npc) {
      Npc monster = (Npc) targetEntity;
      battleMonster = monster;

      // Iniciar batalha
      battle.startBattle(player, monster);

      // Mudar para estado de batalha
      gameState = battleState;

      // Atualizar contexto de áudio para batalha
      updateAudioContextForBattle(monster);
    }
  }

  /**
   * Atualiza o contexto de áudio baseado no tipo de inimigo na batalha.
   */
  private void updateAudioContextForBattle(Npc monster) {
    if (audioManager != null) {
      AudioContext battleContext = AudioContext.fromNpcName(monster.getName());
      audioManager.changeContext(battleContext);

      // Reproduzir efeito sonoro de início de batalha
      audioManager.playSoundEffect("battle_start");
    }
  }

  public void endBattle(boolean playerWon) {
    if (playerWon) {

      LootTable lootTable = null;
      int xpReward = 0;

      String charClass = getPlayer().getCharacterClass().getCharacterClassName();

      if (battleMonster instanceof WolfMonster ) {
        xpReward = ((WolfMonster) battleMonster).getXpReward();
        lootTable = new WolfLootTable();
      } else if (battleMonster instanceof SkeletonMonster) {
        xpReward = ((SkeletonMonster) battleMonster).getXpReward();
        lootTable = new SkeletonLootTable();
      } else if (battleMonster instanceof FrostbornMonster) {
        xpReward = ((FrostbornMonster) battleMonster).getXpReward();
        lootTable = new FrostbornLootTable();
      } else if (battleMonster instanceof OrcMonster) {
        xpReward = ((OrcMonster) battleMonster).getXpReward();
        lootTable = new OrcLootTable();
      } else if (battleMonster instanceof FrostbornBossMonster) {
        xpReward = ((FrostbornBossMonster) battleMonster).getXpReward();
        lootTable = new FrostbornBossLootTable(charClass);
      } else if (battleMonster instanceof OrcBossMonster) {
        xpReward = ((OrcBossMonster) battleMonster).getXpReward();
        lootTable = new OrcBossLootTable(charClass);
      } else if (battleMonster instanceof SkeletonBossMonster) {
        xpReward = ((SkeletonBossMonster) battleMonster).getXpReward();
        lootTable = new SkeletonBossLootTable(charClass);
      } else if (battleMonster instanceof WolfBossMonster) {
        xpReward = ((WolfBossMonster) battleMonster).getXpReward();
        lootTable = new WolfBossLootTable(charClass);
      }
      player.gainXp(xpReward);

      // Drop do monstro
      if (lootTable != null) {
        List<String> itemDrops = lootTable.getDrops();
        if (!itemDrops.isEmpty()) {
          for (String itemName : itemDrops) {
            dialogManager.giveItemToPlayer(itemName, 1);
          }
        } else {
          getGameUI().addMessage("O monstro não deixou nenhum loot.", null, 5000);
        }
      }

      // Remover monstro derrotado do mapa
      removeMonsterFromMap(battleMonster);
    } else {
      System.out.println("Defeat! You were defeated by " + battleMonster.getName());
      // Aplicar penalidade se necessário
      // player.applyDeathPenalty(); // se você tiver este metodo
    }

    // Limpar estado de batalha
    battle.endBattle();

    if (playerWon) {
      // Entrar no estado de vitória
      gameState = victoryState;

      // Reproduzir música de vitória
      if (audioManager != null) {
        audioManager.changeContext(AudioContext.VICTORY);
      }
    } else {
      // Voltar ao jogo normal em caso de derrota
      gameState = playState;

      // Restaurar contexto de áudio do mapa após derrota
      restoreMapAudioContext();
    }

    battleMonster = null;
    lastBattleEndTime = System.currentTimeMillis();
  }

  /**
   * Restaura o contexto de áudio do mapa após o fim da batalha.
   */
  private void restoreMapAudioContext() {
    if (audioManager != null && currentMapName != null) {
      AudioContext mapContext = AudioContext.fromMapName(currentMapName);
      audioManager.changeContext(mapContext);

      // Reproduzir efeito sonoro de fim de batalha
      audioManager.playSoundEffect("battle_end");
    }
  }

  /**
   * Obtém o gerenciador de áudio.
   * @return AudioManager
   */
  public AudioManager getAudioManager() {
    return audioManager;
  }

  /**
   * Reproduz efeito sonoro de interação com objeto.
   */
  public void playInteractionSound(String interactionType) {
    if (audioManager != null) {
      switch (interactionType.toLowerCase()) {
        case "door":
          audioManager.playSoundEffect("door_open");
          break;
        case "chest":
          audioManager.playSoundEffect("item_pickup");
          break;
        case "teleport":
          audioManager.playSoundEffect("teleport");
          break;
        case "button":
          audioManager.playSoundEffect("button_click");
          break;
        default:
          audioManager.playSoundEffect("notification");
          break;
      }
    }
  }

  /**
   * Reproduz efeito sonoro de progresso de quest.
   */
  public void playQuestProgressSound() {
    if (audioManager != null) {
      audioManager.playSoundEffect("quest_complete");
    }
  }

  /**
   * Reproduz efeito sonoro de level up.
   */
  public void playLevelUpSound() {
    if (audioManager != null) {
      audioManager.playSoundEffect("level_up");
    }
  }

  /**
   * Método de teste para verificar o sistema de áudio.
   */
  public void testAudioSystem() {
    System.out.println("=== TESTING AUDIO SYSTEM ===");

    if (audioManager == null) {
      System.out.println("ERROR: AudioManager is null!");
      return;
    }

    System.out.println("AudioManager instance: " + audioManager);
    System.out.println("Current context: " + audioManager.getCurrentContext());
    System.out.println("Music enabled: " + audioManager.isMusicEnabled());
    System.out.println("Muted: " + audioManager.isMuted());
    System.out.println("Master volume: " + audioManager.getMasterVolume());
    System.out.println("Music volume: " + audioManager.getMusicVolume());

    // Testar mudança para floresta
    System.out.println("Testing forest context...");
    AudioContext forestContext = AudioContext.FOREST;
    audioManager.changeContext(forestContext);

    System.out.println("=== END TESTING AUDIO SYSTEM ===");
  }

  /**
    /**
     * Atualiza os pontos de interação baseado na proximidade do jogador
     */
    private void updateInteractionPoints() {
        if (simpleInteractionManager == null) return;

        simpleInteractionManager.clearInteractionPoints();

        // Não processar interações se estivermos em batalha
        if (gameState == battleState) return;

        // Verificar cooldown de batalha para evitar re-engajamento imediato
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBattleEndTime < BATTLE_COOLDOWN) return;

        // Verificar interação com NPCs apenas se houver NPCs no mapa
        if (npcs != null && !npcs.isEmpty()) {
            for (Npc npc : npcs) {
                // Verificar se é um monstro (usar distância de 5 tiles)
                if (npc instanceof WolfMonster || npc instanceof SkeletonMonster ||
                    npc instanceof FrostbornMonster || npc instanceof OrcMonster ||
                    npc instanceof FrostbornBossMonster || npc instanceof OrcBossMonster ||
                    npc instanceof SkeletonBossMonster || npc instanceof WolfBossMonster) {

                    boolean isNearMonster = isPlayerNearMonster(player, npc.getWorldX(), npc.getWorldY());
                    if (isNearMonster && npc.isInteractive()) {
                        // Verificar auto-interação
                        if (npc.isAutoInteraction()) {
                            startBattle(npc);
                            npc.interact();
                        } else {
                            // Usar coordenadas de mundo diretamente
                            simpleInteractionManager.addInteractionPoint(npc.getWorldX(), npc.getWorldY(), "E", "monster");
                        }
                    }
                } else {
                    // NPCs normais (usar distância de 2 tiles)
                    boolean isNearNpc = isPlayerNearNpc(player, npc.getWorldX(), npc.getWorldY());
                    if (isNearNpc && npc.isInteractive()) {
                        // Verificar auto-interação
                        if (npc.isAutoInteraction()) {
                            npc.interact();
                        } else {
                            // Usar coordenadas de mundo diretamente
                            simpleInteractionManager.addInteractionPoint(npc.getWorldX(), npc.getWorldY(), "E", "npc");
                        }
                    }
                }
            }
        }

        // Verificar interação com objetos
        if (objectManager != null) {
            for (MapObject obj : objectManager.getActiveObjects()) {
                if (obj.isInteractive() && obj.isActive()) {
                    // Verificar auto-interação
                    if (obj.isAutoInteraction()) {
                        // Para objetos com auto-interação, verificar colisão real
                        if (isPlayerCollidingWithObject(player, obj)) {
                            obj.interact(player);
                        }
                    } else {
                        // Para objetos sem auto-interação, verificar proximidade e mostrar tecla E
                        boolean isNearObject = isPlayerNearObject(player, obj.getWorldX(), obj.getWorldY());
                        if (isNearObject) {
                            simpleInteractionManager.addInteractionPoint(obj.getWorldX(), obj.getWorldY(), "E", "object");
                        }
                    }
                }
            }
        }

        // Verificar interação com teleportes interativos
        List<MapTile> teleportTiles = tileManager.getTeleportTiles();
        for (MapTile teleportTile : teleportTiles) {
            // Verificar apenas teleportes interativos
            if (teleportTile.interactive != null && teleportTile.interactive) {
                int teleportWorldX = teleportTile.x * tileSize;
                int teleportWorldY = teleportTile.y * tileSize;

                // Teleporte interativo - mostrar tecla E quando próximo
                if (isPlayerNearObject(player, teleportWorldX, teleportWorldY)) {
                    simpleInteractionManager.addInteractionPoint(teleportWorldX, teleportWorldY, "E", "teleport");
                }
            }
        }
    }

    /**
     * Verifica se o jogador está próximo de uma entidade com distância específica.
     */
    private boolean isPlayerNearEntity(Player player, int entityX, int entityY, int distanceInTiles) {
        // Usar o centro do jogador para cálculo de distância
        int playerCenterX = player.getWorldX() + (player.getPlayerSize() / 2);
        int playerCenterY = player.getWorldY() + (player.getPlayerSize() / 2);

        // Usar o centro da entidade
        int entityCenterX = entityX + (tileSize / 2);
        int entityCenterY = entityY + (tileSize / 2);

        // Calcular distância em pixels
        int distanceX = Math.abs(playerCenterX - entityCenterX);
        int distanceY = Math.abs(playerCenterY - entityCenterY);

        // Distância máxima baseada no parâmetro (em pixels)
        int maxDistance = tileSize * distanceInTiles;

        boolean isNear = distanceX <= maxDistance && distanceY <= maxDistance;


        return isNear;
    }

    /**
     * Verifica se o jogador está próximo de um NPC (2 tiles).
     */
    private boolean isPlayerNearNpc(Player player, int entityX, int entityY) {
        boolean isNear = isPlayerNearEntity(player, entityX, entityY, 2);
        return isNear;
    }

    /**
     * Verifica se o jogador está próximo de um objeto (2 tiles).
     */
    private boolean isPlayerNearObject(Player player, int entityX, int entityY) {
        return isPlayerNearEntity(player, entityX, entityY, 2);
    }

    /**
     * Verifica se o jogador está colidindo com um objeto (contato direto).
     */
    private boolean isPlayerCollidingWithObject(Player player, MapObject obj) {
        // Obter as hitboxes do jogador e do objeto
        Rectangle playerHitbox = player.getHitbox();
        Rectangle objHitbox = obj.getHitbox();

        // Ajustar as hitboxes para as coordenadas de mundo
        Rectangle playerWorldHitbox = new Rectangle(
            player.getWorldX() + playerHitbox.x,
            player.getWorldY() + playerHitbox.y,
            playerHitbox.width,
            playerHitbox.height
        );

        Rectangle objWorldHitbox = new Rectangle(
            obj.getWorldX() + objHitbox.x,
            obj.getWorldY() + objHitbox.y,
            objHitbox.width,
            objHitbox.height
        );

        // Verificar se há interseção entre as hitboxes
        return playerWorldHitbox.intersects(objWorldHitbox);
    }

    /**
     * Verifica se o jogador está próximo de um monstro (5 tiles).
     */
    private boolean isPlayerNearMonster(Player player, int entityX, int entityY) {
        return isPlayerNearEntity(player, entityX, entityY, 1);
    }

    /**
     * Verifica e processa interações do jogador com NPCs e objetos.
     */
    public void checkInteraction() {
        // Não processar interações se estivermos em batalha
        if (gameState == battleState) return;

        // Verificar cooldown de batalha para evitar re-engajamento imediato
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBattleEndTime < BATTLE_COOLDOWN) return;

        // Verificar interação com monstros primeiro (maior prioridade) apenas se houver NPCs
        if (npcs != null && !npcs.isEmpty()) {
          for (Npc npc : npcs) {
            if (npc instanceof WolfMonster) {
                if (isPlayerNearMonster(player, npc.getWorldX(), npc.getWorldY()) && npc.isInteractive()) {
                    npc.interact();
                    return;
                }
            }
          }

          // Verificar interação com NPCs normais
          for (Npc npc : npcs) {
            if (!(npc instanceof WolfMonster)) {
                if (isPlayerNearNpc(player, npc.getWorldX(), npc.getWorldY()) && npc.isInteractive()) {
                    npc.interact(this);
                    if (dialogManager != null && dialogManager.isDialogActive()) {
                        gameState = dialogState;
                    }
                    return;
                }
            }
          }
        }

        // Verificar interação com objetos
        if (objectManager != null) {
            for (MapObject obj : objectManager.getActiveObjects()) {
                if (obj.isInteractive() && obj.isActive()) {
                    boolean shouldInteract = false;

                    if (obj.isAutoInteraction()) {
                        // Para objetos com auto-interação, verificar colisão real
                        shouldInteract = isPlayerCollidingWithObject(player, obj);
                    } else {
                        // Para objetos sem auto-interação, verificar proximidade
                        shouldInteract = isPlayerNearObject(player, obj.getWorldX(), obj.getWorldY());
                    }

                    if (shouldInteract) {
                        obj.interact(player);
                        return;
                    }
                }
            }
        }

        // Verificar interação com teleportes (apenas os configurados no TeleportManager)
        List<MapTile> teleportTiles = tileManager.getTeleportTiles();
        for (MapTile teleportTile : teleportTiles) {
            // Verificar se o teleporte está configurado no TeleportManager
            if (teleportTile.id != null && teleportManager.hasTeleport(teleportTile.id)) {
            int teleportWorldX = teleportTile.x * tileSize;
            int teleportWorldY = teleportTile.y * tileSize;

            if (teleportTile.interactive) {
                // Teleporte interativo - verificar proximidade
                if (isPlayerNearObject(player, teleportWorldX, teleportWorldY)) {
                        performInteractiveTeleport(teleportTile);
                    return;
                    }
                }
            }
        }
    }

    /**
     * Renderiza os elementos do jogo.
     * @param graphics Contexto gráfico.
     */
    public void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);
      Graphics2D graphics2D = (Graphics2D) graphics;

      if (gameState == playState) {
        // Renderização normal do jogo
        tileManager.draw(graphics2D);

        // Renderizar objetos
        if (objectManager != null) {
          objectManager.drawObjects(graphics2D);
        }

        // Renderizar NPCs apenas se houver NPCs no mapa
        if (npcs != null && !npcs.isEmpty()) {
          for (Npc npc : npcs) {
            npc.draw(graphics2D, npcSpriteLoader, tileSize, player, player.getScreenX(), player.getScreenY());

              // Renderizar tecla de interação para NPC se necessário
              if (simpleInteractionManager != null) {
                  simpleInteractionManager.renderInteractionKeyForEntity(graphics2D,
                      npc.getWorldX(), npc.getWorldY(),
                      npc.getWorldX() - player.getWorldX() + player.getScreenX(),
                      npc.getWorldY() - player.getWorldY() + player.getScreenY(),
                      "npc", tileSize);
              }
          }
        }

        // Renderizar teclas de interação para objetos
        if (objectManager != null && simpleInteractionManager != null) {
            for (MapObject obj : objectManager.getActiveObjects()) {
                if (obj.isActive()) {
                    simpleInteractionManager.renderInteractionKeyForEntity(graphics2D,
                        obj.getWorldX(), obj.getWorldY(),
                        obj.getWorldX() - player.getWorldX() + player.getScreenX(),
                        obj.getWorldY() - player.getWorldY() + player.getScreenY(),
                        "object", tileSize);
                }
            }
        }

        // Renderizar teclas de interação para teleportes (apenas os configurados)
        if (simpleInteractionManager != null) {
            List<MapTile> teleportTiles = tileManager.getTeleportTiles();
            for (MapTile teleportTile : teleportTiles) {
                // Verificar se o teleporte está configurado no TeleportManager e é interativo
                if (teleportTile.interactive != null && teleportTile.interactive &&
                    teleportTile.id != null && teleportManager.hasTeleport(teleportTile.id)) {
                    int teleportWorldX = teleportTile.x * tileSize;
                    int teleportWorldY = teleportTile.y * tileSize;

                    // Usar o método específico para tiles que centraliza a tecla
                    simpleInteractionManager.renderInteractionKeyForTile(graphics2D,
                        teleportWorldX, teleportWorldY,
                        teleportWorldX - player.getWorldX() + player.getScreenX(),
                        teleportWorldY - player.getWorldY() + player.getScreenY(),
                        "teleport", tileSize);
                }
            }
        }

        // Renderizar player
        player.draw(graphics2D);

        // Interface normal de jogo
        gameUI.draw(graphics2D);

      } else if (gameState == battleState) {
        // Desenhar interface de batalha
        gameUI.drawBattleUI(graphics2D);
      } else if (gameState == inventoryState) {
        // Desenha o jogo de fundo mesmo durante o inventário (para transparência)
        tileManager.draw(graphics2D);

        // Renderizar objetos
        if (objectManager != null) {
          objectManager.drawObjects(graphics2D);
        }

        // Renderizar NPCs apenas se houver NPCs no mapa
        if (npcs != null && !npcs.isEmpty()) {
          for (Npc npc : npcs) {
            npc.draw(graphics2D, npcSpriteLoader, tileSize, player, player.getScreenX(), player.getScreenY());
          }
        }

        // Renderizar player
        player.draw(graphics2D);

        // Interface normal de jogo
        gameUI.draw(graphics2D);

        // Desenhar interface do inventário por cima (transparente)
        gameUI.drawInventoryUI(graphics2D, inventoryManager);
      } else if (gameState == merchantState) {
        // Desenha o jogo de fundo mesmo durante o comerciante (para transparência)
        tileManager.draw(graphics2D);

        // Renderizar objetos
        if (objectManager != null) {
          objectManager.drawObjects(graphics2D);
        }

        // Renderizar NPCs apenas se houver NPCs no mapa
        if (npcs != null && !npcs.isEmpty()) {
          for (Npc npc : npcs) {
            npc.draw(graphics2D, npcSpriteLoader, tileSize, player, player.getScreenX(), player.getScreenY());
          }
        }

        // Renderizar player
        player.draw(graphics2D);

        // Interface normal de jogo
        gameUI.draw(graphics2D);

        // Desenhar interface do comerciante por cima (transparente)
        merchantUI.draw(graphics2D, merchantManager);
      } else if (gameState == dialogState) {
        // Desenha o jogo de fundo mesmo durante o diálogo
        tileManager.draw(graphics2D);

        // Renderizar objetos
        if (objectManager != null) {
          objectManager.drawObjects(graphics2D);
        }

        // Renderizar NPCs apenas se houver NPCs no mapa
        if (npcs != null && !npcs.isEmpty()) {
          for (Npc npc : npcs) {
            npc.draw(graphics2D, npcSpriteLoader, tileSize, player, player.getScreenX(), player.getScreenY());
          }
        }

        // Renderizar player
        player.draw(graphics2D);

        // Interface normal de jogo
        gameUI.draw(graphics2D);

        // Desenhar interface de diálogo por cima
        dialogUI.draw(graphics2D, dialogManager);
      } else if (gameState == pauseState) {
        // Desenha o jogo de fundo durante a pausa
        tileManager.draw(graphics2D);

        // Renderizar objetos
        if (objectManager != null) {
          objectManager.drawObjects(graphics2D);
        }

        // Renderizar NPCs apenas se houver NPCs no mapa
        if (npcs != null && !npcs.isEmpty()) {
          for (Npc npc : npcs) {
            npc.draw(graphics2D, npcSpriteLoader, tileSize, player, player.getScreenX(), player.getScreenY());
          }
        }

        // Renderizar player
        player.draw(graphics2D);

        // Interface normal de jogo
        gameUI.draw(graphics2D);

        // Desenhar overlay de pausa
        drawPauseOverlay(graphics2D);
      } else if (gameState == victoryState) {
        // Desenha o jogo de fundo durante a vitória
        tileManager.draw(graphics2D);

        // Renderizar objetos
        if (objectManager != null) {
          objectManager.drawObjects(graphics2D);
        }

        // Renderizar NPCs apenas se houver NPCs no mapa
        if (npcs != null && !npcs.isEmpty()) {
          for (Npc npc : npcs) {
            npc.draw(graphics2D, npcSpriteLoader, tileSize, player, player.getScreenX(), player.getScreenY());
          }
        }

        // Renderizar player
        player.draw(graphics2D);

        // Interface normal de jogo
        gameUI.draw(graphics2D);

        // Desenhar overlay de vitória
        drawVictoryOverlay(graphics2D);
      }
      graphics2D.dispose();
    }

  /**
   * Desenha o overlay de pausa.
   */
  private void drawPauseOverlay(Graphics2D g2) {
    // Overlay semi-transparente
    g2.setColor(new Color(0, 0, 0, 150));
    g2.fillRect(0, 0, getWidth(), getHeight());

    // Caixa de pausa centralizada
    int boxWidth = 300;
    int boxHeight = 150;
    int boxX = (getWidth() - boxWidth) / 2;
    int boxY = (getHeight() - boxHeight) / 2;

    // Sombra da caixa
    g2.setColor(new Color(0, 0, 0, 100));
    g2.fillRoundRect(boxX + 4, boxY + 4, boxWidth, boxHeight, 20, 20);

    // Caixa principal
    g2.setColor(new Color(50, 40, 60, 250));
    g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

    // Borda da caixa
    g2.setColor(new Color(100, 80, 120, 200));
    g2.setStroke(new BasicStroke(3));
    g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

    // Texto "PAUSADO"
    g2.setColor(Color.WHITE);
    g2.setFont(FontManager.getFont(32f));
    String pauseText = "PAUSADO";
    int textWidth = g2.getFontMetrics().stringWidth(pauseText);
    int textX = boxX + (boxWidth - textWidth) / 2;
    int textY = boxY + 60;

    // Sombra do texto
    g2.setColor(new Color(0, 0, 0, 150));
    g2.drawString(pauseText, textX + 2, textY + 2);

    // Texto principal
    g2.setColor(Color.WHITE);
    g2.drawString(pauseText, textX, textY);

    // Instrução
    g2.setFont(FontManager.getFont(16f));
    String instructionText = "Pressione ESC para continuar";
    int instructionWidth = g2.getFontMetrics().stringWidth(instructionText);
    int instructionX = boxX + (boxWidth - instructionWidth) / 2;
    int instructionY = boxY + 100;

    // Sombra da instrução
    g2.setColor(new Color(0, 0, 0, 150));
    g2.drawString(instructionText, instructionX + 1, instructionY + 1);

    // Instrução principal
    g2.setColor(new Color(200, 200, 200));
    g2.drawString(instructionText, instructionX, instructionY);
  }

  /**
   * Desenha o overlay de vitória.
   */
  private void drawVictoryOverlay(Graphics2D g2) {
    // Overlay semi-transparente
    g2.setColor(new Color(0, 0, 0, 150));
    g2.fillRect(0, 0, getWidth(), getHeight());

    // Caixa de vitória centralizada
    int boxWidth = 400;
    int boxHeight = 200;
    int boxX = (getWidth() - boxWidth) / 2;
    int boxY = (getHeight() - boxHeight) / 2;

    // Sombra da caixa
    g2.setColor(new Color(0, 0, 0, 100));
    g2.fillRoundRect(boxX + 4, boxY + 4, boxWidth, boxHeight, 20, 20);

    // Caixa principal (dourada para vitória)
    g2.setColor(new Color(255, 215, 0, 250));
    g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

    // Borda da caixa
    g2.setColor(new Color(255, 255, 0, 200));
    g2.setStroke(new BasicStroke(3));
    g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

    // Texto "VITÓRIA!"
    g2.setColor(Color.WHITE);
    g2.setFont(FontManager.getFont(36f));
    String victoryText = "VITÓRIA!";
    int textWidth = g2.getFontMetrics().stringWidth(victoryText);
    int textX = boxX + (boxWidth - textWidth) / 2;
    int textY = boxY + 80;

    // Sombra do texto
    g2.setColor(new Color(0, 0, 0, 150));
    g2.drawString(victoryText, textX + 2, textY + 2);

    // Texto principal
    g2.setColor(Color.WHITE);
    g2.drawString(victoryText, textX, textY);

    // Instrução
    g2.setFont(FontManager.getFont(18f));
    String instructionText = "Pressione ENTER para continuar";
    int instructionWidth = g2.getFontMetrics().stringWidth(instructionText);
    int instructionX = boxX + (boxWidth - instructionWidth) / 2;
    int instructionY = boxY + 140;

    // Sombra da instrução
    g2.setColor(new Color(0, 0, 0, 150));
    g2.drawString(instructionText, instructionX + 1, instructionY + 1);

    // Instrução principal
    g2.setColor(new Color(200, 200, 200));
    g2.drawString(instructionText, instructionX, instructionY);
  }

  // Sistema de inventário
  private void updateInventory() {
    if (inventoryManager == null) return;

    // Controles do inventário
    if (keyHandler.upPressed) {
      inventoryManager.moveUp();
      keyHandler.upPressed = false;
    }
    if (keyHandler.downPressed) {
      inventoryManager.moveDown();
      keyHandler.downPressed = false;
    }
    if (keyHandler.leftPressed) {
      inventoryManager.moveLeft();
      keyHandler.leftPressed = false;
    }
    if (keyHandler.rightPressed) {
      inventoryManager.moveRight();
      keyHandler.rightPressed = false;
    }

    // TAB para alternar entre inventário e equipamento
    if (keyHandler.tabPressed) {
      inventoryManager.toggleMode();
      keyHandler.tabPressed = false;
    }

    // ENTER para equipar/desequipar
    if (keyHandler.actionPressed) {
      if (inventoryManager.isInInventoryMode()) {
        inventoryManager.equipSelectedItem();
      } else {
        inventoryManager.unequipSelectedItem();
      }
      keyHandler.actionPressed = false;
    }

    // I para fechar inventário
    if (keyHandler.inventoryPressed) {
      inventoryManager.toggleVisibility();
      gameState = playState;
      keyHandler.inventoryPressed = false;
    }
  }

  // Sistema de comerciante
  private void updateMerchant() {
    if (merchantManager == null) return;

    // Controles do comerciante
    if (keyHandler.upPressed) {
      merchantManager.moveUp();
      keyHandler.upPressed = false;
    }
    if (keyHandler.downPressed) {
      merchantManager.moveDown();
      keyHandler.downPressed = false;
    }
    if (keyHandler.leftPressed) {
      merchantManager.moveLeft();
      keyHandler.leftPressed = false;
    }
    if (keyHandler.rightPressed) {
      merchantManager.moveRight();
      keyHandler.rightPressed = false;
    }

    // ENTER para comprar/vender
    if (keyHandler.actionPressed) {
      merchantManager.executeAction();
      keyHandler.actionPressed = false;
    }

    // ESC ou I para fechar comerciante
    if (keyHandler.escapeKeyPressed || keyHandler.inventoryPressed) {
      merchantManager.closeMerchant();
      gameState = playState;
      keyHandler.escapeKeyPressed = false;
      keyHandler.inventoryPressed = false;
    }
  }

  // Sistema de diálogo
  private void updateDialog() {
    if (dialogManager == null || !dialogManager.isDialogActive()) {
      gameState = playState;
      return;
    }

    // Controles do diálogo
    if (keyHandler.upPressed) {
      dialogManager.selectPreviousOption();
      keyHandler.upPressed = false;
    }
    if (keyHandler.downPressed) {
      dialogManager.selectNextOption();
      keyHandler.downPressed = false;
    }

    // ENTER para confirmar seleção
    if (keyHandler.actionPressed) {
      dialogManager.confirmSelection();
      keyHandler.actionPressed = false;
    }

    // ESC para sair do diálogo
    if (keyHandler.escapeKeyPressed) {
      dialogManager.endDialog();
      gameState = playState;
      keyHandler.escapeKeyPressed = false;
    }
  }

  // Sistema de pausa
  private void updatePause() {
    // ESC para sair da pausa
    if (keyHandler.escapeKeyPressed) {
      gameState = playState;
      keyHandler.escapeKeyPressed = false;
    }
  }

  // Sistema de vitória
  private void updateVictory() {
    // ENTER para sair da tela de vitória
    if (keyHandler.actionPressed) {
      gameState = playState;
      keyHandler.actionPressed = false;

      // Restaurar contexto de áudio do mapa após vitória
      restoreMapAudioContext();
    }
  }

  // Batalha por turnos
  private void updateBattle() {
    if (!battle.isInBattle()) return;

    // Processar apenas entrada do jogador quando for sua vez
    if (battle.isWaitingForPlayerInput()) {
      if (keyHandler.attackPressed) {
        battle.processPlayerAction("ATTACK");
        keyHandler.attackPressed = false;
      }
      else if (keyHandler.defendPressed ) {
        if (player.canApplyBuff("ARMOR")) {
          battle.processPlayerAction("DEFEND");
        } else {
          System.out.println("ARMOR buff is activate or in cooldown!");
        }
        keyHandler.defendPressed = false;
      }
      else if (keyHandler.escapePressed) {
        battle.processPlayerAction("FLEE");
        keyHandler.escapePressed = false;
      }
      else if (keyHandler.specialPressed && player.getAttributeMana() > 15) {
        if (player.canApplyBuff("STRENGTH")) {
          battle.processPlayerAction("SPECIAL");
        } else {
          System.out.println("STRENGTH buff is activate or in cooldown! ");
        }
        keyHandler.specialPressed = false;
      }
      else if (keyHandler.healthPressed) {
        // Se vida atual menor que máxima e tem potion no inventário, pode ser potion
        if (player.getAttributeHealth() < player.getAttributeMaxHealth() && inventoryManager.hasItemById("health_potion")) {
          inventoryManager.consumeItem("health_potion");
          battle.processPlayerAction("HEALTH");
        } else {
          System.out.println("You don't have health potion or your life is full");
        }
        keyHandler.healthPressed = false;
      }
      else if (keyHandler.manaPressed ) {
        // Se mana atual menor que máxima e tem potion no inventário, pode ser potion
        if (player.getAttributeMana() < player.getAttributeMaxMana() && inventoryManager.hasItemById("mana_potion")) {
          inventoryManager.consumeItem("mana_potion");
          battle.processPlayerAction("MANA");
        } else {
          System.out.println("You already have a maximum MANA or your mana is full");
        }
        keyHandler.manaPressed = false;
      }
    }

    // A lógica do monstro é processada automaticamente dentro da Battle class
  }

  // Metodo para remover monstro derrotado do mapa
  private void removeMonsterFromMap(Npc monster) {
    // Notificar QuestManager sobre a morte do NPC
    if (questManager != null && monster != null) {
      questManager.onNpcKilled(monster.getName());
    }
    
    npcs.remove(monster);
  }

  public int getTileSize() {
    return tileSize;
  }
  public int getPlayerSize() {
    return tileSize * 2;
  }
  public int getScreenWidth() {
      return tileSize * maxScreenCol;
  }
  public int getScreenHeight() {
      return tileSize * maxScreenRow;
  }
  public int getMaxScreenCol() {
      return this.maxScreenCol;
  }
  public int getMaxScreenRow() {
      return this.maxScreenRow;
  }
  public Player getPlayer() {
    return player;
  }
  public ColisionChecker getColisionChecker() {
    return colisionChecker;
  }
  public TileManager getTileManager() {
    return tileManager;
  }

  public List<Npc> getNpcs() {
    return npcs;
  }

  public ObjectManager getObjectManager() {
    return objectManager;
  }

  public GameUI getGameUI() {
    return gameUI;
  }

    public KeyboardMapper getKeyboardMapper() {
      return keyboardMapper;
    }

    public InteractionManager getInteractionManager() {
      return interactionManager;
    }

    public SimpleInteractionManager getSimpleInteractionManager() {
      return simpleInteractionManager;
    }

    public NpcSpriteLoader getNpcSpriteLoader() {
      return npcSpriteLoader;
    }

    public InventoryManager getInventoryManager() {
      return inventoryManager;
    }

    public MerchantManager getMerchantManager() {
      return merchantManager;
    }

    public DialogManager getDialogManager() {
      return dialogManager;
    }

    public DialogUI getDialogUI() {
      return dialogUI;
    }

  public TeleportManager getTeleportManager() {
    return teleportManager;
  }

  /**
   * Atualiza o sistema de quests.
   */
  private void updateQuests() {
    if (questManager != null) {
      // Verificar progresso das quests
      questManager.updateQuestProgress();

      // Verificar se boss foi derrotado
      if (questManager.isBossSpawned()) {
        checkBossDefeat();
      }
    }
  }

  /**
   * Verifica se o boss final foi derrotado.
   */
  private void checkBossDefeat() {
    if (npcs != null) {
      for (Npc npc : npcs) {
        if (npc instanceof SupremeMage &&
            npc.getAttributeHealth() <= 0) {
          questManager.onBossDefeated();
          break;
        }
      }
    }
  }

  /**
   * Obtém o gerenciador de quests.
   * @return QuestManager
   */
  public QuestManager getQuestManager() {
    return questManager;
  }

  /**
   * Adiciona um NPC à lista de NPCs do jogo.
   * @param npc NPC a ser adicionado
   */
  public void addNpc(Npc npc) {
    if (npcs != null) {
      npcs.add(npc);
    }
  }

    /**
     * Executa um teleporte rápido usando o ID do quickTeleport.
     * @param quickTeleportId ID do teleporte rápido
     * @return true se o teleporte foi executado com sucesso
     */
    public boolean executeQuickTeleport(String quickTeleportId) {
      try {
        if (teleportManager.hasQuickTeleport(quickTeleportId)) {
          String teleportString = teleportManager.getQuickTeleport(quickTeleportId);
          executeTeleportString(teleportString, "Teleporte rápido executado!");
          return true;
        } else {
          System.err.println("Teleporte rápido '" + quickTeleportId + "' não encontrado!");
          return false;
        }
      } catch (Exception e) {
        System.err.println("Erro ao executar teleporte rápido: " + e.getMessage());
        e.printStackTrace();
        return false;
      }
    }

    /**
     * Executa uma string de teleporte no formato "mapa,x,y" ou "x,y".
     * @param teleportString String de teleporte
     * @param message Mensagem a ser exibida
     */
    public void executeTeleportString(String teleportString, String message) {
      try {
        String[] parts = teleportString.split(",");

        if (parts.length == 2) {
          // Teleporte no mapa atual: "x,y"
          int x = Integer.parseInt(parts[0].trim());
          int y = Integer.parseInt(parts[1].trim());
          player.setWorldX(x);
          player.setWorldY(y);
          gameUI.addMessage(message, null, 3000L);

        } else if (parts.length == 3) {
          // Teleporte para outro mapa: "mapa,x,y"
          String mapName = parts[0].trim();
          int x = Integer.parseInt(parts[1].trim());
          int y = Integer.parseInt(parts[2].trim());

          // Carregar novo mapa
          loadMap(mapName);

          // Posicionar jogador na nova posição
          player.setWorldX(x);
          player.setWorldY(y);

          gameUI.addMessage(message, null, 3000L);

        } else {
          System.err.println("Formato de teleporte inválido: " + teleportString);
        }

      } catch (NumberFormatException e) {
        System.err.println("Erro ao parsear coordenadas de teleporte: " + teleportString);
      } catch (Exception e) {
        System.err.println("Erro ao executar teleporte: " + e.getMessage());
        e.printStackTrace();
      }
    }

      /**
   * Inicializa componentes do jogo (NPCs e objetos).
   */
  private void initializeGameComponents(Player player) {
    // Inicializar GameUI
    this.gameUI = new GameUI(this);

    // Inicializar InventoryManager
    this.inventoryManager = new InventoryManager(this.playerClassName, player);

    // Inicializar sistema de interação
    initializeInteractionSystem();

    // Inicializar NpcSpriteLoader
    try {
      this.npcSpriteLoader = new NpcSpriteLoader("/npc_sprites.json");
    } catch (Exception e) {
      System.err.println("Erro ao inicializar NpcSpriteLoader: " + e.getMessage());
      this.npcSpriteLoader = null;
    }

    // Carregar NPCs do mapa
    loadNpcsFromMap();

    // Inicializar ObjectManager
    try {
      ObjectSpriteLoader objectSpriteLoader = new ObjectSpriteLoader("/objects.json");
      List<TileManager.MapTile> objectTiles = tileManager.getObjectTiles();
      this.objectManager = new ObjectManager(this, objectSpriteLoader, objectTiles);
    } catch (Exception e) {
      System.err.println("Erro ao inicializar ObjectManager: " + e.getMessage());
      e.printStackTrace();
      this.objectManager = null;
    }
  }

  /**
   * Inicializa o sistema de interação com teclas
   */
  private void initializeInteractionSystem() {
    try {
      // Inicializar o sistema de interação com imagens GIF separadas
      this.keyboardMapper = new KeyboardMapper();
      this.interactionManager = new InteractionManager(keyboardMapper);
      this.simpleInteractionManager = new SimpleInteractionManager();
    } catch (Exception e) {
      System.err.println("Erro ao inicializar sistema de interação: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Carrega NPCs das layers do mapa.
   */
  private void loadNpcsFromMap() {
    try {
      // Garantir que a lista de NPCs está inicializada
      if (this.npcs == null) {
        this.npcs = new ArrayList<>();
      } else {
        // Limpar lista de NPCs existente
        this.npcs.clear();
      }

      List<TileManager.MapTile> npcTiles = tileManager.getNpcTiles();
      if (npcTiles != null && !npcTiles.isEmpty()) {
        this.npcs = NpcFactory.loadNpcsFromTiles(npcTiles, tileSize, getPlayerSize());
      } else {
        // Se não há NPCs no mapa, garantir que a lista está vazia
        this.npcs = new ArrayList<>();
      }
    } catch (Exception e) {
      System.err.println("Erro ao carregar NPCs: " + e.getMessage());
      e.printStackTrace();
      // Em caso de erro, garantir que a lista está vazia
      this.npcs = new ArrayList<>();
    }
  }

  /**
   * Verifica teleportes automáticos usando apenas TeleportManager.
   */
  private void checkAutomaticTeleports() {
    List<MapTile> teleportTiles = tileManager.getTeleportTiles();
    for (MapTile teleportTile : teleportTiles) {
      // Verificar apenas teleportes automáticos (não interativos) configurados no TeleportManager
      if ((teleportTile.interactive == null || !teleportTile.interactive) &&
          teleportTile.id != null) {

        String teleportId = teleportTile.id;

        // Verificar se o ID contém dois pontos (formato: "teleportId:spawnPoint")
        if (teleportTile.id.contains(":")) {
          String[] parts = teleportTile.id.split(":", 2);
          teleportId = parts[0].trim();
        }

        // Verificar se há configuração de teleporte no TeleportManager
        if (teleportManager.hasTeleport(teleportId)) {
          int teleportWorldX = teleportTile.x * tileSize;
          int teleportWorldY = teleportTile.y * tileSize;

          if (isPlayerCollidingWithTeleport(player, teleportWorldX, teleportWorldY)) {
            performTeleport(teleportTile);
            return; // Sair após o primeiro teleporte
          }
        }
      }
    }
  }

  /**
   * Verifica se o jogador está colidindo com um teleporte.
   */
  private boolean isPlayerCollidingWithTeleport(Player player, int teleportWorldX, int teleportWorldY) {
    // Obter a posição e tamanho do jogador
    int playerX = player.getWorldX();
    int playerY = player.getWorldY();
    int playerSize = player.getPlayerSize();

    // Calcular o centro do jogador
    int playerCenterX = playerX + (playerSize / 2);
    int playerCenterY = playerY + (playerSize / 2);

    // Calcular o centro do teleporte
    int teleportCenterX = teleportWorldX + (tileSize / 2);
    int teleportCenterY = teleportWorldY + (tileSize / 2);

    // Calcular a distância entre os centros
    double distance = Math.sqrt(
        Math.pow(playerCenterX - teleportCenterX, 2) +
        Math.pow(playerCenterY - teleportCenterY, 2)
    );

    // Considerar colisão se a distância for menor que metade do tamanho do tile
    // Isso torna a detecção mais permissiva
    boolean isColliding = distance <= (tileSize / 2);

    return isColliding;
  }

  /**
   * Executa teleporte interativo usando apenas TeleportManager.
   */
  private void performInteractiveTeleport(MapTile teleportTile) {
    try {
      if (teleportTile.id != null) {
        String teleportId = teleportTile.id;
        String spawnPoint = null;

        // Verificar se o ID contém dois pontos (formato: "teleportId:spawnPoint")
        if (teleportTile.id.contains(":")) {
          String[] parts = teleportTile.id.split(":", 2);
          teleportId = parts[0].trim();
          spawnPoint = parts[1].trim();
        }

        // Verificar se há configuração de teleporte no TeleportManager
        if (teleportManager.hasTeleport(teleportId)) {
          TeleportManager.TeleportConfig config = teleportManager.getTeleport(teleportId);

          // Se há múltiplos pontos de spawn e não foi especificado um, mostrar opções
          if (config.spawnPoints.size() > 1 && spawnPoint == null) {
            showTeleportOptions(config);
          } else {
            // Teleporte direto para o ponto especificado ou único ponto disponível
            performTeleportToConfig(config, spawnPoint);
          }

        } else {
          System.err.println("Teleporte interativo inválido: ID '" + teleportId + "' não encontrado no TeleportManager");
        }
      }

    } catch (Exception e) {
      System.err.println("Erro ao executar teleporte interativo: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Mostra opções de teleporte quando há múltiplos pontos de spawn.
   */
  private void showTeleportOptions(TeleportManager.TeleportConfig config) {
    StringBuilder message = new StringBuilder();
    message.append("Escolha o destino em ").append(config.name).append(":\n");

    int index = 1;
    for (String spawnPoint : config.spawnPoints.keySet()) {
      message.append(index).append(". ").append(spawnPoint).append("\n");
      index++;
    }

    gameUI.addMessage(message.toString(), null, 5000L);

    // Por enquanto, usar o primeiro ponto de spawn
    performTeleportToConfig(config, config.spawnPoints.keySet().iterator().next());
  }

  /**
   * Executa teleporte para uma configuração específica.
   */
  private void performTeleportToConfig(TeleportManager.TeleportConfig config, String spawnPoint) {
    try {
      // Carregar novo mapa
      loadMap(config.map);

      int[] coordinates;
      if (spawnPoint != null) {
        coordinates = config.getSpawnPoint(spawnPoint);
      } else {
        coordinates = config.getFirstSpawnPoint();
      }

      if (coordinates != null && coordinates.length == 2) {
        // Converter coordenadas de tile para pixels
        int pixelX = coordinates[0] * tileSize;
        int pixelY = coordinates[1] * tileSize;
        player.setWorldX(pixelX);
        player.setWorldY(pixelY);
        gameUI.addMessage("Você foi teleportado para " + config.name + "!", null, 3000L);
      } else {
        System.err.println("Coordenadas de spawn inválidas para " + config.name);
      }

    } catch (Exception e) {
      System.err.println("Erro ao executar teleporte para configuração: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Executa o teleporte do jogador usando apenas TeleportManager.
   */
  private void performTeleport(MapTile teleportTile) {
    try {
      System.out.println("=== PERFORM TELEPORT DEBUG ===");
      System.out.println("Tile ID: " + teleportTile.id);
      System.out.println("Tile X: " + teleportTile.x + ", Y: " + teleportTile.y);

      if (teleportTile.id != null) {
        String teleportId = teleportTile.id;
        String spawnPoint = null;

        // Verificar se o ID contém dois pontos (formato: "teleportId:spawnPoint")
        if (teleportTile.id.contains(":")) {
          String[] parts = teleportTile.id.split(":", 2);
          teleportId = parts[0].trim();
          spawnPoint = parts[1].trim();
        }

        // Verificar se há configuração de teleporte no TeleportManager
        if (teleportManager.hasTeleport(teleportId)) {
          TeleportManager.TeleportConfig config = teleportManager.getTeleport(teleportId);

          // Carregar novo mapa
          loadMap(config.map);

          // Usar ponto de spawn específico ou primeiro disponível
          int[] spawnCoords;
          if (spawnPoint != null) {
            spawnCoords = config.getSpawnPoint(spawnPoint);
            if (spawnCoords == null) {
              System.err.println("Ponto de spawn '" + spawnPoint + "' não encontrado para " + config.name);
              gameUI.addMessage("Ponto de spawn não encontrado!", null, 3000L);
              return;
            }
          } else {
            spawnCoords = config.getFirstSpawnPoint();
          }

          if (spawnCoords != null && spawnCoords.length == 2) {
            // Converter coordenadas de tile para pixels
            int pixelX = spawnCoords[0] * tileSize;
            int pixelY = spawnCoords[1] * tileSize;
            player.setWorldX(pixelX);
            player.setWorldY(pixelY);
            gameUI.addMessage("Você foi teleportado para " + config.name + "!", null, 3000L);
          } else {
            System.err.println("Nenhum ponto de spawn válido encontrado para " + config.name);
            gameUI.addMessage("Erro: pontos de spawn inválidos!", null, 3000L);
          }

        } else {
          System.err.println("Teleporte inválido: ID '" + teleportId + "' não encontrado no TeleportManager");
          gameUI.addMessage("Teleporte não configurado!", null, 3000L);
        }
      }

    } catch (Exception e) {
      System.err.println("Erro ao executar teleporte: " + e.getMessage());
      e.printStackTrace();
      gameUI.addMessage("Erro no teleporte!", null, 3000L);
    }
  }

  /**
   * Carrega um novo mapa.
   */
  public void loadMap(String mapName) {
    try {
      String mapPath = "/maps/" + mapName + ".json";
      tileManager.loadMapJson(mapPath);

      // Recarregar NPCs e objetos do novo mapa
      loadNpcsFromMap();

      if (objectManager != null) {
        ObjectSpriteLoader objectSpriteLoader = new ObjectSpriteLoader("/objects.json");
        List<TileManager.MapTile> objectTiles = tileManager.getObjectTiles();
        this.objectManager = new ObjectManager(this, objectSpriteLoader, objectTiles);
      }

      // Notificar QuestManager sobre mudança de mapa
      if (questManager != null) {
        questManager.onPlayerEnterMap(mapName);
      }

      // Atualizar contexto de áudio baseado no novo mapa
      updateAudioContextForMap(mapName);

    } catch (Exception e) {
      System.err.println("Erro ao carregar mapa: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Atualiza o contexto de áudio baseado no mapa atual.
   */
  private void updateAudioContextForMap(String mapName) {
    System.out.println("=== AUDIO DEBUG ===");
    System.out.println("Current map name: " + mapName);
    System.out.println("Previous map name: " + currentMapName);

    if (audioManager != null && !mapName.equals(currentMapName)) {
      currentMapName = mapName;
      AudioContext newContext = AudioContext.fromMapName(mapName);

      System.out.println("New audio context: " + newContext);
      System.out.println("Context display name: " + newContext.getDisplayName());

      audioManager.changeContext(newContext);

      // Reproduzir efeito sonoro de mudança de mapa
      audioManager.playSoundEffect("teleport");

      System.out.println("Audio context changed successfully");
    } else if (audioManager == null) {
      System.out.println("ERROR: AudioManager is null!");
    } else {
      System.out.println("Map name unchanged, no audio context change needed");
    }
    System.out.println("=== END AUDIO DEBUG ===");
  }
}
