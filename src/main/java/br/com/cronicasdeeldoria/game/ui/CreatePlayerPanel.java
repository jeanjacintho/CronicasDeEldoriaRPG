package br.com.cronicasdeeldoria.game.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;

import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.font.FontManager;
import br.com.cronicasdeeldoria.entity.character.races.Race;
import br.com.cronicasdeeldoria.entity.character.races.Archer;
import br.com.cronicasdeeldoria.entity.character.races.Breton;
import br.com.cronicasdeeldoria.entity.character.races.Dwarf;
import br.com.cronicasdeeldoria.entity.character.races.Mage;
import br.com.cronicasdeeldoria.entity.character.races.Orc;

public class CreatePlayerPanel extends JPanel implements ActionListener {

  private final JFrame window;
  private final int screenWidth;
  private final int screenHeight;
  private final int tileSize;
  private final int maxScreenRow;
  private final int maxScreenCol;
  private JTextField nameField;
  private JToggleButton archerButton, bretonButton, dwarfButton, mageButton, orcButton;
  private ButtonGroup classGroup;
  private JButton startGameButton;
  private JButton backButton;

  private String selectedClass = "Breton";
  private JToggleButton[] classButtons;
  private int selectedClassIndex = 1; // Breton é o padrão
  private JButton[] actionButtons;
  private int selectedActionIndex = 0;
  private boolean onClassSelection = true;

  public CreatePlayerPanel(JFrame window, int screenWidth, int screenHeight, int tileSize, int maxScreenRow, int maxScreenCol) {
      this.window = window;
      this.screenWidth = screenWidth;
      this.screenHeight = screenHeight;
      this.tileSize = tileSize;
      this.maxScreenRow = maxScreenRow;
      this.maxScreenCol = maxScreenCol;

      this.setPreferredSize(new Dimension(screenWidth, screenHeight));
      this.setLayout(new BorderLayout(20, 20));
      this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

      JLabel titleLabel = new JLabel("Crie seu personagem", SwingConstants.CENTER);
      titleLabel.setFont(FontManager.getFont(24f));
      this.add(titleLabel, BorderLayout.NORTH);

      JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

      JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      namePanel.add(new JLabel("Nome do personagem:"));
      nameField = new JTextField(20);
      nameField.setFocusTraversalKeysEnabled(false); // Desabilita Tab padrão
      nameField.addKeyListener(new java.awt.event.KeyAdapter() {
          @Override
          public void keyPressed(java.awt.event.KeyEvent e) {
              if (e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB && !e.isShiftDown()) {
                  e.consume();
                  onClassSelection = true;
                  nameField.transferFocus();
                  setClassFocus(selectedClassIndex);
                  CreatePlayerPanel.this.requestFocusInWindow();
              } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB && e.isShiftDown()) {
                  e.consume();
                  onClassSelection = false;
                  setActionFocus(selectedActionIndex);
                  CreatePlayerPanel.this.requestFocusInWindow();
              }
          }
      });
      namePanel.add(nameField);
      centerPanel.add(namePanel, BorderLayout.NORTH);

      JPanel classSelectionPanel = new JPanel(new GridLayout(1, 5, 15, 15));
      classSelectionPanel.setBorder(BorderFactory.createTitledBorder("Escolha uma classe"));

      classGroup = new ButtonGroup();

      JPanel archerPanel = createClassPanel("Archer");
      archerButton = (JToggleButton) archerPanel.getComponent(0);
      JPanel bretanPanel = createClassPanel("Breton");
      bretonButton = (JToggleButton) bretanPanel.getComponent(0);
      JPanel dwarfPanel = createClassPanel("Dwarf");
      dwarfButton = (JToggleButton) dwarfPanel.getComponent(0);
      JPanel magePanel = createClassPanel("Mage");
      mageButton = (JToggleButton) magePanel.getComponent(0);
      JPanel orcPanel = createClassPanel("Orc");
      orcButton = (JToggleButton) orcPanel.getComponent(0);

      bretonButton.setSelected(true);

      classButtons = new JToggleButton[] { archerButton, bretonButton, dwarfButton, mageButton, orcButton };
      setClassFocus(selectedClassIndex);

      classSelectionPanel.add(archerPanel);
      classSelectionPanel.add(bretanPanel);
      classSelectionPanel.add(dwarfPanel);
      classSelectionPanel.add(magePanel);
      classSelectionPanel.add(orcPanel);

      centerPanel.add(classSelectionPanel, BorderLayout.CENTER);
      this.add(centerPanel, BorderLayout.CENTER);

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
      startGameButton = new JButton("INICIAR JOGO");
      backButton = new JButton("VOLTAR");

      startGameButton.addActionListener(this);
      backButton.addActionListener(this);

      buttonPanel.add(startGameButton);
      buttonPanel.add(backButton);
      this.add(buttonPanel, BorderLayout.SOUTH);

      actionButtons = new JButton[] { startGameButton, backButton };
      setActionFocus(selectedActionIndex);

      setupKeyBindings();
  }

  private void setupKeyBindings() {
      InputMap im = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      ActionMap am = this.getActionMap();

      im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
      im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
      im.put(KeyStroke.getKeyStroke("TAB"), "tab");
      im.put(KeyStroke.getKeyStroke("shift TAB"), "shiftTab");
      im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
      im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
      im.put(KeyStroke.getKeyStroke("ENTER"), "select");

      am.put("moveLeft", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              if (onClassSelection) changeClassSelection(-1);
              else if (!onClassSelection) changeActionSelection(-1);
          }
      });
      am.put("moveRight", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              if (onClassSelection) changeClassSelection(1);
              else if (!onClassSelection) changeActionSelection(1);
          }
      });
      am.put("tab", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              if (nameField.isFocusOwner()) {
                  onClassSelection = true;
                  setClassFocus(selectedClassIndex);
                  CreatePlayerPanel.this.requestFocusInWindow();
              } else if (onClassSelection) {
                  onClassSelection = false;
                  setActionFocus(selectedActionIndex);
              } else {
                  nameField.requestFocusInWindow();
              }
          }
      });
      am.put("shiftTab", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              if (nameField.isFocusOwner()) {
                  onClassSelection = false;
                  setActionFocus(selectedActionIndex);
                  CreatePlayerPanel.this.requestFocusInWindow();
              } else if (!onClassSelection) {
                  onClassSelection = true;
                  setClassFocus(selectedClassIndex);
              } else {
                  nameField.requestFocusInWindow();
              }
          }
      });
      am.put("moveUp", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              if (!onClassSelection && !nameField.isFocusOwner()) {
                  onClassSelection = true;
                  setClassFocus(selectedClassIndex);
              }
          }
      });
      am.put("moveDown", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              if (onClassSelection) {
                  onClassSelection = false;
                  setActionFocus(selectedActionIndex);
              } else if (!onClassSelection && !nameField.isFocusOwner()) {
                  nameField.requestFocusInWindow();
              }
          }
      });
      am.put("select", new AbstractAction() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
              if (onClassSelection) {
                  classButtons[selectedClassIndex].doClick();
              } else if (!onClassSelection && !nameField.isFocusOwner()) {
                  actionButtons[selectedActionIndex].doClick();
              }
          }
      });
  }

  private JPanel createClassPanel(String className) {
      JPanel panel = new JPanel(new BorderLayout());
      JToggleButton button = new JToggleButton();
      try {
          String classFolder = className.toLowerCase();
          String path = "/sprites/player/" + classFolder + "/" + classFolder + "_avatar.png";
          var resource = getClass().getResourceAsStream(path);
          if (resource != null) {
              Image img = ImageIO.read(resource);
              Image scaledImg = img.getScaledInstance(128, 128, Image.SCALE_SMOOTH);
              button.setIcon(new ImageIcon(scaledImg));
          } else {
              System.err.println("Resource not found: " + path);
              button.setText(className);
          }
      } catch (IOException | IllegalArgumentException e) {
          System.err.println("Could not load avatar for: " + className);
          button.setText(className);
      }
      button.setPreferredSize(new Dimension(128, 128));
      button.setMargin(new Insets(0, 0, 0, 0));
      button.setActionCommand(className);
      button.addActionListener(this);
      classGroup.add(button);

      JLabel label = new JLabel(className, SwingConstants.CENTER);
      panel.add(button, BorderLayout.CENTER);
      panel.add(label, BorderLayout.SOUTH);

      return panel;
  }

  private void changeClassSelection(int delta) {
      selectedClassIndex = (selectedClassIndex + delta + classButtons.length) % classButtons.length;
      setClassFocus(selectedClassIndex);
  }

  private void setClassFocus(int index) {
      for (int i = 0; i < classButtons.length; i++) {
          classButtons[i].setFocusable(i == index);
          classButtons[i].setSelected(i == index);
      }
      selectedClass = classButtons[index].getActionCommand();
  }

  private void changeActionSelection(int delta) {
      selectedActionIndex = (selectedActionIndex + delta + actionButtons.length) % actionButtons.length;
      setActionFocus(selectedActionIndex);
  }

  private void setActionFocus(int index) {
      for (int i = 0; i < actionButtons.length; i++) {
          actionButtons[i].setFocusable(i == index);
      }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();

      switch (command) {
          case "INICIAR JOGO":
              startGame();
              break;
          case "VOLTAR":
              goBackToMenu();
              break;
          default:
              selectedClass = command;
              break;
      }
  }

  private void startGame() {
      String playerName = nameField.getText();
      if (playerName == null || playerName.trim().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Escolha um nome para o personagem.", "Nome obrigatório", JOptionPane.WARNING_MESSAGE);
          return;
      }

      window.remove(this);

      Race race = switch (selectedClass) {
          case "Archer" -> new Archer(10);
          case "Breton" -> new Breton(15);
          case "Dwarf" -> new Dwarf(20);
          case "Mage" -> new Mage(25);
          case "Orc" -> new Orc(30);
          default -> new Breton(5);
      };

      GamePanel gamePanel = new GamePanel(screenWidth, screenHeight, playerName, race, tileSize, maxScreenRow, maxScreenCol);
      window.add(gamePanel);
      gamePanel.startGameThread();
      gamePanel.setupGame();
      window.revalidate();
      window.repaint();
      gamePanel.requestFocusInWindow();
  }

  private void goBackToMenu() {
      window.remove(this);
      MainMenuPanel mainMenuPanel = new MainMenuPanel(window, tileSize, screenWidth, screenHeight, maxScreenRow, maxScreenCol);
      window.add(mainMenuPanel);
      window.revalidate();
      window.repaint();
  }
}
