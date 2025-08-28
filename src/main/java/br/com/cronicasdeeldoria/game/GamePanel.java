package br.com.cronicasdeeldoria.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.character.races.Race;

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

    // Valores iniciais podem ser ajustados conforme necessário
    int luck = 0;
    int x = screenWidth / 2 - tileSize / 2;
    int y = screenHeight / 2 - tileSize / 2;
    int speed = 4;
    String direction = "down";
    int attributeHealth = 100;
    int attributeForce = 100;
    int attributeDefence = 100;
    int attributeStamina = 100;

    player = new Player(this, keyHandler, race, luck, x, y, speed, direction, playerName, attributeHealth, attributeForce, attributeDefence, attributeStamina);
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
