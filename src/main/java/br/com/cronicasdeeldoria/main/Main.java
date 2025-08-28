package br.com.cronicasdeeldoria.main;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import br.com.cronicasdeeldoria.game.ui.MainMenuPanel;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Crônicas de Eldoria");

        final int originalTileSize = 16;
        final int scale = 3;

        final int tileSize = originalTileSize * scale;
        final int maxScreenCol = 16;
        final int maxScreenRow = 12;
        final int screenWidth = tileSize * maxScreenCol;
        final int screenHeight = tileSize * maxScreenRow;

        MainMenuPanel mainMenuPanel = new MainMenuPanel(window, tileSize, screenWidth, screenHeight, maxScreenRow, maxScreenCol);
        window.add(mainMenuPanel, BorderLayout.CENTER);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}