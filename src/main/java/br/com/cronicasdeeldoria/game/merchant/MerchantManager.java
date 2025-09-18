package br.com.cronicasdeeldoria.game.merchant;

import br.com.cronicasdeeldoria.audio.AudioManager;
import br.com.cronicasdeeldoria.entity.character.npc.MerchantNpc;
import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.game.inventory.ItemFactory;
import br.com.cronicasdeeldoria.game.inventory.InventoryManager;
import br.com.cronicasdeeldoria.game.money.PlayerMoney;

import java.util.List;

/**
 * Gerenciador responsável por controlar as transações do sistema de comércio com compra e venda.
 */
public class MerchantManager {
    private final InventoryManager inventoryManager;
    private final PlayerMoney playerMoney;
    private final AudioManager audioManager;

    private MerchantNpc currentMerchant;
    private boolean visible;
    private int selectedItemIndex;
    private boolean playerInventorySelected; // true = inventário do jogador, false = inventário do comerciante
    private int maxItemsPerRow = 5; // Alterado de 4 para 5

    public MerchantManager(InventoryManager inventoryManager, PlayerMoney playerMoney) {
        this.inventoryManager = inventoryManager;
        this.playerMoney = playerMoney;
        this.audioManager = AudioManager.getInstance();
        this.visible = false;
        this.selectedItemIndex = 0;
        this.playerInventorySelected = false;
    }

    /**
     * Abre a interface do comerciante.
     * @param merchant Comerciante com quem interagir.
     */
    public void openMerchant(MerchantNpc merchant) {
        this.currentMerchant = merchant;
        this.visible = true;
        this.selectedItemIndex = 0;
        this.playerInventorySelected = false;

    }

    /**
     * Fecha a interface do comerciante.
     */
    public void closeMerchant() {
        this.visible = false;
        this.currentMerchant = null;
        this.selectedItemIndex = 0;
        this.playerInventorySelected = false;

    }

    /**
     * Alterna a visibilidade da interface.
     */
    public void toggleVisibility() {
        if (visible) {
            closeMerchant();
        }
    }

    /**
     * Alterna entre o inventário do comerciante e do jogador.
     */
    public void toggleInventory() {
        this.playerInventorySelected = !this.playerInventorySelected;
        this.selectedItemIndex = 0; // Resetar seleção ao alternar
    }

    /**
     * Move a seleção para cima.
     */
    public void moveUp() {
        if (currentMerchant == null) return;

        int currentRow = selectedItemIndex / maxItemsPerRow;

        if (currentRow > 0) {
            selectedItemIndex -= maxItemsPerRow;
            if (selectedItemIndex < 0) {
                selectedItemIndex = 0;
            }
        }
    }

    /**
     * Move a seleção para baixo.
     */
    public void moveDown() {
        if (currentMerchant == null) return;

        int currentRow = selectedItemIndex / maxItemsPerRow;
        int maxRows = 4; // Grid fixo de 5x4

        if (currentRow < maxRows - 1) {
            selectedItemIndex += maxItemsPerRow;
            int maxSlots = maxItemsPerRow * maxRows; // 5x4 = 20 slots
            if (selectedItemIndex >= maxSlots) {
                selectedItemIndex = maxSlots - 1;
            }
        }
    }

    /**
     * Move a seleção para a esquerda.
     */
    public void moveLeft() {
        if (currentMerchant == null) return;

        int currentCol = selectedItemIndex % maxItemsPerRow;

        // Se está na primeira coluna da linha atual
        if (currentCol == 0) {
            // Se está no inventário do jogador, alternar para o do comerciante
            if (playerInventorySelected) {
                toggleInventory();
                return;
            }
        } else {
            // Mover para a esquerda dentro do inventário atual
            if (selectedItemIndex > 0) {
                selectedItemIndex--;
            }
        }
    }

    /**
     * Move a seleção para a direita.
     */
    public void moveRight() {
        if (currentMerchant == null) return;

        int currentCol = selectedItemIndex % maxItemsPerRow;

        // Se está na última coluna da linha atual
        if (currentCol >= maxItemsPerRow - 1) {
            // Se está no inventário do comerciante, alternar para o do jogador
            if (!playerInventorySelected) {
                toggleInventory();
                return;
            }
        } else {
            // Mover para a direita dentro do inventário atual
            int maxSlots = maxItemsPerRow * 4; // 5x4 = 20 slots
            if (selectedItemIndex < maxSlots - 1) {
                selectedItemIndex++;
            }
        }
    }

    /**
     * Tenta comprar o item selecionado do comerciante.
     * @return true se a compra foi bem-sucedida.
     */
    public boolean purchaseSelectedItem() {
        if (currentMerchant == null || playerInventorySelected) return false;

        MerchantNpc.MerchantItem merchantItem = currentMerchant.getMerchantItem(selectedItemIndex);
        if (merchantItem == null) return false;

        // Verificar se o item está disponível
        if (!merchantItem.isAvailable()) {
            return false;
        }

        // Verificar se o jogador tem dinheiro suficiente
        if (!playerMoney.hasEnoughMoney(merchantItem.getPrice())) {
            return false;
        }

        // Verificar se já existe um item igual no inventário que pode ser empilhado
        Item existingItem = findStackableItem(merchantItem.getItemId());
        if (existingItem != null) {
            // Empilhar diretamente no item existente
            Item tempItem = ItemFactory.createItem(merchantItem.getItemId());
            if (tempItem != null) {
                // Usar o preço definido no MerchantItem
                tempItem.setValue(merchantItem.getPrice());
                int stacked = existingItem.stackItem(tempItem);

                if (stacked > 0) {
                    // Remover dinheiro do jogador
                    playerMoney.removeMoney(merchantItem.getPrice());
                    // Reduzir estoque do comerciante
                    merchantItem.reduceStock();
                    // Reproduzir som de compra
                    audioManager.playSoundEffect("item_buy");
                    return true;
                }
            }
        }

        // Se não conseguiu empilhar, criar novo item
        Item purchasedItem = currentMerchant.purchaseItem(selectedItemIndex);
        if (purchasedItem != null) {
            // Remover dinheiro do jogador
            playerMoney.removeMoney(merchantItem.getPrice());

            // Adicionar item ao inventário
            boolean added = inventoryManager.addItem(purchasedItem);
            if (!added) {
                // Se não conseguiu adicionar, devolver o dinheiro
                playerMoney.addMoney(merchantItem.getPrice());
                return false;
            }

            // Reproduzir som de compra
            audioManager.playSoundEffect("item_buy");
            return true;
        }

        return false;
    }

    /**
     * Tenta vender o item selecionado do inventário do jogador.
     * @return true se a venda foi bem-sucedida.
     */
    public boolean sellSelectedItem() {
        if (currentMerchant == null || !playerInventorySelected) return false;

        List<Item> items = inventoryManager.getInventorySlots();
        if (selectedItemIndex >= items.size() || items.get(selectedItemIndex) == null) return false;

        Item item = items.get(selectedItemIndex);

        // Verificar se o item tem valor de venda
        if (item.getValue() <= 0) {
            return false;
        }

        // Calcular valor de venda (20 do valor original)
        int sellPrice = item.getValue() / 5;
        if (sellPrice <= 0) sellPrice = 1; // Mínimo de 1 moeda

        // Verificar se o item é stackable
        if (item.isStackable() && item.getStackSize() > 1) {
            // Reduzir a quantidade do item original
            item.setStackSize(item.getStackSize() - 1);
        } else {
            // Se não é stackable ou tem apenas 1, remover completamente
            Item removedItem = inventoryManager.removeItem(selectedItemIndex);
            if (removedItem == null) {
                return false;
            }
        }

        // Adicionar dinheiro ao jogador
        playerMoney.addMoney(sellPrice);

        // Adicionar o item vendido ao inventário do merchant com preço de revenda
        // O comerciante vende por um preço maior para ter lucro
        Item itemToSell = item.copy();
        itemToSell.setStackSize(1);
        itemToSell.setValue(sellPrice * 2); // Comerciante vende pelo dobro do que pagou (100% do valor original)
        addItemToMerchant(itemToSell);

        return true;
    }

    /**
     * Adiciona um item ao inventário do merchant.
     * @param item Item a ser adicionado.
     */
    private void addItemToMerchant(Item item) {
        if (currentMerchant == null || item == null) return;

        // Tentar empilhar com item existente se possível
        List<MerchantNpc.MerchantItem> merchantItems = currentMerchant.getMerchantItems();
        for (MerchantNpc.MerchantItem merchantItem : merchantItems) {
            if (merchantItem.getItemId().equals(item.getItemId())) {
                // Empilhar com item existente
                merchantItem.addStock(1);
                return;
            }
        }

        // Se não conseguiu empilhar, adicionar novo item
        // Verificar se há espaço no inventário do merchant (máximo 20 slots)
        if (merchantItems.size() < 20) {
            MerchantNpc.MerchantItem newMerchantItem = new MerchantNpc.MerchantItem(
                item.getItemId(),
                item.getValue(),
                1
            );
            merchantItems.add(newMerchantItem);
        }
    }

    /**
     * Executa a ação apropriada baseada no inventário selecionado.
     * @return true se a ação foi bem-sucedida.
     */
    public boolean executeAction() {
        if (playerInventorySelected) {
            return sellSelectedItem();
        } else {
            return purchaseSelectedItem();
        }
    }

    /**
     * Retorna o comerciante atual.
     * @return MerchantNpc atual ou null.
     */
    public MerchantNpc getCurrentMerchant() {
        return currentMerchant;
    }

    /**
     * Verifica se a interface está visível.
     * @return true se visível.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Retorna o índice do item selecionado.
     * @return Índice do item selecionado.
     */
    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    /**
     * Verifica se o inventário do jogador está selecionado.
     * @return true se o inventário do jogador está selecionado.
     */
    public boolean isPlayerInventorySelected() {
        return playerInventorySelected;
    }

    /**
     * Retorna o sistema de dinheiro do jogador.
     * @return PlayerMoney do jogador.
     */
    public PlayerMoney getPlayerMoney() {
        return playerMoney;
    }

    /**
     * Retorna o gerenciador de inventário.
     * @return InventoryManager.
     */
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    /**
     * Retorna o item selecionado (merchant ou player).
     * @return Item selecionado ou null.
     */
    public Object getSelectedItem() {
        if (playerInventorySelected) {
            List<Item> items = inventoryManager.getInventorySlots();
            return (selectedItemIndex < items.size()) ? items.get(selectedItemIndex) : null;
        } else {
            return (currentMerchant != null) ? currentMerchant.getMerchantItem(selectedItemIndex) : null;
        }
    }

    /**
     * Retorna o preço do item selecionado.
     * @return Preço do item ou -1 se nenhum item selecionado.
     */
    public int getSelectedItemPrice() {
        if (playerInventorySelected) {
            Item item = (Item) getSelectedItem();
            return item != null ? item.getValue() / 2 : -1; // Valor de venda é 50% do valor original
        } else {
            MerchantNpc.MerchantItem item = (MerchantNpc.MerchantItem) getSelectedItem();
            return item != null ? item.getPrice() : -1;
        }
    }

    /**
     * Verifica se pode executar ação no item selecionado.
     * @return true se pode comprar/vender.
     */
    public boolean canExecuteAction() {
        if (playerInventorySelected) {
            Item item = (Item) getSelectedItem();
            return item != null && item.getValue() > 0;
        } else {
            MerchantNpc.MerchantItem item = (MerchantNpc.MerchantItem) getSelectedItem();
            return item != null && item.isAvailable() && playerMoney.hasEnoughMoney(item.getPrice());
        }
    }

    /**
     * Encontra um item stackable no inventário pelo ID.
     * Otimizado para melhor performance.
     * @param itemId ID do item.
     * @return Item encontrado ou null.
     */
    private Item findStackableItem(String itemId) {
        List<Item> items = inventoryManager.getInventorySlots();
        for (Item item : items) {
            if (item != null && item.getItemId().equals(itemId) && item.isStackable() && item.getStackSize() < item.getMaxStackSize()) {
                return item;
            }
        }
        return null;
    }
}
