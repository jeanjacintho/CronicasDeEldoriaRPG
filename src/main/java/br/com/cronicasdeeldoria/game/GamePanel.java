package br.com.cronicasdeeldoria.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.character.races.Race;
import br.com.cronicasdeeldoria.tile.TileManager;
import br.com.cronicasdeeldoria.config.CharacterConfigLoader;
import br.com.cronicasdeeldoria.entity.object.ObjectManager;
import br.com.cronicasdeeldoria.game.ui.GameUI;
import br.com.cronicasdeeldoria.entity.character.npc.Npc;
import br.com.cronicasdeeldoria.entity.character.npc.NpcFactory;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import br.com.cronicasdeeldoria.entity.character.npc.NpcSpriteLoader;

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
  private ObjectManager objectManager;
  public GameUI ui = new GameUI(this);
  private List<Npc> npcs = new ArrayList<>();
  private NpcSpriteLoader npcSpriteLoader;

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
        raceInstance = new br.com.cronicasdeeldoria.entity.character.races.Dwarf(special);
        break;
      case "mage":
        raceInstance = new br.com.cronicasdeeldoria.entity.character.races.Mage(special);
        break;
      case "orc":
        raceInstance = new br.com.cronicasdeeldoria.entity.character.races.Orc(special);
        break;
      case "breton":
        raceInstance = new br.com.cronicasdeeldoria.entity.character.races.Breton(special);
        break;
      case "archer":
        raceInstance = new br.com.cronicasdeeldoria.entity.character.races.Archer(special);
        break;
      default:
        raceInstance = race;
    }
    player = new Player(this, keyHandler, raceInstance, x, y, speed, direction, playerName, health, mana, strength, agility, luck);
    objectManager = new ObjectManager(this, tileManager.getObjectDefinitions(), tileManager.getRawObjects());
    try {
        npcs = NpcFactory.loadNpcs("src/main/resources/npcs.json", tileSize, getPlayerSize());
        npcSpriteLoader = new NpcSpriteLoader("src/main/resources/npc_sprites.json");
    } catch (IOException | RuntimeException e) {
        e.printStackTrace();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
  }

  /**
   * Inicia a thread principal do jogo.
   */
  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
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
        for (Npc npc : npcs) {
            npc.update(this, player);
        }
        int playerTileX = player.getWorldX() / getTileSize();
        int playerTileY = player.getWorldY() / getTileSize();
        objectManager.updateActiveObjects(playerTileX, playerTileY);

        if (keyHandler.actionPressed) {
            tryInteractWithNpc();
            for (var obj : new java.util.ArrayList<>(objectManager.getActiveObjects())) {
                if (obj.isActive() && isPlayerNearObject(player, obj, getTileSize())) {
                    obj.interact(player);
                    if (!obj.isActive()) {
                        objectManager.removeRawObject(obj);
                    }
                    break;
                }
            }
            keyHandler.actionPressed = false;
        }
    }

    private boolean isPlayerNearObject(Player player, br.com.cronicasdeeldoria.entity.object.GameObject obj, int tileSize) {
        int px = player.getWorldX();
        int py = player.getWorldY();
        int ox = obj.getWorldX();
        int oy = obj.getWorldY();
        int ow = obj.getWidth() * tileSize;
        int oh = obj.getHeight() * tileSize;

        return (px + player.getPlayerSize() > ox && px < ox + ow &&
                py + player.getPlayerSize() > oy && py < oy + oh);
    }

    /**
     * Renderiza os elementos do jogo.
     * @param graphics Contexto gráfico.
     */
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        tileManager.draw(graphics2D);
        objectManager.drawObjects(graphics2D);
        int playerScreenX = getWidth() / 2 - player.getPlayerSize() / 2;
        int playerScreenY = getHeight() / 2 - player.getPlayerSize() / 2;
        for (Npc npc : npcs) {
            npc.draw(graphics2D, npcSpriteLoader, tileSize, player, playerScreenX, playerScreenY);
        }
        player.draw(graphics2D);
        ui.draw(graphics2D);
        graphics2D.dispose();
    }

    /**
     * Tenta interagir com um NPC próximo ao jogador.
     */
    public void tryInteractWithNpc() {
        for (Npc npc : npcs) {
            if (isPlayerNearNpc(player, npc)) {
                npc.interact();
                break;
            }
        }
    }

    private boolean isPlayerNearNpc(Player player, Npc npc) {
        java.awt.Rectangle playerBox = new java.awt.Rectangle(
            player.getWorldX() + player.getHitbox().x - 16,
            player.getWorldY() + player.getHitbox().y - 16,
            player.getHitbox().width + 32,
            player.getHitbox().height + 32
        );
        java.awt.Rectangle npcBox = new java.awt.Rectangle(
            npc.getWorldX() + npc.getHitbox().x,
            npc.getWorldY() + npc.getHitbox().y,
            npc.getHitbox().width,
            npc.getHitbox().height
        );
        boolean intersects = playerBox.intersects(npcBox);
        
        return intersects;
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
}
