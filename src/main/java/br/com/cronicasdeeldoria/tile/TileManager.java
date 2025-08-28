package br.com.cronicasdeeldoria.tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import br.com.cronicasdeeldoria.game.GamePanel;

public class TileManager {
    private GamePanel gamePanel;
    private Tile[] tiles;
    private int[][] mapTileNumbers;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadMap("/maps/map01.txt");
        initTiles();
    }

    private void loadMap(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int rows = gamePanel.getMaxScreenRow();
            int cols = gamePanel.getMaxScreenCol();
            mapTileNumbers = new int[rows][cols];
            for (int row = 0; row < rows; row++) {
                String line = br.readLine();
                String[] numbers = line.split(" ");
                for (int col = 0; col < cols; col++) {
                    mapTileNumbers[row][col] = Integer.parseInt(numbers[col]);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTiles() {
        // Descubra o maior índice usado no mapa
        int maxTileIndex = 0;
        for (int[] row : mapTileNumbers) {
            for (int n : row) {
                if (n > maxTileIndex) maxTileIndex = n;
            }
        }
        tiles = new Tile[maxTileIndex + 1];
        try {
            // Inicialize cada tile explicitamente conforme seu índice
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(getClass().getResourceAsStream("/sprites/world/grass-0001.png"));

            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(getClass().getResourceAsStream("/sprites/world/wall-0001.png"));

            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(getClass().getResourceAsStream("/sprites/world/water-0001.png"));

            // Inicialize tiles não definidos com o tile 0 (grama)
            for (int i = 0; i < tiles.length; i++) {
                if (tiles[i] == null) {
                    tiles[i] = new Tile();
                    tiles[i].image = tiles[0].image;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int tileSize = gamePanel.getTileSize();
        for (int row = 0; row < gamePanel.getMaxScreenRow(); row++) {
            for (int col = 0; col < gamePanel.getMaxScreenCol(); col++) {
                int tileNum = mapTileNumbers[row][col];
                g2.drawImage(tiles[tileNum].image, col * tileSize, row * tileSize, tileSize, tileSize, null);
            }
        }
    }
}
