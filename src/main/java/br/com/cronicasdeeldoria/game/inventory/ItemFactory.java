package br.com.cronicasdeeldoria.game.inventory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.entity.item.ItemType;
import br.com.cronicasdeeldoria.entity.item.MagicOrb;
import br.com.cronicasdeeldoria.entity.item.QuestItem;
import br.com.cronicasdeeldoria.entity.item.ItemRarity;
import br.com.cronicasdeeldoria.entity.object.MapObject;
import br.com.cronicasdeeldoria.entity.object.ObjectSpriteLoader;

/**
 * Factory para criar itens a partir de objetos do mapa.
 */
public class ItemFactory {
    
    /**
     * Cria um item a partir do ID usando configurações do objects.json.
     * Reutiliza a lógica existente do createItemFromMapObject.
     * @param itemId ID do item.
     * @return Item criado ou null se não encontrado.
     */
    public static Item createItem(String itemId) {
        if (itemId == null) return null;
        
        // Buscar dados do item no objects.json
        try {
            InputStream is = ItemFactory.class.getResourceAsStream("/objects.json");
            if (is != null) {
                Gson gson = new Gson();
                JsonArray objectsArray = gson.fromJson(new InputStreamReader(is), JsonArray.class);
                
                for (int i = 0; i < objectsArray.size(); i++) {
                    JsonObject objectJson = objectsArray.get(i).getAsJsonObject();
                    if (objectJson.has("id") && objectJson.get("id").getAsString().equals(itemId)) {
                        // Criar MapObject temporário para reutilizar createItemFromMapObject
                        MapObject tempMapObject = createTempMapObjectFromJson(objectJson);
                        if (tempMapObject != null) {
                            return createItemFromMapObject(tempMapObject, 48);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar item " + itemId + " do objects.json: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Cria um MapObject temporário a partir de dados JSON para reutilizar createItemFromMapObject.
     * @param objectJson Objeto JSON do objects.json.
     * @return MapObject temporário ou null se dados inválidos.
     */
    private static MapObject createTempMapObjectFromJson(JsonObject objectJson) {
        try {
            String id = objectJson.get("id").getAsString();
            String name = objectJson.get("name").getAsString();
            
            // Criar MapObject temporário (posição 0,0 pois é para inventário)
            MapObject mapObject = new MapObject(id, name, 0, 0, 1, 1, false, true, false, null, 48);
            
            // Criar ObjectDefinition com dados do JSON usando reflexão
            ObjectSpriteLoader.ObjectDefinition def = new ObjectSpriteLoader.ObjectDefinition();
            
            if (objectJson.has("itemType")) {
                Field itemTypeField = def.getClass().getDeclaredField("itemType");
                itemTypeField.setAccessible(true);
                itemTypeField.set(def, objectJson.get("itemType").getAsString());
            }
            
            if (objectJson.has("rarity")) {
                Field rarityField = def.getClass().getDeclaredField("rarity");
                rarityField.setAccessible(true);
                rarityField.set(def, objectJson.get("rarity").getAsString());
            }
            
            if (objectJson.has("description")) {
                Field descriptionField = def.getClass().getDeclaredField("description");
                descriptionField.setAccessible(true);
                descriptionField.set(def, objectJson.get("description").getAsString());
            }
            
            if (objectJson.has("value")) {
                Field valueField = def.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                valueField.set(def, objectJson.get("value").getAsInt());
            }
            
            mapObject.setObjectDefinition(def);
            return mapObject;
            
        } catch (Exception e) {
            System.err.println("Erro ao criar MapObject temporário do JSON: " + e.getMessage());
            return null;
        }
    }
    
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
        
        // Verificar se é um item de quest
        String questItemStr = getObjectProperty(mapObject, "questItem");
        boolean isQuestItem = "true".equalsIgnoreCase(questItemStr);
        
        Item item;
        
        if (isQuestItem && itemType == ItemType.QUEST_ITEM) {
            // Verificar se é uma orbe mágica específica
            String orbType = getObjectProperty(mapObject, "orbType");
            
            if (orbType != null) {
                // Criar MagicOrb
                item = new MagicOrb(
                    orbType,
                    mapObject.getWorldX(),
                    mapObject.getWorldY()
                );
            } else {
                // Criar QuestItem genérico
                item = new QuestItem(
                    mapObject.getObjectId(),
                    mapObject.getName(),
                    mapObject.getWorldX(),
                    mapObject.getWorldY(),
                    itemType,
                    rarity,
                    description,
                    value,
                    stackable,
                    "main_orb_quest"
                );
            }
        } else {
            // Criar Item comum
            item = new Item(
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
        }
        
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
