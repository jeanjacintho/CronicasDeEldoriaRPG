package br.com.cronicasdeeldoria.entity.object;

import br.com.cronicasdeeldoria.entity.Entity;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Representa um objeto do mapa que pode ser renderizado e interagido.
 */
public class MapObject extends Entity {
    private String objectId;
    private String name;
    private boolean collision;
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
     * @param objectDefinition Definição do objeto.
     */
    public MapObject(String objectId, String name, int worldX, int worldY, int width, int height, 
                     boolean collision, ObjectSpriteLoader.ObjectDefinition objectDefinition) {
        super(worldX, worldY, 0, "none", objectId);
        this.objectId = objectId;
        this.name = name;
        this.width = width;
        this.height = height;
        this.collision = collision;
        this.objectDefinition = objectDefinition;
        
        // Configurar hitbox baseada no tamanho do objeto em pixels (tiles * tileSize)
        int tileSize = 48; // tileSize padrão do jogo
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
                    br.com.cronicasdeeldoria.entity.character.player.Player player, 
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
                        java.io.InputStream is = getClass().getResourceAsStream(spritePath);
                        if (is != null) {
                            BufferedImage img = javax.imageio.ImageIO.read(is);
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
        if (interactor instanceof br.com.cronicasdeeldoria.entity.character.player.Player) {
            br.com.cronicasdeeldoria.entity.character.player.Player player = 
                (br.com.cronicasdeeldoria.entity.character.player.Player) interactor;
        }
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
