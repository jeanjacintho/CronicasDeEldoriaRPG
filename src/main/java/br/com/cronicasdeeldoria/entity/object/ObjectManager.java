package br.com.cronicasdeeldoria.entity.object;

import br.com.cronicasdeeldoria.tile.TileManager.MapObjectDefinition;
import br.com.cronicasdeeldoria.tile.TileManager.MapObjectInstance;
import br.com.cronicasdeeldoria.game.GamePanel;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gerencia os objetos do mapa, ativando/desativando conforme a proximidade do jogador.
 */
public class ObjectManager {
    private final GamePanel gamePanel;
    private final Map<String, MapObjectDefinition> objectDefinitions;
    private final List<MapObjectInstance> rawObjects;
    private final List<GameObject> activeObjects = new ArrayList<>();
    private final int buffer = 2;

    /**
     * Cria um novo gerenciador de objetos.
     * @param gamePanel Painel do jogo.
     * @param objectDefinitions Definições dos objetos do mapa.
     * @param rawObjects Instâncias brutas dos objetos do mapa.
     */
    public ObjectManager(GamePanel gamePanel, Map<String, MapObjectDefinition> objectDefinitions, List<MapObjectInstance> rawObjects) {
        this.gamePanel = gamePanel;
        this.objectDefinitions = objectDefinitions;
        this.rawObjects = rawObjects;
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

        for (MapObjectInstance raw : rawObjects) {
            int objX = raw.posicao[0];
            int objY = raw.posicao[1];
            if (objX >= firstCol && objX <= lastCol && objY >= firstRow && objY <= lastRow) {
                boolean alreadyActive = activeObjects.stream().anyMatch(obj -> obj.getWorldX() / tileSize == objX && obj.getWorldY() / tileSize == objY && obj.getName().equals(raw.id));
                if (!alreadyActive) {
                    GameObject obj = instantiateGameObject(raw);
                    if (obj != null) activeObjects.add(obj);
                }
            }
        }
    }

    private GameObject instantiateGameObject(MapObjectInstance raw) {
        MapObjectDefinition def = objectDefinitions.get(raw.id);
        if (def == null) return null;
        if (raw.id.equals("chest")) {
            return new Chest(raw.posicao[0] * gamePanel.getTileSize(), raw.posicao[1] * gamePanel.getTileSize(), def.sprites);
        }
        if (raw.id.equals("key")) {
            return new Key(raw.posicao[0] * gamePanel.getTileSize(), raw.posicao[1] * gamePanel.getTileSize(), def.sprites);
        }
        // Adicione outros tipos aqui
        return null;
    }

    public List<GameObject> getActiveObjects() {
        return activeObjects;
    }

    public List<MapObjectInstance> getRawObjects() {
        return rawObjects;
    }

    /**
     * Remove um objeto da lista de objetos brutos.
     * @param obj Objeto a ser removido.
     */
    public void removeRawObject(GameObject obj) {
        int tileSize = gamePanel.getTileSize();
        rawObjects.removeIf(raw ->
            raw.id.equals(obj.getName()) &&
            raw.posicao[0] == obj.getWorldX() / tileSize &&
            raw.posicao[1] == obj.getWorldY() / tileSize
        );
    }

    /**
     * Desenha os objetos ativos na tela.
     * @param g2 Contexto gráfico.
     */
    public void drawObjects(Graphics2D g2) {
        int tileSize = gamePanel.getTileSize();
        int playerWorldX = gamePanel.getPlayer().getWorldX();
        int playerWorldY = gamePanel.getPlayer().getWorldY();
        int screenX = gamePanel.getPlayer().getScreenX();
        int screenY = gamePanel.getPlayer().getScreenY();
        for (GameObject obj : new ArrayList<>(activeObjects)) {
            if (obj.isActive()) {
                obj.draw(g2, playerWorldX - screenX, playerWorldY - screenY, tileSize);
            }
        }
    }
}
