package br.com.cronicasdeeldoria.game.inventory;

import br.com.cronicasdeeldoria.entity.object.ObjectSpriteLoader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Carregador de sprites para itens do inventário.
 */
public class ItemSpriteLoader {
    private static ItemSpriteLoader instance;
    private final Map<String, BufferedImage> itemSprites;
    private final ObjectSpriteLoader objectSpriteLoader;
    
    private ItemSpriteLoader() {
        this.itemSprites = new HashMap<>();
        try {
            this.objectSpriteLoader = new ObjectSpriteLoader("/objects.json");
        } catch (Exception e) {
            System.err.println("Erro ao inicializar ObjectSpriteLoader: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Obtém a instância singleton do ItemSpriteLoader.
     */
    public static ItemSpriteLoader getInstance() {
        if (instance == null) {
            instance = new ItemSpriteLoader();
        }
        return instance;
    }
    
    /**
     * Carrega e retorna a imagem de um item.
     * @param itemId ID do item.
     * @return BufferedImage do item ou null se não encontrado.
     */
    public BufferedImage getItemSprite(String itemId) {
        if (itemSprites.containsKey(itemId)) {
            return itemSprites.get(itemId);
        }
        
        // Tentar carregar a imagem
        BufferedImage sprite = loadItemSprite(itemId);
        if (sprite != null) {
            itemSprites.put(itemId, sprite);
        }
        
        return sprite;
    }
    
    /**
     * Carrega a imagem de um item do sistema de arquivos.
     */
    private BufferedImage loadItemSprite(String itemId) {
        try {
            // Obter definição do objeto
            ObjectSpriteLoader.ObjectDefinition def = objectSpriteLoader.getObjectDefinition(itemId);
            if (def == null || def.spritePaths == null || def.spritePaths.isEmpty()) {
                return createDefaultItemSprite(itemId);
            }
            
            // Usar o primeiro sprite da primeira linha
            List<String> firstRow = def.spritePaths.get(0);
            if (firstRow == null || firstRow.isEmpty()) {
                return createDefaultItemSprite(itemId);
            }
            
            String spritePath = firstRow.get(0);
            InputStream is = getClass().getResourceAsStream(spritePath);
            
            if (is != null) {
                BufferedImage image = ImageIO.read(is);
                is.close();
                return image;
            } else {
                System.err.println("Sprite não encontrado: " + spritePath);
                return createDefaultItemSprite(itemId);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do item " + itemId + ": " + e.getMessage());
            return createDefaultItemSprite(itemId);
        }
    }
    
    /**
     * Cria um sprite padrão baseado no tipo de item.
     */
    private BufferedImage createDefaultItemSprite(String itemId) {
        BufferedImage sprite = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = sprite.createGraphics();
        
        // Determinar cor baseada no tipo de item
        java.awt.Color color = getDefaultColorForItem(itemId);
        
        // Desenhar um quadrado colorido
        g2.setColor(color);
        g2.fillRect(2, 2, 28, 28);
        
        // Desenhar borda
        g2.setColor(Color.WHITE);
        g2.drawRect(2, 2, 28, 28);
        
        // Desenhar ícone baseado no tipo
        g2.setColor(Color.BLACK);
        drawItemIcon(g2, itemId);
        
        g2.dispose();
        return sprite;
    }
    
    /**
     * Obtém a cor padrão para um tipo de item.
     */
    private java.awt.Color getDefaultColorForItem(String itemId) {
        if (itemId.contains("sword") || itemId.contains("weapon")) {
            return java.awt.Color.GRAY;
        } else if (itemId.contains("armor")) {
            return java.awt.Color.DARK_GRAY;
        } else if (itemId.contains("shield") || itemId.contains("lefthand")) {
            return new java.awt.Color(139, 69, 19); // Brown
        } else if (itemId.contains("boot")) {
            return java.awt.Color.BLACK;
        } else if (itemId.contains("key")) {
            return java.awt.Color.YELLOW;
        } else {
            return java.awt.Color.CYAN;
        }
    }
    
    /**
     * Desenha um ícone simples baseado no tipo de item.
     */
    private void drawItemIcon(Graphics2D g2, String itemId) {
        int centerX = 16;
        int centerY = 16;
        
        if (itemId.contains("sword") || itemId.contains("weapon")) {
            // Desenhar espada
            g2.drawLine(centerX, centerY - 8, centerX, centerY + 8);
            g2.drawLine(centerX - 2, centerY + 6, centerX + 2, centerY + 6);
        } else if (itemId.contains("armor")) {
            // Desenhar armadura (retângulo)
            g2.drawRect(centerX - 6, centerY - 8, 12, 16);
        } else if (itemId.contains("shield") || itemId.contains("lefthand")) {
            // Desenhar escudo (círculo)
            g2.drawOval(centerX - 6, centerY - 6, 12, 12);
        } else if (itemId.contains("boot")) {
            // Desenhar bota (oval)
            g2.drawOval(centerX - 8, centerY - 4, 16, 8);
        } else if (itemId.contains("key")) {
            // Desenhar chave
            g2.drawOval(centerX - 2, centerY - 4, 4, 4);
            g2.drawLine(centerX, centerY, centerX, centerY + 6);
            g2.drawLine(centerX, centerY + 6, centerX + 4, centerY + 6);
        } else {
            // Desenhar ponto genérico
            g2.fillOval(centerX - 2, centerY - 2, 4, 4);
        }
    }
    
    /**
     * Limpa o cache de sprites.
     */
    public void clearCache() {
        itemSprites.clear();
    }
    
    /**
     * Pré-carrega sprites de itens específicos.
     */
    public void preloadSprites(String... itemIds) {
        for (String itemId : itemIds) {
            getItemSprite(itemId);
        }
    }
}
