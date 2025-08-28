package br.com.cronicasdeeldoria.tile;

import java.awt.Graphics2D;
import java.io.InputStream;
import javax.imageio.ImageIO;
import br.com.cronicasdeeldoria.game.GamePanel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class TileManager {
    private GamePanel gamePanel;
    private Tile[] tiles;
    private int[][][] mapLayers;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadMapJson("/maps/map01.json");
        initTilesFromJson();
    }

    private void loadMapJson(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                throw new RuntimeException("Mapa JSON não encontrado: " + path);
            }
            ObjectMapper mapper = new ObjectMapper();
            MapJson mapJson = mapper.readValue(is, MapJson.class);
            int layersCount = mapJson.layers.size();
            int rows = mapJson.height;
            int cols = mapJson.width;
            mapLayers = new int[layersCount][rows][cols];
            for (int l = 0; l < layersCount; l++) {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        mapLayers[l][r][c] = mapJson.layers.get(l).get(r).get(c);
                    }
                }
            }
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

    public static class MapJson {
        public int width;
        public int height;
        public List<List<List<Integer>>> layers;
    }

    public void draw(Graphics2D g2) {
        int tileSize = gamePanel.getTileSize();
        int worldRows = mapLayers[0].length;
        int worldCols = mapLayers[0][0].length;
        int layersCount = mapLayers.length;

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
                for (int l = 0; l < layersCount; l++) {
                    int tileNum = mapLayers[l][row][col];
                    if (tileNum != 0) {
                        g2.drawImage(tiles[tileNum].image, 
                            col * tileSize - playerWorldX + screenX, 
                            row * tileSize - playerWorldY + screenY, 
                            tileSize, tileSize, null);
                    }
                }
            }
        }
    }

    public int[][][] getMapLayers() {
        return mapLayers;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public int getMapWidth() {
        return mapLayers[0][0].length;
    }
    public int getMapHeight() {
        return mapLayers[0].length;
    }
}
