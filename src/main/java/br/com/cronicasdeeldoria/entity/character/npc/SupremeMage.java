package br.com.cronicasdeeldoria.entity.character.npc;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.game.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

/**
 * Boss final da quest principal - Mago Supremo.
 * Segue o jogador e inicia batalha quando próximo o suficiente.
 */
public class SupremeMage extends Npc {
    private boolean isFollowingPlayer;
    private int followDistance;
    private int battleTriggerDistance;
    
    /**
     * Construtor para criar o Mago Supremo.
     * @param x Posição X inicial
     * @param y Posição Y inicial
     */
    public SupremeMage(int x, int y) {
        super("Mago Supremo", false, "", x, y, "supreme_mage", 48, true, false, 0);
        
        setAttributeHealth(200);
        setAttributeMaxHealth(200);
        setAttributeMana(100);
        setAttributeMaxMana(100);
        setAttributeStrength(25);
        setAttributeDefence(15);
        setAttributeAgility(8);
        setSpeed(3);
        
        // Hitbox personalizada para o boss maior (64x64 pixels)
        int bossSize = 64;
        int hitboxWidth = 48;
        int hitboxHeight = 52;
        int hitboxX = (bossSize - hitboxWidth) / 2;
        int hitboxY = bossSize / 2;
        this.setHitbox(new java.awt.Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight));
        
        this.isFollowingPlayer = true;
        this.followDistance = 150;
        this.battleTriggerDistance = 50;
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
     * Faz o Mago Supremo seguir o jogador usando Line of Sight.
     * @param gamePanel Painel do jogo
     * @param player Jogador
     */
    private void followPlayer(GamePanel gamePanel, Player player) {
        int deltaX = player.getWorldX() - getWorldX();
        int deltaY = player.getWorldY() - getWorldY();
        int distance = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance <= battleTriggerDistance) {
            // Próximo o suficiente para iniciar batalha
            if (player.getHitbox() != null && getHitbox() != null) {
                if (player.getHitbox().intersects(getHitbox())) {
                    initiateBattle(gamePanel, player);
                }
            }
        } else if (distance > followDistance) {
            // Muito longe - usar Line of Sight para seguir
            if (hasLineOfSight(player, gamePanel)) {
                // Tem linha de visão - mover diretamente
                moveDirectlyToPlayer(deltaX, deltaY, gamePanel, player);
            } else {
                // Sem linha de visão - usar pathfinding simples
                moveTowardsPlayerWithPathfinding(deltaX, deltaY, gamePanel, player);
            }
        }
        // Se está na distância intermediária, apenas seguir sem iniciar batalha
    }
    
    
    /**
     * Verifica se há linha de visão clara entre o boss e o jogador.
     * @param player Jogador
     * @param gamePanel Painel do jogo
     * @return true se há linha de visão clara
     */
    private boolean hasLineOfSight(Player player, GamePanel gamePanel) {
        // Usar o centro das hitboxes para cálculo mais preciso
        int startX = getWorldX() + (getHitbox() != null ? getHitbox().width / 2 : gamePanel.getTileSize() / 2);
        int startY = getWorldY() + (getHitbox() != null ? getHitbox().height / 2 : gamePanel.getTileSize() / 2);
        int endX = player.getWorldX() + (player.getHitbox() != null ? player.getHitbox().width / 2 : gamePanel.getPlayerSize() / 2);
        int endY = player.getWorldY() + (player.getHitbox() != null ? player.getHitbox().height / 2 : gamePanel.getPlayerSize() / 2);
        
        // Calcular pontos intermediários na linha
        int steps = Math.max(Math.abs(endX - startX), Math.abs(endY - startY)) / gamePanel.getTileSize();
        if (steps == 0) return true;
        
        for (int i = 1; i < steps; i++) {
            int checkX = startX + (endX - startX) * i / steps;
            int checkY = startY + (endY - startY) * i / steps;
            
            // Verificar se há colisão neste ponto
            if (hasCollisionAtTile(checkX, checkY, gamePanel)) {
                return false; // Linha de visão bloqueada
            }
        }
        
        return true; // Linha de visão clara
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
     * Move o Mago Supremo em direção ao jogador usando pathfinding simples quando não há linha de visão.
     * @param deltaX Diferença X entre boss e jogador
     * @param deltaY Diferença Y entre boss e jogador
     * @param gamePanel Painel do jogo
     * @param player Jogador
     */
    private void moveTowardsPlayerWithPathfinding(int deltaX, int deltaY, GamePanel gamePanel, Player player) {
        // Determinar direção principal baseada na maior diferença
        String primaryDirection;
        String secondaryDirection;
        
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Movimento horizontal é prioritário
            primaryDirection = deltaX > 0 ? "right" : "left";
            secondaryDirection = deltaY > 0 ? "down" : "up";
        } else {
            // Movimento vertical é prioritário
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
        
        // Se não conseguir mover nas direções principais, tentar outras direções
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
    private void moveDirectlyToPlayer(int deltaX, int deltaY, GamePanel gamePanel, Player player) {
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
        } else {
            // Se há colisão, tentar movimento alternativo simples
            trySimpleAlternativeMovement(deltaX, deltaY, gamePanel);
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
     * Tenta movimento alternativo simples quando há colisão.
     * @param deltaX Diferença X
     * @param deltaY Diferença Y
     * @param gamePanel Painel do jogo
     */
    private void trySimpleAlternativeMovement(int deltaX, int deltaY, GamePanel gamePanel) {
        // Tentar mover apenas no eixo X
        if (Math.abs(deltaX) > 0) {
            int newX = getWorldX() + (deltaX > 0 ? getSpeed() : -getSpeed());
            if (!hasCollisionAt(newX, getWorldY(), gamePanel)) {
                setWorldX(newX);
                setDirection(deltaX > 0 ? "right" : "left");
                return;
            }
        }
        
        // Tentar mover apenas no eixo Y
        if (Math.abs(deltaY) > 0) {
            int newY = getWorldY() + (deltaY > 0 ? getSpeed() : -getSpeed());
            if (!hasCollisionAt(getWorldX(), newY, gamePanel)) {
                setWorldY(newY);
                setDirection(deltaY > 0 ? "down" : "up");
                return;
            }
        }
        
        // Se não conseguir mover em nenhuma direção, ficar parado
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
}
