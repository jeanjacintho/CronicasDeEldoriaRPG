package br.com.cronicasdeeldoria.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

import br.com.cronicasdeeldoria.entity.character.npc.SkeletonMonster;
import br.com.cronicasdeeldoria.entity.character.npc.WolfMonster;
import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.character.races.*;
import br.com.cronicasdeeldoria.entity.character.races.Barbarian;
import br.com.cronicasdeeldoria.entity.object.MapObject;
import br.com.cronicasdeeldoria.entity.object.ObjectManager;
import br.com.cronicasdeeldoria.entity.object.ObjectSpriteLoader;
import br.com.cronicasdeeldoria.game.ui.GameUI;
import br.com.cronicasdeeldoria.game.ui.KeyboardMapper;
import br.com.cronicasdeeldoria.game.ui.InteractionManager;
import br.com.cronicasdeeldoria.entity.character.npc.Npc;
import br.com.cronicasdeeldoria.entity.character.npc.NpcFactory;
import br.com.cronicasdeeldoria.entity.character.npc.NpcSpriteLoader;
import br.com.cronicasdeeldoria.tile.TileManager;
import br.com.cronicasdeeldoria.tile.TileManager.MapTile;
import br.com.cronicasdeeldoria.config.CharacterConfigLoader;
import java.util.List;
import java.util.ArrayList;

import br.com.cronicasdeeldoria.entity.Entity;
import br.com.cronicasdeeldoria.game.inventory.InventoryManager;
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
  private InventoryManager inventoryManager;

  // Sistema de batalha
  public int gameState;
  public final int playState = 1;
  public final int battleState = 2;
  public final int inventoryState = 3;
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
   * @param race Raça do jogador.
   * @param tileSize Tamanho do tile.
   * @param maxScreenRow Máximo de linhas na tela.
   * @param maxScreenCol Máximo de colunas na tela.
   */
  public GamePanel(int screenWidth, int screenHeight, String playerName, Race race, int tileSize, int maxScreenRow, int maxScreenCol) {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.maxScreenRow = maxScreenRow;
    this.maxScreenCol = maxScreenCol;
    this.setBackground(Color.BLACK);
    this.tileSize = tileSize;
    this.setDoubleBuffered(true);
    this.addKeyListener(keyHandler);
    this.setFocusable(true);

    this.battle = new Battle(this);
    gameState = playState;

    CharacterConfigLoader configLoader = CharacterConfigLoader.getInstance();
    String raceName = race.getRaceName().toLowerCase();
    int playerSize = getPlayerSize();

    this.tileManager = new TileManager(this);
    this.maxWorldCol = tileManager.getMapWidth();
    this.maxWorldRow = tileManager.getMapHeight();

    initializeGameComponents();

    int x = (maxWorldCol * tileSize) / 2 - (playerSize / 2);
    int y = (maxWorldRow * tileSize) / 2 - (playerSize / 2);
    int speed = configLoader.getIntAttribute(raceName, "speed", 4);
    String direction = configLoader.getStringAttribute(raceName, "direction", "down");
    int health = configLoader.getIntAttribute(raceName, "health", 100);
    int maxHealth = health;
    int mana = configLoader.getIntAttribute(raceName, "mana", 100);
    int maxMana = mana;
    int strength = configLoader.getIntAttribute(raceName, "strength", 10);
    int agility = configLoader.getIntAttribute(raceName, "agility", 10);
    int luck = configLoader.getIntAttribute(raceName, "luck", 0);
    int armor = configLoader.getIntAttribute(raceName, "armor", 0);

    int special = 0;
    switch (raceName) {
      case "dwarf":
        special = configLoader.getIntAttribute(raceName, "endurance", 0);
        break;
      case "mage":
        special = configLoader.getIntAttribute(raceName, "magicPower", 0);
        break;
      case "orc":
        special = configLoader.getIntAttribute(raceName, "rage", 0);
        break;
      case "breton":
        special = configLoader.getIntAttribute(raceName, "willpower", 0);
        break;
      case "archer":
        special = configLoader.getIntAttribute(raceName, "dexterity", 0);
        break;
      default:
        special = 0;
    }
    Race raceInstance;
    switch (raceName) {
      case "dwarf": raceInstance = new Paladin(special); break;
      case "mage": raceInstance = new Mage(special); break;
      case "orc": raceInstance = new Orc(special); break;
      case "breton": raceInstance = new Barbarian(special); break;
      case "archer": raceInstance = new Ranger(special); break;
      default: raceInstance = race;
    }
    player = new Player(this, keyHandler, raceInstance, x, y, speed, direction, playerName, health, maxHealth, mana, maxMana, strength, agility, luck, armor);
  }

  public void setupGame() {
    // Garantir que começa no estado de jogo
    gameState = playState;
  }

  /**
   * Inicia a thread principal do jogo.
   */
  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
    this.requestFocusInWindow(); // Garantir que o GamePanel tenha foco
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
      }

      if (gameState == battleState) {
        updateBattle();
      }

      if (gameState == inventoryState) {
        updateInventory();
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

      System.out.println("Entered battle state with " + monster.getName());
    } else {
      System.out.println("Cannot start battle: target is not a monster");
    }
  }

  public void endBattle(boolean playerWon) {
    if (playerWon) {
      System.out.println("Victory! You defeated " + battleMonster.getName());

      int xpReward = 0;
      if (battleMonster instanceof WolfMonster ) {
        xpReward = ((WolfMonster) battleMonster).getXpReward();
      } else if (battleMonster instanceof SkeletonMonster) {
        xpReward = ((SkeletonMonster) battleMonster).getXpReward();
      }
      player.gainXp(xpReward);

      // Remover monstro derrotado do mapa
      removeMonsterFromMap(battleMonster);
    } else {
      System.out.println("Defeat! You were defeated by " + battleMonster.getName());
      // Aplicar penalidade se necessário
      // player.applyDeathPenalty(); // se você tiver este metodo
    }

    // Limpar estado de batalha
    battle.endBattle();
    gameState = playState;
    battleMonster = null;
    lastBattleEndTime = System.currentTimeMillis();
  }

  /**
    /**
     * Atualiza os pontos de interação baseado na proximidade do jogador
     */
    private void updateInteractionPoints() {
        if (interactionManager == null) return;

        interactionManager.clearInteractionPoints();

        // Não processar interações se estivermos em batalha
        if (gameState == battleState) return;

        // Verificar cooldown de batalha para evitar re-engajamento imediato
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBattleEndTime < BATTLE_COOLDOWN) return;

        // Verificar interação com NPCs apenas se houver NPCs no mapa
        if (npcs != null && !npcs.isEmpty()) {
          for (Npc npc : npcs) {
            // Verificar se é um monstro (usar distância de 5 tiles)
            if (npc instanceof WolfMonster) {
                if (isPlayerNearMonster(player, npc.getWorldX(), npc.getWorldY()) && npc.isInteractive()) {

                    // Verificar auto-interação
                    if (npc.isAutoInteraction()) {
                        startBattle(npc);
                        npc.interact();
                    } else {
                        // Usar coordenadas de mundo diretamente
                        interactionManager.addInteractionPoint(npc.getWorldX(), npc.getWorldY(), "E", "monster");
                    }
                }
            } else {
                // NPCs normais (usar distância de 2 tiles)
                if (isPlayerNearNpc(player, npc.getWorldX(), npc.getWorldY()) && npc.isInteractive()) {

                    // Verificar auto-interação
                    if (npc.isAutoInteraction()) {
                        System.out.println("AUTO-INTERAÇÃO com NPC: " + npc.getName());
                        npc.interact();
                    } else {
                        // Usar coordenadas de mundo diretamente
                        interactionManager.addInteractionPoint(npc.getWorldX(), npc.getWorldY(), "E", "npc");
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
                        if (isPlayerNearObject(player, obj.getWorldX(), obj.getWorldY())) {
                            interactionManager.addInteractionPoint(obj.getWorldX(), obj.getWorldY(), "E", "object");
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
                    interactionManager.addInteractionPoint(teleportWorldX, teleportWorldY, "E", "teleport");
                }
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

        return distanceX <= maxDistance && distanceY <= maxDistance;
    }

    /**
     * Verifica se o jogador está próximo de um NPC (2 tiles).
     */
    private boolean isPlayerNearNpc(Player player, int entityX, int entityY) {
        return isPlayerNearEntity(player, entityX, entityY, 2);
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
                    System.out.println("INTERAÇÃO COM NPC: " + npc.getName());
                    npc.interact();
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
                        System.out.println("INTERAÇÃO COM OBJETO: " + obj.getName() + " (" + obj.getObjectId() + ")");
                        obj.interact(player);
                        return;
                    }
                }
            }
        }

        // Verificar interação com teleportes
        List<MapTile> teleportTiles = tileManager.getTeleportTiles();
        for (MapTile teleportTile : teleportTiles) {
            int teleportWorldX = teleportTile.x * tileSize;
            int teleportWorldY = teleportTile.y * tileSize;

            if (teleportTile.interactive) {
                // Teleporte interativo - verificar proximidade
                if (isPlayerNearObject(player, teleportWorldX, teleportWorldY)) {
                    System.out.println("INTERAÇÃO COM TELEPORTE: " + teleportTile.id);
                    performTeleport(teleportTile);
                    return;
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
              if (interactionManager != null) {
                  interactionManager.renderInteractionKeyForEntity(graphics2D,
                      npc.getWorldX(), npc.getWorldY(),
                      npc.getWorldX() - player.getWorldX() + player.getScreenX(),
                      npc.getWorldY() - player.getWorldY() + player.getScreenY(),
                      "npc", tileSize);
              }
          }
        }

        // Renderizar teclas de interação para objetos
        if (objectManager != null && interactionManager != null) {
            for (MapObject obj : objectManager.getActiveObjects()) {
                if (obj.isActive()) {
                    interactionManager.renderInteractionKeyForEntity(graphics2D,
                        obj.getWorldX(), obj.getWorldY(),
                        obj.getWorldX() - player.getWorldX() + player.getScreenX(),
                        obj.getWorldY() - player.getWorldY() + player.getScreenY(),
                        "object", tileSize);
                }
            }
        }

        // Renderizar teclas de interação para teleportes
        if (interactionManager != null) {
            List<MapTile> teleportTiles = tileManager.getTeleportTiles();
            for (MapTile teleportTile : teleportTiles) {
                if (teleportTile.interactive != null && teleportTile.interactive) {
                    int teleportWorldX = teleportTile.x * tileSize;
                    int teleportWorldY = teleportTile.y * tileSize;

                    // Usar o método específico para tiles que centraliza a tecla
                    interactionManager.renderInteractionKeyForTile(graphics2D,
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
        // Desenhar interface do inventário
        gameUI.drawInventoryUI(graphics2D, inventoryManager);
      }
      graphics2D.dispose();
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

  // Batalha por turnos
  private void updateBattle() {
    if (!battle.isInBattle()) return;

    // Processar apenas entrada do jogador quando for sua vez
    if (battle.isWaitingForPlayerInput()) {
      if (keyHandler.attackPressed) {
        battle.processPlayerAction("ATTACK");
        keyHandler.attackPressed = false;
      } else if (keyHandler.defendPressed) {
        battle.processPlayerAction("DEFEND");
        keyHandler.defendPressed = false;
      } else if (keyHandler.escapePressed) {
        battle.processPlayerAction("FLEE");
        keyHandler.escapePressed = false;
      } else if (keyHandler.specialPressed) {
        battle.processPlayerAction("SPECIAL");
        keyHandler.specialPressed = false;
      } else if (keyHandler.healthPressed) {
        battle.processPlayerAction("HEALTH");
        keyHandler.healthPressed = false;
      } else if (keyHandler.manaPressed) {
        battle.processPlayerAction("MANA");
        keyHandler.manaPressed = false;
      }
    }

    // A lógica do monstro é processada automaticamente dentro da Battle class
  }

  // Metodo para remover monstro derrotado do mapa
  private void removeMonsterFromMap(Npc monster) {
    npcs.remove(monster);
    //System.out.println("Removed " + monster.getName() + " from the map");
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

    public NpcSpriteLoader getNpcSpriteLoader() {
      return npcSpriteLoader;
    }

    public InventoryManager getInventoryManager() {
      return inventoryManager;
    }

      /**
   * Inicializa componentes do jogo (NPCs e objetos).
   */
  private void initializeGameComponents() {
    // Inicializar GameUI
    this.gameUI = new GameUI(this);

    // Inicializar InventoryManager
    this.inventoryManager = new InventoryManager();

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
   * Verifica teleportes automáticos independentemente do sistema de interação.
   */
  private void checkAutomaticTeleports() {
    List<MapTile> teleportTiles = tileManager.getTeleportTiles();
    for (MapTile teleportTile : teleportTiles) {
      // Verificar apenas teleportes automáticos (não interativos)
      if (teleportTile.interactive == null || !teleportTile.interactive) {
        int teleportWorldX = teleportTile.x * tileSize;
        int teleportWorldY = teleportTile.y * tileSize;

        if (isPlayerCollidingWithTeleport(player, teleportWorldX, teleportWorldY)) {
          System.out.println("TELEPORTE AUTOMÁTICO ativado!");
          performTeleport(teleportTile);
          return; // Sair após o primeiro teleporte
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
   * Executa o teleporte do jogador.
   */
  private void performTeleport(MapTile teleportTile) {
    if (teleportTile.toMap != null && !teleportTile.toMap.isEmpty()) {
      System.out.println("Teleportando para: " + teleportTile.toMap + " na posição (" + teleportTile.toX + ", " + teleportTile.toY + ")");

      // Carregar novo mapa
      loadMap(teleportTile.toMap);

      // Posicionar jogador na nova posição
      player.setWorldX(teleportTile.toX * tileSize);
      player.setWorldY(teleportTile.toY * tileSize);
    }
  }

  /**
   * Carrega um novo mapa.
   */
  private void loadMap(String mapName) {
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

      System.out.println("Mapa carregado: " + mapName);
    } catch (Exception e) {
      System.err.println("Erro ao carregar mapa: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
