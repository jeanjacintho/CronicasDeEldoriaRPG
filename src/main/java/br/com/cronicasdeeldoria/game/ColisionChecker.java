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

    int tileSize = gamePanel.getTileSize();

    switch (entity.getDirection()) {
      case "up": {
        entityTopRow = (entityTopWorldY - entity.getSpeed()) / tileSize;
        int firstCol = entityLeftWorldX / tileSize;
        int lastCol = entityRightWorldX / tileSize;
        for (int col = firstCol; col <= lastCol; col++) {
          if (gamePanel.getTileManager().isCollisionAt(col, entityTopRow)) {
            entity.setCollisionOn(true);
            break;
          }
        }
        break;
      }
      case "down": {
        entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / tileSize;
        int firstCol = entityLeftWorldX / tileSize;
        int lastCol = entityRightWorldX / tileSize;
        for (int col = firstCol; col <= lastCol; col++) {
          if (gamePanel.getTileManager().isCollisionAt(col, entityBottomRow)) {
            entity.setCollisionOn(true);
            break;
          }
        }
        break;
      }
      case "left": {
        entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / tileSize;
        int firstRow = entityTopWorldY / tileSize;
        int lastRow = entityBottomWorldY / tileSize;
        for (int row = firstRow; row <= lastRow; row++) {
          if (gamePanel.getTileManager().isCollisionAt(entityLeftCol, row)) {
            entity.setCollisionOn(true);
            break;
          }
        }
        break;
      }
      case "right": {
        entityRightCol = (entityRightWorldX + entity.getSpeed()) / tileSize;
        int firstRow = entityTopWorldY / tileSize;
        int lastRow = entityBottomWorldY / tileSize;
        for (int row = firstRow; row <= lastRow; row++) {
          if (gamePanel.getTileManager().isCollisionAt(entityRightCol, row)) {
            entity.setCollisionOn(true);
            break;
          }
        }
        break;
      }
      default:
        break;
    }
  }

  public void checkEntity(Entity entity, java.util.List<? extends Entity> entities) {
    if (entities != null) {
      for (Entity targetEntity : entities) {
        if (targetEntity != entity) {
          // Verificar se as hitboxes se intersectam
          if (entity.getHitbox() != null && targetEntity.getHitbox() != null) {
            java.awt.Rectangle entityBox = new java.awt.Rectangle(
              entity.getWorldX() + entity.getHitbox().x,
              entity.getWorldY() + entity.getHitbox().y,
              entity.getHitbox().width,
              entity.getHitbox().height
            );

            java.awt.Rectangle targetBox = new java.awt.Rectangle(
              targetEntity.getWorldX() + targetEntity.getHitbox().x,
              targetEntity.getWorldY() + targetEntity.getHitbox().y,
              targetEntity.getHitbox().width,
              targetEntity.getHitbox().height
            );

            // Verificar colisão baseada na direção do movimento
            java.awt.Rectangle futureEntityBox = new java.awt.Rectangle(entityBox);

            switch (entity.getDirection()) {
              case "up": futureEntityBox.y -= entity.getSpeed(); break;
              case "down": futureEntityBox.y += entity.getSpeed(); break;
              case "left": futureEntityBox.x -= entity.getSpeed(); break;
              case "right": futureEntityBox.x += entity.getSpeed(); break;
              default: break;
            }
            if (futureEntityBox.intersects(targetBox)) {
              entity.setCollisionOn(true);
              break;
            }
          }
        }
      }
    }
  }

  /**
   * Verifica colisão com objetos.
   * @param entity Entidade para verificar colisão.
   */
  public void checkObject(Entity entity) {
    if (gamePanel.getObjectManager() != null) {
      for (br.com.cronicasdeeldoria.entity.object.MapObject obj : gamePanel.getObjectManager().getActiveObjects()) {
        if (obj.isActive() && obj.hasCollision()) {
          // Verificar se as hitboxes se intersectam
          if (entity.getHitbox() != null && obj.getHitbox() != null) {
            java.awt.Rectangle entityBox = new java.awt.Rectangle(
              entity.getWorldX() + entity.getHitbox().x,
              entity.getWorldY() + entity.getHitbox().y,
              entity.getHitbox().width,
              entity.getHitbox().height
            );

            java.awt.Rectangle objBox = new java.awt.Rectangle(
              obj.getWorldX() + obj.getHitbox().x,
              obj.getWorldY() + obj.getHitbox().y,
              obj.getHitbox().width,
              obj.getHitbox().height
            );

            // Verificar colisão baseada na direção do movimento
            java.awt.Rectangle futureEntityBox = new java.awt.Rectangle(entityBox);

            switch (entity.getDirection()) {
              case "up": futureEntityBox.y -= entity.getSpeed(); break;
              case "down": futureEntityBox.y += entity.getSpeed(); break;
              case "left": futureEntityBox.x -= entity.getSpeed(); break;
              case "right": futureEntityBox.x += entity.getSpeed(); break;
              default: break;
            }
            if (futureEntityBox.intersects(objBox)) {
              entity.setCollisionOn(true);
              break;
            }
          }
        }
      }
    }
  }
}
