package br.com.cronicasdeeldoria.game.inventory;

import br.com.cronicasdeeldoria.entity.character.Character;
import br.com.cronicasdeeldoria.entity.character.classes.CharacterClass;
import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.entity.item.ItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia o inventário e sistema de equipamento do jogador.
 */
public class InventoryManager {
    private static final int INVENTORY_COLUMNS = 5;
    private static final int INVENTORY_ROWS = 4;
    private static final int TOTAL_INVENTORY_SLOTS = INVENTORY_COLUMNS * INVENTORY_ROWS;

    private final List<Item> inventorySlots;
    private final Equipment equipment;
    private boolean isVisible = false;
    private int selectedRow = 0;
    private int selectedColumn = 0;
    private boolean inInventoryMode = true; // true = inventário, false = equipamento
    private String playerClassName;

  public InventoryManager(String playerClassName) {
        this.inventorySlots = new ArrayList<>();
        this.equipment = new Equipment();
        this.playerClassName = playerClassName;

    // Inicializar slots vazios
        for (int i = 0; i < TOTAL_INVENTORY_SLOTS; i++) {
            inventorySlots.add(null);
        }
    }

    /**
     * Adiciona um item ao inventário.
     * @param item Item a ser adicionado.
     * @return true se o item foi adicionado com sucesso.
     */
    public boolean addItem(Item item) {
        if (item == null) return false;

        // Tentar empilhar com item existente se possível
        if (item.isStackable()) {
            for (int i = 0; i < TOTAL_INVENTORY_SLOTS; i++) {
                Item existingItem = inventorySlots.get(i);
                if (existingItem != null && item.canStackWith(existingItem)) {
                    // Empilhar diretamente no item existente
                    int stacked = existingItem.stackItem(item);

                    if (stacked > 0) {
                        // Se o item foi completamente empilhado, retornar true
                        if (item.getStackSize() == 0) {
                            return true;
                        }
                        // Se ainda há itens para empilhar, continuar com o próximo slot
                    }
                }
            }
        }

        // Se ainda há itens para adicionar (não foram todos empilhados)
        if (item.getStackSize() > 0) {
            // Procurar slot vazio
            for (int i = 0; i < TOTAL_INVENTORY_SLOTS; i++) {
                if (inventorySlots.get(i) == null) {
                    inventorySlots.set(i, item);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Remove um item do inventário.
     * @param slotIndex Índice do slot.
     * @return Item removido ou null se vazio.
     */
    public Item removeItem(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= TOTAL_INVENTORY_SLOTS) return null;

        Item item = inventorySlots.get(slotIndex);
        inventorySlots.set(slotIndex, null);
        return item;
    }

    /**
     * Obtém um item do inventário.
     * @param slotIndex Índice do slot.
     * @return Item no slot ou null se vazio.
     */
    public Item getItem(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= TOTAL_INVENTORY_SLOTS) return null;
        return inventorySlots.get(slotIndex);
    }

    /**
     * Obtém o item no slot selecionado.
     * @return Item selecionado ou null se vazio.
     */
    public Item getSelectedItem() {
        if (inInventoryMode) {
            int slotIndex = selectedRow * INVENTORY_COLUMNS + selectedColumn;
            return getItem(slotIndex);
        } else {
            return equipment.getSelectedItem();
        }
    }

    /**
     * Verifica se o jogador pode equipar um item.
     * @param item Item a ser verificado.
     * @return true se o jogador pode equipar o item.
     */
    public boolean canPlayerEquipItem(Item item) {
      if (item == null || !item.isEquipable()) {
        return false;
      }

      // Verificar se o jogador tem a classe necessária
      String playerClass = playerClassName;
      return item.canBeEquippedBy(playerClass);
    }

    /**
     * Equipa o item selecionado.
     * @return true se o item foi equipado com sucesso.
     */
    public boolean equipSelectedItem() {
        if (!inInventoryMode) return false;

        Item selectedItem = getSelectedItem();
        if (selectedItem == null || !selectedItem.isEquipable()) {
            return false;
        }

        // NOVA VERIFICAÇÃO: Verificar se o jogador pode equipar o item
        if (!canPlayerEquipItem(selectedItem)) {
          String playerClass = playerClassName;
          String allowedClasses = selectedItem.getAllowedClass() != null ? String.join(", ", selectedItem.getAllowedClass()) : "nenhuma";
          System.out.printf("Classe %s não pode equipar o item da classe %s", playerClass,allowedClasses );
          return false;
        }

        // Determinar slot de equipamento baseado no tipo
        Equipment.EquipmentSlot slot = getEquipmentSlotForItem(selectedItem);
        if (slot == null) return false;

        // Tentar equipar
        Item previousItem = equipment.equipItem(slot, selectedItem);

        // Se havia item equipado, adicionar de volta ao inventário
        if (previousItem != null) {
            addItem(previousItem);
        }

        // Remover item do inventário
        int slotIndex = selectedRow * INVENTORY_COLUMNS + selectedColumn;
        removeItem(slotIndex);

        return true;
    }

    /**
     * Desequipa o item selecionado do equipamento.
     * @return true se o item foi desequipado com sucesso.
     */
    public boolean unequipSelectedItem() {
        if (inInventoryMode) return false;

        Item selectedItem = equipment.getSelectedItem();
        if (selectedItem == null) return false;

        // Tentar adicionar ao inventário
        if (addItem(selectedItem)) {
            equipment.unequipItem(equipment.getSelectedEquipmentSlot());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determina o slot de equipamento apropriado para um item.
     */
    private Equipment.EquipmentSlot getEquipmentSlotForItem(Item item) {
        ItemType itemType = item.getItemType();

        return switch (itemType) {
            case WEAPON -> Equipment.EquipmentSlot.WEAPON;
            case ARMOR -> Equipment.EquipmentSlot.ARMOR;
            case LEFTHAND -> Equipment.EquipmentSlot.LEFTHAND;
            case BOOT -> Equipment.EquipmentSlot.BOOT;
            default -> null;
        };
    }

    /**
     * Navega para cima.
     */
    public void moveUp() {
        if (inInventoryMode) {
            if (selectedRow > 0) {
                selectedRow--;
            }
        } else {
            equipment.moveUp();
        }
    }

    /**
     * Navega para baixo.
     */
    public void moveDown() {
        if (inInventoryMode) {
            if (selectedRow < INVENTORY_ROWS - 1) {
                selectedRow++;
            }
        } else {
            equipment.moveDown();
        }
    }

    /**
     * Navega para a esquerda.
     */
    public void moveLeft() {
        if (inInventoryMode) {
            if (selectedColumn > 0) {
                selectedColumn--;
            }
        }
    }

    /**
     * Navega para a direita.
     */
    public void moveRight() {
        if (inInventoryMode) {
            if (selectedColumn < INVENTORY_COLUMNS - 1) {
                selectedColumn++;
            }
        }
    }

    /**
     * Alterna entre modo inventário e equipamento.
     */
    public void toggleMode() {
        inInventoryMode = !inInventoryMode;
    }

    /**
     * Alterna a visibilidade do inventário.
     */
    public void toggleVisibility() {
        isVisible = !isVisible;
        if (isVisible) {
            // Resetar seleção quando abrir
            selectedRow = 0;
            selectedColumn = 0;
            equipment.setSelectedSlot(0);
            inInventoryMode = true;
        }
    }

  /**
   * Verifica se um item está no inventário pelo ID.
   *
   * @param itemId ID único do item (ex: "healing_potion")
   * @return true se o item estiver no inventário, false caso contrário.
   */
  public boolean hasItemById(String itemId) {
    for (Item item : inventorySlots) {
      if (item != null && item.getItemId().equals(itemId)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Conta a quantidade total de um item específico no inventário.
   *
   * @param itemId ID único do item (ex: "healing_potion")
   * @return Quantidade total encontrada (0 se não existir).
   */
  public int countItemById(String itemId) {
    int total = 0;
    for (Item item : inventorySlots) {
      if (item != null && item.getItemId().equals(itemId)) {
        total += item.getStackSize();
      }
    }
    return total;
  }

  /**
   * Consome uma unidade de um item específico no inventário.
   *
   * @param itemId ID do item (ex: "healing_potion")
   * @return true se o item foi consumido com sucesso, false se não havia.
   */
  public boolean consumeItem(String itemId) {
    for (int i = 0; i < TOTAL_INVENTORY_SLOTS; i++) {
      Item item = inventorySlots.get(i);

      if (item != null && item.getItemId().equals(itemId)) {
        // Reduz o tamanho da pilha
        int newStack = item.getStackSize() - 1;

        if (newStack > 0) {
          item.setStackSize(newStack);
        } else {
          // Se chegou a 0, remove o item do slot
          inventorySlots.set(i, null);
        }
        return true;
      }
    }
    return false; // não encontrou o item
  }

    // Getters
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { this.isVisible = visible; }

    public boolean isInInventoryMode() { return inInventoryMode; }
    public void setInInventoryMode(boolean inInventoryMode) { this.inInventoryMode = inInventoryMode; }

    public int getSelectedRow() { return selectedRow; }
    public int getSelectedColumn() { return selectedColumn; }

    public int getInventoryColumns() { return INVENTORY_COLUMNS; }
    public int getInventoryRows() { return INVENTORY_ROWS; }
    public int getTotalInventorySlots() { return TOTAL_INVENTORY_SLOTS; }

    public List<Item> getInventorySlots() { return new ArrayList<>(inventorySlots); }
    public Equipment getEquipment() { return equipment; }

    /**
     * Verifica se o inventário está cheio.
     */
    public boolean isInventoryFull() {
        for (Item item : inventorySlots) {
            if (item == null) return false;
        }
        return true;
    }

    /**
     * Conta quantos slots do inventário estão ocupados.
     */
    public int getOccupiedInventorySlots() {
        int count = 0;
        for (Item item : inventorySlots) {
            if (item != null) count++;
        }
        return count;
    }

    /**
     * Limpa todo o inventário.
     */
    public void clearInventory() {
        for (int i = 0; i < TOTAL_INVENTORY_SLOTS; i++) {
            inventorySlots.set(i, null);
        }
    }

    /**
     * Limpa todo o equipamento.
     */
    public void clearEquipment() {
        for (int i = 0; i < equipment.getTotalSlots(); i++) {
            equipment.unequipItem(Equipment.EquipmentSlot.fromIndex(i));
        }
    }
}
