package br.com.cronicasdeeldoria.tile;

import java.awt.Graphics2D;
import java.io.InputStream;
import javax.imageio.ImageIO;
import br.com.cronicasdeeldoria.game.GamePanel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TileManager {
    private GamePanel gamePanel;
    private Tile[] tiles;
    private int[][][] mapLayers;

    private Map<String, MapObjectDefinition> objectDefinitions;
    private java.util.List<MapObjectInstance> rawObjects;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadMapJson("/maps/map01.json");
        initTilesFromJson();
        initObjectsFromJson();
        loadMapObjects();
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

    private void initObjectsFromJson() {
        try {
            InputStream is = getClass().getResourceAsStream("/objects.json");
            if (is == null) {
                throw new RuntimeException("objects.json não encontrado no classpath!");
            }
            ObjectMapper mapper = new ObjectMapper();
            List<MapObjectDefinition> defs = mapper.readValue(is, new TypeReference<List<MapObjectDefinition>>() {});
            objectDefinitions = new java.util.HashMap<>();
            for (MapObjectDefinition def : defs) {
                def.loadImages();
                objectDefinitions.put(def.id, def);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMapObjects() {
        try {
            InputStream is = getClass().getResourceAsStream("/maps/map01.json");
            if (is == null) {
                throw new RuntimeException("map01.json não encontrado no classpath!");
            }
            ObjectMapper mapper = new ObjectMapper();
            MapJson mapJson = mapper.readValue(is, MapJson.class);
            rawObjects = new ArrayList<>();
            if (mapJson.objetos != null) {
                for (MapObjectInstance obj : mapJson.objetos) {
                    rawObjects.add(obj);
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

    public static class MapObjectDefinition {
        public String id;
        public String name;
        public String[][] spritePaths;
        public int[] size;
        public boolean collision;
        public transient BufferedImage[][] sprites;
        public void loadImages() throws Exception {
            int rows = this.spritePaths.length;
            int cols = this.spritePaths[0].length;
            sprites = new BufferedImage[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    sprites[r][c] = ImageIO.read(getClass().getResourceAsStream(this.spritePaths[r][c]));
                }
            }
        }
    }
    public static class MapObjectInstance {
        public String id;
        public int[] posicao;
    }

    public static class MapJson {
        public int width;
        public int height;
        public List<List<List<Integer>>> layers;
        public List<MapObjectInstance> objetos;
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
                for (int l = 0; l < Math.min(2, layersCount); l++) {
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

        gamePanel.getPlayer().draw(g2);

        if (layersCount > 2) {
            for (int row = firstRow; row <= lastRow; row++) {
                for (int col = firstCol; col <= lastCol; col++) {
                    int tileNum = mapLayers[2][row][col];
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

    public boolean isObjectCollisionTile(int row, int col) {
        if (rawObjects == null || objectDefinitions == null) return false;
        for (MapObjectInstance obj : rawObjects) {
            MapObjectDefinition def = objectDefinitions.get(obj.id);
            if (def == null) continue;
            int objRow = obj.posicao[1];
            int objCol = obj.posicao[0];
            for (int r = 0; r < def.size[1]; r++) {
                for (int c = 0; c < def.size[0]; c++) {
                    if (def.collision) {
                        if ((objRow + r) == row && (objCol + c) == col) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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

    public java.util.Map<String, MapObjectDefinition> getObjectDefinitions() {
        return objectDefinitions;
    }
    public java.util.List<MapObjectInstance> getRawObjects() {
        return rawObjects;
    }
}
