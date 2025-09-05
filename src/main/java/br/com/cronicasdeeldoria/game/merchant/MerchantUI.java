package br.com.cronicasdeeldoria.game.merchant;

import br.com.cronicasdeeldoria.entity.character.npc.MerchantNpc;
import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.font.FontManager;
import br.com.cronicasdeeldoria.game.inventory.ItemFactory;
import br.com.cronicasdeeldoria.game.inventory.ItemSpriteLoader;
import br.com.cronicasdeeldoria.game.inventory.InventoryManager;
import br.com.cronicasdeeldoria.game.money.PlayerMoney;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface de usuário para o sistema de comércio com compra e venda.
 */
public class MerchantUI {
    private final GamePanel gamePanel;
    private final Font titleFont;
    private final Font itemFont;
    private final Font smallFont;
    private final Font priceFont;
    private final ItemSpriteLoader spriteLoader;
    private BufferedImage coinIcon;
    
    // Cache de informações de itens para performance
    private final Map<String, Item> itemInfoCache = new HashMap<>();
    
    // Posições pré-calculadas dos slots para performance (será inicializado no construtor)
    private int[][] slotPositions;
    
    // Cores
    private final Color backgroundColor = new Color(0, 0, 0, 200);
    private final Color borderColor = Color.WHITE;
    private final Color selectedColor = Color.YELLOW;
    private final Color availableColor = new Color(100, 255, 100, 100);
    private final Color unavailableColor = new Color(255, 100, 100, 100);
    private final Color moneyColor = new Color(255, 215, 0); // Cor dourada
    private final Color playerInventoryColor = new Color(100, 100, 255, 100);
    
    // Dimensões
    private static final int SLOT_SIZE = 48;
    private static final int SLOT_SPACING = 6;
    private static final int ITEMS_PER_ROW = 5; // Alterado de 4 para 5
    private static final int MAX_ROWS = 4; // Alterado de 3 para 4
    private static final int INVENTORY_WIDTH = ITEMS_PER_ROW * SLOT_SIZE + (ITEMS_PER_ROW - 1) * SLOT_SPACING;
    private static final int INVENTORY_HEIGHT = MAX_ROWS * SLOT_SIZE + (MAX_ROWS - 1) * SLOT_SPACING;
    private static final int PADDING = 20;
    private static final int INVENTORY_SPACING = 40; // Espaço entre os dois inventários
    
    public MerchantUI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.titleFont = FontManager.getFont(20f);
        this.itemFont = FontManager.getFont(12f);
        this.smallFont = FontManager.getFont(10f);
        this.priceFont = FontManager.getFont(14f);
        this.spriteLoader = ItemSpriteLoader.getInstance();
        
        // Inicializar posições pré-calculadas dos slots
        this.slotPositions = new int[20][2];
        for (int i = 0; i < 20; i++) {
            slotPositions[i][0] = (i % ITEMS_PER_ROW) * (SLOT_SIZE + SLOT_SPACING);
            slotPositions[i][1] = (i / ITEMS_PER_ROW) * (SLOT_SIZE + SLOT_SPACING);
        }
        
        loadCoinIcon();
    }
    
    /**
     * Carrega o ícone da moeda.
     */
    private void loadCoinIcon() {
        try {
            coinIcon = ImageIO.read(getClass().getResourceAsStream("/ui/coin.png"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar ícone da moeda: " + e.getMessage());
        }
    }
    
    /**
     * Obtém informações do item usando cache para performance.
     * @param itemId ID do item.
     * @return Item com informações completas.
     */
    private Item getCachedItemInfo(String itemId) {
        return itemInfoCache.computeIfAbsent(itemId, ItemFactory::createItem);
    }
    
    /**
     * Limpa o cache de informações de itens.
     */
    public void clearItemCache() {
        itemInfoCache.clear();
    }
    
    /**
     * Renderiza a interface completa do comerciante com dois inventários.
     * @param g2 Contexto gráfico.
     * @param merchantManager Gerenciador do comerciante.
     */
    public void draw(Graphics2D g2, MerchantManager merchantManager) {
        if (!merchantManager.isVisible()) return;
        
        MerchantNpc merchant = merchantManager.getCurrentMerchant();
        if (merchant == null) return;
        
        int screenWidth = gamePanel.getWidth();
        int screenHeight = gamePanel.getHeight();
        
        // Calcular dimensões totais para dois inventários lado a lado
        int totalWidth = (INVENTORY_WIDTH * 2) + INVENTORY_SPACING + (PADDING * 2);
        int totalHeight = INVENTORY_HEIGHT + PADDING * 3 + 120; // Espaço extra para título e informações
        
        int startX = (screenWidth - totalWidth) / 2;
        int startY = (screenHeight - totalHeight) / 2;
        
        // Desenhar fundo
        g2.setColor(backgroundColor);
        g2.fillRect(startX, startY, totalWidth, totalHeight);
        
        // Desenhar borda
        g2.setColor(borderColor);
        g2.drawRect(startX, startY, totalWidth, totalHeight);
        
        // Desenhar título
        drawTitle(g2, merchant, startX, startY, totalWidth);
        
        // Desenhar dinheiro do jogador no canto superior direito
        drawPlayerMoney(g2, merchantManager, gamePanel.getWidth());
        
        // Calcular posições dos inventários
        int merchantInventoryX = startX + PADDING;
        int playerInventoryX = merchantInventoryX + INVENTORY_WIDTH + INVENTORY_SPACING;
        int inventoriesY = startY + PADDING + 70;
        
        // Desenhar inventário do comerciante
        drawMerchantInventory(g2, merchantManager, merchantInventoryX, inventoriesY);
        
        // Desenhar inventário do jogador
        drawPlayerInventory(g2, merchantManager, playerInventoryX, inventoriesY);
        
        // Desenhar informações do item selecionado
        drawItemInfo(g2, merchantManager, startX, startY + totalHeight - 80, totalWidth);
    }
    
    /**
     * Desenha o título da interface.
     */
    private void drawTitle(Graphics2D g2, MerchantNpc merchant, int x, int y, int width) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        
        String title = "COMÉRCIO - " + merchant.getMerchantName().toUpperCase();
        FontMetrics fm = g2.getFontMetrics();
        int titleX = x + (width - fm.stringWidth(title)) / 2;
        int titleY = y + 25;
        
        g2.drawString(title, titleX, titleY);
    }
    
    /**
     * Desenha o dinheiro atual do jogador.
     */
    private void drawPlayerMoney(Graphics2D g2, MerchantManager merchantManager, int screenWidth) {
        PlayerMoney playerMoney = merchantManager.getPlayerMoney();
        if (playerMoney == null) return;
        
        g2.setFont(priceFont);
        
        int iconSize = 40;
        int padding = 20;
        
        // Calcular posição no canto superior direito
        String moneyText = playerMoney.getMoneyDisplay();
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(moneyText);
        
        int totalWidth = iconSize + 5 + textWidth; // ícone + espaçamento + texto
        int x = screenWidth - totalWidth - padding;
        int y = 15; // Mesma posição Y dos corações
        
        // Desenhar ícone da moeda
        if (coinIcon != null) {
            g2.drawImage(coinIcon, x, y, iconSize, iconSize, null);
        }
        
        // Desenhar texto do dinheiro
        g2.setColor(Color.WHITE);
        g2.drawString(moneyText, x + iconSize + 5, y + iconSize/2 + 5);
    }
    
    /**
     * Desenha o inventário do comerciante.
     */
    private void drawMerchantInventory(Graphics2D g2, MerchantManager merchantManager, int startX, int startY) {
        MerchantNpc merchant = merchantManager.getCurrentMerchant();
        if (merchant == null) return;
        
        // Desenhar título do inventário do comerciante
        g2.setColor(Color.WHITE);
        g2.setFont(itemFont);
        String merchantTitle = merchantManager.isPlayerInventorySelected() ? "LOJA" : "LOJA [SELECIONADO]";
        g2.drawString(merchantTitle, startX, startY - 5);
        
        int selectedIndex = merchantManager.getSelectedItemIndex();
        int totalSlots = ITEMS_PER_ROW * MAX_ROWS; // Sempre desenhar todos os slots
        
        for (int i = 0; i < totalSlots; i++) {
            // Usar posições pré-calculadas para melhor performance
            int slotX = startX + slotPositions[i][0];
            int slotY = startY + slotPositions[i][1];
            
            // Verificar se é o item selecionado
            boolean isSelected = selectedIndex == i && !merchantManager.isPlayerInventorySelected();
            
            // Verificar se há item neste slot e se está disponível
            boolean hasItem = i < merchant.getItemCount();
            boolean isAvailable = hasItem && merchant.isItemAvailable(i);
            
            drawItemSlot(g2, slotX, slotY, isSelected, isAvailable, false);
            
            // Desenhar item se houver
            MerchantNpc.MerchantItem merchantItem = merchant.getMerchantItem(i);
            if (merchantItem != null) {
                drawMerchantItemInSlot(g2, merchantItem, slotX, slotY);
            }
        }
    }
    
    /**
     * Desenha o inventário do jogador.
     */
    private void drawPlayerInventory(Graphics2D g2, MerchantManager merchantManager, int startX, int startY) {
        InventoryManager inventoryManager = merchantManager.getInventoryManager();
        if (inventoryManager == null) return;
        
        // Desenhar título do inventário do jogador
        g2.setColor(Color.WHITE);
        g2.setFont(itemFont);
        String playerTitle = merchantManager.isPlayerInventorySelected() ? "SEU INVENTÁRIO [SELECIONADO]" : "SEU INVENTÁRIO";
        g2.drawString(playerTitle, startX, startY - 5);
        
        int selectedIndex = merchantManager.getSelectedItemIndex();
        List<Item> items = inventoryManager.getInventorySlots();
        int totalSlots = ITEMS_PER_ROW * MAX_ROWS; // Sempre desenhar todos os slots
        
        for (int i = 0; i < totalSlots; i++) {
            // Usar posições pré-calculadas para melhor performance
            int slotX = startX + slotPositions[i][0];
            int slotY = startY + slotPositions[i][1];
            
            // Verificar se é o item selecionado
            boolean isSelected = selectedIndex == i && merchantManager.isPlayerInventorySelected();
            
            // Verificar se há item neste slot e se está disponível para venda
            boolean hasItem = i < items.size() && items.get(i) != null;
            boolean isAvailable = hasItem && items.get(i).getValue() > 0;
            
            drawItemSlot(g2, slotX, slotY, isSelected, isAvailable, true);
            
            // Desenhar item se houver
            if (items.get(i) != null) {
                drawPlayerItemInSlot(g2, items.get(i), slotX, slotY);
            }
        }
    }
    
    /**
     * Desenha um slot de item.
     */
    private void drawItemSlot(Graphics2D g2, int x, int y, boolean isSelected, boolean isAvailable, boolean isPlayerInventory) {
        // Fundo do slot baseado na disponibilidade e tipo
        if (isSelected) {
            g2.setColor(selectedColor);
        } else if (isPlayerInventory) {
            g2.setColor(playerInventoryColor);
        } else if (isAvailable) {
            g2.setColor(availableColor);
        } else {
            g2.setColor(unavailableColor);
        }
        g2.fillRect(x, y, SLOT_SIZE, SLOT_SIZE);
        
        // Borda do slot
        g2.setColor(borderColor);
        g2.drawRect(x, y, SLOT_SIZE, SLOT_SIZE);
    }
    
    /**
     * Desenha um item do comerciante dentro de um slot.
     */
    private void drawMerchantItemInSlot(Graphics2D g2, MerchantNpc.MerchantItem merchantItem, int slotX, int slotY) {
        // Tentar carregar a imagem do item
        BufferedImage itemSprite = spriteLoader.getItemSprite(merchantItem.getItemId());
        
        if (itemSprite != null) {
            // Desenhar a imagem do item
            g2.drawImage(itemSprite, slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8, null);
        } else {
            // Fallback: desenhar quadrado colorido
            g2.setColor(Color.GRAY);
            g2.fillRect(slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8);
            
            // Desenhar borda do item
            g2.setColor(Color.WHITE);
            g2.drawRect(slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8);
        }
        
        // Desenhar preço no canto inferior direito
        g2.setColor(moneyColor);
        g2.setFont(smallFont);
        String priceText = String.valueOf(merchantItem.getPrice());
        FontMetrics fm = g2.getFontMetrics();
        int priceX = slotX + SLOT_SIZE - fm.stringWidth(priceText) - 2;
        int priceY = slotY + SLOT_SIZE - 2;
        g2.drawString(priceText, priceX, priceY);
        
        // Desenhar estoque no canto superior esquerdo (formato "X5")
        g2.setColor(Color.WHITE);
        g2.setFont(smallFont);
        String stockText = "X" + merchantItem.getStock();
        FontMetrics stockFm = g2.getFontMetrics();
        int stockX = slotX + 2;
        int stockY = slotY + stockFm.getHeight() + 2;
        g2.drawString(stockText, stockX, stockY);
        
        // Se não está disponível, desenhar X vermelho
        if (!merchantItem.isAvailable()) {
            g2.setColor(Color.RED);
            g2.setFont(FontManager.getFont(16f));
            FontMetrics xFm = g2.getFontMetrics();
            int xX = slotX + (SLOT_SIZE - xFm.stringWidth("X")) / 2;
            int xY = slotY + SLOT_SIZE / 2 + 4;
            g2.drawString("X", xX, xY);
        }
    }
    
    /**
     * Desenha um item do jogador dentro de um slot.
     */
    private void drawPlayerItemInSlot(Graphics2D g2, Item item, int slotX, int slotY) {
        // Tentar carregar a imagem do item
        BufferedImage itemSprite = spriteLoader.getItemSprite(item.getItemId());
        
        if (itemSprite != null) {
            // Desenhar a imagem do item
            g2.drawImage(itemSprite, slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8, null);
        } else {
            // Fallback: desenhar quadrado colorido
            g2.setColor(Color.GRAY);
            g2.fillRect(slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8);
            
            // Desenhar borda do item
            g2.setColor(Color.WHITE);
            g2.drawRect(slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8);
        }
        
        // Desenhar valor de venda no canto inferior direito
        g2.setColor(moneyColor);
        g2.setFont(smallFont);
        String valueText = String.valueOf(item.getValue());
        FontMetrics fm = g2.getFontMetrics();
        int valueX = slotX + SLOT_SIZE - fm.stringWidth(valueText) - 2;
        int valueY = slotY + SLOT_SIZE - 2;
        g2.drawString(valueText, valueX, valueY);
        
        // Desenhar quantidade no canto superior esquerdo (formato "X5")
        g2.setColor(Color.WHITE);
        g2.setFont(smallFont);
        String quantityText = "X" + item.getStackSize();
        FontMetrics qtyFm = g2.getFontMetrics();
        int qtyX = slotX + 2;
        int qtyY = slotY + qtyFm.getHeight() + 2;
        g2.drawString(quantityText, qtyX, qtyY);
    }
    
    /**
     * Desenha informações do item selecionado.
     */
    private void drawItemInfo(Graphics2D g2, MerchantManager merchantManager, int x, int y, int width) {
        MerchantNpc merchant = merchantManager.getCurrentMerchant();
        if (merchant == null) return;
        
        int selectedIndex = merchantManager.getSelectedItemIndex();
        boolean isPlayerInventory = merchantManager.isPlayerInventorySelected();
        
        g2.setColor(Color.WHITE);
        g2.setFont(itemFont);
        
        int infoY = y + 20;
        
        if (isPlayerInventory) {
            // Informações do item do jogador
            InventoryManager inventoryManager = merchantManager.getInventoryManager();
            if (inventoryManager != null) {
                List<Item> items = inventoryManager.getInventorySlots();
                if (selectedIndex < items.size() && items.get(selectedIndex) != null) {
                    Item item = items.get(selectedIndex);
                    
                    // Nome do item
                    g2.setColor(Color.WHITE);
                    g2.setFont(itemFont);
                    g2.drawString(item.getName(), x + 10, infoY);
                    infoY += 15;
                    
                    // Raridade do item
                    g2.setColor(item.getRarity().getColor());
                    g2.drawString("Raridade: " + item.getRarity().getDisplayName(), x + 10, infoY);
                    infoY += 15;
                    
                    // Descrição do item
                    g2.setColor(Color.WHITE);
                    g2.setFont(smallFont);
                    String description = item.getDescription();
                    if (description != null && !description.isEmpty()) {
                        // Quebrar descrição em linhas se for muito longa
                        String[] words = description.split(" ");
                        StringBuilder currentLine = new StringBuilder();
                        int maxWidth = width - 20;
                        
                        for (String word : words) {
                            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                            FontMetrics fm = g2.getFontMetrics();
                            if (fm.stringWidth(testLine) <= maxWidth) {
                                currentLine = new StringBuilder(testLine);
                            } else {
                                if (currentLine.length() > 0) {
                                    g2.drawString(currentLine.toString(), x + 10, infoY);
                                    infoY += 12;
                                    currentLine = new StringBuilder(word);
                                } else {
                                    g2.drawString(word, x + 10, infoY);
                                    infoY += 12;
                                }
                            }
                        }
                        if (currentLine.length() > 0) {
                            g2.drawString(currentLine.toString(), x + 10, infoY);
                            infoY += 12;
                        }
                    }
                    
                    // Quantidade (se stackable)
                    infoY += 5;
                    g2.setFont(itemFont);
                    g2.setColor(Color.WHITE);
                    if (item.isStackable()) {
                        g2.drawString("Quantidade: " + item.getStackSize(), x + 10, infoY);
                    }
                }
            }
        } else {
            // Informações do item do comerciante
            MerchantNpc.MerchantItem selectedItem = merchant.getMerchantItem(selectedIndex);
            if (selectedItem != null) {
                // Usar cache para obter informações completas
                Item tempItem = getCachedItemInfo(selectedItem.getItemId());
                if (tempItem != null) {
                    // Nome do item
                    g2.setColor(Color.WHITE);
                    g2.setFont(itemFont);
                    g2.drawString(tempItem.getName(), x + 10, infoY);
                    infoY += 15;
                    
                    // Raridade do item
                    g2.setColor(tempItem.getRarity().getColor());
                    g2.drawString("Raridade: " + tempItem.getRarity().getDisplayName(), x + 10, infoY);
                    infoY += 15;
                    
                    // Descrição do item
                    g2.setColor(Color.WHITE);
                    g2.setFont(smallFont);
                    String description = tempItem.getDescription();
                    if (description != null && !description.isEmpty()) {
                        // Quebrar descrição em linhas se for muito longa
                        String[] words = description.split(" ");
                        StringBuilder currentLine = new StringBuilder();
                        int maxWidth = width - 20;
                        
                        for (String word : words) {
                            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                            FontMetrics fm = g2.getFontMetrics();
                            if (fm.stringWidth(testLine) <= maxWidth) {
                                currentLine = new StringBuilder(testLine);
                            } else {
                                if (currentLine.length() > 0) {
                                    g2.drawString(currentLine.toString(), x + 10, infoY);
                                    infoY += 12;
                                    currentLine = new StringBuilder(word);
                                } else {
                                    g2.drawString(word, x + 10, infoY);
                                    infoY += 12;
                                }
                            }
                        }
                        if (currentLine.length() > 0) {
                            g2.drawString(currentLine.toString(), x + 10, infoY);
                            infoY += 12;
                        }
                    }
                }
            }
        }
    }
}