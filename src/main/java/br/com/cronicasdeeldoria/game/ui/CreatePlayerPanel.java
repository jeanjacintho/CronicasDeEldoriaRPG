package br.com.cronicasdeeldoria.game.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

import br.com.cronicasdeeldoria.game.GamePanel;
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

  private JTextField nameField;
  private JToggleButton archerButton, bretonButton, dwarfButton, mageButton, orcButton;
  private ButtonGroup classGroup;
  private JButton startGameButton;
  private JButton backButton;

  private String selectedClass = "Breton";

  public CreatePlayerPanel(JFrame window, int screenWidth, int screenHeight, int tileSize) {
      this.window = window;
      this.screenWidth = screenWidth;
      this.screenHeight = screenHeight;
      this.tileSize = tileSize;
      this.setPreferredSize(new Dimension(screenWidth, screenHeight));
      this.setLayout(new BorderLayout(20, 20));
      this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

      JLabel titleLabel = new JLabel("Create Your Character", SwingConstants.CENTER);
      titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
      this.add(titleLabel, BorderLayout.NORTH);

      JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

      JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      namePanel.add(new JLabel("Character Name:"));
      nameField = new JTextField(20);
      namePanel.add(nameField);
      centerPanel.add(namePanel, BorderLayout.NORTH);

      JPanel classSelectionPanel = new JPanel(new GridLayout(1, 5, 15, 15));
      classSelectionPanel.setBorder(BorderFactory.createTitledBorder("Choose a Class"));

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

      classSelectionPanel.add(archerPanel);
      classSelectionPanel.add(bretanPanel);
      classSelectionPanel.add(dwarfPanel);
      classSelectionPanel.add(magePanel);
      classSelectionPanel.add(orcPanel);

      centerPanel.add(classSelectionPanel, BorderLayout.CENTER);
      this.add(centerPanel, BorderLayout.CENTER);

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
      startGameButton = new JButton("Start Game");
      backButton = new JButton("Back");

      startGameButton.addActionListener(this);
      backButton.addActionListener(this);

      buttonPanel.add(startGameButton);
      buttonPanel.add(backButton);
      this.add(buttonPanel, BorderLayout.SOUTH);
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

  @Override
  public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();

      switch (command) {
          case "Start Game":
              startGame();
              break;
          case "Back":
              goBackToMenu();
              break;
          default:
              selectedClass = command;
              System.out.println("Selected class: " + selectedClass);
              break;
      }
  }

  private void startGame() {
      String playerName = nameField.getText();
      if (playerName == null || playerName.trim().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Please enter a character name.", "Name Required", JOptionPane.WARNING_MESSAGE);
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

      GamePanel gamePanel = new GamePanel(screenWidth, screenHeight, playerName, race, tileSize);
      window.add(gamePanel);
      gamePanel.startGameThread();
      window.revalidate();
      window.repaint();
      gamePanel.requestFocusInWindow();
  }

  private void goBackToMenu() {
      window.remove(this);
      MainMenuPanel mainMenuPanel = new MainMenuPanel(window, tileSize, screenWidth, screenHeight);
      window.add(mainMenuPanel);
      window.revalidate();
      window.repaint();
  }
}
