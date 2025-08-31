package br.com.cronicasdeeldoria.entity.object;

import br.com.cronicasdeeldoria.tile.TileManager.MapTile;
import br.com.cronicasdeeldoria.game.GamePanel;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia os objetos do mapa, ativando/desativando conforme a proximidade do jogador.
 */
public class ObjectManager {
    private final GamePanel gamePanel;
    private final ObjectSpriteLoader objectSpriteLoader;
    private final List<MapTile> rawObjectTiles;
    private final List<MapObject> activeObjects = new ArrayList<>();
    private final int buffer = 2;

    /**
     * Cria um novo gerenciador de objetos.
     * @param gamePanel Painel do jogo.
     * @param objectSpriteLoader Loader de sprites de objetos.
     * @param rawObjectTiles Tiles brutos dos objetos do mapa.
     */
    public ObjectManager(GamePanel gamePanel, ObjectSpriteLoader objectSpriteLoader, List<MapTile> rawObjectTiles) {
        this.gamePanel = gamePanel;
        this.objectSpriteLoader = objectSpriteLoader;
        this.rawObjectTiles = rawObjectTiles;
    }

    /**
     * Atualiza a lista de objetos ativos conforme a posição do jogador.
     * @param playerTileX Tile X do jogador.
     * @param playerTileY Tile Y do jogador.
     */
    public void updateActiveObjects(int playerTileX, int playerTileY) {
        int tileSize = gamePanel.getTileSize();
        int playerWorldX = gamePanel.getPlayer().getWorldX();
        int playerWorldY = gamePanel.getPlayer().getWorldY();
        int screenX = gamePanel.getPlayer().getScreenX();
        int screenY = gamePanel.getPlayer().getScreenY();
        int playerSize = gamePanel.getPlayerSize();
        int worldCols = gamePanel.getTileManager().getMapWidth();
        int worldRows = gamePanel.getTileManager().getMapHeight();
        int firstCol = Math.max((playerWorldX - screenX) / tileSize - buffer, 0);
        int lastCol  = Math.min((playerWorldX + screenX + playerSize) / tileSize + buffer, worldCols - 1);
        int firstRow = Math.max((playerWorldY - screenY) / tileSize - buffer, 0);
        int lastRow  = Math.min((playerWorldY + screenY + playerSize) / tileSize + buffer, worldRows - 1);

        activeObjects.removeIf(obj -> {
            int objTileX = obj.getWorldX() / tileSize;
            int objTileY = obj.getWorldY() / tileSize;
            return objTileX < firstCol || objTileX > lastCol || objTileY < firstRow || objTileY > lastRow;
        });

        for (MapTile raw : rawObjectTiles) {
            int objX = raw.x;
            int objY = raw.y;
            if (objX >= firstCol && objX <= lastCol && objY >= firstRow && objY <= lastRow) {
                boolean alreadyActive = activeObjects.stream().anyMatch(obj -> obj.getWorldX() / tileSize == objX && obj.getWorldY() / tileSize == objY && obj.getObjectId().equals(raw.id));
                if (!alreadyActive) {
                    MapObject obj = instantiateMapObject(raw);
                    if (obj != null) {
                        activeObjects.add(obj);
                    }
                }
                

            }
        }
    }

    private MapObject instantiateMapObject(MapTile raw) {
        ObjectSpriteLoader.ObjectDefinition objDef = objectSpriteLoader.getObjectDefinition(raw.id);
        if (objDef == null) {
            System.err.println("DEBUG: Definição não encontrada para objeto: " + raw.id);
            return null;
        }
        
        int x = raw.x * gamePanel.getTileSize();
        int y = raw.y * gamePanel.getTileSize();
        
        return new MapObject(
            objDef.id,
            objDef.name,
            x,
            y,
            objDef.size[0], // width
            objDef.size[1], // height
            objDef.collision,
            objDef
        );
    }

    public List<MapObject> getActiveObjects() {
        return activeObjects;
    }

    public List<MapTile> getRawObjectTiles() {
        return rawObjectTiles;
    }

    /**
     * Remove um objeto da lista de objetos brutos.
     * @param obj Objeto a ser removido.
     */
    public void removeRawObject(MapObject obj) {
        int tileSize = gamePanel.getTileSize();
        rawObjectTiles.removeIf(raw ->
            raw.id.equals(obj.getObjectId()) &&
            raw.x == obj.getWorldX() / tileSize &&
            raw.y == obj.getWorldY() / tileSize
        );
    }

    /**
     * Desenha os objetos ativos na tela.
     * @param g2 Contexto gráfico.
     */
    public void drawObjects(Graphics2D g2) {
        int tileSize = gamePanel.getTileSize();
        int screenX = gamePanel.getPlayer().getScreenX();
        int screenY = gamePanel.getPlayer().getScreenY();
        for (MapObject obj : new ArrayList<>(activeObjects)) {
            if (obj.isActive()) {
                obj.draw(g2, objectSpriteLoader, tileSize, gamePanel.getPlayer(), screenX, screenY);
            }
        }
    }
}
