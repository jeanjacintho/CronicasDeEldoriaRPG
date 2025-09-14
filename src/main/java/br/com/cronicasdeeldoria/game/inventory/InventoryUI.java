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

    // Cores do tema
    private final Color titleColor = Color.WHITE; // Título branco

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
     * Renderiza a interface completa do inventário com estilo da box de diálogo.
     * @param g2 Contexto gráfico.
     * @param inventoryManager Gerenciador do inventário.
     */
    public void draw(Graphics2D g2, InventoryManager inventoryManager) {
        if (!inventoryManager.isVisible()) return;

        int screenWidth = gamePanel.getWidth();
        int screenHeight = gamePanel.getHeight();

        // Overlay semi-transparente
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Calcular posição central
        int totalWidth = INVENTORY_WIDTH + EQUIPMENT_WIDTH + PADDING * 3;
        int totalHeight = Math.max(INVENTORY_HEIGHT, EQUIPMENT_HEIGHT) + PADDING * 4;
        int borderRadius = 15;

        int startX = (screenWidth - totalWidth) / 2;
        int startY = (screenHeight - totalHeight) / 2 - 80; // Mover 80px para cima

        // Sombra da janela
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(startX + 4, startY + 4, totalWidth, totalHeight, borderRadius, borderRadius);

        // Fundo principal da janela
        g2.setColor(new Color(50, 40, 60, 250));
        g2.fillRoundRect(startX, startY, totalWidth, totalHeight, borderRadius, borderRadius);

        // Borda externa
        g2.setColor(new Color(100, 80, 120, 200));
        g2.setStroke(new java.awt.BasicStroke(3));
        g2.drawRoundRect(startX, startY, totalWidth, totalHeight, borderRadius, borderRadius);

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
     * Desenha o título da interface
     */
    private void drawTitle(Graphics2D g2, int x, int y, int width) {
        g2.setFont(titleFont);

        String title = "INVENTÁRIO";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = x + (width - fm.stringWidth(title)) / 2;
        int titleY = y + 35;

        // Sombra do título
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(title, titleX + 2, titleY + 2);

        // Título principal
        g2.setColor(titleColor);
        g2.drawString(title, titleX, titleY);

        // Linha decorativa abaixo do título
        g2.setColor(new Color(100, 80, 120, 200));
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawLine(titleX, titleY + 5, titleX + fm.stringWidth(title), titleY + 5);
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
     * Desenha um slot individual com estilo da box de diálogo.
     */
    private void drawSlot(Graphics2D g2, int x, int y, boolean isSelected) {
        int slotBorderRadius = 8;

        // Sombra do slot
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(x + 1, y + 1, SLOT_SIZE, SLOT_SIZE, slotBorderRadius, slotBorderRadius);

        // Fundo do slot
        if (isSelected) {
            g2.setColor(new Color(255, 215, 0, 50)); // Dourado com transparência
        } else {
            g2.setColor(new Color(50, 50, 70, 150)); // Cinza escuro com transparência
        }
        g2.fillRoundRect(x, y, SLOT_SIZE, SLOT_SIZE, slotBorderRadius, slotBorderRadius);

        // Borda do slot
        g2.setColor(new Color(150, 150, 150));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(x, y, SLOT_SIZE, SLOT_SIZE, slotBorderRadius, slotBorderRadius);

        // Destaque interno se selecionado
        if (isSelected) {
            g2.setColor(new Color(255, 255, 255, 120));
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.drawRoundRect(x + 1, y + 1, SLOT_SIZE - 2, SLOT_SIZE - 2, slotBorderRadius - 1, slotBorderRadius - 1);
        }
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
     * Desenha informações do item selecionado
     */
    private void drawItemInfo(Graphics2D g2, InventoryManager inventoryManager, int x, int y, int width) {
        Item selectedItem = inventoryManager.getSelectedItem();
        if (selectedItem == null) return;

        // Desenhar box de informações do item (sempre aparece)
        drawItemInfoBox(g2, selectedItem, x, y + 150, width);
    }

    /**
     * Desenha uma box completa de informações do item
     */
    private void drawItemInfoBox(Graphics2D g2, Item item, int x, int y, int width) {

        // Calcular dimensões da box de informações
        int padding = 15;
        int borderRadius = 10;
        int boxWidth = width - 20;

        // Calcular altura dinâmica baseada no conteúdo
        int baseHeight = 100; // Altura base para nome, raridade e valor
        int descriptionHeight = 0;
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            // Calcular altura necessária para a descrição
            String[] words = item.getDescription().split(" ");
            StringBuilder currentLine = new StringBuilder();
            int maxWidth = boxWidth - (padding * 2);
            int lines = 1;

            for (String word : words) {
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                FontMetrics textFm = g2.getFontMetrics(smallFont);
                if (textFm.stringWidth(testLine) <= maxWidth) {
                    currentLine = new StringBuilder(testLine);
                } else {
                    lines++;
                    currentLine = new StringBuilder(word);
                }
            }
            descriptionHeight = lines * 12 + 20; // 12px por linha + padding
        }

        int boxHeight = baseHeight + descriptionHeight;

        int boxX = x + 10;
        int boxY = y - 70;

        // Sombra da box de informações
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(boxX + 2, boxY + 2, boxWidth, boxHeight, borderRadius, borderRadius);

        // Fundo da box de informações
        g2.setColor(new Color(50, 40, 60, 250)); // Mesmo estilo do pause overlay
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, borderRadius, borderRadius);

        // Borda externa da box de informações
        g2.setColor(new Color(100, 80, 120, 200));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, borderRadius, borderRadius);

        // Título "INFORMAÇÕES DO ITEM"
        g2.setFont(smallFont);
        String titleText = "INFORMAÇÕES DO ITEM";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = boxX + (boxWidth - fm.stringWidth(titleText)) / 2;
        int titleY = boxY + 15;

        // Sombra do título
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(titleText, titleX + 2, titleY + 2);

        // Título principal
        g2.setColor(Color.WHITE);
        g2.drawString(titleText, titleX, titleY);

        // Linha decorativa abaixo do título
        g2.setColor(new Color(100, 80, 120, 200));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(titleX, titleY + 3, titleX + fm.stringWidth(titleText), titleY + 3);

        int textY = titleY + 20;

        // Nome do item
        g2.setFont(itemFont);
        String nameText = item.getName();
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(nameText, boxX + padding + 2, textY + 2);
        g2.setColor(item.getRarity().getColor());
        g2.drawString(nameText, boxX + padding, textY);
        textY += 15;

        // Strength Bônus
        g2.setFont(smallFont);
        String strengthText = "Strength Bônus: " + item.getStrengthFromEquip();
        //String rarityText = "Strength: +" + item.getRarity().getDisplayName();
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(strengthText, boxX + padding + 2, textY + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(strengthText, boxX + padding, textY);
        textY += 15;

        // Armor Bônus
        String armorText = "Armor Bônus: " + item.getArmorFromEquip();
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(armorText, boxX + padding + 2, textY + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(armorText, boxX + padding, textY);
        textY += 15;

        // Health Bônus
        g2.setFont(smallFont);
        String healthText = "Health Bônus: " + item.getHealthFromEquip();
        //String rarityText = "Strength: +" + item.getRarity().getDisplayName();
        g2.setColor(new Color(0, 0, 30, 150));
        g2.drawString(healthText, boxX + padding + 2, textY + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(healthText, boxX + padding, textY);
        textY += 15;

        // Mana Bônus
        String manaText = "Mana Bônus: " + item.getManaFromEquip();
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(manaText, boxX + padding, textY + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(manaText, boxX + padding, textY);
        textY += 15;

        // Descrição se houver
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            textY += 5; // Espaçamento extra

            // Título da descrição
            String descTitleText = "Descrição:";
            g2.setColor(new Color(0, 0, 0, 150));
            g2.drawString(descTitleText, boxX + padding + 2, textY + 2);
            g2.setColor(Color.WHITE);
            g2.drawString(descTitleText, boxX + padding, textY);
            textY += 15;

            // Texto da descrição
            String description = item.getDescription();
            String[] words = description.split(" ");
            StringBuilder currentLine = new StringBuilder();
            int maxWidth = boxWidth - (padding * 2);

            for (String word : words) {
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                FontMetrics textFm = g2.getFontMetrics();
                if (textFm.stringWidth(testLine) <= maxWidth) {
                    currentLine = new StringBuilder(testLine);
                } else {
                    if (currentLine.length() > 0) {
                        // Sombra do texto
                        g2.setColor(new Color(0, 0, 0, 150));
                        g2.drawString(currentLine.toString(), boxX + padding + 2, textY + 2);
                        // Texto principal
                        g2.setColor(Color.WHITE);
                        g2.drawString(currentLine.toString(), boxX + padding, textY);
                        textY += 12;
                        currentLine = new StringBuilder(word);
                    } else {
                        // Sombra do texto
                        g2.setColor(new Color(0, 0, 0, 150));
                        g2.drawString(word, boxX + padding + 2, textY + 2);
                        // Texto principal
                        g2.setColor(Color.WHITE);
                        g2.drawString(word, boxX + padding, textY);
                        textY += 12;
                    }
                }
            }
            if (currentLine.length() > 0) {
                // Sombra do texto
                g2.setColor(new Color(0, 0, 0, 150));
                g2.drawString(currentLine.toString(), boxX + padding + 2, textY + 2);
                // Texto principal
                g2.setColor(Color.WHITE);
                g2.drawString(currentLine.toString(), boxX + padding, textY);
            }
        }
     }
}
