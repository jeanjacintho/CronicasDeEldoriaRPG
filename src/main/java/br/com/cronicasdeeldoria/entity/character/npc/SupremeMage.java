package br.com.cronicasdeeldoria.entity.character.npc;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.game.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private static final long PATH_UPDATE_INTERVAL = 150;
    private static final int MAX_PATH_LENGTH = 30;
    private boolean isStuck;
    private long stuckTime;
    private int lastPosX;
    private int lastPosY;
    private static final long STUCK_THRESHOLD = 150;

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
        this.setHitbox(new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight));

        this.isFollowingPlayer = true;
        this.followDistance = 400; // Distância para começar a seguir
        this.battleTriggerDistance = 32; // Distância para iniciar batalha

        this.currentPath = new ArrayList<>();
        this.pathIndex = 0;
        this.lastPathUpdate = 0;
        this.isStuck = false;
        this.stuckTime = System.currentTimeMillis();
        this.lastPosX = x;
        this.lastPosY = y;
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

        // Calcular LOS antes de decidir por atualização de path
        boolean hasLOS = hasDirectLineOfSight(player, gamePanel);

        // Atualizar path se necessário
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPathUpdate > PATH_UPDATE_INTERVAL || currentPath.isEmpty() || isStuck || !hasLOS) {
            updatePath(player, gamePanel);
            lastPathUpdate = currentTime;
        }

        if (hasLOS && !isStuck) {
            // LOS direto - movimento mais suave
            moveDirectlyToPlayer(deltaX, deltaY, gamePanel);
        } else if (!currentPath.isEmpty()) {
            // Seguir path calculado
            followPath(gamePanel);
        } else {
            lastPathUpdate = 0;
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
        // Usar centro real das hitboxes considerando offsets
        int startX;
        int startY;
        if (getHitbox() != null) {
            startX = getWorldX() + getHitbox().x + (getHitbox().width / 2);
            startY = getWorldY() + getHitbox().y + (getHitbox().height / 2);
        } else {
            startX = getWorldX() + (gamePanel.getTileSize() / 2);
            startY = getWorldY() + (gamePanel.getTileSize() / 2);
        }
        int endX;
        int endY;
        if (player.getHitbox() != null) {
            endX = player.getWorldX() + player.getHitbox().x + (player.getHitbox().width / 2);
            endY = player.getWorldY() + player.getHitbox().y + (player.getHitbox().height / 2);
        } else {
            endX = player.getWorldX() + (gamePanel.getPlayerSize() / 2);
            endY = player.getWorldY() + (gamePanel.getPlayerSize() / 2);
        }

        // Calcular distância para otimização
        int distance = (int) Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY));

        // Se muito próximo, considerar LOS direto
        if (distance < gamePanel.getTileSize() * 2) {
            return true;
        }

        return bresenhamLineOfSight(startX, startY, endX, endY, gamePanel);
    }

    /**
     * Implementa algoritmo de linha de Bresenham para verificação de LOS.
     * @param startX Coordenada X inicial
     * @param startY Coordenada Y inicial
     * @param endX Coordenada X final
     * @param endY Coordenada Y final
     * @param gamePanel Painel do jogo
     * @return true se a linha está livre de obstáculos
     */
    private boolean bresenhamLineOfSight(int startX, int startY, int endX, int endY, GamePanel gamePanel) {
        int deltaX = Math.abs(endX - startX);
        int deltaY = Math.abs(endY - startY);
        int stepX = startX < endX ? 1 : -1;
        int stepY = startY < endY ? 1 : -1;
        int error = deltaX - deltaY;

        int currentX = startX;
        int currentY = startY;
        int tileSize = gamePanel.getTileSize();
        int stepCount = 0;
        int maxSteps = Math.max(deltaX, deltaY) / (tileSize / 4);

        // Verificar pontos ao longo da linha
        while (stepCount < maxSteps) {
            if ((currentX != startX || currentY != startY) && (currentX != endX || currentY != endY)) {
                if (hasCollisionAtTile(currentX, currentY, gamePanel)) {
                    return false;
                }
            }

            if (currentX == endX && currentY == endY) break;

            int error2 = 2 * error;
            if (error2 > -deltaY) {
                error -= deltaY;
                currentX += stepX;
            }
            if (error2 < deltaX) {
                error += deltaX;
                currentY += stepY;
            }
            stepCount++;
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
        int dx = Math.abs(getWorldX() - lastPosX);
        int dy = Math.abs(getWorldY() - lastPosY);

        if (dx + dy >= getSpeed()) {
            stuckTime = currentTime;
            isStuck = false;
            lastPosX = getWorldX();
            lastPosY = getWorldY();
            return;
        }

        // Considerar preso se parado por tempo acima do limiar
        isStuck = (currentTime - stuckTime) > STUCK_THRESHOLD;
    }

    /**
     * Atualiza o path para o jogador usando pathfinding robusto.
     * @param player Jogador
     * @param gamePanel Painel do jogo
     */
    private void updatePath(Player player, GamePanel gamePanel) {
        currentPath.clear();
        pathIndex = 0;

        calculateBfsPath(player, gamePanel);

        // Se BFS falhou, tentar greedy como fallback
        if (currentPath.size() <= 1) {
            calculateSimplePath(player, gamePanel);
        }
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

        // Adicionar ponto inicial (centro do tile atual)
        currentPath.add(new Point(startTileX * tileSize + tileSize / 2, startTileY * tileSize + tileSize / 2));

        // Calcular path usando movimento em grade com melhor lógica
        while (currentPath.size() < MAX_PATH_LENGTH && (currentX != targetTileX || currentY != targetTileY)) {
            int deltaX = targetTileX - currentX;
            int deltaY = targetTileY - currentY;

            // Determinar próxima direção
            int nextX = currentX;
            int nextY = currentY;
            boolean moved = false;

            // Tentar movimento diagonal
            if (deltaX != 0 && deltaY != 0) {
                int stepX = (deltaX > 0 ? 1 : -1);
                int stepY = (deltaY > 0 ? 1 : -1);
                int diagonalX = currentX + stepX;
                int diagonalY = currentY + stepY;

                boolean freeDiagonal = !hasCollisionAtTile(diagonalX * tileSize, diagonalY * tileSize, gamePanel);
                boolean freeHoriz = !hasCollisionAtTile((currentX + stepX) * tileSize, currentY * tileSize, gamePanel);
                boolean freeVert  = !hasCollisionAtTile(currentX * tileSize, (currentY + stepY) * tileSize, gamePanel);

                // Prevenir corner-cutting: só permite diagonal se diagonal e ambos adjacentes estiverem livres
                if (freeDiagonal && freeHoriz && freeVert) {
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

            if (moved) {
                currentPath.add(new Point(currentX * tileSize + tileSize / 2, currentY * tileSize + tileSize / 2));
            } else {
                break;
            }
        }

        int lastX = currentPath.get(currentPath.size() - 1).x;
        int lastY = currentPath.get(currentPath.size() - 1).y;
        int distanceToTarget = (int) Math.sqrt((targetX - lastX) * (targetX - lastX) + (targetY - lastY) * (targetY - lastY));

        if (distanceToTarget > tileSize) {
            currentPath.add(new Point(targetX, targetY));
        }
    }

    /**
     * Calcula um path básico usando BFS em grid de tiles, evitando colisões de tiles.
     * Limita a expansão para evitar custo alto.
     */
    private void calculateBfsPath(Player player, GamePanel gamePanel) {
        int tileSize = gamePanel.getTileSize();
        int startTileX = getWorldX() / tileSize;
        int startTileY = getWorldY() / tileSize;
        int targetTileX = player.getWorldX() / tileSize;
        int targetTileY = player.getWorldY() / tileSize;

        int maxWidth = gamePanel.getTileManager().getMapWidth();
        int maxHeight = gamePanel.getTileManager().getMapHeight();

        boolean[][] visited = new boolean[maxWidth][maxHeight];
        Point[][] parent = new Point[maxWidth][maxHeight];
        ArrayDeque<Point> queue = new ArrayDeque<>();

        Point start = new Point(startTileX, startTileY);
        Point goal = new Point(targetTileX, targetTileY);
        queue.add(start);
        visited[startTileX][startTileY] = true;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}}; // apenas 4-direções para evitar corner cutting
        int expansions = 0;
        int maxExpansions = 500;

        while (!queue.isEmpty() && expansions < maxExpansions) {
            Point cur = queue.poll();
            expansions++;
            if (cur.x == goal.x && cur.y == goal.y) break;

            for (int[] d : dirs) {
                int nx = cur.x + d[0];
                int ny = cur.y + d[1];
                if (nx < 0 || ny < 0 || nx >= maxWidth || ny >= maxHeight) continue;
                if (visited[nx][ny]) continue;
                if (gamePanel.getTileManager().isCollisionAt(nx, ny)) continue;
                visited[nx][ny] = true;
                parent[nx][ny] = cur;
                queue.add(new Point(nx, ny));
            }
        }

        if (!visited[targetTileX][targetTileY]) {
            return;
        }

        LinkedList<Point> tilesPath = new LinkedList<>();
        Point cur = new Point(targetTileX, targetTileY);
        while (cur != null && !(cur.x == startTileX && cur.y == startTileY)) {
            tilesPath.addFirst(cur);
            cur = parent[cur.x][cur.y];
        }

        currentPath.clear();
        currentPath.add(new Point(startTileX * tileSize + tileSize / 2, startTileY * tileSize + tileSize / 2));
        for (Point t : tilesPath) {
            currentPath.add(new Point(t.x * tileSize + tileSize / 2, t.y * tileSize + tileSize / 2));
        }
    }

    /**
     * Segue o path calculado com melhor responsividade.
     * @param gamePanel Painel do jogo
     */
    private void followPath(GamePanel gamePanel) {
        if (currentPath.isEmpty() || pathIndex >= currentPath.size()) {
            lastPathUpdate = 0;
            return;
        }

        Point targetPoint = currentPath.get(pathIndex);
        int currentCenterX = (getHitbox() != null)
                ? getWorldX() + getHitbox().x + (getHitbox().width / 2)
                : getWorldX() + (gamePanel.getTileSize() / 2);
        int currentCenterY = (getHitbox() != null)
                ? getWorldY() + getHitbox().y + (getHitbox().height / 2)
                : getWorldY() + (gamePanel.getTileSize() / 2);
        int deltaX = targetPoint.x - currentCenterX;
        int deltaY = targetPoint.y - currentCenterY;

        // Se chegou próximo ao ponto atual do path, avançar para o próximo
        int distanceToPoint = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        int threshold = Math.max(getSpeed() * 2, gamePanel.getTileSize() / 2);

        if (distanceToPoint < threshold) {
            pathIndex++;
            if (pathIndex >= currentPath.size()) {
                lastPathUpdate = 0;
                return;
            }
            targetPoint = currentPath.get(pathIndex);
            currentCenterX = (getHitbox() != null)
                    ? getWorldX() + getHitbox().x + (getHitbox().width / 2)
                    : getWorldX() + (gamePanel.getTileSize() / 2);
            currentCenterY = (getHitbox() != null)
                    ? getWorldY() + getHitbox().y + (getHitbox().height / 2)
                    : getWorldY() + (gamePanel.getTileSize() / 2);
            deltaX = targetPoint.x - currentCenterX;
            deltaY = targetPoint.y - currentCenterY;
        }

        int beforeX = getWorldX();
        int beforeY = getWorldY();
        moveTowardsPoint(deltaX, deltaY, gamePanel);

        boolean actuallyMoved = (getWorldX() != beforeX) || (getWorldY() != beforeY);
        if (!actuallyMoved) {
            lastPathUpdate = 0;
        }
    }

    /**
     * Move em direção a um ponto específico.
     * @param deltaX Diferença X
     * @param deltaY Diferença Y
     * @param gamePanel Painel do jogo
     */
    private void moveTowardsPoint(int deltaX, int deltaY, GamePanel gamePanel) {
        if (Math.abs(deltaX) <= getSpeed() && Math.abs(deltaY) <= getSpeed()) {
            return;
        }

        boolean movedAny = false;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Mover horizontalmente primeiro
            int stepX = deltaX > 0 ? getSpeed() : -getSpeed();
            int newX = getWorldX() + stepX;
            if (!hasCollisionAt(newX, getWorldY(), gamePanel)) {
                setWorldX(newX);
                setDirection(deltaX > 0 ? "right" : "left");
                movedAny = true;
            }
        } else {
            // Mover verticalmente primeiro
            int stepY = deltaY > 0 ? getSpeed() : -getSpeed();
            int newY = getWorldY() + stepY;
            if (!hasCollisionAt(getWorldX(), newY, gamePanel)) {
                setWorldY(newY);
                setDirection(deltaY > 0 ? "down" : "up");
                movedAny = true;
            }
        }

        if (!movedAny) {
            if (Math.abs(deltaX) <= Math.abs(deltaY)) {
                // Tentar horizontal agora
                int stepX = deltaX > 0 ? getSpeed() : -getSpeed();
                int newX = getWorldX() + stepX;
                if (!hasCollisionAt(newX, getWorldY(), gamePanel)) {
                    setWorldX(newX);
                    setDirection(deltaX > 0 ? "right" : "left");
                    movedAny = true;
                }
            } else {
                // Tentar vertical agora
                int stepY = deltaY > 0 ? getSpeed() : -getSpeed();
                int newY = getWorldY() + stepY;
                if (!hasCollisionAt(getWorldX(), newY, gamePanel)) {
                    setWorldY(newY);
                    setDirection(deltaY > 0 ? "down" : "up");
                    movedAny = true;
                }
            }
        }

        if (movedAny) {
            stuckTime = System.currentTimeMillis();
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

        // Movimento separado por eixo (X depois Y)
        boolean movedAny = false;
        int proposedX = getWorldX() + (int)(normalizedX * getSpeed());
        if (!hasCollisionAt(proposedX, getWorldY(), gamePanel)) {
            setWorldX(proposedX);
            movedAny = true;
        }
        int proposedY = getWorldY() + (int)(normalizedY * getSpeed());
        if (!hasCollisionAt(getWorldX(), proposedY, gamePanel)) {
            setWorldY(proposedY);
            movedAny = true;
        }

        if (movedAny) {
            if (Math.abs(normalizedX) > Math.abs(normalizedY)) {
                setDirection(normalizedX > 0 ? "right" : "left");
            } else {
                setDirection(normalizedY > 0 ? "down" : "up");
            }
            stuckTime = System.currentTimeMillis();
        } else {
            tryAlternativeMovement(deltaX, deltaY, gamePanel);
        }
    }

    /**
     * Verifica se há colisão na posição especificada SEM alterar a posição do boss.
     * @param x Posição X
     * @param y Posição Y
     * @param gamePanel Painel do jogo
     * @return true se há colisão
     */
    private boolean hasCollisionAt(int x, int y, GamePanel gamePanel) {
        // Verificar colisão diretamente com tiles sem mover o boss
        if (getHitbox() != null) {
            // Calcular área da hitbox na nova posição
            int hitboxLeft = x + getHitbox().x;
            int hitboxRight = x + getHitbox().x + getHitbox().width - 1;
            int hitboxTop = y + getHitbox().y;
            int hitboxBottom = y + getHitbox().y + getHitbox().height - 1;

            int tileSize = gamePanel.getTileSize();

            // Verificar tiles que a hitbox tocaria
            int leftTile = hitboxLeft / tileSize;
            int rightTile = hitboxRight / tileSize;
            int topTile = hitboxTop / tileSize;
            int bottomTile = hitboxBottom / tileSize;

            // Verificar cada tile na área da hitbox
            int mapWidth = gamePanel.getTileManager().getMapWidth();
            int mapHeight = gamePanel.getTileManager().getMapHeight();

            for (int tileY = topTile; tileY <= bottomTile; tileY++) {
                for (int tileX = leftTile; tileX <= rightTile; tileX++) {
                    // Verificar bounds do mapa
                    if (tileX < 0 || tileY < 0 || tileX >= mapWidth || tileY >= mapHeight) {
                        return true; // Fora do mapa = colisão
                    }
                    if (gamePanel.getTileManager().isCollisionAt(tileX, tileY)) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            int tileX = x / gamePanel.getTileSize();
            int tileY = y / gamePanel.getTileSize();
            return gamePanel.getTileManager().isCollisionAt(tileX, tileY);
        }
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
                lastPathUpdate = 0;
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
                lastPathUpdate = 0;
                return;
            }
        }

        // Se não conseguir mover em nenhuma direção, tentar direções aleatórias
        String[] directions = {"up", "down", "left", "right"};
        for (String direction : directions) {
            if (canMoveInDirection(direction, gamePanel)) {
                moveInDirection(direction);
                stuckTime = System.currentTimeMillis();
                lastPathUpdate = 0;
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
