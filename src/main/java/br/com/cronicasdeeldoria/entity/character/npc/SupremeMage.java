package br.com.cronicasdeeldoria.entity.character.npc;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.game.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Boss final da quest principal - Mago Supremo.
 * Segue o jogador e inicia batalha quando próximo o suficiente.
 */
public class SupremeMage extends Npc {
    private boolean isFollowingPlayer;
    private int followDistance;
    private int battleTriggerDistance;
    
    private List<Point> currentPath;
    private int pathIndex;
    private long lastPathUpdate;
    private static final long PATH_UPDATE_INTERVAL = 300; // Atualizar path a cada 300ms
    private static final int MAX_PATH_LENGTH = 30;
    private boolean isStuck;
    private long stuckTime;
    private static final long STUCK_THRESHOLD = 1500; // 1.5 segundos para considerar "preso"

    /**
     * Construtor para criar o Mago Supremo.
     * @param x Posição X inicial
     * @param y Posição Y inicial
     */
    public SupremeMage(int x, int y) {
        super("Mago Supremo", false, "", x, y, "supreme_mage", 48, true, false, 0);
        setAttributeHealth(500);
        setAttributeMaxHealth(500);
        setAttributeStrength(65);
        setAttributeDefence(55);
        setAttributeAgility(30);
        setSpeed(3);

        // Hitbox personalizada para o boss maior (64x64 pixels)
        int bossSize = 64;
        int hitboxWidth = 48;
        int hitboxHeight = 52;
        int hitboxX = (bossSize - hitboxWidth) / 2;
        int hitboxY = bossSize / 2;
        this.setHitbox(new java.awt.Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight));

        this.isFollowingPlayer = true;
        this.followDistance = 400; // Distância para começar a seguir
        this.battleTriggerDistance = 80; // Distância para iniciar batalha
        
        this.currentPath = new ArrayList<>();
        this.pathIndex = 0;
        this.lastPathUpdate = 0;
        this.isStuck = false;
        this.stuckTime = 0;
    }

    /**
     * Atualiza o comportamento do Mago Supremo.
     * @param gamePanel Painel do jogo
     * @param player Jogador
     */
    @Override
    public void update(GamePanel gamePanel, Player player) {
        if (isFollowingPlayer) {
            followPlayer(gamePanel, player);
        }

        super.update(gamePanel, player);
    }

    /**
     * Sistema melhorado de seguimento com LOS e pathfinding integrado.
     * @param gamePanel Painel do jogo
     * @param player Jogador
     */
    private void followPlayer(GamePanel gamePanel, Player player) {
        int deltaX = player.getWorldX() - getWorldX();
        int deltaY = player.getWorldY() - getWorldY();
        int distance = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Verificar se está próximo o suficiente para iniciar batalha
        if (distance <= battleTriggerDistance) {
            if (player.getHitbox() != null && getHitbox() != null) {
                if (player.getHitbox().intersects(getHitbox())) {
                    initiateBattle(gamePanel, player);
                    return;
                }
            }
        }

        // Se está muito longe, não seguir
        if (distance > followDistance) {
            currentPath.clear();
            pathIndex = 0;
            return;
        }

        // Sistema de detecção de "preso"
        checkIfStuck();
        
        // Atualizar path se necessário
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPathUpdate > PATH_UPDATE_INTERVAL || currentPath.isEmpty() || isStuck) {
            updatePath(player, gamePanel);
            lastPathUpdate = currentTime;
        }

        // Mover seguindo o path ou LOS direto
        boolean hasLOS = hasDirectLineOfSight(player, gamePanel);
        
        if (hasLOS && !isStuck) {
            // LOS direto - movimento mais suave
            moveDirectlyToPlayer(deltaX, deltaY, gamePanel);
        } else if (!currentPath.isEmpty()) {
            // Seguir path calculado
            followPath(gamePanel);
        } else {
            // Fallback: movimento simples
            moveTowardsPlayerSimple(deltaX, deltaY, gamePanel);
        }
    }


    /**
     * Verifica se há linha de visão direta melhorada entre o boss e o jogador.
     * @param player Jogador
     * @param gamePanel Painel do jogo
     * @return true se há linha de visão clara
     */
    private boolean hasDirectLineOfSight(Player player, GamePanel gamePanel) {
        // Usar centro das hitboxes para cálculo mais preciso
        int startX = getWorldX() + (getHitbox() != null ? getHitbox().width / 2 : gamePanel.getTileSize() / 2);
        int startY = getWorldY() + (getHitbox() != null ? getHitbox().height / 2 : gamePanel.getTileSize() / 2);
        int endX = player.getWorldX() + (player.getHitbox() != null ? player.getHitbox().width / 2 : gamePanel.getPlayerSize() / 2);
        int endY = player.getWorldY() + (player.getHitbox() != null ? player.getHitbox().height / 2 : gamePanel.getPlayerSize() / 2);

        // Calcular distância para otimização
        int distance = (int) Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY));
        
        // Se muito próximo, considerar LOS direto
        if (distance < gamePanel.getTileSize() * 2) {
            return true;
        }

        // Usar algoritmo de linha de Bresenham para verificação mais precisa
        return bresenhamLineOfSight(startX, startY, endX, endY, gamePanel);
    }

    /**
     * Implementa algoritmo de linha de Bresenham para verificação de LOS.
     * @param x0 Coordenada X inicial
     * @param y0 Coordenada Y inicial
     * @param x1 Coordenada X final
     * @param y1 Coordenada Y final
     * @param gamePanel Painel do jogo
     * @return true se a linha está livre de obstáculos
     */
    private boolean bresenhamLineOfSight(int x0, int y0, int x1, int y1, GamePanel gamePanel) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        
        int x = x0;
        int y = y0;
        int tileSize = gamePanel.getTileSize();
        int steps = 0;
        int maxSteps = Math.max(dx, dy) / (tileSize / 4); // Verificar a cada 1/4 de tile para melhor precisão
        
        // Verificar pontos ao longo da linha
        while (steps < maxSteps) {
            // Verificar colisão no ponto atual (exceto pontos inicial e final)
            if ((x != x0 || y != y0) && (x != x1 || y != y1)) {
                if (hasCollisionAtTile(x, y, gamePanel)) {
                    return false;
                }
            }
            
            // Parar se chegou ao destino
            if (x == x1 && y == y1) {
                break;
            }
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
            
            steps++;
        }
        
        return true;
    }

    /**
     * Verifica se há colisão em um tile específico.
     * @param x Posição X
     * @param y Posição Y
     * @param gamePanel Painel do jogo
     * @return true se há colisão
     */
    private boolean hasCollisionAtTile(int x, int y, GamePanel gamePanel) {
        // Converter coordenadas para tile
        int tileX = x / gamePanel.getTileSize();
        int tileY = y / gamePanel.getTileSize();

        // Verificar se o tile tem colisão usando o método correto
        return gamePanel.getTileManager().isCollisionAt(tileX, tileY);
    }

    /**
     * Verifica se o boss está "preso" (não se moveu por um tempo).
     */
    private void checkIfStuck() {
        long currentTime = System.currentTimeMillis();
        
        // Se não está se movendo há muito tempo, considerar preso
        if (currentTime - stuckTime > STUCK_THRESHOLD) {
            isStuck = true;
        } else {
            isStuck = false;
        }
    }

    /**
     * Atualiza o path para o jogador usando pathfinding simples.
     * @param player Jogador
     * @param gamePanel Painel do jogo
     */
    private void updatePath(Player player, GamePanel gamePanel) {
        currentPath.clear();
        pathIndex = 0;
        
        // Calcular path simples usando algoritmo de "greedy" pathfinding
        calculateSimplePath(player, gamePanel);
    }

    /**
     * Calcula um path simples usando algoritmo greedy melhorado.
     * @param player Jogador
     * @param gamePanel Painel do jogo
     */
    private void calculateSimplePath(Player player, GamePanel gamePanel) {
        int startX = getWorldX();
        int startY = getWorldY();
        int targetX = player.getWorldX();
        int targetY = player.getWorldY();
        
        int tileSize = gamePanel.getTileSize();
        
        // Converter para coordenadas de tile
        int startTileX = startX / tileSize;
        int startTileY = startY / tileSize;
        int targetTileX = targetX / tileSize;
        int targetTileY = targetY / tileSize;
        
        int currentX = startTileX;
        int currentY = startTileY;
        
        // Adicionar ponto inicial
        currentPath.add(new Point(startX, startY));
        
        // Calcular path usando movimento em grade com melhor lógica
        while (currentPath.size() < MAX_PATH_LENGTH && (currentX != targetTileX || currentY != targetTileY)) {
            int deltaX = targetTileX - currentX;
            int deltaY = targetTileY - currentY;
            
            // Determinar próxima direção com prioridade inteligente
            int nextX = currentX;
            int nextY = currentY;
            boolean moved = false;
            
            // Tentar movimento diagonal primeiro (mais eficiente)
            if (deltaX != 0 && deltaY != 0) {
                int diagonalX = currentX + (deltaX > 0 ? 1 : -1);
                int diagonalY = currentY + (deltaY > 0 ? 1 : -1);
                
                if (!hasCollisionAtTile(diagonalX * tileSize, diagonalY * tileSize, gamePanel)) {
                    currentX = diagonalX;
                    currentY = diagonalY;
                    moved = true;
                }
            }
            
            // Se não conseguiu movimento diagonal, tentar movimento em linha reta
            if (!moved) {
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    // Mover horizontalmente
                    nextX += deltaX > 0 ? 1 : -1;
                    if (!hasCollisionAtTile(nextX * tileSize, currentY * tileSize, gamePanel)) {
                        currentX = nextX;
                        moved = true;
                    }
                } else {
                    // Mover verticalmente
                    nextY += deltaY > 0 ? 1 : -1;
                    if (!hasCollisionAtTile(currentX * tileSize, nextY * tileSize, gamePanel)) {
                        currentY = nextY;
                        moved = true;
                    }
                }
            }
            
            // Se não conseguiu mover na direção principal, tentar direção alternativa
            if (!moved) {
                if (deltaX != 0) {
                    nextX = currentX + (deltaX > 0 ? 1 : -1);
                    if (!hasCollisionAtTile(nextX * tileSize, currentY * tileSize, gamePanel)) {
                        currentX = nextX;
                        moved = true;
                    }
                }
                if (!moved && deltaY != 0) {
                    nextY = currentY + (deltaY > 0 ? 1 : -1);
                    if (!hasCollisionAtTile(currentX * tileSize, nextY * tileSize, gamePanel)) {
                        currentY = nextY;
                        moved = true;
                    }
                }
            }
            
            // Se conseguiu mover, adicionar ao path
            if (moved) {
                currentPath.add(new Point(currentX * tileSize, currentY * tileSize));
            } else {
                // Não conseguiu mover, parar o pathfinding
                break;
            }
        }
        
        // Adicionar ponto final se não estiver muito próximo
        int lastX = currentPath.get(currentPath.size() - 1).x;
        int lastY = currentPath.get(currentPath.size() - 1).y;
        int distanceToTarget = (int) Math.sqrt((targetX - lastX) * (targetX - lastX) + (targetY - lastY) * (targetY - lastY));
        
        if (distanceToTarget > tileSize) {
            currentPath.add(new Point(targetX, targetY));
        }
    }

    /**
     * Segue o path calculado com melhor responsividade.
     * @param gamePanel Painel do jogo
     */
    private void followPath(GamePanel gamePanel) {
        if (currentPath.isEmpty() || pathIndex >= currentPath.size()) {
            return;
        }
        
        Point targetPoint = currentPath.get(pathIndex);
        int deltaX = targetPoint.x - getWorldX();
        int deltaY = targetPoint.y - getWorldY();
        
        // Se chegou próximo ao ponto atual do path, avançar para o próximo
        int distanceToPoint = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        int threshold = gamePanel.getTileSize() / 3; // Threshold menor para melhor responsividade
        
        if (distanceToPoint < threshold) {
            pathIndex++;
            if (pathIndex >= currentPath.size()) {
                return;
            }
            targetPoint = currentPath.get(pathIndex);
            deltaX = targetPoint.x - getWorldX();
            deltaY = targetPoint.y - getWorldY();
        }
        
        // Mover em direção ao próximo ponto do path
        moveTowardsPoint(deltaX, deltaY, gamePanel);
    }

    /**
     * Move em direção a um ponto específico.
     * @param deltaX Diferença X
     * @param deltaY Diferença Y
     * @param gamePanel Painel do jogo
     */
    private void moveTowardsPoint(int deltaX, int deltaY, GamePanel gamePanel) {
        // Normalizar direção
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance == 0) return;
        
        double normalizedX = deltaX / distance;
        double normalizedY = deltaY / distance;
        
        // Calcular nova posição
        int newX = getWorldX() + (int)(normalizedX * getSpeed());
        int newY = getWorldY() + (int)(normalizedY * getSpeed());
        
        // Verificar colisões
        if (!hasCollisionAt(newX, newY, gamePanel)) {
            setWorldX(newX);
            setWorldY(newY);
            
            // Atualizar direção
            if (Math.abs(normalizedX) > Math.abs(normalizedY)) {
                setDirection(normalizedX > 0 ? "right" : "left");
            } else {
                setDirection(normalizedY > 0 ? "down" : "up");
            }
            
            // Resetar timer de "preso"
            stuckTime = System.currentTimeMillis();
        } else {
            // Tentar movimento alternativo
            tryAlternativeMovement(deltaX, deltaY, gamePanel);
        }
    }

    /**
     * Movimento simples em direção ao jogador (fallback).
     * @param deltaX Diferença X
     * @param deltaY Diferença Y
     * @param gamePanel Painel do jogo
     */
    private void moveTowardsPlayerSimple(int deltaX, int deltaY, GamePanel gamePanel) {
        // Determinar direção principal
        String primaryDirection;
        String secondaryDirection;
        
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            primaryDirection = deltaX > 0 ? "right" : "left";
            secondaryDirection = deltaY > 0 ? "down" : "up";
        } else {
            primaryDirection = deltaY > 0 ? "down" : "up";
            secondaryDirection = deltaX > 0 ? "right" : "left";
        }
        
        // Tentar mover na direção principal
        if (canMoveInDirection(primaryDirection, gamePanel)) {
            moveInDirection(primaryDirection);
            return;
        }
        
        // Tentar mover na direção secundária
        if (canMoveInDirection(secondaryDirection, gamePanel)) {
            moveInDirection(secondaryDirection);
            return;
        }
        
        // Tentar outras direções
        String[] allDirections = {"up", "down", "left", "right"};
        for (String direction : allDirections) {
            if (canMoveInDirection(direction, gamePanel)) {
                moveInDirection(direction);
                break;
            }
        }
    }

    /**
     * Verifica se pode mover na direção especificada.
     * @param direction Direção para mover
     * @param gamePanel Painel do jogo
     * @return true se pode mover
     */
    private boolean canMoveInDirection(String direction, GamePanel gamePanel) {
        int newX = getWorldX();
        int newY = getWorldY();

        switch (direction) {
            case "up": newY -= getSpeed(); break;
            case "down": newY += getSpeed(); break;
            case "left": newX -= getSpeed(); break;
            case "right": newX += getSpeed(); break;
        }

        return !hasCollisionAt(newX, newY, gamePanel);
    }

    /**
     * Move o boss na direção especificada.
     * @param direction Direção para mover
     */
    private void moveInDirection(String direction) {
        setDirection(direction);

        switch (direction) {
            case "up":
                setWorldY(getWorldY() - getSpeed());
                break;
            case "down":
                setWorldY(getWorldY() + getSpeed());
                break;
            case "left":
                setWorldX(getWorldX() - getSpeed());
                break;
            case "right":
                setWorldX(getWorldX() + getSpeed());
                break;
        }
    }
    /**
     * Move diretamente em direção ao jogador (LOS direto).
     * @param deltaX Diferença X
     * @param deltaY Diferença Y
     * @param gamePanel Painel do jogo
     */
    private void moveDirectlyToPlayer(int deltaX, int deltaY, GamePanel gamePanel) {
        // Calcular direção normalizada para movimento suave
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance == 0) return;

        // Normalizar direção
        double normalizedX = deltaX / distance;
        double normalizedY = deltaY / distance;

        // Calcular nova posição
        int newX = getWorldX() + (int)(normalizedX * getSpeed());
        int newY = getWorldY() + (int)(normalizedY * getSpeed());

        // Verificar colisões básicas
        if (!hasCollisionAt(newX, newY, gamePanel)) {
            // Mover para nova posição
            setWorldX(newX);
            setWorldY(newY);

            // Definir direção baseada no movimento
            if (Math.abs(normalizedX) > Math.abs(normalizedY)) {
                setDirection(normalizedX > 0 ? "right" : "left");
            } else {
                setDirection(normalizedY > 0 ? "down" : "up");
            }
            
            // Resetar timer de "preso"
            stuckTime = System.currentTimeMillis();
        } else {
            // Se há colisão, tentar movimento alternativo
            tryAlternativeMovement(deltaX, deltaY, gamePanel);
        }
    }

    /**
     * Verifica se há colisão na posição especificada.
     * @param x Posição X
     * @param y Posição Y
     * @param gamePanel Painel do jogo
     * @return true se há colisão
     */
    private boolean hasCollisionAt(int x, int y, GamePanel gamePanel) {
        // Salvar posição atual
        int originalX = getWorldX();
        int originalY = getWorldY();

        // Definir nova posição temporariamente
        setWorldX(x);
        setWorldY(y);

        // Verificar colisão com tiles
        setCollisionOn(false);
        gamePanel.getColisionChecker().checkTile(this);
        boolean hasCollision = isCollisionOn();

        // Restaurar posição original
        setWorldX(originalX);
        setWorldY(originalY);

        return hasCollision;
    }

    /**
     * Tenta movimento alternativo quando há colisão.
     * @param deltaX Diferença X
     * @param deltaY Diferença Y
     * @param gamePanel Painel do jogo
     */
    private void tryAlternativeMovement(int deltaX, int deltaY, GamePanel gamePanel) {
        // Tentar mover apenas no eixo X
        if (Math.abs(deltaX) > 0) {
            int newX = getWorldX() + (deltaX > 0 ? getSpeed() : -getSpeed());
            if (!hasCollisionAt(newX, getWorldY(), gamePanel)) {
                setWorldX(newX);
                setDirection(deltaX > 0 ? "right" : "left");
                stuckTime = System.currentTimeMillis();
                return;
            }
        }

        // Tentar mover apenas no eixo Y
        if (Math.abs(deltaY) > 0) {
            int newY = getWorldY() + (deltaY > 0 ? getSpeed() : -getSpeed());
            if (!hasCollisionAt(getWorldX(), newY, gamePanel)) {
                setWorldY(newY);
                setDirection(deltaY > 0 ? "down" : "up");
                stuckTime = System.currentTimeMillis();
                return;
            }
        }

        // Se não conseguir mover em nenhuma direção, tentar direções aleatórias
        String[] directions = {"up", "down", "left", "right"};
        for (String direction : directions) {
            if (canMoveInDirection(direction, gamePanel)) {
                moveInDirection(direction);
                stuckTime = System.currentTimeMillis();
                return;
            }
        }
        
        // Se não conseguir mover em nenhuma direção, marcar como preso
        if (stuckTime == 0) {
            stuckTime = System.currentTimeMillis();
        }
    }

    /**
     * Inicia a batalha com o jogador.
     * @param gamePanel Painel do jogo
     * @param player Jogador
     */
    private void initiateBattle(GamePanel gamePanel, Player player) {
        if (gamePanel != null) {
            gamePanel.startBattle(this);
        }
    }

    /**
     * Interage com o Mago Supremo (inicia batalha).
     * @param gamePanel Painel do jogo
     */
    @Override
    public void interact(GamePanel gamePanel) {
        // Iniciar batalha quando interagir
        if (gamePanel != null && gamePanel.getPlayer() != null) {
            initiateBattle(gamePanel, gamePanel.getPlayer());
        }
    }

    /**
     * Verifica se o Mago Supremo está seguindo o jogador.
     * @return true se está seguindo
     */
    public boolean isFollowingPlayer() {
        return isFollowingPlayer;
    }

    /**
     * Define se o Mago Supremo deve seguir o jogador.
     * @param isFollowingPlayer true para seguir
     */
    public void setFollowingPlayer(boolean isFollowingPlayer) {
        this.isFollowingPlayer = isFollowingPlayer;
    }

    /**
     * Obtém a distância de seguimento.
     * @return Distância para começar a seguir
     */
    public int getFollowDistance() {
        return followDistance;
    }

    /**
     * Define a distância de seguimento.
     * @param followDistance Nova distância
     */
    public void setFollowDistance(int followDistance) {
        this.followDistance = followDistance;
    }

    /**
     * Obtém a distância para iniciar batalha.
     * @return Distância para iniciar batalha
     */
    public int getBattleTriggerDistance() {
        return battleTriggerDistance;
    }

    /**
     * Define a distância para iniciar batalha.
     * @param battleTriggerDistance Nova distância
     */
    public void setBattleTriggerDistance(int battleTriggerDistance) {
        this.battleTriggerDistance = battleTriggerDistance;
    }

    /**
     * Verifica se o boss está próximo o suficiente para iniciar batalha.
     * @param player Jogador
     * @return true se está próximo o suficiente
     */
    public boolean isCloseEnoughForBattle(Player player) {
        int deltaX = player.getWorldX() - getWorldX();
        int deltaY = player.getWorldY() - getWorldY();
        int distance = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        return distance <= battleTriggerDistance;
    }

    /**
     * Desenha o Supreme Mage na tela com tamanho correto (64x64 pixels).
     * Override do método da classe pai para usar escala adequada ao boss.
     * @param g Contexto gráfico
     * @param spriteLoader Loader de sprites
     * @param tileSize Tamanho do tile
     * @param player Jogador
     * @param playerScreenX Posição X do jogador na tela
     * @param playerScreenY Posição Y do jogador na tela
     */
    @Override
    public void draw(Graphics2D g, NpcSpriteLoader spriteLoader, int tileSize, Player player, int playerScreenX, int playerScreenY) {
        String direction = getDirection();
        java.util.List<String> sprites = spriteLoader.getSprites(skin, direction);
        int screenX = getWorldX() - player.getWorldX() + playerScreenX;
        int screenY = getWorldY() - player.getWorldY() + playerScreenY;

        // Supreme Mage usa tamanho de 4 tiles (64x64 pixels) em vez do tamanho padrão do player
        int bossSize = tileSize * 4; // 64x64 pixels para sprite de 64x64

        int spriteIdx = 0;
        // Para o Supreme Mage, sempre usar o sprite base (índice 0) por enquanto
        // Futuramente pode ser expandido para incluir animações de movimento

        if (sprites != null && !sprites.isEmpty()) {
            try {
                InputStream is = getClass().getResourceAsStream("/sprites/" + sprites.get(spriteIdx));
                if (is != null) {
                    BufferedImage img = ImageIO.read(is);
                    g.drawImage(img, screenX, screenY, bossSize, bossSize, null);
                } else {
                    System.err.println("Sprite não encontrado: /sprites/" + sprites.get(spriteIdx));
                    g.setColor(Color.RED);
                    g.fillRect(screenX, screenY, bossSize, bossSize);
                }
            } catch (java.io.IOException e) {
                System.err.println("Erro ao carregar sprite: " + e.getMessage());
                g.setColor(Color.RED);
                g.fillRect(screenX, screenY, bossSize, bossSize);
            }
        } else {
            System.err.println("Nenhum sprite encontrado para skin: " + skin + ", direção: " + direction);
            g.setColor(Color.RED);
            g.fillRect(screenX, screenY, bossSize, bossSize);
        }
    }
    
    /**
     * Classe auxiliar para representar pontos no pathfinding.
     */
    private static class Point {
        public final int x;
        public final int y;
        
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return "Point(" + x + ", " + y + ")";
        }
    }
}
