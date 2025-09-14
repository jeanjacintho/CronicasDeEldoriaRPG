package br.com.cronicasdeeldoria.game.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.com.cronicasdeeldoria.audio.AudioManager;
import br.com.cronicasdeeldoria.audio.AudioContext;

/**
 * Painel do menu principal do jogo, com opções para iniciar, carregar ou sair.
 */
public class MainMenuPanel extends JPanel implements ActionListener {
  private JButton newGame;
  private JButton loadGame;
  private JButton exitGame;
  private JButton[] buttons;
  private int selectedIndex = 0;
  final int screenWidth;
  final int screenHeight;
  final int maxScreenRow;
  final int maxScreenCol;
  final int tileSize;
  private JFrame window;
  private AudioManager audioManager;

  /**
   * Cria o painel do menu principal.
   * @param window Janela principal.
   * @param tileSize Tamanho do tile.
   * @param screenWidth Largura da tela.
   * @param screenHeight Altura da tela.
   * @param maxScreenRow Máximo de linhas na tela.
   * @param maxScreenCol Máximo de colunas na tela.
   */
  public MainMenuPanel(JFrame window, int tileSize, int screenWidth, int screenHeight, int maxScreenRow, int maxScreenCol) {
      this.setPreferredSize(new Dimension(screenWidth, screenHeight));
      this.screenWidth = screenWidth;
      this.screenHeight = screenHeight;

      this.maxScreenRow = maxScreenRow;
      this.maxScreenCol = maxScreenCol;

      this.window = window;
      this.tileSize = tileSize;

      this.setLayout(new GridLayout(3, 1, 10, 10));

      // Inicializar sistema de áudio
      this.audioManager = AudioManager.getInstance();
      audioManager.changeContext(AudioContext.MENU);

      newGame = new JButton("NOVO JOGO");
      loadGame = new JButton("CARREGAR JOGO");
      exitGame = new JButton("SAIR");

      newGame.addActionListener(this);
      loadGame.addActionListener(this);
      exitGame.addActionListener(this);

      this.add(newGame);
      this.add(loadGame);
      this.add(exitGame);

      buttons = new JButton[] { newGame, loadGame, exitGame };
      setButtonFocus(0);

      setupKeyBindings();
  }

  private void setupKeyBindings() {
      InputMap im = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      ActionMap am = this.getActionMap();

      im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
      im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
      im.put(KeyStroke.getKeyStroke("ENTER"), "select");

      am.put("moveUp", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              changeSelection(-1);
          }
      });
      am.put("moveDown", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              changeSelection(1);
          }
      });
      am.put("select", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              buttons[selectedIndex].doClick();
          }
      });
  }

  private void changeSelection(int delta) {
      selectedIndex = (selectedIndex + delta + buttons.length) % buttons.length;
      setButtonFocus(selectedIndex);
  }

  private void setButtonFocus(int index) {
      for (int i = 0; i < buttons.length; i++) {
          buttons[i].setFocusable(i == index);
      }
      buttons[index].requestFocusInWindow();
  }

  /**
   * Trata eventos dos botões do menu.
   * @param e Evento de ação.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
      // Reproduzir efeito sonoro de clique
      if (audioManager != null) {
          audioManager.playSoundEffect("button_click");
      }
      
      if (e.getSource() == newGame) {
          window.remove(this);
          CreatePlayerPanel createPlayerPanel = new CreatePlayerPanel(window, screenWidth, screenHeight, tileSize, maxScreenRow, maxScreenCol);
          window.add(createPlayerPanel);
          window.revalidate();
          window.repaint();
          createPlayerPanel.requestFocusInWindow();
      } else if (e.getSource() == loadGame) {
          JOptionPane.showMessageDialog(this, "Carregando jogo...");
          // Add logic to load a game
      } else if (e.getSource() == exitGame) {
          System.exit(0);
      }

  }

}

