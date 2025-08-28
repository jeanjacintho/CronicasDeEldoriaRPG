package br.com.cronicasdeeldoria.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.character.races.Race;
import br.com.cronicasdeeldoria.config.CharacterConfigLoader;

public class GamePanel extends JPanel implements Runnable{
  private static final int FPS = 60;

  KeyHandler keyHandler = new KeyHandler();
  Thread gameThread;
  Player player;
  private int tileSize;

  public GamePanel(int screenWidth, int screenHeight, String playerName, Race race, int tileSize) {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.BLACK);
    this.tileSize = tileSize;
    this.setDoubleBuffered(true);
    this.addKeyListener(keyHandler);
    this.setFocusable(true);

    CharacterConfigLoader configLoader = CharacterConfigLoader.getInstance();
    String raceName = race.getRaceName().toLowerCase();
    int x = configLoader.getIntAttribute(raceName, "x", screenWidth / 2 - tileSize / 2);
    int y = configLoader.getIntAttribute(raceName, "y", screenHeight / 2 - tileSize / 2);
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
  }

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
        int drawCount = 0;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }
            if(timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        player.update();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        player.draw(graphics2D);
        graphics2D.dispose();
    }

    public int getTileSize() {
      return tileSize;
    }
}
