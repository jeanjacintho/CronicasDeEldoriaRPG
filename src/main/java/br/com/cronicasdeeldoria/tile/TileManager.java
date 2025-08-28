package br.com.cronicasdeeldoria.tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import br.com.cronicasdeeldoria.game.GamePanel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class TileManager {
    private GamePanel gamePanel;
    private Tile[] tiles;
    private int[][] mapTileNumbers;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadMap("/maps/map01.txt");
        initTilesFromJson();
    }

    private void loadMap(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int rows = gamePanel.maxWorldRow;
            int cols = gamePanel.maxWorldCol;
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

    private void initTilesFromJson() {
        try {
            InputStream is = getClass().getResourceAsStream("/tiles.json");
            if (is == null) {
                throw new RuntimeException("tiles.json não encontrado no classpath!");
            }
            ObjectMapper mapper = new ObjectMapper();
            List<TileDefinition> tileDefs = mapper.readValue(is, new TypeReference<List<TileDefinition>>() {});
            int maxId = tileDefs.stream().mapToInt(td -> td.id).max().orElse(0);
            tiles = new Tile[maxId + 1];
            for (TileDefinition def : tileDefs) {
                Tile t = new Tile();
                t.image = ImageIO.read(getClass().getResourceAsStream(def.image));
                t.collision = def.collision;
                tiles[def.id] = t;
            }
            for (int i = 0; i < tiles.length; i++) {
                if (tiles[i] == null) {
                    tiles[i] = tiles[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class TileDefinition {
        public int id;
        public String name;
        public String image;
        public boolean collision;
    }

    public void draw(Graphics2D g2) {
        int tileSize = gamePanel.getTileSize();
        int worldRows = mapTileNumbers.length;
        int worldCols = mapTileNumbers[0].length;

        int playerWorldX = gamePanel.getPlayer().getWorldX();
        int playerWorldY = gamePanel.getPlayer().getWorldY();
        int screenX = gamePanel.getPlayer().getScreenX();
        int screenY = gamePanel.getPlayer().getScreenY();
        int playerSize = gamePanel.getPlayerSize();

        int firstCol = Math.max((playerWorldX - screenX) / tileSize, 0);
        int lastCol  = Math.min((playerWorldX + screenX + playerSize) / tileSize, worldCols - 1);
        int firstRow = Math.max((playerWorldY - screenY) / tileSize, 0);
        int lastRow  = Math.min((playerWorldY + screenY + playerSize) / tileSize, worldRows - 1);

        for (int row = firstRow; row <= lastRow; row++) {
            for (int col = firstCol; col <= lastCol; col++) {
                int worldX = col * tileSize;
                int worldY = row * tileSize;
                int screenTileX = worldX - playerWorldX + screenX;
                int screenTileY = worldY - playerWorldY + screenY;

                int tileNum = mapTileNumbers[row][col];
                g2.drawImage(tiles[tileNum].image, screenTileX, screenTileY, tileSize, tileSize, null);
            }
        }
    }

    public int[][] getMapTileNumbers() {
        return mapTileNumbers;
    }

    public Tile[] getTiles() {
        return tiles;
    }
}
