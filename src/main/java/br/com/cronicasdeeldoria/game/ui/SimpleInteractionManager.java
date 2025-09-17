package br.com.cronicasdeeldoria.game.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as interações e exibe as teclas de interação - Versão Simplificada
 */
public class SimpleInteractionManager {
    
    private InteractionRenderer renderer;
    private List<InteractionPoint> interactionPoints;
    
    public SimpleInteractionManager() {
        this.renderer = new InteractionRenderer();
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
        if (renderer.hasKey(key)) {
            interactionPoints.add(new InteractionPoint(worldX, worldY, key, entityType));
        }
    }
    
    /**
     * Remove todos os pontos de interação
     */
    public void clearInteractionPoints() {
        interactionPoints.clear();
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
            renderer.renderInteractionKey(g2d, screenX, screenY, key, tileSize);
        }
    }
    
    /**
     * Renderiza uma tecla de interação centralizada em um tile
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
            renderer.renderInteractionKey(g2d, screenX, screenY, key, tileSize);
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
