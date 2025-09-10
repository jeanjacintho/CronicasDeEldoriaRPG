package br.com.cronicasdeeldoria.game.quest;

/**
 * Representa as recompensas que uma quest pode dar ao jogador.
 */
public class QuestReward {
    private int money;
    private int experience;
    private String[] itemIds;
    private String message;
    
    /**
     * Construtor para criar recompensas de quest.
     * @param money Quantidade de moedas
     * @param experience Quantidade de experiência
     * @param itemIds IDs dos itens como recompensa
     * @param message Mensagem de recompensa
     */
    public QuestReward(int money, int experience, String[] itemIds, String message) {
        this.money = money;
        this.experience = experience;
        this.itemIds = itemIds != null ? itemIds.clone() : new String[0];
        this.message = message;
    }
    
    /**
     * Construtor simplificado apenas com moedas e experiência.
     * @param money Quantidade de moedas
     * @param experience Quantidade de experiência
     */
    public QuestReward(int money, int experience) {
        this(money, experience, null, "Quest completada!");
    }
    
    // Getters
    public int getMoney() { 
        return money; 
    }
    
    public int getExperience() { 
        return experience; 
    }
    
    public String[] getItemIds() { 
        return itemIds.clone(); 
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}

