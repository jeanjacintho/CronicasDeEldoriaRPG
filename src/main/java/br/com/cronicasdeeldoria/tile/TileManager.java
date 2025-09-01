package br.com.cronicasdeeldoria.tile;

import java.awt.Graphics2D;
import java.io.InputStream;
import javax.imageio.ImageIO;
import br.com.cronicasdeeldoria.game.GamePanel;
// Jackson imports removidos para versão simplificada
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.ArrayList;

public class TileManager {
    private GamePanel gamePanel;
    private Map<String, Tile> tiles;
    private List<MapLayer> mapLayers;
    private BufferedImage spritesheet;
    private int tileSize;
    private int mapWidth;
    private int mapHeight;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadTilesFromJson();
        loadMapJson("/maps/map01.json");
    }

    private void loadTilesFromJson() {
        try {
            InputStream is = getClass().getResourceAsStream("/tiles.json");
            if (is == null) {
                throw new RuntimeException("tiles.json não encontrado no classpath!");
            }

            // Usar Gson como alternativa ao Jackson
            com.google.gson.Gson gson = new com.google.gson.Gson();
            TilesJson tilesJson = gson.fromJson(new java.io.InputStreamReader(is), TilesJson.class);

            // IMPORTANTE: Usar o tileSize do GamePanel (com escala) em vez do tiles.json
            this.tileSize = gamePanel.getTileSize();
            int originalTileSize = tilesJson.tileSize; // Tamanho original da spritesheet

            InputStream spritesheetStream = getClass().getResourceAsStream(tilesJson.spritesheet);
            if (spritesheetStream == null) {
                throw new RuntimeException("Spritesheet não encontrada: " + tilesJson.spritesheet);
            }

            this.spritesheet = ImageIO.read(spritesheetStream);

            tiles = new HashMap<>();
            for (TileDefinition def : tilesJson.tiles) {
                Tile tile = new Tile();
                // Extrair subimagem usando tamanho original da spritesheet
                tile.image = spritesheet.getSubimage(
                    def.x * originalTileSize,
                    def.y * originalTileSize,
                    originalTileSize,
                    originalTileSize
                );
                tile.collision = def.collision;
                tiles.put(def.id, tile);
            }
        } catch (Exception e) {
            System.err.println("ERRO ao carregar tiles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMapJson(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                throw new RuntimeException("Mapa JSON não encontrado: " + path);
            }

            // Usar Gson como alternativa ao Jackson
            com.google.gson.Gson gson = new com.google.gson.Gson();
            MapJson mapJson = gson.fromJson(new java.io.InputStreamReader(is), MapJson.class);

            this.mapWidth = mapJson.mapWidth;
            this.mapHeight = mapJson.mapHeight;
            this.mapLayers = mapJson.layers;

        } catch (Exception e) {
            System.err.println("ERRO ao carregar mapa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class TileDefinition {
        public String id;
        public int x;
        public int y;
        public boolean collision;
    }

    public static class TilesJson {
        public int tileSize;
        public String spritesheet;
        public List<TileDefinition> tiles;
    }

    public static class MapTile {
        public String id;
        public int x;
        public int y;
    }

    public static class MapLayer {
        public String name;
        public List<MapTile> tiles;
        public boolean collider;
    }

    public static class MapJson {
        public int tileSize;
        public int mapWidth;
        public int mapHeight;
        public List<MapLayer> layers;
    }

    public void draw(Graphics2D g2) {
        int playerWorldX = gamePanel.getPlayer().getWorldX();
        int playerWorldY = gamePanel.getPlayer().getWorldY();
        int screenX = gamePanel.getPlayer().getScreenX();
        int screenY = gamePanel.getPlayer().getScreenY();
        int playerSize = gamePanel.getPlayerSize();

        // Calcular área visível
        int firstCol = Math.max((playerWorldX - screenX) / tileSize, 0);
        int lastCol = Math.min((playerWorldX + screenX + playerSize) / tileSize, mapWidth - 1);
        int firstRow = Math.max((playerWorldY - screenY) / tileSize, 0);
        int lastRow = Math.min((playerWorldY + screenY + playerSize) / tileSize, mapHeight - 1);

        // Separar layers por tipo
        List<MapLayer> normalLayers = new ArrayList<>();
        List<MapLayer> overlayLayers = new ArrayList<>();
        List<MapLayer> objectLayers = new ArrayList<>();
        List<MapLayer> npcLayers = new ArrayList<>();
        List<MapLayer> monsterLayers = new ArrayList<>();

        for (MapLayer layer : mapLayers) {
            String layerName = layer.name.toLowerCase();
            if (layerName.contains("overlay")) {
                overlayLayers.add(layer);
            } else if (layerName.contains("objetos") || layerName.contains("objects") || layerName.contains("items")) {
                objectLayers.add(layer);
            } else if (layerName.contains("npcs") || layerName.contains("npc")) {
                npcLayers.add(layer);
            } else if (layerName.contains("monsters") || layerName.contains("monster")) {
              monsterLayers.add(layer);
            }  else {
                normalLayers.add(layer);
            }
        }

        // 1. Renderizar layers normais (fundo)
        for (MapLayer layer : normalLayers) {
            renderLayer(g2, layer, firstCol, lastCol, firstRow, lastRow, playerWorldX, playerWorldY, screenX, screenY);
        }

        // 2. Renderizar objetos
        for (MapLayer layer : objectLayers) {
            renderLayer(g2, layer, firstCol, lastCol, firstRow, lastRow, playerWorldX, playerWorldY, screenX, screenY);
        }

        // 3. Renderizar NPCs
        for (MapLayer layer : npcLayers) {
            renderLayer(g2, layer, firstCol, lastCol, firstRow, lastRow, playerWorldX, playerWorldY, screenX, screenY);
        }

        // 3. Renderizar Monsters
        for (MapLayer layer : monsterLayers) {
          renderLayer(g2, layer, firstCol, lastCol, firstRow, lastRow, playerWorldX, playerWorldY, screenX, screenY);
        }

        // 4. Renderizar o player
        gamePanel.getPlayer().draw(g2);

        // 5. Renderizar layers overlay (acima do player)
        for (MapLayer layer : overlayLayers) {
            renderLayer(g2, layer, firstCol, lastCol, firstRow, lastRow, playerWorldX, playerWorldY, screenX, screenY);
        }
    }

    private void renderLayer(Graphics2D g2, MapLayer layer, int firstCol, int lastCol, int firstRow, int lastRow,
                           int playerWorldX, int playerWorldY, int screenX, int screenY) {
        int tilesRendered = 0;
        for (MapTile tile : layer.tiles) {
            // Verificar se o tile está na área visível
            if (tile.x >= firstCol && tile.x <= lastCol &&
                tile.y >= firstRow && tile.y <= lastRow) {

                Tile tileDef = tiles.get(tile.id);
                if (tileDef != null && tileDef.image != null) {
                    int drawX = tile.x * tileSize - playerWorldX + screenX;
                    int drawY = tile.y * tileSize - playerWorldY + screenY;
                    // IMPORTANTE: Renderizar com o tileSize escalado
                    g2.drawImage(tileDef.image, drawX, drawY, tileSize, tileSize, null);
                    tilesRendered++;
                }
            }
        }
    }

    /**
     * Verifica se há colisão em uma posição específica do mapa.
     * A colisão é determinada pela propriedade collider da layer.
     */
    public boolean isCollisionAt(int x, int y) {
        for (MapLayer layer : mapLayers) {
            if (layer.collider) {
                for (MapTile tile : layer.tiles) {
                    if (tile.x == x && tile.y == y) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getTileSize() {
        return tileSize;
    }

    /**
     * Retorna os tiles de NPCs das layers do mapa.
     */
    public List<MapTile> getNpcTiles() {
        List<MapTile> npcTiles = new ArrayList<>();

        for (MapLayer layer : mapLayers) {
            String layerName = layer.name.toLowerCase();
            if (layerName.contains("npcs") || layerName.contains("npc")) {
                npcTiles.addAll(layer.tiles);
            }
        }

        return npcTiles;
    }

    /**
     * Retorna os tiles de Monsters das layers do mapa.
     */
    public List<MapTile> getMonsterTiles() {
      List<MapTile> monsterTiles = new ArrayList<>();

      for (MapLayer layer : mapLayers) {
        String layerName = layer.name.toLowerCase();
        if (layerName.contains("monsters") || layerName.contains("monster")) {
          monsterTiles.addAll(layer.tiles);
        }
      }

      return monsterTiles;
    }


  /**
     * Retorna os tiles de objetos das layers do mapa.
     */
    public List<MapTile> getObjectTiles() {
        List<MapTile> objectTiles = new ArrayList<>();

        for (MapLayer layer : mapLayers) {
            String layerName = layer.name.toLowerCase();
            if (layerName.contains("objetos") || layerName.contains("objects") || layerName.contains("items")) {
                objectTiles.addAll(layer.tiles);
            }
        }

        return objectTiles;
    }
}
