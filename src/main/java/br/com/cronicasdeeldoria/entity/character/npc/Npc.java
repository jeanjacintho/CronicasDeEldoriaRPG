package br.com.cronicasdeeldoria.entity.character.npc;

import br.com.cronicasdeeldoria.entity.character.Character;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.game.GamePanel;

/**
 * Representa um NPC (personagem não jogável) no jogo.
 */
public class Npc extends Character {
    protected boolean isStatic;
    protected String dialog;
    protected String skin;
    protected boolean interactive;
    protected boolean autoInteraction;
    protected int dialogId; // ID do diálogo inicial do NPC
    private int actionCounter = 0;
    private int actionInterval = 120;
    private int spriteCounter = 0;
    private int spriteNum = 1;
    private boolean isMoving = false;

    /**
     * Cria um novo NPC.
     * @param name Nome do NPC.
     * @param isStatic Indica se o NPC é estático.
     * @param dialog Diálogo do NPC.
     * @param x Posição X.
     * @param y Posição Y.
     * @param skin Skin do NPC.
     * @param playerSize Tamanho do jogador (para hitbox).
     * @param interactive Indica se o NPC é interativo.
     * @param autoInteraction Indica se a interação é automática.
     */
    public Npc(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize, boolean interactive, boolean autoInteraction) {
        super(x, y, 1, "down", name, null, 35, 35, 0, 0, 12, 10, 7);
        this.isStatic = isStatic;
        this.dialog = dialog;
        this.skin = skin;
        this.interactive = interactive;
        this.autoInteraction = autoInteraction;
        this.dialogId = 0; // Diálogo padrão
        int hitboxWidth = 32;
        int hitboxHeight = 36;
        int hitboxX = (playerSize - hitboxWidth) / 2;
        int hitboxY = playerSize / 2;
        this.setHitbox(new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight));
    }

    /**
     * Cria um novo NPC com ID de diálogo específico.
     * @param name Nome do NPC.
     * @param isStatic Indica se o NPC é estático.
     * @param dialog Diálogo do NPC.
     * @param x Posição X.
     * @param y Posição Y.
     * @param skin Skin do NPC.
     * @param playerSize Tamanho do jogador (para hitbox).
     * @param interactive Indica se o NPC é interativo.
     * @param autoInteraction Indica se a interação é automática.
     * @param dialogId ID do diálogo inicial.
     */
    public Npc(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize, boolean interactive, boolean autoInteraction, int dialogId) {
        super(x, y, 1, "down", name, null, 50, 50, 0, 0, 12, 10, 6);
        this.isStatic = isStatic;
        this.dialog = dialog;
        this.skin = skin;
        this.interactive = interactive;
        this.autoInteraction = autoInteraction;
        this.dialogId = dialogId;
        int hitboxWidth = 32;
        int hitboxHeight = 36;
        int hitboxX = (playerSize - hitboxWidth) / 2;
        int hitboxY = playerSize / 2;
        this.setHitbox(new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight));
    }

    /**
     * Interage com o NPC, iniciando o diálogo.
     * @param gamePanel Painel do jogo para acessar o DialogManager
     */
    public void interact(GamePanel gamePanel) {
        if (interactive && gamePanel.getDialogManager() != null) {
            // Primeiro notificar o QuestManager sobre a interação com o NPC
            if (gamePanel.getQuestManager() != null) {
                gamePanel.getQuestManager().onPlayerTalkToNpc(getName());
            }
            
            // Depois tentar usar diálogo condicional
            if (gamePanel.getDialogManager().startAppropriateDialog(getName())) {
                return;
            }
            
            // Fallback para diálogo fixo se não houver diálogo condicional
            if (dialogId > 0) {
                gamePanel.getDialogManager().startDialog(dialogId);
            }
        }
    }

    /**
     * Verifica se o NPC é interativo.
     * @return true se o NPC é interativo
     */
    public boolean isInteractive() {
        return interactive;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    /**
     * Verifica se o NPC tem auto-interação.
     * @return true se o NPC tem auto-interação
     */
    public boolean isAutoInteraction() {
        return autoInteraction;
    }

    public void setAutoInteraction(boolean autoInteraction) {
        this.autoInteraction = autoInteraction;
    }

    /**
     * Atualiza o estado do NPC.
     * @param gamePanel Painel do jogo.
     * @param player Jogador.
     */
    public void update(GamePanel gamePanel, Player player) {
        if (!isStatic) {
            walk(gamePanel, player);
        }
    }

    /**
     * Faz o NPC andar, considerando colisões e o jogador.
     * @param gamePanel Painel do jogo.
     * @param player Jogador.
     */
    public void walk(GamePanel gamePanel, Player player) {
        if (!isStatic) {
            actionCounter++;
            Random random = new Random();

            if (actionCounter >= actionInterval) {
                actionCounter = 0;
                isMoving = false;

                if (random.nextInt(100) < 80) {
                    String[] directions = {"up", "down", "left", "right"};
                    List<String> dirList = Arrays.asList(directions);
                    Collections.shuffle(dirList, random);

                    for (String dir : dirList) {
                        if (canMove(dir, gamePanel, player)) {
                            setDirection(dir);
                            break;
                        }
                    }
                }

                actionInterval = 120 + random.nextInt(120);
            }

            if (canMove(getDirection(), gamePanel, player)) {
                isMoving = true;
                switch (getDirection()) {
                    case "up": setWorldY(getWorldY() - getSpeed()); break;
                    case "down": setWorldY(getWorldY() + getSpeed()); break;
                    case "left": setWorldX(getWorldX() - getSpeed()); break;
                    case "right": setWorldX(getWorldX() + getSpeed()); break;
                }
            } else {
                isMoving = false;
            }

            if (isMoving) {
                spriteCounter++;
                if (spriteCounter > 15 - getSpeed()) {
                    spriteNum = (spriteNum == 1) ? 2 : 1;
                    spriteCounter = 0;
                }
            } else {
                spriteNum = 1;
                spriteCounter = 0;
            }
        }
    }

    private boolean canMove(String direction, GamePanel gamePanel, Player player) {
        int newX = getWorldX();
        int newY = getWorldY();
        switch (direction) {
            case "up": newY -= getSpeed(); break;
            case "down": newY += getSpeed(); break;
            case "left": newX -= getSpeed(); break;
            case "right": newX += getSpeed(); break;
        }

        String originalDirection = getDirection();
        setDirection(direction);
        setCollisionOn(false);
        gamePanel.getColisionChecker().checkTile(this);
        boolean tileCollision = isCollisionOn();
        setDirection(originalDirection);

        if (tileCollision) {
            return false;
        }

        if (this.getHitbox() != null && player.getHitbox() != null) {
            Rectangle npcFutureBox = new Rectangle(
                newX + getHitbox().x,
                newY + getHitbox().y,
                getHitbox().width,
                getHitbox().height
            );
            Rectangle playerBox = new Rectangle(
                player.getWorldX() + player.getHitbox().x,
                player.getWorldY() + player.getHitbox().y,
                player.getHitbox().width,
                player.getHitbox().height
            );
            if (npcFutureBox.intersects(playerBox)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Desenha o NPC na tela.
     * @param g Contexto gráfico.
     * @param spriteLoader Loader de sprites.
     * @param tileSize Tamanho do tile.
     * @param player Jogador.
     * @param playerScreenX Posição X do jogador na tela.
     * @param playerScreenY Posição Y do jogador na tela.
     */
    public void draw(Graphics2D g, NpcSpriteLoader spriteLoader, int tileSize, Player player, int playerScreenX, int playerScreenY) {
        String direction = getDirection();
        java.util.List<String> sprites = spriteLoader.getSprites(skin, direction);
        int screenX = getWorldX() - player.getWorldX() + playerScreenX;
        int screenY = getWorldY() - player.getWorldY() + playerScreenY;
        int npcSize = player.getPlayerSize();
        int spriteIdx = 0;
        if (isMoving && sprites != null && sprites.size() > 2) {
            spriteIdx = (spriteNum == 1) ? 1 : 2;
        }
        if (sprites != null && !sprites.isEmpty()) {
            try {
                InputStream is = getClass().getResourceAsStream("/sprites/" + sprites.get(spriteIdx));
                if (is != null) {
                    BufferedImage img = ImageIO.read(is);
                    g.drawImage(img, screenX, screenY, npcSize, npcSize, null);
                } else {
                    System.err.println("Sprite não encontrado: /sprites/" + sprites.get(spriteIdx));
                    g.setColor(Color.RED);
                    g.fillRect(screenX, screenY, npcSize, npcSize);
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar sprite: " + e.getMessage());
                g.setColor(Color.RED);
                g.fillRect(screenX, screenY, npcSize, npcSize);
            }
        } else {
            System.err.println("Nenhum sprite encontrado para skin: " + skin + ", direção: " + direction);
            g.setColor(Color.RED);
            g.fillRect(screenX, screenY, npcSize, npcSize);
        }
    }

    public String getSkin() { return skin; }
    public void setSkin(String skin) { this.skin = skin; }
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    public String getDialog() { return dialog; }
    public void setDialog(String dialog) { this.dialog = dialog; }
    public int getDialogId() { return dialogId; }
    public void setDialogId(int dialogId) { this.dialogId = dialogId; }
}
