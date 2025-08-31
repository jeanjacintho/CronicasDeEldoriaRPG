package br.com.cronicasdeeldoria.entity.character.player;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.KeyHandler;
import br.com.cronicasdeeldoria.entity.character.races.Race;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 * Representa o jogador controlado pelo usuário no jogo.
 */
public class Player extends Character {
  private int luck;
  private int totalXp = 0;
  private int currentLevel = 1;
  private LevelManager levelManager;
  GamePanel gamePanel;
  KeyHandler keyHandler;
  private int spriteCounter = 0;
  private int spriteNum = 1;
  private boolean isMoving = false;

  private final int screenX;
  private final int screenY;

  /**
   * Cria um novo jogador.
   * @param gamePanel Painel do jogo.
   * @param keyHandler Handler de teclas.
   * @param race Raça do jogador.
   * @param x Posição X.
   * @param y Posição Y.
   * @param speed Velocidade do jogador.
   * @param direction Direção inicial.
   * @param name Nome do jogador.
   * @param health Vida inicial.
   * @param mana Mana inicial.
   * @param strength Força inicial.
   * @param agility Agilidade inicial.
   * @param luck Sorte inicial.
   */
  public Player(GamePanel gamePanel, KeyHandler keyHandler, Race race, int x, int y, int speed, String direction, String name, int health, int mana, int strength, int agility, int luck) {
    super(x, y, speed, direction, name, race, health, mana, strength, agility);
    this.luck = luck;
    this.gamePanel = gamePanel;
    this.keyHandler = keyHandler;
    this.levelManager = LevelManager.getInstance();
    this.getPlayerImage();

    screenX = (gamePanel.getScreenWidth() - gamePanel.getPlayerSize()) / 2;
    screenY = (gamePanel.getScreenHeight() - gamePanel.getPlayerSize()) / 2;

    int playerSize = gamePanel.getPlayerSize();
    int hitboxWidth = 32;
    int hitboxHeight = 36;
    int hitboxX = (playerSize - hitboxWidth) / 2;
    int hitboxY = playerSize / 2;
    setHitbox(new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight));
  }

  /**
   * Carrega as imagens do jogador de acordo com a raça.
   */
  public void getPlayerImage() {
    try {
      String classFolder = getRace().getRaceName().toLowerCase();
      this.up = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_back.png"));
      this.down = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_front.png"));
      this.left = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_left.png"));
      this.right = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_right.png"));
      
      this.up1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_back_walk1.png"));
      this.up2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_back_walk2.png"));
      this.down1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_front_walk1.png"));
      this.down2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_front_walk2.png"));
      this.left1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_left_walk1.png"));
      this.left2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_left_walk2.png"));
      this.right1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_right_walk1.png"));
      this.right2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/" + classFolder + "/" + classFolder + "_right_walk2.png"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Atualiza o estado do jogador (movimentação, colisão, animação).
   */
  public void update() {
    if(keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed) {
      isMoving = true;
      if(keyHandler.upPressed) {
        setDirection("up");
      } else if(keyHandler.downPressed) {
        setDirection("down");
      } else if(keyHandler.leftPressed) {
        setDirection("left");
      } else if(keyHandler.rightPressed) {
        setDirection("right");
      }

      setCollisionOn(false);
      gamePanel.getColisionChecker().checkTile(this);
      gamePanel.getColisionChecker().checkEntity(this, gamePanel.getNpcs());
      gamePanel.getColisionChecker().checkObject(this);

      if(isCollisionOn() == false) {
        switch (getDirection()) {
          case "up":
            setWorldY(getWorldY() - getSpeed());
            break;
            case "down":
            setWorldY(getWorldY() + getSpeed());
            break;
          case "left":
            setWorldX(getWorldX() - getSpeed());
            break;
          case "right":
            setWorldX(getWorldX() + getSpeed());
            break;
        }
      }

      spriteCounter++;

      if(spriteCounter > 15 - getSpeed()) {
        spriteNum = (spriteNum == 1) ? 2 : 1;
        spriteCounter = 0;
      }
    } else {
      isMoving = false;
      spriteNum = 1;
      spriteCounter = 0;
    }
    
    // Verificar interação com tecla E
    if (keyHandler.actionPressed) {
      gamePanel.checkInteraction();
      keyHandler.actionPressed = false;
    }
    
    // Teste do sistema de XP (tecla X)
    if (keyHandler.xPressed) {
      gainXp(50);
      keyHandler.xPressed = false;
    }
    
    // Teste do sistema de XP (tecla Z)
    if (keyHandler.zPressed) {
      gainXp(100);
      keyHandler.zPressed = false;
    }
  }

  /**
   * Desenha o jogador na tela.
   * @param graphics2d Contexto gráfico.
   */
  public void draw(Graphics2D graphics2d) {
    BufferedImage image = null;

    switch(getDirection()) {
      case "up":
        if (isMoving) {
            image = (spriteNum == 1) ? up1 : up2;
        } else {
            image = up;
        }
        break;
      case "down":
        if (isMoving) {
            image = (spriteNum == 1) ? down1 : down2;
        } else {
            image = down;
        }
        break;
      case "left":
        if (isMoving) {
            image = (spriteNum == 1) ? left1 : left2;
        } else {
            image = left;
        }
        break;
      case "right":
        if (isMoving) {
            image = (spriteNum == 1) ? right1 : right2;
        } else {
            image = right;
        }
        break;
    }

    graphics2d.drawImage(image, screenX, screenY, gamePanel.getPlayerSize(), gamePanel.getPlayerSize(), null);
  }
 
  public int getLuck() {
    return luck;
  }

  public void setLuck(int luck) {
    this.luck = luck;
  }

  public int getScreenX() {
    return screenX;
  }

  public int getScreenY() {
    return screenY;
  }

  public int getPlayerSize() {
    return gamePanel.getPlayerSize();
  }

  public GamePanel getGamePanel() {
    return gamePanel;
  }

  /**
   * Adiciona XP ao jogador e verifica se subiu de nível.
   * @param xp Quantidade de XP a ser adicionada.
   */
  public void gainXp(int xp) {
    if (xp <= 0) return;
    
    int oldLevel = currentLevel;
    totalXp += xp;
    
    // Recalcular nível baseado no XP total
    currentLevel = levelManager.calculateLevel(totalXp);
    
    // Se subiu de nível, aplicar bônus
    if (currentLevel > oldLevel) {
      levelUp(oldLevel, currentLevel);
    }
    
    System.out.println("XP ganho: " + xp + " | Total: " + totalXp + " | Nível: " + currentLevel);
  }

  /**
   * Aplica os bônus de atributos ao subir de nível.
   * @param oldLevel Nível anterior.
   * @param newLevel Novo nível.
   */
  private void levelUp(int oldLevel, int newLevel) {
    System.out.println("🎉 NÍVEL UP! " + oldLevel + " → " + newLevel);
    
    // Aplicar bônus de todos os níveis entre oldLevel e newLevel
    for (int level = oldLevel + 1; level <= newLevel; level++) {
      LevelManager.LevelDefinition levelDef = levelManager.getLevelDefinition(level);
      if (levelDef != null) {
        setAttributeHealth(getAttributeLife() + levelDef.healthBonus);
        setAttributeMana(getAttributeMana() + levelDef.manaBonus);
        setAttributeStrength(getAttributeStrength() + levelDef.strengthBonus);
        setAttributeAgility(getAttributeAgility() + levelDef.agilityBonus);
        setLuck(getLuck() + levelDef.luckBonus);
        
        System.out.println("Bônus do nível " + level + ": HP+" + levelDef.healthBonus + 
                          " MP+" + levelDef.manaBonus + " STR+" + levelDef.strengthBonus + 
                          " AGI+" + levelDef.agilityBonus + " LUCK+" + levelDef.luckBonus);
      }
    }
  }

  /**
   * Retorna o XP total do jogador.
   */
  public int getTotalXp() {
    return totalXp;
  }

  /**
   * Retorna o nível atual do jogador.
   */
  public int getCurrentLevel() {
    return currentLevel;
  }

  /**
   * Retorna o XP necessário para o próximo nível.
   */
  public int getXpForNextLevel() {
    return levelManager.getXpForNextLevel(currentLevel);
  }

  /**
   * Retorna o progresso do XP para o próximo nível (0.0 a 1.0).
   */
  public double getXpProgress() {
    return levelManager.getXpProgress(totalXp, currentLevel);
  }

  /**
   * Retorna o XP atual no nível atual.
   */
  public int getCurrentXpInLevel() {
    int xpForCurrent = levelManager.getXpForCurrentLevel(currentLevel);
    return totalXp - xpForCurrent;
  }

  /**
   * Retorna o XP necessário para completar o nível atual.
   */
  public int getXpNeededForLevel() {
    int xpForCurrent = levelManager.getXpForCurrentLevel(currentLevel);
    int xpForNext = levelManager.getXpForNextLevel(currentLevel);
    
    if (xpForNext == -1) {
      return 0; // Nível máximo
    }
    
    return xpForNext - xpForCurrent;
  }
}
