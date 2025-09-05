package br.com.cronicasdeeldoria.game.inventory;

import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.font.FontManager;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Classe responsável pela renderização da interface do inventário.
 */
public class InventoryUI {
    private final GamePanel gamePanel;
    private final Font titleFont;
    private final Font itemFont;
    private final Font smallFont;
    private final ItemSpriteLoader spriteLoader;
    
    // Cores
    private final Color backgroundColor = new Color(0, 0, 0, 0);
    private final Color borderColor = Color.WHITE;
    private final Color selectedColor = Color.YELLOW;
    private final Color emptySlotColor = new Color(100, 100, 100, 100);
    
    // Dimensões
    private static final int SLOT_SIZE = 48;
    private static final int SLOT_SPACING = 4;
    private static final int INVENTORY_WIDTH = 5 * SLOT_SIZE + 4 * SLOT_SPACING;
    private static final int INVENTORY_HEIGHT = 4 * SLOT_SIZE + 3 * SLOT_SPACING;
    private static final int EQUIPMENT_WIDTH = SLOT_SIZE;
    private static final int EQUIPMENT_HEIGHT = 4 * SLOT_SIZE + 3 * SLOT_SPACING;
    private static final int PADDING = 20;
    
    public InventoryUI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.titleFont = FontManager.getFont(20f);
        this.itemFont = FontManager.getFont(10f);
        this.smallFont = FontManager.getFont(12f);
        this.spriteLoader = ItemSpriteLoader.getInstance();
    }
    
    /**
     * Renderiza a interface completa do inventário.
     * @param g2 Contexto gráfico.
     * @param inventoryManager Gerenciador do inventário.
     */
    public void draw(Graphics2D g2, InventoryManager inventoryManager) {
        if (!inventoryManager.isVisible()) return;
        
        int screenWidth = gamePanel.getWidth();
        int screenHeight = gamePanel.getHeight();
        
        // Calcular posição central
        int totalWidth = INVENTORY_WIDTH + EQUIPMENT_WIDTH + PADDING * 3;
        int totalHeight = Math.max(INVENTORY_HEIGHT, EQUIPMENT_HEIGHT) + PADDING * 4;
        
        int startX = (screenWidth - totalWidth) / 2;
        int startY = (screenHeight - totalHeight) / 2;
        
        // Desenhar fundo
        g2.setColor(backgroundColor);
        g2.fillRect(startX, startY, totalWidth, totalHeight);
        
        // Desenhar borda
        g2.setColor(borderColor);
        g2.drawRect(startX, startY, totalWidth, totalHeight);
        
        // Desenhar título
        drawTitle(g2, startX, startY, totalWidth);
        
        // Desenhar inventário
        int inventoryX = startX + PADDING;
        int inventoryY = startY + PADDING + 30;
        drawInventory(g2, inventoryManager, inventoryX, inventoryY);
        
        // Desenhar equipamento
        int equipmentX = inventoryX + INVENTORY_WIDTH + PADDING;
        int equipmentY = inventoryY;
        drawEquipment(g2, inventoryManager, equipmentX, equipmentY);
        
        // Desenhar informações do item selecionado
        drawItemInfo(g2, inventoryManager, startX, startY + totalHeight - 60, totalWidth);
    }
    
    /**
     * Desenha o título da interface.
     */
    private void drawTitle(Graphics2D g2, int x, int y, int width) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        
        String title = "INVENTÁRIO";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = x + (width - fm.stringWidth(title)) / 2;
        int titleY = y + 25;
        
        g2.drawString(title, titleX, titleY);
    }
    
    /**
     * Desenha o grid do inventário.
     */
    private void drawInventory(Graphics2D g2, InventoryManager inventoryManager, int startX, int startY) {
        // Desenhar slots do inventário
        for (int row = 0; row < inventoryManager.getInventoryRows(); row++) {
            for (int col = 0; col < inventoryManager.getInventoryColumns(); col++) {
                int slotX = startX + col * (SLOT_SIZE + SLOT_SPACING);
                int slotY = startY + row * (SLOT_SIZE + SLOT_SPACING);
                
                // Verificar se é o slot selecionado
                boolean isSelected = inventoryManager.isInInventoryMode() && 
                                   inventoryManager.getSelectedRow() == row && 
                                   inventoryManager.getSelectedColumn() == col;
                
                drawSlot(g2, slotX, slotY, isSelected);
                
                // Desenhar item se houver
                int slotIndex = row * inventoryManager.getInventoryColumns() + col;
                Item item = inventoryManager.getItem(slotIndex);
                if (item != null) {
                    drawItemInSlot(g2, item, slotX, slotY);
                }
            }
        }
    }
    
    /**
     * Desenha o equipamento.
     */
    private void drawEquipment(Graphics2D g2, InventoryManager inventoryManager, int startX, int startY) {
        Equipment equipment = inventoryManager.getEquipment();
        
        // Desenhar slots de equipamento
        for (int i = 0; i < equipment.getTotalSlots(); i++) {
            int slotX = startX;
            int slotY = startY + i * (SLOT_SIZE + SLOT_SPACING);
            
            // Verificar se é o slot selecionado
            boolean isSelected = !inventoryManager.isInInventoryMode() && 
                               equipment.getSelectedSlot() == i;
            
            drawSlot(g2, slotX, slotY, isSelected);
            
            // Desenhar item se houver
            Item item = equipment.getEquippedItem(i);
            if (item != null) {
                drawItemInSlot(g2, item, slotX, slotY);
            }
        }
    }
    
    /**
     * Desenha um slot individual.
     */
    private void drawSlot(Graphics2D g2, int x, int y, boolean isSelected) {
        // Fundo do slot
        if (isSelected) {
            g2.setColor(selectedColor);
        } else {
            g2.setColor(emptySlotColor);
        }
        g2.fillRect(x, y, SLOT_SIZE, SLOT_SIZE);
        
        // Borda do slot
        g2.setColor(borderColor);
        g2.drawRect(x, y, SLOT_SIZE, SLOT_SIZE);
    }
    
    /**
     * Desenha um item dentro de um slot.
     */
    private void drawItemInSlot(Graphics2D g2, Item item, int slotX, int slotY) {
        // Tentar carregar a imagem do item
        BufferedImage itemSprite = spriteLoader.getItemSprite(item.getItemId());
        
        if (itemSprite != null) {
            // Desenhar a imagem do item
            g2.drawImage(itemSprite, slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8, null);
        } else {
            // Fallback: desenhar quadrado colorido baseado na raridade
            Color itemColor = item.getRarity().getColor();
            g2.setColor(itemColor);
            g2.fillRect(slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8);
            
            // Desenhar borda do item
            g2.setColor(Color.WHITE);
            g2.drawRect(slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8);
        }
        
        // Desenhar borda da raridade
        g2.setColor(item.getRarity().getColor());
        g2.drawRect(slotX + 2, slotY + 2, SLOT_SIZE - 4, SLOT_SIZE - 4);
        
        // Se for um item empilhável com quantidade > 1, mostrar número
        if (item.isStackable() && item.getStackSize() > 1) {
            // Fundo para o texto
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(slotX + SLOT_SIZE - 16, slotY + SLOT_SIZE - 16, 14, 14);
            
            // Texto da quantidade
            g2.setColor(Color.WHITE);
            g2.setFont(smallFont);
            String stackText = String.valueOf(item.getStackSize());
            FontMetrics fm = g2.getFontMetrics();
            int textX = slotX + SLOT_SIZE - 9 - (fm.stringWidth(stackText) / 2);
            int textY = slotY + SLOT_SIZE - 4;
            g2.drawString(stackText, textX, textY);
        }
    }
    
    /**
     * Desenha informações do item selecionado.
     */
    private void drawItemInfo(Graphics2D g2, InventoryManager inventoryManager, int x, int y, int width) {
        Item selectedItem = inventoryManager.getSelectedItem();
        if (selectedItem == null) return;
        
        g2.setColor(Color.WHITE);
        g2.setFont(itemFont);
        
        int infoY = y + 80;
        
        // Nome do item
        g2.setColor(selectedItem.getRarity().getColor());
        g2.drawString(selectedItem.getName(), x + 10, infoY);
        
        infoY += 15;
        g2.drawString("Raridade: " + selectedItem.getRarity().getDisplayName(), x + 10, infoY);
        
        // Descrição se houver
        if (selectedItem.getDescription() != null && !selectedItem.getDescription().isEmpty()) {
            infoY += 15;
            g2.drawString(selectedItem.getDescription(), x + 10, infoY);
        }

        // Valor do item
        infoY += 15;
        g2.drawString("Valor: " + selectedItem.getValue(), x + 10, infoY);
    }
}
