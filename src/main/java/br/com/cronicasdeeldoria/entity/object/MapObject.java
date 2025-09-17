package br.com.cronicasdeeldoria.entity.object;

import br.com.cronicasdeeldoria.entity.Entity;
import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.entity.item.QuestItem;
import br.com.cronicasdeeldoria.game.inventory.ItemFactory;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Representa um objeto do mapa que pode ser renderizado e interagido.
 */
public class MapObject extends Entity {
    private String objectId;
    private String name;
    private boolean collision;
    private boolean interactive;
    private boolean autoInteraction;
    private int width;
    private int height;
    private ObjectSpriteLoader.ObjectDefinition objectDefinition;
    private boolean active = true;

    /**
     * Cria um novo objeto do mapa.
     * @param objectId ID do objeto.
     * @param name Nome do objeto.
     * @param worldX Posição X no mundo.
     * @param worldY Posição Y no mundo.
     * @param width Largura em tiles.
     * @param height Altura em tiles.
     * @param collision Se tem colisão.
     * @param interactive Se é interativo.
     * @param autoInteraction Se tem auto-interação.
     * @param objectDefinition Definição do objeto.
     * @param tileSize Tamanho do tile.
     */
    public MapObject(String objectId, String name, int worldX, int worldY, int width, int height,
                     boolean collision, boolean interactive, boolean autoInteraction, ObjectSpriteLoader.ObjectDefinition objectDefinition, int tileSize) {
        super(worldX, worldY, 0,"none", objectId);
        this.objectId = objectId;
        this.name = name;
        this.width = width;
        this.height = height;
        this.collision = collision;
        this.interactive = interactive;
        this.autoInteraction = autoInteraction;
        this.objectDefinition = objectDefinition;

        // Configurar hitbox baseada no tamanho do objeto em pixels (tiles * tileSize)
        this.setHitbox(new Rectangle(0, 0, width * tileSize, height * tileSize));
        this.setCollisionOn(collision);
    }

    /**
     * Desenha o objeto na tela.
     * @param g Contexto gráfico.
     * @param spriteLoader Loader de sprites.
     * @param tileSize Tamanho do tile.
     * @param player Jogador.
     * @param playerScreenX Posição X do jogador na tela.
     * @param playerScreenY Posição Y do jogador na tela.
     */
    public void draw(Graphics2D g, ObjectSpriteLoader spriteLoader, int tileSize,
                   Player player,
                    int playerScreenX, int playerScreenY) {
        if (!active || objectDefinition == null || objectDefinition.spritePaths == null ||
            objectDefinition.spritePaths.isEmpty()) {
            return;
        }

        int screenX = getWorldX() - player.getWorldX() + playerScreenX;
        int screenY = getWorldY() - player.getWorldY() + playerScreenY;

        // Renderizar o objeto tile por tile baseado no tamanho
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Determinar qual sprite usar baseado na posição
                int spriteRow = Math.min(row, objectDefinition.spritePaths.size() - 1);
                List<String> spriteRowPaths = objectDefinition.spritePaths.get(spriteRow);

                if (spriteRowPaths != null && !spriteRowPaths.isEmpty()) {
                    int spriteCol = Math.min(col, spriteRowPaths.size() - 1);
                    String spritePath = spriteRowPaths.get(spriteCol);

                    try {
                        InputStream is = getClass().getResourceAsStream(spritePath);
                        if (is != null) {
                            BufferedImage img = ImageIO.read(is);
                            int drawX = screenX + (col * tileSize);
                            int drawY = screenY + (row * tileSize);

                            g.drawImage(img, drawX, drawY, tileSize, tileSize, null);

                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar sprite: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Interage com o objeto.
     * @param interactor Entidade que interage com o objeto.
     */
    public void interact(Entity interactor) {
        if (interactive && active) {
            if (interactor instanceof Player) {
                
                // Verificar se é um item coletável
                if (isCollectibleItem()) {
                    collectItem((Player) interactor);
                }
            }
        }
    }
    
    /**
     * Verifica se o objeto é um item coletável.
     * @return true se é um item coletável.
     */
    public boolean isCollectibleItem() {
        // Usar o ItemFactory para verificar se pode criar um item a partir deste objeto
        boolean canCollect = ItemFactory.canCreateItem(this);
        
        return canCollect;
    }
    
    /**
     * Coleta o item e adiciona ao inventário do jogador.
     * @param player Jogador que está coletando.
     */
    private void collectItem(Player player) {
        // Verificar se o objeto já foi coletado
        if (!active) {
            return;
        }
        
        try {
            // Usar reflexão para acessar o GamePanel e InventoryManager
            Field gamePanelField = player.getClass().getDeclaredField("gamePanel");
            gamePanelField.setAccessible(true);
            Object gamePanel = gamePanelField.get(player);
            
            Method getInventoryManagerMethod = gamePanel.getClass().getMethod("getInventoryManager");
            Object inventoryManager = getInventoryManagerMethod.invoke(gamePanel);
            
            // Criar item a partir do objeto
            Item item = ItemFactory.createItemFromMapObject(this, 48);
            
            if (item != null) {
                // Adicionar ao inventário
                Method addItemMethod = inventoryManager.getClass().getMethod("addItem", Item.class);
                boolean added = (Boolean) addItemMethod.invoke(inventoryManager, item);
                
                if (added) {
                    // Chamar onCollect se o item for um QuestItem
                    if (item instanceof QuestItem) {
                        ((QuestItem) item).onCollect();
                    }
                    
                    // Notificar QuestManager sobre coleta de item
                    try {
                        Method getQuestManagerMethod = gamePanel.getClass().getMethod("getQuestManager");
                        Object questManager = getQuestManagerMethod.invoke(gamePanel);
                        if (questManager != null) {
                            Method onItemCollectedMethod = questManager.getClass().getMethod("onItemCollected", String.class);
                            onItemCollectedMethod.invoke(questManager, item.getItemId());
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao notificar QuestManager sobre coleta de item: " + e.getMessage());
                    }
                    
                    Method getGameUIMethod = gamePanel.getClass().getMethod("getGameUI");
                    Object gameUI = getGameUIMethod.invoke(gamePanel);
                    Method addMessageMethod = gameUI.getClass().getMethod("addMessage", String.class, java.awt.Image.class, long.class);
                    addMessageMethod.invoke(gameUI, "1X " + getName(), null, 3500L);
                    setActive(false);
                    setInteractive(false);
                } else {
                    Method getGameUIMethod = gamePanel.getClass().getMethod("getGameUI");
                    Object gameUI = getGameUIMethod.invoke(gamePanel);
                    Method addMessageMethod = gameUI.getClass().getMethod("addMessage", String.class, java.awt.Image.class, long.class);
                    addMessageMethod.invoke(gameUI, "Inventário cheio! Não foi possível coletar: " + getName(), null, 3500L);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao coletar item: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica se o objeto é interativo.
     * @return true se o objeto é interativo
     */
    public boolean isInteractive() {
        return interactive;
    }
    
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }
    
    /**
     * Verifica se o objeto tem auto-interação.
     * @return true se o objeto tem auto-interação
     */
    public boolean isAutoInteraction() {
        return autoInteraction;
    }
    
    public void setAutoInteraction(boolean autoInteraction) {
        this.autoInteraction = autoInteraction;
    }

    public String getObjectId() { return objectId; }
    public void setObjectId(String objectId) { this.objectId = objectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean hasCollision() { return collision; }
    public void setCollision(boolean collision) {
        this.collision = collision;
        this.setCollisionOn(collision);
    }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public ObjectSpriteLoader.ObjectDefinition getObjectDefinition() { return objectDefinition; }
    public void setObjectDefinition(ObjectSpriteLoader.ObjectDefinition objectDefinition) {
        this.objectDefinition = objectDefinition;
    }
}
