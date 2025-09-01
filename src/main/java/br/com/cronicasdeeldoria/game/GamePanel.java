package br.com.cronicasdeeldoria.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.character.races.Archer;
import br.com.cronicasdeeldoria.entity.character.races.Breton;
import br.com.cronicasdeeldoria.entity.character.races.Dwarf;
import br.com.cronicasdeeldoria.entity.character.races.Mage;
import br.com.cronicasdeeldoria.entity.character.races.Orc;
import br.com.cronicasdeeldoria.entity.character.races.Race;
import br.com.cronicasdeeldoria.entity.object.MapObject;
import br.com.cronicasdeeldoria.entity.object.ObjectManager;
import br.com.cronicasdeeldoria.entity.object.ObjectSpriteLoader;
import br.com.cronicasdeeldoria.game.ui.GameUI;
import br.com.cronicasdeeldoria.entity.character.npc.Npc;
import br.com.cronicasdeeldoria.entity.character.npc.NpcFactory;
import br.com.cronicasdeeldoria.entity.character.npc.NpcSpriteLoader;
import br.com.cronicasdeeldoria.tile.TileManager;
import br.com.cronicasdeeldoria.config.CharacterConfigLoader;
import java.util.List;
import java.util.ArrayList;

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
  private br.com.cronicasdeeldoria.entity.character.npc.NpcSpriteLoader npcSpriteLoader;
  private br.com.cronicasdeeldoria.entity.object.ObjectManager objectManager;
  private br.com.cronicasdeeldoria.game.ui.GameUI gameUI;

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
    int mana = configLoader.getIntAttribute(raceName, "mana", 100);
    int strength = configLoader.getIntAttribute(raceName, "strength", 10);
    int agility = configLoader.getIntAttribute(raceName, "agility", 10);
    int luck = configLoader.getIntAttribute(raceName, "luck", 0);

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
      case "dwarf":
        raceInstance = new Dwarf(special);
        break;
      case "mage":
        raceInstance = new Mage(special);
        break;
      case "orc":
        raceInstance = new Orc(special);
        break;
      case "breton":
        raceInstance = new Breton(special);
        break;
      case "archer":
        raceInstance = new Archer(special);
        break;
      default:
        raceInstance = race;
    }
            player = new Player(this, keyHandler, raceInstance, x, y, speed, direction, playerName, health, mana, strength, agility, luck);
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

        player.update();

        // Atualizar NPCs
        for (Npc npc : npcs) {
            npc.update(this, player);
        }

        // Atualizar objetos
        if (objectManager != null) {
            objectManager.updateActiveObjects(player.getWorldX() / tileSize, player.getWorldY() / tileSize);
        }

        // Verificar se o GamePanel perdeu o foco e restaurá-lo
        if (!this.hasFocus()) {
            this.requestFocusInWindow();
        }
    }

    /**
     * Verifica se o jogador está próximo de uma entidade (NPC ou objeto).
     */
    private boolean isPlayerNearEntity(Player player, int entityX, int entityY) {
        int playerTileX = player.getWorldX() / tileSize;
        int playerTileY = player.getWorldY() / tileSize;
        int entityTileX = entityX / tileSize;
        int entityTileY = entityY / tileSize;

        int distanceX = Math.abs(playerTileX - entityTileX);
        int distanceY = Math.abs(playerTileY - entityTileY);

        return distanceX <= 2 && distanceY <= 2;
    }

    /**
     * Verifica e processa interações do jogador com NPCs e objetos.
     */
    public void checkInteraction() {
        // Verificar interação com NPCs primeiro
        for (Npc npc : npcs) {
            if (isPlayerNearEntity(player, npc.getWorldX(), npc.getWorldY())) {
                System.out.println("INTERAÇÃO COM NPC: " + npc.getName());
                npc.interact();
                return;
            }
        }

        // Verificar interação com objetos
        if (objectManager != null) {
            for (MapObject obj : objectManager.getActiveObjects()) {
                if (isPlayerNearEntity(player, obj.getWorldX(), obj.getWorldY())) {
                    System.out.println("INTERAÇÃO COM OBJETO: " + obj.getName() + " (" + obj.getObjectId() + ")");
                    obj.interact(player);
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

        // Renderizar o mapa (layers de fundo, player e overlay)
        tileManager.draw(graphics2D);

        // Renderizar objetos
        if (objectManager != null) {
            objectManager.drawObjects(graphics2D);
        }

        // Renderizar NPCs
        for (Npc npc : npcs) {
            npc.draw(graphics2D, npcSpriteLoader, tileSize, player, player.getScreenX(), player.getScreenY());
        }

        // Renderizar interface do usuário
        gameUI.draw(graphics2D);

        graphics2D.dispose();
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

      /**
   * Inicializa componentes do jogo (NPCs e objetos).
   */
  private void initializeGameComponents() {
    // Inicializar GameUI
    this.gameUI = new GameUI(this);

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
   * Carrega NPCs das layers do mapa.
   */
  private void loadNpcsFromMap() {
    try {
      List<TileManager.MapTile> npcTiles = tileManager.getNpcTiles();
      if (npcTiles != null && !npcTiles.isEmpty()) {
        this.npcs = NpcFactory.loadNpcsFromTiles(npcTiles, tileSize, getPlayerSize());
      }
    } catch (Exception e) {
      System.err.println("Erro ao carregar NPCs: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
