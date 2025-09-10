package br.com.cronicasdeeldoria.entity.item;

import br.com.cronicasdeeldoria.game.quest.QuestManager;

/**
 * Orbe mágica específica da quest principal.
 * Representa uma das 4 orbes que devem ser coletadas e depositadas no Totem Central.
 */
public class MagicOrb extends QuestItem {
    private String orbType; // "fire", "water", "earth", "air"
    private int orbPower;
    private boolean isDeposited;
    
    /**
     * Construtor para criar uma orbe mágica.
     * @param orbType Tipo da orbe (fire, water, earth, air)
     * @param worldX Posição X no mundo
     * @param worldY Posição Y no mundo
     */
    public MagicOrb(String orbType, int worldX, int worldY) {
        super("orb_" + orbType, "Orbe de " + getOrbName(orbType), 
              worldX, worldY, ItemType.QUEST_ITEM, ItemRarity.LEGENDARY,
              "Uma orbe mágica de " + getOrbName(orbType) + 
              " roubada pelo Mago Supremo.", 0, false, "main_orb_quest");
        
        this.orbType = orbType;
        this.orbPower = calculateOrbPower(orbType);
        this.isDeposited = false;
    }
    
    /**
     * Construtor para criar uma orbe mágica sem posição no mundo.
     * @param orbType Tipo da orbe (fire, water, earth, air)
     */
    public MagicOrb(String orbType) {
        super("orb_" + orbType, "Orbe de " + getOrbName(orbType), 
              ItemType.QUEST_ITEM, ItemRarity.LEGENDARY,
              "Uma orbe mágica de " + getOrbName(orbType) + 
              " roubada pelo Mago Supremo.", 0, false, "main_orb_quest");
        
        this.orbType = orbType;
        this.orbPower = calculateOrbPower(orbType);
        this.isDeposited = false;
    }
    
    /**
     * Deposita a orbe no Totem Central.
     * Notifica o QuestManager sobre o depósito.
     */
    public void depositInTotem() {
        if (!isDeposited) {
            this.isDeposited = true;
            if (QuestManager.getInstance() != null) {
                QuestManager.getInstance().onOrbDeposited(this);
            }
        }
    }
    
    /**
     * Obtém o nome da orbe baseado no tipo.
     * @param type Tipo da orbe
     * @return Nome em português
     */
    private static String getOrbName(String type) {
        switch (type.toLowerCase()) {
            case "fire": return "Fogo";
            case "water": return "Água";
            case "earth": return "Terra";
            case "air": return "Ar";
            default: return "Desconhecido";
        }
    }
    
    /**
     * Calcula o poder da orbe baseado no tipo.
     * @param type Tipo da orbe
     * @return Poder da orbe
     */
    private int calculateOrbPower(String type) {
        switch (type.toLowerCase()) {
            case "fire": return 25;
            case "water": return 20;
            case "earth": return 30;
            case "air": return 15;
            default: return 10;
        }
    }
    
    /**
     * Verifica se a orbe foi depositada no totem.
     * @return true se foi depositada
     */
    public boolean isDeposited() {
        return isDeposited;
    }
    
    /**
     * Obtém o tipo da orbe.
     * @return Tipo da orbe
     */
    public String getOrbType() {
        return orbType;
    }
    
    /**
     * Obtém o poder da orbe.
     * @return Poder da orbe
     */
    public int getOrbPower() {
        return orbPower;
    }
    
    /**
     * Obtém o nome da orbe em português.
     * @return Nome da orbe
     */
    public String getOrbDisplayName() {
        return getOrbName(orbType);
    }
    
    /**
     * Verifica se a orbe é de um tipo específico.
     * @param type Tipo a verificar
     * @return true se for do tipo especificado
     */
    public boolean isOrbType(String type) {
        return orbType != null && orbType.equalsIgnoreCase(type);
    }
}

