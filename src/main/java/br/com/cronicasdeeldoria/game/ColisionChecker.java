package br.com.cronicasdeeldoria.game;

import br.com.cronicasdeeldoria.entity.Entity;

public class ColisionChecker {
  GamePanel gamePanel;

  public ColisionChecker(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
  }

  public void checkTile(Entity entity) {
    int entityLeftWorldX = entity.getWorldX() + entity.getHitbox().x;
    int entityRightWorldX = entity.getWorldX() + entity.getHitbox().x + entity.getHitbox().width - 1;
    int entityTopWorldY = entity.getWorldY() + entity.getHitbox().y;
    int entityBottomWorldY = entity.getWorldY() + entity.getHitbox().y + entity.getHitbox().height - 1;

    int entityLeftCol = entityLeftWorldX / gamePanel.getTileSize();
    int entityRightCol = entityRightWorldX / gamePanel.getTileSize();
    int entityTopRow = entityTopWorldY / gamePanel.getTileSize();
    int entityBottomRow = entityBottomWorldY / gamePanel.getTileSize();

    int mapRows = gamePanel.getTileManager().getMapTileNumbers().length;
    int mapCols = gamePanel.getTileManager().getMapTileNumbers()[0].length;
    int tileSize = gamePanel.getTileSize();
    switch (entity.getDirection()) {
      case "up": {
        entityTopRow = (entityTopWorldY - entity.getSpeed()) / tileSize;
        int firstCol = entityLeftWorldX / tileSize;
        int lastCol = entityRightWorldX / tileSize;
        for (int col = firstCol; col <= lastCol; col++) {
          if (entityTopRow >= 0 && entityTopRow < mapRows && col >= 0 && col < mapCols) {
            int tileNum = gamePanel.getTileManager().getMapTileNumbers()[entityTopRow][col];
            if (gamePanel.getTileManager().getTiles()[tileNum].collision) {
              entity.setCollisionOn(true);
              break;
            }
          }
        }
        break;
      }
      case "down": {
        entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / tileSize;
        int firstCol = entityLeftWorldX / tileSize;
        int lastCol = entityRightWorldX / tileSize;
        for (int col = firstCol; col <= lastCol; col++) {
          if (entityBottomRow >= 0 && entityBottomRow < mapRows && col >= 0 && col < mapCols) {
            int tileNum = gamePanel.getTileManager().getMapTileNumbers()[entityBottomRow][col];
            if (gamePanel.getTileManager().getTiles()[tileNum].collision) {
              entity.setCollisionOn(true);
              break;
            }
          }
        }
        break;
      }
      case "left": {
        entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / tileSize;
        int firstRow = entityTopWorldY / tileSize;
        int lastRow = entityBottomWorldY / tileSize;
        for (int row = firstRow; row <= lastRow; row++) {
          if (row >= 0 && row < mapRows && entityLeftCol >= 0 && entityLeftCol < mapCols) {
            int tileNum = gamePanel.getTileManager().getMapTileNumbers()[row][entityLeftCol];
            if (gamePanel.getTileManager().getTiles()[tileNum].collision) {
              entity.setCollisionOn(true);
              break;
            }
          }
        }
        break;
      }
      case "right": {
        entityRightCol = (entityRightWorldX + entity.getSpeed()) / tileSize;
        int firstRow = entityTopWorldY / tileSize;
        int lastRow = entityBottomWorldY / tileSize;
        for (int row = firstRow; row <= lastRow; row++) {
          if (row >= 0 && row < mapRows && entityRightCol >= 0 && entityRightCol < mapCols) {
            int tileNum = gamePanel.getTileManager().getMapTileNumbers()[row][entityRightCol];
            if (gamePanel.getTileManager().getTiles()[tileNum].collision) {
              entity.setCollisionOn(true);
              break;
            }
          }
        }
        break;
      }
      default:
        break;
    }
  }
}
