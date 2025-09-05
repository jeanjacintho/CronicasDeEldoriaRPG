package br.com.cronicasdeeldoria.game.inventory;

import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.entity.item.ItemType;

/**
 * Sistema de equipamento com 4 slots específicos.
 */
public class Equipment {
    public enum EquipmentSlot {
        WEAPON(0, "Arma", ItemType.WEAPON),
        LEFTHAND(1, "Mão Esquerda", ItemType.LEFTHAND),
        ARMOR(2, "Armadura", ItemType.ARMOR),
        BOOT(3, "Bota", ItemType.BOOT);
        
        private final int index;
        private final String displayName;
        private final ItemType requiredType;
        
        EquipmentSlot(int index, String displayName, ItemType requiredType) {
            this.index = index;
            this.displayName = displayName;
            this.requiredType = requiredType;
        }
        
        public int getIndex() { return index; }
        public String getDisplayName() { return displayName; }
        public ItemType getRequiredType() { return requiredType; }
        
        public static EquipmentSlot fromIndex(int index) {
            for (EquipmentSlot slot : values()) {
                if (slot.index == index) {
                    return slot;
                }
            }
            return null;
        }
    }
    
    private static final int TOTAL_SLOTS = 4;
    private final Item[] equippedItems;
    private int selectedSlot = 0;
    
    public Equipment() {
        this.equippedItems = new Item[TOTAL_SLOTS];
    }
    
    /**
     * Equipa um item no slot apropriado.
     * @param slot Slot de equipamento.
     * @param item Item a ser equipado.
     * @return Item que estava equipado anteriormente (se houver).
     */
    public Item equipItem(EquipmentSlot slot, Item item) {
        if (slot == null || item == null) return null;
        
        // Verificar se o item é compatível com o slot
        // Anéis podem ser equipados no slot LEFTHAND
        boolean isCompatible = item.getItemType().equals(slot.getRequiredType()) ||
                              (slot == EquipmentSlot.LEFTHAND);
        
        if (!isCompatible) {
            return null;
        }
        
        // Verificar se o item é equipável
        if (!item.isEquipable()) {
            return null;
        }
        
        Item previousItem = equippedItems[slot.getIndex()];
        equippedItems[slot.getIndex()] = item;
        
        return previousItem;
    }
    
    /**
     * Remove um item do slot de equipamento.
     * @param slot Slot de equipamento.
     * @return Item removido.
     */
    public Item unequipItem(EquipmentSlot slot) {
        if (slot == null) return null;
        
        Item item = equippedItems[slot.getIndex()];
        equippedItems[slot.getIndex()] = null;
        return item;
    }
    
    /**
     * Obtém o item equipado em um slot específico.
     * @param slot Slot de equipamento.
     * @return Item equipado ou null se vazio.
     */
    public Item getEquippedItem(EquipmentSlot slot) {
        if (slot == null) return null;
        return equippedItems[slot.getIndex()];
    }
    
    /**
     * Obtém o item equipado por índice.
     * @param index Índice do slot (0-3).
     * @return Item equipado ou null se vazio.
     */
    public Item getEquippedItem(int index) {
        if (index < 0 || index >= TOTAL_SLOTS) return null;
        return equippedItems[index];
    }
    
    /**
     * Move o cursor para cima.
     */
    public void moveUp() {
        if (selectedSlot > 0) {
            selectedSlot--;
        }
    }
    
    /**
     * Move o cursor para baixo.
     */
    public void moveDown() {
        if (selectedSlot < TOTAL_SLOTS - 1) {
            selectedSlot++;
        }
    }
    
    /**
     * Obtém o slot selecionado.
     * @return Índice do slot selecionado.
     */
    public int getSelectedSlot() {
        return selectedSlot;
    }
    
    /**
     * Define o slot selecionado.
     * @param slot Índice do slot (0-3).
     */
    public void setSelectedSlot(int slot) {
        if (slot >= 0 && slot < TOTAL_SLOTS) {
            selectedSlot = slot;
        }
    }
    
    /**
     * Obtém o item no slot selecionado.
     * @return Item no slot selecionado.
     */
    public Item getSelectedItem() {
        return getEquippedItem(selectedSlot);
    }
    
    /**
     * Obtém o slot de equipamento selecionado.
     * @return EquipmentSlot selecionado.
     */
    public EquipmentSlot getSelectedEquipmentSlot() {
        return EquipmentSlot.fromIndex(selectedSlot);
    }
    
    /**
     * Obtém o número total de slots.
     * @return Total de slots de equipamento.
     */
    public int getTotalSlots() {
        return TOTAL_SLOTS;
    }
    
    /**
     * Verifica se um slot está vazio.
     * @param slot Slot de equipamento.
     * @return true se o slot está vazio.
     */
    public boolean isSlotEmpty(EquipmentSlot slot) {
        return getEquippedItem(slot) == null;
    }
    
    /**
     * Verifica se um slot está vazio por índice.
     * @param index Índice do slot.
     * @return true se o slot está vazio.
     */
    public boolean isSlotEmpty(int index) {
        return getEquippedItem(index) == null;
    }
    
    /**
     * Conta quantos slots estão ocupados.
     * @return Número de slots ocupados.
     */
    public int getOccupiedSlots() {
        int count = 0;
        for (Item item : equippedItems) {
            if (item != null) count++;
        }
        return count;
    }
    
    /**
     * Obtém todos os itens equipados.
     * @return Array com todos os itens equipados.
     */
    public Item[] getAllEquippedItems() {
        return equippedItems.clone();
    }
    
    /**
     * Verifica se há algum item equipado.
     * @return true se pelo menos um item está equipado.
     */
    public boolean hasAnyItemEquipped() {
        return getOccupiedSlots() > 0;
    }
}

