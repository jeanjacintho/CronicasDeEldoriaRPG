package br.com.cronicasdeeldoria.game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.awt.Image;
import java.awt.image.BufferedImage;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.entity.character.npc.Npc;
import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.game.FloatingText;
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
  Font dogicaFont_24;
  Font dogicaFont_22;
  Font dogicaFont_20;
  Font dogicaFont_18;
  Font dogicaFont_16;
  Font dogicaFont_14;
  Font dogicaFont_12;
  private BufferedImage heartFull;
  private BufferedImage heartThreeQuarters;
  private BufferedImage heartHalf;
  private BufferedImage heartQuarter;
  private BufferedImage heartEmpty;
  private BufferedImage coinIcon;
  private boolean showStatsWindow = false;
  private boolean showQuestWindow = false;
  private String centerMessage = "";
  private long centerMessageStartTime = 0;
  private long centerMessageDuration = 3000; // 3 segundos por padrão
  private boolean centerMessageVisible = false;
  private List<FloatingText> floatingTexts = new ArrayList<>();

  // Variáveis para scroll da janela de quests
  private int questScrollOffset = 0;
  private int maxQuestScrollOffset = 0;
  private static final int QUEST_SCROLL_SPEED = 20;

  private final List<Message> messages = new ArrayList<>();
  private InventoryUI inventoryUI;

  /**
   * Cria uma nova interface de jogo.
   * @param gamePanel Painel principal do jogo.
   */
  public GameUI(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
    dogicaFont_40 = FontManager.getFont(40f);
    dogicaFont_24 = FontManager.getFont(24f);
    dogicaFont_22 = FontManager.getFont(22f);
    dogicaFont_20 = FontManager.getFont(20f);
    dogicaFont_18 = FontManager.getFont(18f);
    dogicaFont_16 = FontManager.getFont(16f);
    dogicaFont_14 = FontManager.getFont(14f);
    dogicaFont_12 = FontManager.getFont(12f);

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
   * Alterna a visibilidade da janela de quests.
   */
  public void toggleQuestWindow() {
    showQuestWindow = !showQuestWindow;
    if (showQuestWindow) {
      // Resetar scroll quando abrir a janela
      questScrollOffset = 0;
      calculateMaxScrollOffset();
    }
  }

  /**
   * Verifica se a janela de quests está visível.
   */
  public boolean isQuestWindowVisible() {
    return showQuestWindow;
  }

  /**
   * Move o scroll da janela de quests para cima.
   */
  public void scrollQuestWindowUp() {
    if (showQuestWindow && questScrollOffset > 0) {
      questScrollOffset = Math.max(0, questScrollOffset - QUEST_SCROLL_SPEED);
    }
  }

  /**
   * Move o scroll da janela de quests para baixo.
   */
  public void scrollQuestWindowDown() {
    if (showQuestWindow && questScrollOffset < maxQuestScrollOffset) {
      questScrollOffset = Math.min(maxQuestScrollOffset, questScrollOffset + QUEST_SCROLL_SPEED);
    }
  }

  private void calculateMaxScrollOffset() {
    var questManager = br.com.cronicasdeeldoria.game.quest.QuestManager.getInstance();
    var activeQuests = questManager.getActiveQuests();

    if (activeQuests.isEmpty()) {
      maxQuestScrollOffset = 0;
      return;
    }

    int contentHeight = 0;
    int spacing = 20;

    for (var quest : activeQuests.values()) {
      contentHeight += 60; // Altura base da quest (título + descrição + progresso)

      // Adicionar altura de todos os objetivos
      var objectives = quest.getObjectives();
      contentHeight += objectives.size() * spacing;

      contentHeight += spacing + 5; // Separador entre quests
    }

    int windowHeight = 600;
    int contentAreaHeight = windowHeight - 120; // Área disponível para conteúdo
    maxQuestScrollOffset = Math.max(0, contentHeight - contentAreaHeight);
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
    //Desenhar interface básica (sempre visível)
    if (gamePanel.gameState != gamePanel.battleState) {
      drawPlayerStats(graphics2D);
      drawPlayerMoney(graphics2D);
    }

    //Desenhar interface de batalha se necessário
    if (gamePanel.gameState == gamePanel.battleState && gamePanel.battle.isInBattle()) {
      drawBattleUI(graphics2D);
    }

    // TERCEIRO: Desenhar elementos que ficam por cima de tudo
    drawMessages(graphics2D);

    // Desenhar janela de stats se estiver visível
    if (showStatsWindow) {
      drawStatsWindow(graphics2D);
    }

    // Desenhar janela de quests se estiver visível
    if (showQuestWindow) {
      drawQuestWindow(graphics2D);
    }

    // Desenhar mensagem central se estiver visível
    if (centerMessageVisible) {
      drawCenterMessage(graphics2D);
    }

    // Desenhar textos flutuantes sempre por último
    drawFloatingTexts(graphics2D);
  }

  /**
   * Desenha as mensagens no canto inferior esquerdo com sombra.
   * @param graphics2D Contexto gráfico.
   */
  private void drawMessages(Graphics2D graphics2D) {
    graphics2D.setFont(dogicaFont_14);

    int screenHeight = gamePanel.getHeight();
    int y = screenHeight - 50;
    int x = 20;
    int spacing = 30;

    messages.removeIf(Message::isExpired);

    for (int i = messages.size() - 1; i >= 0; i--) {
      Message msg = messages.get(i);
      if (msg.getImage() != null) {
        graphics2D.drawImage(msg.getImage(), x, y - 32, 32, 32, null);

        // Sombra do texto
        graphics2D.setColor(new Color(0, 0, 0, 150));
        graphics2D.drawString(msg.getText(), x + 41, y + 1);

        // Texto principal
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(msg.getText(), x + 40, y);
      } else {
        // Sombra do texto
        graphics2D.setColor(new Color(0, 0, 0, 150));
        graphics2D.drawString(msg.getText(), x + 1, y + 1);

        // Texto principal
        graphics2D.setColor(Color.WHITE);
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

    // Sombra do texto de nível
    graphics2D.setColor(new Color(0, 0, 0, 150));
    graphics2D.drawString("Level " + player.getCurrentLevel(), x + 1, y + heartSize + 16);

    // Texto principal do nível
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
    // Sombra do texto
    graphics2D.setColor(new Color(0, 0, 0, 150));
    graphics2D.drawString(moneyText, x + iconSize + 6, y + iconSize/2 + 6);

    // Texto principal
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
   * Desenha a janela de stats do jogador
   * @param graphics2D Contexto gráfico.
   */
  private void drawStatsWindow(Graphics2D graphics2D) {
    var player = gamePanel.getPlayer();
    if (player == null) return;

    // Overlay semi-transparente
    graphics2D.setColor(new Color(0, 0, 0, 150));
    graphics2D.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());

    // Cores do tema (mesmo estilo do pause overlay)
    Color backgroundColor = new Color(50, 40, 60, 250); // Fundo principal
    Color textColor = Color.WHITE; // Texto branco
    Color titleColor = Color.WHITE; // Título branco
    Color borderColor = new Color(100, 80, 120, 200); // Borda roxa
    Color shadowColor = new Color(0, 0, 0, 100); // Sombra da caixa
    Color textShadowColor = new Color(0, 0, 0, 150); // Sombra do texto
    Color separatorColor = new Color(150, 150, 150); // Cinza para separadores

    graphics2D.setFont(dogicaFont_16);

    int windowWidth = 350;
    int windowHeight = 500;
    int x = (gamePanel.getWidth() - windowWidth) / 2;
    int y = (gamePanel.getHeight() - windowHeight) / 2;
    int borderRadius = 20;
    int padding = 25;

    // Sombra da janela
    graphics2D.setColor(shadowColor);
    graphics2D.fillRoundRect(x + 4, y + 4, windowWidth, windowHeight, borderRadius, borderRadius);

    // Fundo principal da janela
    graphics2D.setColor(backgroundColor);
    graphics2D.fillRoundRect(x, y, windowWidth, windowHeight, borderRadius, borderRadius);

    // Borda da janela
    graphics2D.setColor(borderColor);
    graphics2D.setStroke(new BasicStroke(3));
    graphics2D.drawRoundRect(x, y, windowWidth, windowHeight, borderRadius, borderRadius);

    // Título com sombra
    graphics2D.setFont(FontManager.getFont(20f));
    String title = "STATS DO JOGADOR";
    graphics2D.setColor(textShadowColor);
    graphics2D.drawString(title, x + padding + 2, y + 45 + 2);
    graphics2D.setColor(titleColor);
    graphics2D.drawString(title, x + padding, y + 45);

    // Linha decorativa abaixo do título
    java.awt.FontMetrics fm = graphics2D.getFontMetrics();
    int titleWidth = fm.stringWidth(title);
    graphics2D.setColor(borderColor);
    graphics2D.setStroke(new BasicStroke(2));
    graphics2D.drawLine(x + padding, y + 50, x + padding + titleWidth, y + 50);

    graphics2D.setFont(dogicaFont_16);
    graphics2D.setColor(textColor);

    int textY = y + 85;
    int spacing = 28;

    // Informações básicas
    drawStatLine(graphics2D, "Nome", player.getName(), x + padding, textY, textColor);
    textY += spacing;

    drawStatLine(graphics2D, "Classe", player.getCharacterClass().getCharacterClassName(), x + padding, textY, textColor);
    textY += spacing;

    drawStatLine(graphics2D, "Nível", String.valueOf(player.getCurrentLevel()), x + padding, textY, titleColor);
    textY += spacing;

    drawStatLine(graphics2D, "XP Total", String.valueOf(player.getTotalXp()), x + padding, textY, textColor);
    textY += spacing + 10;

    // Separador
    graphics2D.setColor(separatorColor);
    graphics2D.setStroke(new BasicStroke(2));
    graphics2D.drawLine(x + padding, textY, x + windowWidth - padding, textY);
    textY += spacing;

    // Atributos principais
    graphics2D.setColor(textColor);
    drawStatLine(graphics2D, "Vida", player.getAttributeHealth() + " / " + player.getAttributeMaxHealth(), x + padding, textY, textColor);
    textY += spacing;

    drawStatLine(graphics2D, "Mana", player.getAttributeMana() + " / " + player.getAttributeMaxMana(), x + padding, textY, textColor);
    textY += spacing;

    drawStatLine(graphics2D, "Força", String.valueOf(player.getAttributeStrength()), x + padding, textY, textColor);
    textY += spacing;

    drawStatLine(graphics2D, "Agilidade", String.valueOf(player.getAttributeAgility()), x + padding, textY, textColor);
    textY += spacing;

    drawStatLine(graphics2D, "Armor", String.valueOf(player.getAttributeArmor()), x + padding, textY, textColor);
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

    drawStatLine(graphics2D, translatedAttrName, String.valueOf(specialAttrValue), x + padding, textY, new Color(100, 255, 255));
    textY += spacing + 10;

    // Separador
    graphics2D.setColor(separatorColor);
    graphics2D.setStroke(new java.awt.BasicStroke(2));
    graphics2D.drawLine(x + padding, textY, x + windowWidth - padding, textY);
    textY += spacing;

    // Informações de XP
    graphics2D.setColor(textColor);
    drawStatLine(graphics2D, "Próximo nível", String.valueOf(player.getXpForNextLevel()) + " XP", x + padding, textY, textColor);
    textY += spacing;

    double xpProgress = player.getXpProgress() * 100;
    drawStatLine(graphics2D, "Progresso", String.format("%.1f", xpProgress) + "%", x + padding, textY, textColor);
    textY += spacing + 20;

    // Instrução para fechar
    graphics2D.setFont(FontManager.getFont(14f));
    String instructionText = "Pressione Q para fechar";

    // Sombra da instrução
    graphics2D.setColor(new Color(0, 0, 0, 150));
    graphics2D.drawString(instructionText, x + padding + 1, textY + 1);

    // Instrução principal
    graphics2D.setColor(new Color(200, 200, 200));
    graphics2D.drawString(instructionText, x + padding, textY);
  }

  /**
   * Desenha uma linha de estatística com formatação consistente.
   * @param g2 Contexto gráfico
   * @param label Rótulo da estatística
   * @param value Valor da estatística
   * @param x Posição X
   * @param y Posição Y
   * @param color Cor do texto
   */
  private void drawStatLine(Graphics2D g2, String label, String value, int x, int y, Color color) {
    String text = label + ": " + value;

    // Sombra do texto
    g2.setColor(new Color(0, 0, 0, 150));
    g2.drawString(text, x + 2, y + 2);

    // Texto principal
    g2.setColor(color);
    g2.drawString(text, x, y);
  }

  /**
   * Desenha a janela de quests ativas.
   * @param graphics2D Contexto gráfico.
   */
  private void drawQuestWindow(Graphics2D graphics2D) {
    var questManager = br.com.cronicasdeeldoria.game.quest.QuestManager.getInstance();

    // Overlay semi-transparente
    graphics2D.setColor(new Color(0, 0, 0, 150));
    graphics2D.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());

    // Cores do tema (mesmo estilo da janela de stats)
    Color backgroundColor = new Color(50, 40, 60, 250); // Fundo principal
    Color textColor = Color.WHITE; // Texto branco
    Color titleColor = Color.WHITE; // Título branco
    Color borderColor = new Color(100, 80, 120, 200); // Borda roxa
    Color shadowColor = new Color(0, 0, 0, 100); // Sombra da caixa
    Color textShadowColor = new Color(0, 0, 0, 150); // Sombra do texto
    Color separatorColor = new Color(150, 150, 150); // Cinza para separadores
    Color questActiveColor = new Color(255, 255, 100); // Amarelo para quests ativas
    Color questCompletedColor = new Color(100, 255, 100); // Verde para objetivos completos
    Color questInProgressColor = new Color(255, 200, 100); // Laranja para objetivos em progresso

    graphics2D.setFont(dogicaFont_16);

    int windowWidth = 450;
    int windowHeight = 600;
    int x = (gamePanel.getWidth() - windowWidth) / 2;
    int y = (gamePanel.getHeight() - windowHeight) / 2;
    int borderRadius = 20;
    int padding = 25;

    // Sombra da janela
    graphics2D.setColor(shadowColor);
    graphics2D.fillRoundRect(x + 4, y + 4, windowWidth, windowHeight, borderRadius, borderRadius);

    // Fundo principal da janela
    graphics2D.setColor(backgroundColor);
    graphics2D.fillRoundRect(x, y, windowWidth, windowHeight, borderRadius, borderRadius);

    // Borda da janela
    graphics2D.setColor(borderColor);
    graphics2D.setStroke(new BasicStroke(3));
    graphics2D.drawRoundRect(x, y, windowWidth, windowHeight, borderRadius, borderRadius);

    // Título com sombra
    graphics2D.setFont(FontManager.getFont(20f));
    String title = "QUESTS ATIVAS";
    graphics2D.setColor(textShadowColor);
    graphics2D.drawString(title, x + padding + 2, y + 45 + 2);
    graphics2D.setColor(titleColor);
    graphics2D.drawString(title, x + padding, y + 45);

    // Linha decorativa abaixo do título
    java.awt.FontMetrics fm = graphics2D.getFontMetrics();
    int titleWidth = fm.stringWidth(title);
    graphics2D.setColor(borderColor);
    graphics2D.setStroke(new BasicStroke(2));
    graphics2D.drawLine(x + padding, y + 50, x + padding + titleWidth, y + 50);

    graphics2D.setFont(dogicaFont_14);
    int textY = y + 85 - questScrollOffset; // Aplicar offset de scroll
    int spacing = 20;
    int contentStartY = y + 85;
    int contentEndY = y + windowHeight - 80;

    // Obter quests ativas
    var activeQuests = questManager.getActiveQuests();

    if (activeQuests.isEmpty()) {
      // Nenhuma quest ativa
      graphics2D.setColor(textColor);
      graphics2D.drawString("Nenhuma quest ativa no momento.", x + padding, textY);
    } else {
      // Aplicar clipping para área de conteúdo
      graphics2D.setClip(x + padding, contentStartY, windowWidth - 2 * padding, contentEndY - contentStartY);

      // Mostrar todas as quests ativas
      for (var quest : activeQuests.values()) {
        // Nome da quest
        graphics2D.setFont(dogicaFont_16);
        graphics2D.setColor(questActiveColor);
        graphics2D.drawString("• " + quest.getTitle(), x + padding, textY);
        textY += spacing + 5;

        // Descrição da quest (truncada se necessário)
        graphics2D.setFont(dogicaFont_12);
        graphics2D.setColor(textColor);
        String description = quest.getDescription();
        if (description.length() > 50) {
          description = description.substring(0, 47) + "...";
        }
        graphics2D.drawString("  " + description, x + padding, textY);
        textY += spacing;

        // Progresso geral
        graphics2D.setFont(dogicaFont_14);
        int progress = quest.getProgressPercentage();
        Color progressColor = progress == 100 ? questCompletedColor : questInProgressColor;
        graphics2D.setColor(progressColor);
        graphics2D.drawString("  Progresso: " + progress + "%", x + padding, textY);
        textY += spacing;

        // Objetivos
        var objectives = quest.getObjectives();
        for (var objective : objectives) {
          graphics2D.setFont(dogicaFont_12);
          Color objColor = objective.isCompleted() ? questCompletedColor : textColor;
          graphics2D.setColor(objColor);
          String objStatus = objective.isCompleted() ? "✓" : "○";
          graphics2D.drawString("    " + objStatus + " " + objective.getDescription(), x + padding, textY);
          textY += spacing;
        }

        // Separador entre quests
        textY += 5;
        graphics2D.setColor(separatorColor);
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.drawLine(x + padding, textY, x + windowWidth - padding, textY);
        textY += spacing;
      }

      // Restaurar clipping
      graphics2D.setClip(null);
    }

    // Instrução para fechar e controles de scroll
    graphics2D.setFont(FontManager.getFont(14f));
    String instructionText = "Pressione J para fechar";
    int instructionY = y + windowHeight - 40;

    // Sombra da instrução
    graphics2D.setColor(new Color(0, 0, 0, 150));
    graphics2D.drawString(instructionText, x + padding + 1, instructionY + 1);

    // Instrução principal
    graphics2D.setColor(new Color(200, 200, 200));
    graphics2D.drawString(instructionText, x + padding, instructionY);

    // Instruções de scroll se houver conteúdo para rolar
    if (maxQuestScrollOffset > 0) {
      String scrollText = "↑↓ para rolar";
      graphics2D.setColor(new Color(0, 0, 0, 150));
      graphics2D.drawString(scrollText, x + windowWidth - padding - 100 + 1, instructionY + 1);
      graphics2D.setColor(new Color(200, 200, 200));
      graphics2D.drawString(scrollText, x + windowWidth - padding - 100, instructionY);
    }

    // Indicador de scroll (barra lateral)
    if (maxQuestScrollOffset > 0) {
      int scrollBarWidth = 8;
      int scrollBarHeight = 100;
      int scrollBarX = x + windowWidth - padding - scrollBarWidth - 5;
      int scrollBarY = y + 100;

      // Fundo da barra de scroll
      graphics2D.setColor(new Color(100, 100, 100, 100));
      graphics2D.fillRoundRect(scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight, 4, 4);

      // Indicador de posição
      int indicatorHeight = Math.max(20, (int)(scrollBarHeight * (1.0 - (double)questScrollOffset / maxQuestScrollOffset)));
      int indicatorY = scrollBarY + (scrollBarHeight - indicatorHeight);

      graphics2D.setColor(new Color(200, 200, 200, 200));
      graphics2D.fillRoundRect(scrollBarX, indicatorY, scrollBarWidth, indicatorHeight, 4, 4);
    }
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

    // Sombra do texto central
    graphics2D.setColor(new Color(0, 0, 0, 200));
    graphics2D.drawString(centerMessage, x + 2, y + 2);

    // Texto principal (branco)
    graphics2D.setColor(Color.WHITE);
    graphics2D.drawString(centerMessage, x, y);
  }

  public void showDamage(Character target, int damage, String source) {
    int offsetX = 130; // dano deslocado para direita do player
    int offsetY = 0;

    if ("DAMAGEOVERTIME".equals(source)) {
      offsetX = 150;
      offsetY = 90;
      showFloatingText(target, "-" + damage, Color.BLACK, offsetX, offsetY);
    }
    showFloatingText(target, "-" + damage, Color.RED, offsetX, offsetY);
  }

  public void showHeal(Character target, int heal, String source) {
    int offsetX = 0; // vida aparece a esquerda do player
    int offsetY = 0;

    if ("REGEN".equals(source)) {
      offsetX = -55;
      offsetY = 90;
    }
    showFloatingText(target, "+" + heal, Color.GREEN, offsetX, offsetY);
  }

  public void showMana(Character target, int mana) {
    int offsetX = 0; //mana aparece a esquerda do player
    int offsetY = 0; //mana aparece a esquerda do player

    showFloatingText(target, "+" + mana, Color.BLUE, offsetX, offsetY);
  }

  public void showFloatingText(Character target, String text, Color color, int offsetX, int offsetY) {
    int screenHeight = gamePanel.getHeight();
    int x, y;

    if (target instanceof Player) {
      x = 145;
      y = screenHeight - 290;
    } else {
      x = 495;
      y = screenHeight - 465;
    }

    floatingTexts.add(new FloatingText(text, x + offsetX, y + offsetY, color));
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
    BufferedImage waterOrbImg = null;
    BufferedImage fireOrbImg = null;

    // Desenha o plano de fundo da batalha com base no mapa atual
    Image battleBackground = null;
    if (gamePanel.getBattleEffectManager() != null) {
      battleBackground = gamePanel.getBattleEffectManager().loadBattleBackground(gamePanel.getCurrentMapName());
    }

    if (battleBackground != null) {
      g2.drawImage(battleBackground, 0, 0, screenWidth, screenHeight, null);
    } else {
      g2.setColor(new Color(50, 50, 35));
      g2.fillRect(0, 0, screenWidth, screenHeight);
    }

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

    // Desenhar overlays configuráveis (GIF) por cima do jogador e monstro
    if (gamePanel.getBattleEffectManager() != null) {
      int monsterX = screenWidth / 2 + 2 * tileSize;
      int monsterY = screenHeight / 6;

      gamePanel.getBattleEffectManager().drawOverlays(
        g2,
        gamePanel,
        playerX, playerY,
        monsterX, monsterY,
        tileSize * 3, tileSize * 3
      );

      // Desenha efeitos persistentes com base em buffs ativos (escudo enquanto o buff de ARMADURA estiver ativo)
      gamePanel.getBattleEffectManager().drawPersistentEffects(
        g2,
        gamePanel,
        playerX, playerY,
        monsterX, monsterY,
        tileSize * 3, tileSize * 3,
        player,
        battleMonster
      );
    }

    // Interface de batalha (painel inferior)
    g2.setColor(new Color(255, 255, 255, 200));
    g2.fillRect(0, screenHeight - 145, screenWidth, 150);

    // Opções de ação
    g2.setColor(Color.BLACK);
    g2.setFont(dogicaFont_16);


    int potionIconSize = 35;
    try {

      InputStream healthStream = getClass().getResourceAsStream("/sprites/objects/items/health_potion.png");
      InputStream manaStream = getClass().getResourceAsStream("/sprites/objects/items/mana_potion.png");
      
      if (healthStream != null) {
        healthPotionImg = ImageIO.read(healthStream);
      }
      if (manaStream != null) {
        manaPotionImg = ImageIO.read(manaStream);
      }

    } catch (IOException e) {
      System.err.println("Erro ao carregar imagens de poção: " + e.getMessage());
      e.printStackTrace();
    }

    // Mostrar ações disponíveis
    g2.drawString("Escolha sua ação:", 20, screenHeight - 115);
    g2.drawString("(1) - " + player.getCharacterClass().getSpecialAbilityName(), 20, screenHeight - 75);
    g2.drawString("(2) - Ataque Básico", 20, screenHeight - 35);
    g2.drawString("(3) - Defender", 280, screenHeight - 75);
    g2.drawString("(4) - Tentar Fugir", 280, screenHeight - 35);

    // Se o player possui a orb no inventario libera pra utilizar o buff
    if (player.getGamePanel().getInventoryManager().hasItemById("orb_fire")) {
      g2.drawString("(9) - Fire Orb Debuff", 500, screenHeight - 40);
    }
    if (player.getGamePanel().getInventoryManager().hasItemById("orb_water")) {
      g2.drawString("(0) - Water Orb Buff", 500, screenHeight - 10);
    }

    g2.setColor(Color.BLACK);
    int amountOfPotionHp = gamePanel.getInventoryManager().countItemById("health_potion");
    int amountOfPotionMp = gamePanel.getInventoryManager().countItemById("mana_potion");

    if (healthPotionImg != null) {
      g2.drawImage(healthPotionImg, 555, screenHeight - 142, potionIconSize - 5, potionIconSize, null);
    }
    g2.drawString("(6) - ", 500, screenHeight - 115);

    if (manaPotionImg != null) {
      g2.drawImage(manaPotionImg, 555, screenHeight - 100, potionIconSize - 5, potionIconSize , null);
    }
    g2.drawString("(7) - ", 500, screenHeight - 75);

    // Exibe a quantidade de poções no inventário
    g2.setFont(dogicaFont_18);
    g2.drawString(String.valueOf(amountOfPotionHp), 570, screenHeight - 110);
    g2.drawString(String.valueOf(amountOfPotionMp), 570, screenHeight - 70);

    g2.setFont(dogicaFont_16);
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
      InputStream swordStream = getClass().getResourceAsStream("/sprites/objects/items/sword_common.png");
      InputStream shieldStream = getClass().getResourceAsStream("/sprites/objects/items/shield_common.png");
      InputStream waterOrbStream = getClass().getResourceAsStream("/sprites/objects/quest/orb_water.png");
      InputStream fireOrbStream = getClass().getResourceAsStream("/sprites/objects/quest/orb_fire.png");
      
      if (swordStream != null) {
        swordImg = ImageIO.read(swordStream);
      }
      if (shieldStream != null) {
        shieldImg = ImageIO.read(shieldStream);
      }
      if (waterOrbStream != null) {
        waterOrbImg = ImageIO.read(waterOrbStream);
      }
      if (fireOrbStream != null) {
        fireOrbImg = ImageIO.read(fireOrbStream);
      }
    } catch (IOException e) {
      System.err.println("Erro ao carregar imagens de buff: " + e.getMessage());
      e.printStackTrace();
    }

    // Player buffs
    if (player.hasActiveBuff("ARMOR")) {
      if (shieldImg != null) {
        g2.drawImage(shieldImg, 135, screenHeight - 300, buffIconSize, buffIconSize , null);
      }
    }
    if (player.hasActiveBuff("STRENGTH")) {
      if (swordImg != null) {
        g2.drawImage(swordImg, 135, screenHeight - 270, buffIconSize, buffIconSize, null);
      }
    }
    if (player.hasActiveBuff("HOT")) {
      if (waterOrbImg != null) {
        g2.drawImage(waterOrbImg, 135, screenHeight - 240, buffIconSize, buffIconSize, null);
      }
    }

    // Monster buff
    if (battleMonster.hasActiveBuff("ARMOR")) {
      if (shieldImg != null) {
        g2.drawImage(shieldImg, 610, screenHeight - 465, buffIconSize, buffIconSize, null);
      }
    }
    if (battleMonster.hasActiveBuff("DOT")) {
      if (fireOrbImg != null) {
        g2.drawImage(fireOrbImg, 610, screenHeight - 425, buffIconSize, buffIconSize, null);
      }
    }

    // Informações do monstro (canto superior direito)
    if (battleMonster != null) {
      // Nome do monstro
      g2.setColor(Color.WHITE);
      g2.setFont(dogicaFont_14);
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
      g2.setFont(dogicaFont_12);
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
        g2.setFont(dogicaFont_12);
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
    g2.setFont(dogicaFont_12);
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
    g2.setFont(dogicaFont_12);
    g2.drawString("MP: " + player.getAttributeMana() + "/" + player.getAttributeMaxMana(),
      playerInfoX + 10, playerMpBarY + 18);

    // Desenha o dano recebido no character
    Iterator<FloatingText> it = floatingTexts.iterator();
    while (it.hasNext()) {
      FloatingText dt = it.next();

      if (dt.isExpired()) {
        it.remove();
        continue;
      }

      g2.setColor(Color.RED);
      g2.setFont(dogicaFont_16);
      g2.drawString(dt.text, dt.x, dt.y);

      // animação simples: o texto sobe
      dt.y -= 1;
    }
    drawFloatingTexts(g2);
  }

  private void drawFloatingTexts(Graphics2D g2) {
    Iterator<FloatingText> iterator = floatingTexts.iterator();
    while (iterator.hasNext()) {
      FloatingText text = iterator.next();

      // Desenha sombra
      g2.setColor(new Color(0, 0, 0,180 ));
      g2.drawString(text.getText(), text.getX() + 1, text.getY() + 1);

      // Desenha texto principal na cor correta
      g2.setColor(text.getColor());
      g2.drawString(text.getText(), text.getX(), text.getY());

      // Atualiza posição (subindo aos poucos)
      text.update();

      // Remove se expirou
      if (text.isExpired()) {
        iterator.remove();
      }
    }
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
