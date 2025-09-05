package br.com.cronicasdeeldoria.entity.item;

import java.awt.Color;

/**
 * Enum que define as raridades dos itens e suas cores correspondentes.
 */
public enum ItemRarity {
    COMMON("Comum", "common", Color.GRAY),
    UNCOMMON("Incomum", "uncommon", Color.GREEN),
    RARE("Raro", "rare", Color.BLUE),
    EPIC("Épico", "epic", Color.MAGENTA),
    LEGENDARY("Lendário", "legendary", Color.ORANGE);
    
    private final String displayName;
    private final String jsonValue;
    private final Color color;
    
    ItemRarity(String displayName, String jsonValue, Color color) {
        this.displayName = displayName;
        this.jsonValue = jsonValue;
        this.color = color;
    }
    
    /**
     * Retorna o nome de exibição da raridade.
     * @return Nome em português para exibição.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Retorna o valor usado no JSON.
     * @return Valor em inglês para arquivos JSON.
     */
    public String getJsonValue() {
        return jsonValue;
    }
    
    /**
     * Retorna a cor associada à raridade.
     * @return Cor para exibição do item.
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Converte uma string para ItemRarity.
     * @param value Valor a ser convertido.
     * @return ItemRarity correspondente ou COMMON se não encontrado.
     */
    public static ItemRarity fromString(String value) {
        if (value == null) return COMMON;
        
        for (ItemRarity rarity : values()) {
            if (rarity.jsonValue.equalsIgnoreCase(value) || 
                rarity.displayName.equalsIgnoreCase(value)) {
                return rarity;
            }
        }
        return COMMON;
    }
}
