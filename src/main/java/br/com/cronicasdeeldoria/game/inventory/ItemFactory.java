package br.com.cronicasdeeldoria.game.inventory;

import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.entity.item.ItemType;
import br.com.cronicasdeeldoria.entity.item.ItemRarity;
import br.com.cronicasdeeldoria.entity.object.MapObject;
import br.com.cronicasdeeldoria.entity.object.ObjectSpriteLoader;

/**
 * Factory para criar itens a partir de objetos do mapa.
 */
public class ItemFactory {
    
    /**
     * Cria um item a partir de um MapObject.
     * @param mapObject Objeto do mapa.
     * @param tileSize Tamanho do tile.
     * @return Item criado ou null se não for um item válido.
     */
    public static Item createItemFromMapObject(MapObject mapObject, int tileSize) {
        if (mapObject == null) return null;
        
        // Verificar se o objeto tem propriedades de item
        String itemTypeStr = getObjectProperty(mapObject, "itemType");
        if (itemTypeStr == null) return null;
        
        ItemType itemType = ItemType.fromString(itemTypeStr);
        if (itemType == null) return null;
        
        String rarityStr = getObjectProperty(mapObject, "rarity");
        ItemRarity rarity = ItemRarity.fromString(rarityStr);
        
        String description = getObjectProperty(mapObject, "description");
        if (description == null) description = "";
        
        String valueStr = getObjectProperty(mapObject, "value");
        int value = 0;
        try {
            value = Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            // Valor padrão
        }
        
        // Determinar se o item é empilhável baseado no tipo
        boolean stackable = itemType == ItemType.KEY || itemType == ItemType.CONSUMABLE;
        int maxStackSize = stackable ? 99 : 1;
        
        // Criar o item
        Item item = new Item(
            mapObject.getObjectId(),
            mapObject.getName(),
            mapObject.getWorldX(),
            mapObject.getWorldY(),
            itemType,
            rarity,
            description,
            value,
            stackable,
            maxStackSize,
            mapObject.getObjectDefinition(),
            tileSize
        );
        
        return item;
    }
    
    /**
     * Verifica se um MapObject pode ser convertido em item.
     * @param mapObject Objeto do mapa.
     * @return true se pode ser convertido em item.
     */
    public static boolean canCreateItem(MapObject mapObject) {
        if (mapObject == null) return false;
        
        String itemTypeStr = getObjectProperty(mapObject, "itemType");
        boolean canCreate = itemTypeStr != null && ItemType.fromString(itemTypeStr) != null;
        
        return canCreate;
    }
    
    /**
     * Obtém uma propriedade de um objeto do ObjectDefinition.
     * Usa reflexão para acessar campos adicionais do JSON.
     */
    private static String getObjectProperty(MapObject mapObject, String property) {
        try {
            ObjectSpriteLoader.ObjectDefinition def = mapObject.getObjectDefinition();
            if (def == null) return null;
            
            // Usar reflexão para acessar campos adicionais do JSON
            java.lang.reflect.Field field = def.getClass().getDeclaredField(property);
            field.setAccessible(true);
            Object value = field.get(def);
            
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            // Se o campo não existir, retornar null
            return null;
        }
    }
}
