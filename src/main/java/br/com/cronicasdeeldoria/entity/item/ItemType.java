package br.com.cronicasdeeldoria.entity.item;

/**
 * Enum que define os tipos de itens disponíveis no jogo.
 */
public enum ItemType {
    WEAPON("Arma", "weapon"),
    ARMOR("Armadura", "armor"),
    LEFTHAND("Mão Esquerda", "lefthand"),
    BOOT("Bota", "boot"),
    KEY("Chave", "key"),
    CONSUMABLE("Consumível", "consumable");
    
    private final String displayName;
    private final String jsonValue;
    
    ItemType(String displayName, String jsonValue) {
        this.displayName = displayName;
        this.jsonValue = jsonValue;
    }
    
    /**
     * Retorna o nome de exibição do tipo de item.
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
     * Converte uma string para ItemType.
     * @param value Valor a ser convertido.
     * @return ItemType correspondente ou null se não encontrado.
     */
    public static ItemType fromString(String value) {
        if (value == null) return null;
        
        for (ItemType type : values()) {
            if (type.jsonValue.equalsIgnoreCase(value) || 
                type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * Verifica se o tipo de item é equipável.
     * @return true se o item pode ser equipado.
     */
    public boolean isEquipable() {
        return this == WEAPON || this == ARMOR || this == LEFTHAND || this == BOOT;
    }
}
