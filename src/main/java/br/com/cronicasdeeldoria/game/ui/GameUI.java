package br.com.cronicasdeeldoria.game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;
import java.awt.Image;

import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.font.FontManager;

/**
 * Gerencia a interface gráfica do jogo, exibindo mensagens e elementos de UI.
 */
public class GameUI {
  GamePanel gamePanel;
  Font dogicaFont_40;

  private final List<Message> messages = new ArrayList<>();

  /**
   * Cria uma nova interface de jogo.
   * @param gamePanel Painel principal do jogo.
   */
  public GameUI(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
    dogicaFont_40 = FontManager.getFont(40f);
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
    graphics2D.setFont(dogicaFont_40);
    graphics2D.setColor(Color.WHITE);

    int y = 50;
    int x = 20;
    int spacing = 48;
    messages.removeIf(Message::isExpired);
    for (Message msg : messages) {
      if (msg.getImage() != null) {
        graphics2D.drawImage(msg.getImage(), x, y - 32, 32, 32, null);
        graphics2D.drawString(msg.getText(), x + 40, y);
      } else {
        graphics2D.drawString(msg.getText(), x, y);
      }
      y += spacing;
    }
  }
}
