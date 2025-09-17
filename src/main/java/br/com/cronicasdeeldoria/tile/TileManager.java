package br.com.cronicasdeeldoria.tile;

import java.awt.Graphics2D;
import java.io.InputStream;
import javax.imageio.ImageIO;

import com.google.gson.Gson;

import br.com.cronicasdeeldoria.game.GamePanel;
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
        loadMapJson("/maps/houses/player_house.json");
    }

    private void loadTilesFromJson() {
        try {
            InputStream is = getClass().getResourceAsStream("/tiles.json");
            if (is == null) {
                throw new RuntimeException("tiles.json não encontrado no classpath!");
            }

            Gson gson = new Gson();
            TilesJson tilesJson = gson.fromJson(new java.io.InputStreamReader(is), TilesJson.class);

            // IMPORTANTE: Usar o tileSize do GamePanel (com escala) em vez do tiles.json
            this.tileSize = gamePanel.getTileSize();
            int originalTileSize = tilesJson.tileSize;

            InputStream spritesheetStream = getClass().getResourceAsStream(tilesJson.spritesheet);
            if (spritesheetStream == null) {
                throw new RuntimeException("Spritesheet não encontrada: " + tilesJson.spritesheet);
            }

            this.spritesheet = ImageIO.read(spritesheetStream);

            tiles = new HashMap<>();
            for (TileDefinition def : tilesJson.tiles) {
                Tile tile = new Tile();
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

    public void loadMapJson(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                throw new RuntimeException("Mapa JSON não encontrado: " + path);
            }

            Gson gson = new Gson();
            MapJson mapJson = gson.fromJson(new java.io.InputStreamReader(is), MapJson.class);

            this.mapWidth = mapJson.mapWidth;
            this.mapHeight = mapJson.mapHeight;
            this.mapLayers = mapJson.layers;
            
            // Mostrar mensagem do mapa se presente
            if (mapJson.message != null && !mapJson.message.isEmpty()) {
                gamePanel.getGameUI().showCenterMessage(mapJson.message, 4000);
            }

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
        public Boolean interactive = false;
        public String toMap;
        public int toX;
        public int toY;
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
        public String message;
        public List<MapLayer> layers;
    }

    /**
     * Renderiza todas as camadas do mapa (método original mantido para compatibilidade).
     * @param g2 Contexto gráfico
     */
    public void draw(Graphics2D g2) {
        drawBackgroundLayers(g2);
        
        // Renderizar o player
        gamePanel.getPlayer().draw(g2);
        
        drawOverlayLayers(g2);
    }

    /**
     * Renderiza apenas as camadas de fundo (sem overlay).
     * @param g2 Contexto gráfico
     */
    public void drawBackgroundLayers(Graphics2D g2) {
        int playerWorldX = gamePanel.getPlayer().getWorldX();
        int playerWorldY = gamePanel.getPlayer().getWorldY();
        int screenX = gamePanel.getPlayer().getScreenX();
        int screenY = gamePanel.getPlayer().getScreenY();
        int playerSize = gamePanel.getPlayerSize();

        int firstCol = Math.max((playerWorldX - screenX) / tileSize, 0);
        int lastCol = Math.min((playerWorldX + screenX + playerSize) / tileSize, mapWidth - 1);
        int firstRow = Math.max((playerWorldY - screenY) / tileSize, 0);
        int lastRow = Math.min((playerWorldY + screenY + playerSize) / tileSize, mapHeight - 1);

        List<MapLayer> normalLayers = new ArrayList<>();
        List<MapLayer> objectLayers = new ArrayList<>();
        List<MapLayer> npcLayers = new ArrayList<>();
        List<MapLayer> monsterLayers = new ArrayList<>();

        for (MapLayer layer : mapLayers) {
            String layerName = layer.name.toLowerCase();
            if (layerName.contains("overlay")) {
                // Pular camadas overlay - serão renderizadas separadamente
                continue;
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

        // 3. Renderizar NPCs apenas se houver NPCs reais no GamePanel
        if (gamePanel.getNpcs() != null && !gamePanel.getNpcs().isEmpty()) {
          for (MapLayer layer : npcLayers) {
              renderLayer(g2, layer, firstCol, lastCol, firstRow, lastRow, playerWorldX, playerWorldY, screenX, screenY);
          }
        }

        // 4. Renderizar Monsters apenas se houver monstros reais no GamePanel
        if (gamePanel.getNpcs() != null && !gamePanel.getNpcs().isEmpty()) {
          for (MapLayer layer : monsterLayers) {
            renderLayer(g2, layer, firstCol, lastCol, firstRow, lastRow, playerWorldX, playerWorldY, screenX, screenY);
          }
        }
    }

    /**
     * Renderiza apenas as camadas overlay (acima do player).
     * @param g2 Contexto gráfico
     */
    public void drawOverlayLayers(Graphics2D g2) {
        int playerWorldX = gamePanel.getPlayer().getWorldX();
        int playerWorldY = gamePanel.getPlayer().getWorldY();
        int screenX = gamePanel.getPlayer().getScreenX();
        int screenY = gamePanel.getPlayer().getScreenY();
        int playerSize = gamePanel.getPlayerSize();

        int firstCol = Math.max((playerWorldX - screenX) / tileSize, 0);
        int lastCol = Math.min((playerWorldX + screenX + playerSize) / tileSize, mapWidth - 1);
        int firstRow = Math.max((playerWorldY - screenY) / tileSize, 0);
        int lastRow = Math.min((playerWorldY + screenY + playerSize) / tileSize, mapHeight - 1);

        List<MapLayer> overlayLayers = new ArrayList<>();

        for (MapLayer layer : mapLayers) {
            String layerName = layer.name.toLowerCase();
            if (layerName.contains("overlay")) {
                overlayLayers.add(layer);
            }
        }

        // Renderizar layers overlay (acima do player)
        for (MapLayer layer : overlayLayers) {
            renderLayer(g2, layer, firstCol, lastCol, firstRow, lastRow, playerWorldX, playerWorldY, screenX, screenY);
        }
    }

    private void renderLayer(Graphics2D g2, MapLayer layer, int firstCol, int lastCol, int firstRow, int lastRow,
                           int playerWorldX, int playerWorldY, int screenX, int screenY) {

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
     * Retorna os tiles de teleporte das layers do mapa.
     */
    public List<MapTile> getTeleportTiles() {
        List<MapTile> teleportTiles = new ArrayList<>();

        for (MapLayer layer : mapLayers) {
            String layerName = layer.name.toLowerCase();
            if (layerName.contains("teleport")) {
                teleportTiles.addAll(layer.tiles);
            }
        }

        return teleportTiles;
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
