package br.com.cronicasdeeldoria.game.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

/**
 * Gerencia as interações e exibe as teclas de interação
 */
public class InteractionManager {

    private KeyboardMapper keyboardMapper;
    private List<InteractionPoint> interactionPoints;

    public InteractionManager(KeyboardMapper keyboardMapper) {
        this.keyboardMapper = keyboardMapper;
        this.interactionPoints = new ArrayList<>();
    }

    /**
     * Adiciona um ponto de interação
     * @param worldX Posição X no mundo (em pixels)
     * @param worldY Posição Y no mundo (em pixels)
     * @param key Tecla para interação (ex: "E")
     * @param entityType Tipo da entidade (ex: "player", "item", "npc")
     */
    public void addInteractionPoint(int worldX, int worldY, String key, String entityType) {
        if (keyboardMapper.hasKey(key)) {
            //System.out.println("Adicionando ponto de interação: " + entityType + " em (" + worldX + ", " + worldY + ") com tecla '" + key + "'");
            interactionPoints.add(new InteractionPoint(worldX, worldY, key, entityType));
        } else {
            System.out.println("Tecla '" + key + "' não encontrada no mapeamento para " + entityType + " em (" + worldX + ", " + worldY + ")");
        }
    }

    /**
     * Remove todos os pontos de interação
     */
    public void clearInteractionPoints() {
        interactionPoints.clear();
    }

    /**
     * Remove pontos de interação de um tipo específico
     * @param entityType Tipo da entidade
     */
    public void removeInteractionPointsByType(String entityType) {
        interactionPoints.removeIf(point -> point.getEntityType().equals(entityType));
    }

    /**
     * Verifica se uma entidade específica deve mostrar a tecla de interação
     * @param worldX Posição X da entidade no mundo
     * @param worldY Posição Y da entidade no mundo
     * @param entityType Tipo da entidade
     * @return Tecla a ser mostrada ou null se não deve mostrar
     */
    public String getInteractionKeyForEntity(int worldX, int worldY, String entityType) {
        for (InteractionPoint point : interactionPoints) {
            if (point.getWorldX() == worldX && point.getWorldY() == worldY &&
                point.getEntityType().equals(entityType)) {
                return point.getKey();
            }
        }
        return null;
    }

    /**
     * Renderiza uma tecla de interação centralizada em um tile.
     * @param g2d Graphics2D para renderização
     * @param worldX Posição X do tile no mundo
     * @param worldY Posição Y do tile no mundo
     * @param screenX Posição X do tile na tela
     * @param screenY Posição Y do tile na tela
     * @param entityType Tipo da entidade
     * @param tileSize Tamanho do tile
     */
    public void renderInteractionKeyForTile(Graphics2D g2d, int worldX, int worldY,
                                           int screenX, int screenY, String entityType, int tileSize) {
        String key = getInteractionKeyForEntity(worldX, worldY, entityType);
        if (key != null) {
            // Tentar usar ImageIcon para GIFs animados primeiro
            ImageIcon keyImageIcon = keyboardMapper.getKeyImageIcon(key);
            if (keyImageIcon != null) {
                int keyScreenX = screenX + (tileSize / 2) - (tileSize / 2);
                int keyScreenY = screenY + (tileSize / 2) - (tileSize / 2) - 40;

                g2d.drawImage(keyImageIcon.getImage(), keyScreenX, keyScreenY, tileSize, tileSize, null);
            } else {
                BufferedImage keySprite = keyboardMapper.getKeySprite(key);
                if (keySprite != null) {
                    int keyScreenX = screenX + (tileSize / 2) - (tileSize / 2);
                    int keyScreenY = screenY + (tileSize / 2) - (tileSize / 2) - 40;

                    g2d.drawImage(keySprite, keyScreenX, keyScreenY, tileSize, tileSize, null);
                }
            }
        }
    }

    /**
     * Renderiza uma tecla de interação para uma entidade específica
     * @param g2d Graphics2D para renderização
     * @param worldX Posição X da entidade no mundo
     * @param worldY Posição Y da entidade no mundo
     * @param screenX Posição X da entidade na tela
     * @param screenY Posição Y da entidade na tela
     * @param entityType Tipo da entidade
     * @param tileSize Tamanho do tile
     */
    public void renderInteractionKeyForEntity(Graphics2D g2d, int worldX, int worldY,
                                             int screenX, int screenY, String entityType, int tileSize) {
        String key = getInteractionKeyForEntity(worldX, worldY, entityType);
        if (key != null) {
            System.out.println("Renderizando tecla '" + key + "' para " + entityType + " em (" + worldX + ", " + worldY + ") -> (" + screenX + ", " + screenY + ")");

            // Tentar usar ImageIcon para GIFs animados primeiro
            ImageIcon keyImageIcon = keyboardMapper.getKeyImageIcon(key);
            if (keyImageIcon != null) {
                int entitySize = tileSize * 2;
                int keyOffsetX = (entitySize - tileSize) / 2;

                // Posicionar a tecla acima da cabeça da entidade
                int keyOffsetY = entitySize + (tileSize / 4);

                int keyScreenX = screenX - keyOffsetX + tileSize;
                int keyScreenY = screenY - keyOffsetY + (tileSize * 2) - 8;

                System.out.println("Renderizando ImageIcon em (" + keyScreenX + ", " + keyScreenY + ") com tamanho " + tileSize);
                g2d.drawImage(keyImageIcon.getImage(), keyScreenX, keyScreenY, tileSize, tileSize, null);
            } else {
                BufferedImage keySprite = keyboardMapper.getKeySprite(key);
                if (keySprite != null) {
                    int entitySize = tileSize * 2;
                    int keyOffsetX = (entitySize - tileSize) / 2;

                    // Posicionar a tecla acima da cabeça da entidade
                    int keyOffsetY = entitySize + (tileSize / 4);

                    int keyScreenX = screenX - keyOffsetX + tileSize;
                    int keyScreenY = screenY - keyOffsetY + (tileSize * 2) - 8;

                    System.out.println("Renderizando BufferedImage em (" + keyScreenX + ", " + keyScreenY + ") com tamanho " + tileSize);
                    g2d.drawImage(keySprite, keyScreenX, keyScreenY, tileSize, tileSize, null);
                } else {
                    System.out.println("Nenhuma imagem encontrada para a tecla '" + key + "'");
                }
            }
        }
    }

    /**
     * Obtém o número de pontos de interação ativos
     * @return Número de pontos de interação
     */
    public int getInteractionPointsCount() {
        return interactionPoints.size();
    }

    /**
     * Renderiza as teclas de interação
     * @param g2d Graphics2D para renderização
     * @param screenX Offset X da tela
     * @param screenY Offset Y da tela
     * @param tileSize Tamanho do tile para escalar a tecla
     */
    public void renderInteractionKeys(Graphics2D g2d, int screenX, int screenY, int tileSize) {
        for (InteractionPoint point : interactionPoints) {
            BufferedImage keySprite = keyboardMapper.getKeySprite(point.getKey());
            if (keySprite != null) {
                int worldPosX = point.getWorldX();
                int worldPosY = point.getWorldY();

                int entitySize = tileSize * 2;
                int keyOffsetX = (entitySize - tileSize) / 2;

                // Posicionar a tecla acima da cabeça da entidade
                int keyOffsetY = entitySize + (tileSize / 4);

                int screenPosX = worldPosX - keyOffsetX - screenX;
                int screenPosY = worldPosY - keyOffsetY - screenY;

                g2d.drawImage(keySprite, screenPosX, screenPosY, tileSize, tileSize, null);
            }
        }
    }

    /**
     * Método de debug para verificar o estado do sistema
     */
    public void debugSystem() {
        System.out.println("=== DEBUG INTERACTION MANAGER ===");
        System.out.println("KeyboardMapper disponível: " + (keyboardMapper != null));
        if (keyboardMapper != null) {
            System.out.println("Teclas mapeadas: " + keyboardMapper.getMappedKeys());
            System.out.println("Tecla 'E' disponível: " + keyboardMapper.hasKey("E"));
        }
        System.out.println("Pontos de interação ativos: " + interactionPoints.size());
        for (InteractionPoint point : interactionPoints) {
            System.out.println("  - " + point.getEntityType() + " em (" + point.getWorldX() + ", " + point.getWorldY() + ") com tecla '" + point.getKey() + "'");
        }
        System.out.println("=== FIM DEBUG ===");
    }

    /**
     * Classe interna para representar um ponto de interação
     */
    private static class InteractionPoint {
        private int worldX, worldY;
        private String key;
        private String entityType;

        public InteractionPoint(int worldX, int worldY, String key, String entityType) {
            this.worldX = worldX;
            this.worldY = worldY;
            this.key = key;
            this.entityType = entityType;
        }

        public int getWorldX() { return worldX; }
        public int getWorldY() { return worldY; }
        public String getKey() { return key; }
        public String getEntityType() { return entityType; }
    }
}
