package br.com.cronicasdeeldoria.game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;

import br.com.cronicasdeeldoria.entity.character.npc.Npc;
import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.font.FontManager;
import br.com.cronicasdeeldoria.game.inventory.InventoryManager;
import br.com.cronicasdeeldoria.game.inventory.InventoryUI;

/**
 * Gerencia a interface gráfica do jogo, exibindo mensagens e elementos de UI.
 */
public class GameUI {
  GamePanel gamePanel;
  Font dogicaFont_40;
  Font dogicaFont_16;
  private BufferedImage heartFull;
  private BufferedImage heartThreeQuarters;
  private BufferedImage heartHalf;
  private BufferedImage heartQuarter;
  private BufferedImage heartEmpty;
  private BufferedImage coinIcon;
  private boolean showStatsWindow = false;
  private String centerMessage = "";
  private long centerMessageStartTime = 0;
  private long centerMessageDuration = 3000; // 3 segundos por padrão
  private boolean centerMessageVisible = false;

  private final List<Message> messages = new ArrayList<>();
  private InventoryUI inventoryUI;

  /**
   * Cria uma nova interface de jogo.
   * @param gamePanel Painel principal do jogo.
   */
  public GameUI(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
    dogicaFont_40 = FontManager.getFont(40f);
    dogicaFont_16 = FontManager.getFont(16f);

    loadHeartImages();
    loadCoinIcon();

    // Inicializar InventoryUI
    this.inventoryUI = new InventoryUI(gamePanel);
  }

  /**
   * Alterna a visibilidade da janela de stats.
   */
  public void toggleStatsWindow() {
    showStatsWindow = !showStatsWindow;
  }

  /**
   * Verifica se a janela de stats está visível.
   */
  public boolean isStatsWindowVisible() {
    return showStatsWindow;
  }

  /**
   * Exibe uma mensagem no centro da tela por um tempo determinado.
   * @param message Mensagem a ser exibida.
   * @param duration Duração em milissegundos (opcional, padrão 3000ms).
   */
  public void showCenterMessage(String message, long duration) {
    this.centerMessage = message;
    this.centerMessageDuration = duration;
    this.centerMessageStartTime = System.currentTimeMillis();
    this.centerMessageVisible = true;
  }

  /**
   * Exibe uma mensagem no centro da tela por 3 segundos.
   * @param message Mensagem a ser exibida.
   */
  public void showCenterMessage(String message) {
    showCenterMessage(message, 3000);
  }

  /**
   * Carrega as imagens dos corações.
   */
  private void loadHeartImages() {
    try {
      heartFull = ImageIO.read(getClass().getResourceAsStream("/ui/hearth-01.png"));
      heartThreeQuarters = ImageIO.read(getClass().getResourceAsStream("/ui/hearth-02.png"));
      heartHalf = ImageIO.read(getClass().getResourceAsStream("/ui/hearth-03.png"));
      heartQuarter = ImageIO.read(getClass().getResourceAsStream("/ui/hearth-04.png"));
      heartEmpty = ImageIO.read(getClass().getResourceAsStream("/ui/hearth-05.png"));
    } catch (Exception e) {
      System.err.println("Erro ao carregar imagens dos corações: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Carrega o ícone da moeda.
   */
  private void loadCoinIcon() {
    try {
      coinIcon = ImageIO.read(getClass().getResourceAsStream("/ui/coin.png"));
    } catch (Exception e) {
      System.err.println("Erro ao carregar ícone da moeda: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Adiciona uma mensagem à interface.
   * @param text Texto da mensagem.
   * @param image Imagem opcional.
   * @param durationMillis Duração em milissegundos.
   */
  public void addMessage(String text, Image image, long durationMillis) {
    messages.add(new Message(text, image, durationMillis));
  }

  /**
   * Desenha a interface gráfica e as mensagens na tela.
   * @param graphics2D Contexto gráfico.
   */
  public void draw(Graphics2D graphics2D) {
    // Desenhar interface do jogador (vida, mana, nível)
    drawPlayerStats(graphics2D);

    // Desenhar mensagens no canto inferior esquerdo
    drawMessages(graphics2D);

    // Desenhar janela de stats se estiver visível
    if (showStatsWindow) {
      drawStatsWindow(graphics2D);
    }

    // Desenhar mensagem central se estiver visível
    if (centerMessageVisible) {
      drawCenterMessage(graphics2D);
    }
  }

  /**
   * Desenha as mensagens no canto inferior esquerdo.
   * @param graphics2D Contexto gráfico.
   */
  private void drawMessages(Graphics2D graphics2D) {
    graphics2D.setFont(dogicaFont_16);
    graphics2D.setColor(Color.WHITE);

    int screenHeight = gamePanel.getHeight();
    int y = screenHeight - 50;
    int x = 20;
    int spacing = 48;

    messages.removeIf(Message::isExpired);

    for (int i = messages.size() - 1; i >= 0; i--) {
      Message msg = messages.get(i);
      if (msg.getImage() != null) {
        graphics2D.drawImage(msg.getImage(), x, y - 32, 32, 32, null);
        graphics2D.drawString(msg.getText(), x + 40, y);
      } else {
        graphics2D.drawString(msg.getText(), x, y);
      }
      y -= spacing;
    }
  }

  /**
   * Desenha as estatísticas do jogador no canto superior esquerdo.
   * @param graphics2D Contexto gráfico.
   */
  private void drawPlayerStats(Graphics2D graphics2D) {
    var player = gamePanel.getPlayer();
    if (player == null) return;

    graphics2D.setFont(dogicaFont_16);

    int x = 20;
    int y = 15; // Movido mais para cima (era 20)
    int heartSize = 24;

    drawHearts(graphics2D, x, y, player.getAttributeHealth(), heartSize);

    graphics2D.setColor(Color.WHITE);
    graphics2D.drawString("Level " + player.getCurrentLevel(), x, y + heartSize + 15);
    
    // Desenhar dinheiro do jogador no canto superior direito
    drawPlayerMoney(graphics2D);
  }

  /**
   * Desenha o dinheiro do jogador no canto superior direito.
   * @param graphics2D Contexto gráfico.
   */
  private void drawPlayerMoney(Graphics2D graphics2D) {
    var player = gamePanel.getPlayer();
    if (player == null) return;

    graphics2D.setFont(dogicaFont_16);
    
    int screenWidth = gamePanel.getWidth();
    int iconSize = 40;
    int padding = 20;
    
    // Calcular posição no canto superior direito
    String moneyText = player.getPlayerMoney().getMoneyDisplay();
    FontMetrics fm = graphics2D.getFontMetrics();
    int textWidth = fm.stringWidth(moneyText);
    
    int totalWidth = iconSize + 5 + textWidth; // ícone + espaçamento + texto
    int x = screenWidth - totalWidth - padding;
    int y = 15;
    
    // Desenhar ícone da moeda (centralizado com os corações)
    if (coinIcon != null) {
      graphics2D.drawImage(coinIcon, x, y, iconSize, iconSize, null);
    }
    
    // Desenhar texto do dinheiro (melhor alinhado com o ícone)
    graphics2D.setColor(Color.WHITE);
    graphics2D.drawString(moneyText, x + iconSize + 5, y + iconSize/2 + 5);
  }

  /**
   * Desenha os corações baseado na vida atual do jogador.
   * @param graphics2D Contexto gráfico.
   * @param x Posição X inicial.
   * @param y Posição Y.
   * @param currentHealth Vida atual.
   * @param heartSize Tamanho dos corações.
   */
  private void drawHearts(Graphics2D graphics2D, int x, int y, int currentHealth, int heartSize) {
    int healthPerHeart = 50;
    int maxHealth = gamePanel.getPlayer().getAttributeMaxHealth();
    int heartsNeeded = maxHealth / healthPerHeart;

    for (int i = 0; i < heartsNeeded; i++) {
      int heartX = x + (i * (heartSize + 2));
      BufferedImage heartImage = getHeartImage(currentHealth, i, healthPerHeart);

      if (heartImage != null) {
        graphics2D.drawImage(heartImage, heartX, y, heartSize, heartSize, null);
      }
    }
  }

  /**
   * Retorna a imagem do coração baseado na vida atual e posição.
   * @param currentHealth Vida atual.
   * @param heartIndex Índice do coração (0-9).
   * @param healthPerHeart Vida por coração.
   * @return Imagem do coração apropriada.
   */
  private BufferedImage getHeartImage(int currentHealth, int heartIndex, int healthPerHeart) {
    int heartStartHealth = heartIndex * healthPerHeart;
    int heartEndHealth = heartStartHealth + healthPerHeart;

    if (currentHealth <= heartStartHealth) {
      return heartEmpty;
    }

    if (currentHealth >= heartEndHealth) {
      return heartFull;
    }

    // Calcular quanto deste coração está preenchido
    int healthInThisHeart = currentHealth - heartStartHealth;
    double fillPercentage = (double) healthInThisHeart / healthPerHeart;

    // Retornar coração baseado na porcentagem de preenchimento
    if (fillPercentage >= 0.75) {
      return heartThreeQuarters;
    } else if (fillPercentage >= 0.5) {
      return heartHalf;
    } else if (fillPercentage >= 0.25) {
      return heartQuarter;
    } else {
      return heartEmpty;
    }
  }

  /**
   * Desenha a janela de stats do jogador.
   * @param graphics2D Contexto gráfico.
   */
  private void drawStatsWindow(Graphics2D graphics2D) {
    var player = gamePanel.getPlayer();
    if (player == null) return;

    graphics2D.setFont(dogicaFont_16);

    int windowWidth = 300;
    int windowHeight = 450;
    int x = (gamePanel.getWidth() - windowWidth) / 2;
    int y = (gamePanel.getHeight() - windowHeight) / 2;

    graphics2D.setColor(new Color(0, 0, 0, 200));
    graphics2D.fillRect(x, y, windowWidth, windowHeight);

    graphics2D.setColor(Color.WHITE);
    graphics2D.drawRect(x, y, windowWidth, windowHeight);

    graphics2D.setColor(Color.YELLOW);
    graphics2D.setFont(dogicaFont_16);
    graphics2D.drawString("STATS DO JOGADOR", x + 20, y + 40);

    graphics2D.setColor(Color.WHITE);

    int textY = y + 80;
    int spacing = 25;

    graphics2D.drawString("Nome: " + player.getName(), x + 20, textY);
    textY += spacing;

    graphics2D.drawString("Classe: " + player.getCharacterClass().getCharacterClassName(), x + 20, textY);
    textY += spacing;

    graphics2D.drawString("Nível: " + player.getCurrentLevel(), x + 20, textY);
    textY += spacing;

    graphics2D.drawString("XP Total: " + player.getTotalXp(), x + 20, textY);
    textY += spacing;

    graphics2D.setColor(Color.GRAY);
    graphics2D.drawLine(x + 20, textY, x + windowWidth - 20, textY);
    textY += spacing;

    graphics2D.setColor(Color.WHITE);
    graphics2D.drawString("Vida: " + player.getAttributeHealth() + " / " + player.getAttributeMaxHealth(), x + 20, textY);
    textY += spacing;

    graphics2D.drawString("Mana: " + player.getAttributeMana() + " / " + player.getAttributeMaxMana(), x + 20, textY);
    textY += spacing;

    graphics2D.drawString("Força: " + player.getAttributeStrength(), x + 20, textY);
    textY += spacing;

    graphics2D.drawString("Agilidade: " + player.getAttributeAgility(), x + 20, textY);
    textY += spacing;

    graphics2D.drawString("Sorte: " + player.getLuck(), x + 20, textY);
    textY += spacing;

    String specialAttrName = player.getCharacterClass().getSpecialAttributeName();
    int specialAttrValue = player.getCharacterClass().getSpecialAttributeValue();

    String translatedAttrName = switch (specialAttrName.toLowerCase()) {
      case "rage" -> "Raiva";
      case "dexterity" -> "Destreza";
      case "willpower" -> "Autocontrole";
      case "endurance" -> "Resistência";
      case "magicpower" -> "Poder Mágico";
      default -> specialAttrName;
    };

    graphics2D.setColor(Color.CYAN);
    graphics2D.drawString(translatedAttrName + ": " + specialAttrValue, x + 20, textY);
    textY += spacing;

    graphics2D.setColor(Color.GRAY);
    graphics2D.drawLine(x + 20, textY, x + windowWidth - 20, textY);
    textY += spacing;

    graphics2D.setColor(Color.WHITE);
    graphics2D.drawString("Próximo nível: " + player.getXpForNextLevel() + " XP", x + 20, textY);
    textY += spacing;

    double xpProgress = player.getXpProgress() * 100;
    graphics2D.drawString("Progresso: " + String.format("%.1f", xpProgress) + "%", x + 20, textY);
    textY += spacing;

    graphics2D.setColor(Color.YELLOW);
    graphics2D.drawString("Pressione Q para fechar", x, textY + 20);
  }

  /**
   * Desenha a mensagem central na tela.
   * @param graphics2D Contexto gráfico.
   */
  private void drawCenterMessage(Graphics2D graphics2D) {
    // Verificar se a mensagem ainda deve ser exibida
    long currentTime = System.currentTimeMillis();
    if (currentTime - centerMessageStartTime > centerMessageDuration) {
      centerMessageVisible = false;
      return;
    }

    int screenWidth = gamePanel.getWidth();
    int screenHeight = gamePanel.getHeight();

    graphics2D.setFont(dogicaFont_40);

    FontMetrics fontMetrics = graphics2D.getFontMetrics();
    int textWidth = fontMetrics.stringWidth(centerMessage);
    int textHeight = fontMetrics.getHeight();

    int x = (screenWidth - textWidth) / 2;
    int y = (screenHeight + textHeight) / 2;

    graphics2D.setColor(new Color(0, 0, 0, 180));
    int padding = 20;
    graphics2D.fillRect(x - padding, y - textHeight - padding,
                       textWidth + (padding * 2), textHeight + (padding * 2));

    // Desenhar borda
    graphics2D.setColor(Color.WHITE);
    graphics2D.drawRect(x - padding, y - textHeight - padding,
                       textWidth + (padding * 2), textHeight + (padding * 2));

    graphics2D.setColor(Color.YELLOW);
    graphics2D.drawString(centerMessage, x, y);
  }

  public void drawBattleUI(Graphics2D g2) {
    if (gamePanel.gameState != gamePanel.battleState || !gamePanel.battle.isInBattle()) return;

    int screenWidth = gamePanel.getWidth();
    int screenHeight = gamePanel.getHeight();
    int tileSize = gamePanel.getTileSize();
    Player player = gamePanel.battle.getPlayer();
    Npc battleMonster = gamePanel.battle.getMonster();
    BufferedImage healthPotionImg = null;
    BufferedImage manaPotionImg = null;
    BufferedImage swordImg = null;
    BufferedImage shieldImg = null;

    // Fundo de batalha
    g2.setColor(new Color(50, 50, 35));
    g2.fillRect(0, 0, screenWidth, screenHeight);

    // Desenhar o monstro (lado direito)
    if (battleMonster != null) {
      int monsterX = screenWidth / 2 + 2 * tileSize;
      int monsterY = screenHeight / 6;
      BufferedImage monsterSkin = null;
      try {
        String spritePath = gamePanel.getNpcSpriteLoader().getFrontSprite(battleMonster.getSkin());
        if (spritePath != null) {
          InputStream is = getClass().getResourceAsStream("/sprites/" + spritePath);
          if (is != null) {
            monsterSkin = ImageIO.read(is);
          }
        }
      } catch (Exception e) {
        System.err.println("Erro ao carregar sprite do monstro: " + e.getMessage());
        e.printStackTrace();
      }

      if (monsterSkin != null) {
        g2.drawImage(monsterSkin, monsterX, monsterY, tileSize * 3, tileSize * 3, null);
      }
    }

    // Desenhar o jogador (lado esquerdo, de costas)
    int playerX = screenWidth / 5;
    int playerY = screenHeight / 2 - tileSize;
    g2.drawImage(player.getUp(), playerX, playerY, tileSize * 3, tileSize * 3, null);

    // Interface de batalha (painel inferior)
    g2.setColor(new Color(255, 255, 255, 200));
    g2.fillRect(0, screenHeight - 145, screenWidth, 150);

    // Opções de ação
    g2.setColor(Color.BLACK);
    g2.setFont(new Font("Arial", Font.PLAIN, 24));

    if (gamePanel.battle.isWaitingForPlayerInput()) {
      int potionIconSize = 35;
      try {
        healthPotionImg = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/objects/healthPotion-0002.png")));
        manaPotionImg   = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/objects/manaPotion-0001.png")));
      } catch (IOException e) {
        e.printStackTrace();
      }

      // Mostrar opções disponíveis

      g2.drawString("Escolha sua ação:", 20, screenHeight - 115);
      g2.drawString("(1) - " + player.getCharacterClass().getSpecialAbilityName(), 20, screenHeight - 75);
      g2.drawString("(2) - Ataque Básico", 20, screenHeight - 35);
      g2.drawString("(3) - Defender", 280, screenHeight - 75);
      g2.drawString("(4) - Tentar Fugir", 280, screenHeight - 35);

      g2.setColor(Color.BLACK);
      int amountOfPotionHp = gamePanel.getInventoryManager().countItemById("health_potion");
      int amountOfPotionMp = gamePanel.getInventoryManager().countItemById("mana_potion");

      if (healthPotionImg != null) {
        g2.drawImage(healthPotionImg, 550, screenHeight - 102, potionIconSize - 5, potionIconSize, null);
      }
      g2.drawString("(6) - ", 500, screenHeight - 75);

      if (manaPotionImg != null) {
        g2.drawImage(manaPotionImg, 550, screenHeight - 60, potionIconSize - 5, potionIconSize , null);
      }
      g2.drawString("(7) - ", 500, screenHeight - 35);

      // Exibe a quantidade de poções no inventário
      g2.setFont(new Font("Arial", Font.BOLD, 20));
      g2.drawString(String.valueOf(amountOfPotionHp), 565, screenHeight - 70);
      g2.drawString(String.valueOf(amountOfPotionMp), 565, screenHeight - 30);

      g2.setFont(new Font("Arial", Font.PLAIN, 24));
      g2.setColor(Color.BLACK);
      // Destacar opções indisponíveis
      if (player.getAttributeMana() < 15) {
        g2.setColor(Color.GRAY);
        g2.drawString("(1) - " + player.getCharacterClass().getSpecialAbilityName(), 20, screenHeight - 75);
      }
      if (!player.canApplyBuff("ARMOR")) {
        g2.setColor(Color.GRAY);
        g2.drawString("(3) - Defender", 280, screenHeight - 75);
      } else if (player.canApplyBuff("ARMOR")){
        g2.setColor(Color.BLACK);
        g2.drawString("(3) - Defender", 280, screenHeight - 75);
      }
      if (!player.canApplyBuff("STRENGTH")) {
        g2.setColor(Color.GRAY);
        g2.drawString("(1) - " + player.getCharacterClass().getSpecialAbilityName(), 20, screenHeight - 75);
      } else if (player.canApplyBuff("STRENGTH")) {
        g2.setColor(Color.BLACK);
        g2.drawString("(1) - " + player.getCharacterClass().getSpecialAbilityName(), 20, screenHeight - 75);
      }
      //    else {
//      // Mostrar turno atual
//      Character currentChar = gamePanel.battle.getCurrentCharacter();
//      g2.setColor(Color.BLUE);
//      g2.setFont(new Font("Arial", Font.BOLD, 18));
//      g2.drawString("Turno de: " + currentChar.getName(), 20, screenHeight - 50);
//    }

      int buffIconSize = 35;
      try {
        swordImg = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/objects/items/sword_common.png")));
        shieldImg   = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/objects/items/shield_common.png")));
      } catch (IOException e) {
        e.printStackTrace();
      }

      // Player buffs
      if (player.hasActiveBuff("ARMOR")) {
        if (shieldImg != null) {
          g2.drawImage(shieldImg, 135, screenHeight - 300, buffIconSize, buffIconSize , null);
        }
      } else if (player.hasActiveBuff("STRENGTH")) {
        if (swordImg != null) {
          g2.drawImage(swordImg, 135, screenHeight - 270, buffIconSize, buffIconSize, null);
        }
      }

      // Monster buff
      if (battleMonster.hasActiveBuff("ARMOR")) {
        if (swordImg != null) {
          g2.drawImage(shieldImg, 610, screenHeight - 465, buffIconSize, buffIconSize, null);
        }
      }
    }


    // Informações do monstro (canto superior direito)
    if (battleMonster != null) {
      // Nome do monstro
      g2.setColor(Color.WHITE);
      g2.setFont(new Font("Arial", Font.PLAIN, 16));
      g2.drawString(battleMonster.getName(), screenWidth - 260, screenHeight - 505);

      // Barra de HP do monstro
      int monsterHpBarX = screenWidth - 272;
      int monsterHpBarY = screenHeight - 500;
      int barWidth = 115;
      int barHeight = 25;

      // Fundo da barra (vermelho)
      g2.setColor(Color.RED);
      g2.fillRect(monsterHpBarX, monsterHpBarY, barWidth, barHeight);

      // Barra de HP atual (verde)
      g2.setColor(Color.GREEN);
      double hpPercentage = (double) battleMonster.getAttributeHealth() / battleMonster.getAttributeMaxHealth();
      g2.fillRect(monsterHpBarX, monsterHpBarY, (int) (barWidth * hpPercentage), barHeight);

      // Contorno da barra
      g2.setColor(Color.BLACK);
      g2.drawRect(monsterHpBarX, monsterHpBarY, barWidth, barHeight);

      // Texto de HP
      g2.drawString("HP: " + battleMonster.getAttributeHealth() + "/" +
        battleMonster.getAttributeMaxHealth(), monsterHpBarX + 15, monsterHpBarY + 18);

      // Barra de MP do monstro (se tiver)
      if (battleMonster.getAttributeMaxMana() > 0) {
        int monsterMpBarY = monsterHpBarY + 25;

        // Fundo da barra (cinza)
        g2.setColor(Color.GRAY);
        g2.fillRect(monsterHpBarX, monsterMpBarY, barWidth, barHeight);

        // Barra de MP atual (azul)
        g2.setColor(Color.BLUE);
        double mpPercentage = (double) battleMonster.getAttributeMana() / battleMonster.getAttributeMaxMana();
        g2.fillRect(monsterHpBarX, monsterMpBarY, (int) (barWidth * mpPercentage), barHeight);

        // Contorno da barra
        g2.setColor(Color.BLACK);
        g2.drawRect(monsterHpBarX, monsterMpBarY, barWidth, barHeight);

        // Texto de MP
        g2.drawString("MP: " + battleMonster.getAttributeMana() + "/" +
          battleMonster.getAttributeMaxMana(), monsterHpBarX + 15, monsterMpBarY - 50);
      }
    }

    // Informações do jogador (canto inferior esquerdo, perto do sprite)
    int playerInfoX = screenWidth - 605;
    int playerHpBarY = screenHeight - 200;
    int barWidth = 115;
    int barHeight = 25;

    // Barra de HP do jogador
    g2.setColor(Color.RED);
    g2.fillRect(playerInfoX, playerHpBarY, barWidth, barHeight);

    g2.setColor(Color.GREEN);
    double playerHpPercentage = (double) player.getAttributeHealth() / player.getAttributeMaxHealth();
    g2.fillRect(playerInfoX, playerHpBarY, (int) (barWidth * playerHpPercentage), barHeight);

    g2.setColor(Color.BLACK);
    g2.drawRect(playerInfoX, playerHpBarY, barWidth, barHeight);
    g2.drawString("HP: " + player.getAttributeHealth() + "/" + player.getAttributeMaxHealth(),
      playerInfoX + 10, playerHpBarY + 17);

    // Barra de Mana do jogador
    int playerMpBarY = playerHpBarY + 25;

    g2.setColor(Color.GRAY);
    g2.fillRect(playerInfoX, playerMpBarY, barWidth, barHeight);

    g2.setColor(Color.BLUE);
    double playerMpPercentage = (double) player.getAttributeMana() / player.getAttributeMaxMana();
    g2.fillRect(playerInfoX, playerMpBarY, (int) (barWidth * playerMpPercentage), barHeight);

    g2.setColor(Color.BLACK);
    g2.drawRect(playerInfoX, playerMpBarY, barWidth, barHeight);

    g2.setColor(Color.WHITE);
    g2.drawString("MP: " + player.getAttributeMana() + "/" + player.getAttributeMaxMana(),
      playerInfoX + 10, playerMpBarY + 18);
  }

  /**
   * Desenha a interface do inventário.
   * @param g2 Contexto gráfico.
   * @param inventoryManager Gerenciador do inventário.
   */
  public void drawInventoryUI(Graphics2D g2, InventoryManager inventoryManager) {
    if (inventoryUI != null) {
      inventoryUI.draw(g2, inventoryManager);
    }
  }
}
