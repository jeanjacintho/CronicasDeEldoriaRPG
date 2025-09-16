package br.com.cronicasdeeldoria.game;

import br.com.cronicasdeeldoria.entity.Entity;
import br.com.cronicasdeeldoria.entity.object.MapObject;
import java.awt.Rectangle;
import java.util.Map;
import java.util.HashMap;

/**
 * Sistema otimizado de verificação de colisões.
 * Implementa cache de hitboxes e verificação espacial para melhor performance.
 */
public class ColisionChecker {
  private GamePanel gamePanel;
  
  // Cache de hitboxes para evitar criação excessiva de objetos
  private Map<Entity, Rectangle> entityHitboxCache = new HashMap<>();
  private Map<MapObject, Rectangle> objectHitboxCache = new HashMap<>();
  
  // Cache de verificação de colisão para evitar verificações redundantes
  private Map<String, Boolean> collisionCache = new HashMap<>();
  private long lastCacheClear = 0;
  private static final long CACHE_CLEAR_INTERVAL = 1000; // Limpar cache a cada 1 segundo

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

  /**
   * Verifica colisão entre entidades com otimizações de performance.
   * @param entity Entidade para verificar colisão.
   * @param entities Lista de entidades para verificar.
   */
  public void checkEntity(Entity entity, java.util.List<? extends Entity> entities) {
    if (entities == null || entities.isEmpty()) return;
    
    // Limpar cache periodicamente
    clearCacheIfNeeded();
    
    // Obter hitbox da entidade (usar cache se disponível)
    Rectangle entityBox = getEntityHitbox(entity);
    if (entityBox == null) return;

    // Verificar colisão baseada na direção do movimento
    Rectangle futureEntityBox = getFutureHitbox(entity, entityBox);

    for (Entity targetEntity : entities) {
      if (targetEntity != entity && targetEntity.getHitbox() != null) {
        // Verificar cache de colisão
        String cacheKey = getCollisionCacheKey(entity, targetEntity);
        Boolean cachedResult = collisionCache.get(cacheKey);
        
        if (cachedResult != null) {
          if (cachedResult) {
            entity.setCollisionOn(true);
            break;
          }
          continue;
        }
        
        // Obter hitbox da entidade alvo
        Rectangle targetBox = getEntityHitbox(targetEntity);
        if (targetBox == null) continue;
        
        // Verificar colisão
        boolean collides = futureEntityBox.intersects(targetBox);
        collisionCache.put(cacheKey, collides);
        
        if (collides) {
          entity.setCollisionOn(true);
          break;
        }
      }
    }
  }

  /**
   * Verifica colisão com objetos com otimizações de performance.
   * @param entity Entidade para verificar colisão.
   */
  public void checkObject(Entity entity) {
    if (gamePanel.getObjectManager() == null) return;
    
    // Limpar cache periodicamente
    clearCacheIfNeeded();
    
    // Obter hitbox da entidade
    Rectangle entityBox = getEntityHitbox(entity);
    if (entityBox == null) return;

    // Verificar colisão baseada na direção do movimento
    Rectangle futureEntityBox = getFutureHitbox(entity, entityBox);

    for (MapObject obj : gamePanel.getObjectManager().getActiveObjects()) {
      if (obj.isActive() && obj.hasCollision() && obj.getHitbox() != null) {
        // Verificar cache de colisão
        String cacheKey = getObjectCollisionCacheKey(entity, obj);
        Boolean cachedResult = collisionCache.get(cacheKey);
        
        if (cachedResult != null) {
          if (cachedResult) {
            entity.setCollisionOn(true);
            break;
          }
          continue;
        }
        
        // Obter hitbox do objeto
        Rectangle objBox = getObjectHitbox(obj);
        if (objBox == null) continue;
        
        // Verificar colisão
        boolean collides = futureEntityBox.intersects(objBox);
        collisionCache.put(cacheKey, collides);
        
        if (collides) {
          entity.setCollisionOn(true);
          break;
        }
      }
    }
  }

  /**
   * Obtém a hitbox de uma entidade usando cache.
   */
  private Rectangle getEntityHitbox(Entity entity) {
    Rectangle cached = entityHitboxCache.get(entity);
    if (cached != null) {
      // Atualizar posição da hitbox em cache
      cached.x = entity.getWorldX() + entity.getHitbox().x;
      cached.y = entity.getWorldY() + entity.getHitbox().y;
      return cached;
    }
    
    // Criar nova hitbox e adicionar ao cache
    Rectangle hitbox = new Rectangle(
      entity.getWorldX() + entity.getHitbox().x,
      entity.getWorldY() + entity.getHitbox().y,
      entity.getHitbox().width,
      entity.getHitbox().height
    );
    entityHitboxCache.put(entity, hitbox);
    return hitbox;
  }

  /**
   * Obtém a hitbox de um objeto usando cache.
   */
  private Rectangle getObjectHitbox(MapObject obj) {
    Rectangle cached = objectHitboxCache.get(obj);
    if (cached != null) {
      // Atualizar posição da hitbox em cache
      cached.x = obj.getWorldX() + obj.getHitbox().x;
      cached.y = obj.getWorldY() + obj.getHitbox().y;
      return cached;
    }
    
    // Criar nova hitbox e adicionar ao cache
    Rectangle hitbox = new Rectangle(
      obj.getWorldX() + obj.getHitbox().x,
      obj.getWorldY() + obj.getHitbox().y,
      obj.getHitbox().width,
      obj.getHitbox().height
    );
    objectHitboxCache.put(obj, hitbox);
    return hitbox;
  }

  /**
   * Calcula a hitbox futura baseada na direção do movimento.
   */
  private Rectangle getFutureHitbox(Entity entity, Rectangle currentBox) {
    Rectangle futureBox = new Rectangle(currentBox);
    
    switch (entity.getDirection()) {
      case "up": futureBox.y -= entity.getSpeed(); break;
      case "down": futureBox.y += entity.getSpeed(); break;
      case "left": futureBox.x -= entity.getSpeed(); break;
      case "right": futureBox.x += entity.getSpeed(); break;
      default: break;
    }
    
    return futureBox;
  }

  /**
   * Gera chave única para cache de colisão entre entidades.
   */
  private String getCollisionCacheKey(Entity entity1, Entity entity2) {
    return entity1.hashCode() + "_" + entity2.hashCode() + "_" + entity1.getDirection();
  }

  /**
   * Gera chave única para cache de colisão entre entidade e objeto.
   */
  private String getObjectCollisionCacheKey(Entity entity, MapObject obj) {
    return entity.hashCode() + "_obj_" + obj.hashCode() + "_" + entity.getDirection();
  }

  /**
   * Limpa o cache periodicamente para evitar vazamentos de memória.
   */
  private void clearCacheIfNeeded() {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastCacheClear > CACHE_CLEAR_INTERVAL) {
      collisionCache.clear();
      lastCacheClear = currentTime;
    }
  }

  /**
   * Limpa todos os caches (chamado quando entidades são removidas).
   */
  public void clearAllCaches() {
    entityHitboxCache.clear();
    objectHitboxCache.clear();
    collisionCache.clear();
    lastCacheClear = System.currentTimeMillis();
  }
}
