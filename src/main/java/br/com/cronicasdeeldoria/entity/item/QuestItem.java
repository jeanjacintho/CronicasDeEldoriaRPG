package br.com.cronicasdeeldoria.entity.item;

import br.com.cronicasdeeldoria.game.quest.QuestManager;

/**
 * Item especial usado em quests.
 * Herda de Item e adiciona funcionalidades específicas para quests.
 */
public class QuestItem extends Item {
    private boolean isQuestItem;
    private String questId;
    
    /**
     * Construtor para criar um item de quest.
     * @param itemId ID único do item
     * @param name Nome do item
     * @param worldX Posição X no mundo
     * @param worldY Posição Y no mundo
     * @param itemType Tipo do item
     * @param rarity Raridade do item
     * @param description Descrição do item
     * @param value Valor em moedas
     * @param stackable Se o item pode ser empilhado
     * @param questId ID da quest relacionada
     */
    public QuestItem(String itemId, String name, int worldX, int worldY, 
                     ItemType itemType, ItemRarity rarity, String description, 
                     int value, boolean stackable, String questId) {
        super(itemId, name, worldX, worldY, itemType, rarity, description, 
              value, stackable, 1, null, 0);
        this.isQuestItem = true;
        this.questId = questId;
    }
    
    /**
     * Construtor para criar um item de quest sem posição no mundo (inventário).
     * @param itemId ID único do item
     * @param name Nome do item
     * @param itemType Tipo do item
     * @param rarity Raridade do item
     * @param description Descrição do item
     * @param value Valor em moedas
     * @param stackable Se o item pode ser empilhado
     * @param questId ID da quest relacionada
     */
    public QuestItem(String itemId, String name, ItemType itemType, ItemRarity rarity, 
                     String description, int value, boolean stackable, String questId) {
        super(itemId, name, itemType, rarity, description, value, stackable);
        this.isQuestItem = true;
        this.questId = questId;
    }
    
    /**
     * Chamado quando o item é coletado.
     * Notifica o QuestManager sobre a coleta.
     */
    public void onCollect() {
        if (questId != null && QuestManager.getInstance() != null) {
            QuestManager.getInstance().onQuestItemCollected(this);
        }
    }
    
    // Getters
    public boolean isQuestItem() { 
        return isQuestItem; 
    }
    
    public String getQuestId() { 
        return questId; 
    }
    
    public void setQuestId(String questId) {
        this.questId = questId;
    }
}
