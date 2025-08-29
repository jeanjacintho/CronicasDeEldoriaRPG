package br.com.cronicasdeeldoria.game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;
import java.awt.Image;

import br.com.cronicasdeeldoria.game.GamePanel;

public class GameUI {
  GamePanel gamePanel;
  Font arial_40;

  private final List<Message> messages = new ArrayList<>();

  public GameUI(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
    arial_40 = new Font("Arial", Font.PLAIN, 40);
  }

  public void addMessage(String text, Image image, long durationMillis) {
    messages.add(new Message(text, image, durationMillis));
  }

  public void draw(Graphics2D graphics2D) {
    graphics2D.setFont(arial_40);
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
