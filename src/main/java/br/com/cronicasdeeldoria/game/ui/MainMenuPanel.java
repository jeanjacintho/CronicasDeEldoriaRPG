package br.com.cronicasdeeldoria.game.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel implements ActionListener {
  private JButton newGame;
  private JButton loadGame;
  private JButton exitGame;
  final int screenWidth;
  final int screenHeight;
  final int maxScreenRow;
  final int maxScreenCol;
  final int tileSize;
  private JFrame window;

  public MainMenuPanel(JFrame window, int tileSize, int screenWidth, int screenHeight, int maxScreenRow, int maxScreenCol) {
      this.setPreferredSize(new Dimension(screenWidth, screenHeight));
      this.screenWidth = screenWidth;
      this.screenHeight = screenHeight;

      this.maxScreenRow = maxScreenRow;
      this.maxScreenCol = maxScreenCol;

      this.window = window;
      this.tileSize = tileSize;

      this.setLayout(new GridLayout(3, 1, 10, 10));

      newGame = new JButton("Start Game");
      loadGame = new JButton("Load Game");
      exitGame = new JButton("Exit");

      newGame.addActionListener(this);
      loadGame.addActionListener(this);
      exitGame.addActionListener(this);

      this.add(newGame);
      this.add(loadGame);
      this.add(exitGame);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
      if (e.getSource() == newGame) {
          window.remove(this);
          CreatePlayerPanel createPlayerPanel = new CreatePlayerPanel(window, screenWidth, screenHeight, tileSize, maxScreenRow, maxScreenCol);
          window.add(createPlayerPanel);
          window.revalidate();
          window.repaint();
          createPlayerPanel.requestFocusInWindow();
      } else if (e.getSource() == loadGame) {
          JOptionPane.showMessageDialog(this, "Loading game...");
          // Add logic to load a game
      } else if (e.getSource() == exitGame) {
          System.exit(0);
      }

  }

}

