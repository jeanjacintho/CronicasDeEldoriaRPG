package br.com.cronicasdeeldoria.game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.awt.Image;
import java.awt.image.BufferedImage;

import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.font.FontManager;

/**
 * Gerencia a interface gráfica do jogo, exibindo mensagens e elementos de UI.
 */
public class GameUI {
  GamePanel gamePanel;
  Font dogicaFont_40;
  Font dogicaFont_16;
  private java.awt.image.BufferedImage heartFull;
  private java.awt.image.BufferedImage heartThreeQuarters;
  private java.awt.image.BufferedImage heartHalf;
  private java.awt.image.BufferedImage heartQuarter;
  private java.awt.image.BufferedImage heartEmpty;
  private boolean showStatsWindow = false;
  private String centerMessage = "";
  private long centerMessageStartTime = 0;
  private long centerMessageDuration = 3000; // 3 segundos por padrão
  private boolean centerMessageVisible = false;

  private final List<Message> messages = new ArrayList<>();

  /**
   * Cria uma nova interface de jogo.
   * @param gamePanel Painel principal do jogo.
   */
  public GameUI(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
    dogicaFont_40 = FontManager.getFont(40f);
    dogicaFont_16 = FontManager.getFont(16f);
  
    loadHeartImages();
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
    int y = 30;
    int heartSize = 24;
    
    drawHearts(graphics2D, x, y, player.getAttributeLife(), heartSize);
    
    graphics2D.setColor(Color.WHITE);
    graphics2D.drawString("Level " + player.getCurrentLevel(), x, y + heartSize + 15);
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
  
    graphics2D.drawString("Raça: " + player.getRace().getRaceName(), x + 20, textY);
    textY += spacing;
    
    graphics2D.drawString("Nível: " + player.getCurrentLevel(), x + 20, textY);
    textY += spacing;
    
    graphics2D.drawString("XP Total: " + player.getTotalXp(), x + 20, textY);
    textY += spacing;
    
    graphics2D.setColor(Color.GRAY);
    graphics2D.drawLine(x + 20, textY, x + windowWidth - 20, textY);
    textY += spacing;
    
    graphics2D.setColor(Color.WHITE);
    graphics2D.drawString("Vida: " + player.getAttributeLife() + " / " + player.getAttributeMaxHealth(), x + 20, textY);
    textY += spacing;
    
    graphics2D.drawString("Mana: " + player.getAttributeMana() + " / " + player.getAttributeMaxMana(), x + 20, textY);
    textY += spacing;
    
    graphics2D.drawString("Força: " + player.getAttributeStrength(), x + 20, textY);
    textY += spacing;
    
    graphics2D.drawString("Agilidade: " + player.getAttributeAgility(), x + 20, textY);
    textY += spacing;
    
    graphics2D.drawString("Sorte: " + player.getLuck(), x + 20, textY);
    textY += spacing;
    
    String specialAttrName = player.getRace().getSpecialAttributeName();
    int specialAttrValue = player.getRace().getSpecialAttributeValue();
    
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
}
