package br.com.cronicasdeeldoria.entity.character.npc;

import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.inventory.ItemFactory;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * NPC comerciante que vende itens para o jogador.
 */
public class MerchantNpc extends Npc {
    private List<MerchantItem> merchantItems;
    private List<ItemConfig> itemConfigs;
    private String merchantName;
    private static Map<String, Integer> itemPricesCache = new HashMap<>();
    
    /**
     * Classe interna para configuração de itens com probabilidade.
     */
    public static class ItemConfig {
        private String itemId;
        private int minQuantity;
        private int maxQuantity;
        private double probability;
        
        public ItemConfig(String itemId, int minQuantity, int maxQuantity, double probability) {
            this.itemId = itemId;
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
            this.probability = Math.max(0.0, Math.min(1.0, probability));
        }
        
        public boolean shouldInclude() {
            return Math.random() < probability;
        }
        
        public int generateQuantity() {
            if (minQuantity == maxQuantity) {
                return minQuantity;
            }
            return minQuantity + (int)(Math.random() * (maxQuantity - minQuantity + 1));
        }
        
        // Getters
        public String getItemId() { return itemId; }
        public int getMinQuantity() { return minQuantity; }
        public int getMaxQuantity() { return maxQuantity; }
        public double getProbability() { return probability; }
    }
    
    /**
     * Classe interna para representar um item do comerciante com preço.
     */
    public static class MerchantItem {
        private String itemId;
        private int price;
        private int stock;
        
        public MerchantItem(String itemId, int price, int stock) {
            this.itemId = itemId;
            this.price = price;
            this.stock = stock;
        }
        
        public String getItemId() { return itemId; }
        public int getPrice() { return price; }
        public int getStock() { return stock; }
        
        public void reduceStock() {
            if (stock > 0) {
                stock--;
            }
        }
        
        public void addStock(int amount) {
            stock += amount;
        }
        
        public boolean isAvailable() {
            return stock > 0;
        }
    }
    
    public MerchantNpc(GamePanel gamePanel, String name, int worldX, int worldY, int speed, String direction) {
        super(name, true, "Bem-vindo à minha loja!", worldX, worldY, "guardiao", 48, true, false);
        this.merchantName = name;
        this.itemConfigs = new ArrayList<>();
        this.merchantItems = new ArrayList<>();
        loadItemConfigsFromFile();
        initializeMerchantItemsFromConfigs();
    }
    
    /**
     * Construtor para comerciante com sistema de probabilidade.
     * @param gamePanel Painel do jogo.
     * @param name Nome do comerciante.
     * @param worldX Posição X no mundo.
     * @param worldY Posição Y no mundo.
     * @param speed Velocidade do NPC.
     * @param direction Direção inicial.
     * @param itemProbabilities Lista de itens com probabilidades.
     */
    public MerchantNpc(GamePanel gamePanel, String name, int worldX, int worldY, int speed, String direction, List<ItemConfig> itemConfigs) {
        super(name, true, "Bem-vindo à minha loja!", worldX, worldY, "guardiao", 48, true, false);
        this.merchantName = name;
        this.itemConfigs = new ArrayList<>(itemConfigs);
        this.merchantItems = new ArrayList<>();
        initializeMerchantItemsFromConfigs();
    }

    /**
     * Permite ajustar a skin do comerciante após a construção (usado pela NpcFactory).
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }
    
    /**
     * Carrega configurações de itens do arquivo npcs.json.
     */
    private void loadItemConfigsFromFile() {
        try {
            // Usar o método estático do NpcFactory para carregar configurações
            List<ItemConfig> loadedConfigs = NpcFactory.loadMerchantItemConfigs();
            itemConfigs.addAll(loadedConfigs);
        } catch (Exception e) {
            System.err.println("Erro ao carregar configurações de comerciante: " + e.getMessage());
        }
    }
    
    /**
     * Inicializa os itens baseado nas configurações definidas.
     */
    private void initializeMerchantItemsFromConfigs() {
        for (ItemConfig itemConfig : itemConfigs) {
            if (itemConfig.shouldInclude()) {
                int quantity = itemConfig.generateQuantity();
                // Buscar o preço do item no objects.json
                int price = getItemPriceFromConfig(itemConfig.getItemId());
                merchantItems.add(new MerchantItem(itemConfig.getItemId(), price, quantity));
            }
        }
        
        // Se nenhum item foi selecionado, adicionar pelo menos alguns itens básicos
        if (merchantItems.isEmpty()) {
            merchantItems.add(new MerchantItem("health_potion", getItemPriceFromConfig("health_potion"), 3));
            merchantItems.add(new MerchantItem("mana_potion", getItemPriceFromConfig("mana_potion"), 3));
        }
    }
    
    /**
     * Busca o preço de um item no arquivo objects.json com cache.
     * @param itemId ID do item.
     * @return Preço do item ou 25 como fallback.
     */
    private int getItemPriceFromConfig(String itemId) {
        // Verificar cache primeiro
        if (itemPricesCache.containsKey(itemId)) {
            return itemPricesCache.get(itemId);
        }
        
        try {
            java.io.InputStream is = MerchantNpc.class.getResourceAsStream("/objects.json");
            if (is != null) {
                Gson gson = new Gson();
                JsonArray objectsArray = gson.fromJson(new InputStreamReader(is), JsonArray.class);
                
                for (int i = 0; i < objectsArray.size(); i++) {
                    JsonObject objectJson = objectsArray.get(i).getAsJsonObject();
                    if (objectJson.has("id") && objectJson.get("id").getAsString().equals(itemId)) {
                        if (objectJson.has("value")) {
                            int price = objectJson.get("value").getAsInt();
                            itemPricesCache.put(itemId, price); // Cache o resultado
                            return price;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar preço do item " + itemId + ": " + e.getMessage());
        }
        
        int fallbackPrice = 25;
        itemPricesCache.put(itemId, fallbackPrice); // Cache o fallback também
        return fallbackPrice;
    }
    
    /**
     * Retorna a lista de itens do comerciante.
     * @return Lista de MerchantItem.
     */
    public List<MerchantItem> getMerchantItems() {
        return merchantItems;
    }
    
    /**
     * Retorna um item específico do comerciante.
     * @param index Índice do item.
     * @return MerchantItem ou null se índice inválido.
     */
    public MerchantItem getMerchantItem(int index) {
        if (index >= 0 && index < merchantItems.size()) {
            return merchantItems.get(index);
        }
        return null;
    }
    
    /**
     * Retorna o nome do comerciante.
     * @return Nome do comerciante.
     */
    public String getMerchantName() {
        return merchantName;
    }
    
    /**
     * Retorna a lista de configurações de itens.
     * @return Lista de ItemConfig.
     */
    public List<ItemConfig> getItemConfigs() {
        return itemConfigs;
    }
    
    /**
     * Adiciona uma configuração de item.
     * @param itemConfig Configuração de item.
     */
    public void addItemConfig(ItemConfig itemConfig) {
        itemConfigs.add(itemConfig);
    }
    
    /**
     * Reinicializa os itens baseado nas configurações atuais.
     */
    public void refreshItems() {
        merchantItems.clear();
        initializeMerchantItemsFromConfigs();
    }
    
    /**
     * Verifica se um item está disponível para compra.
     * @param index Índice do item.
     * @return true se o item está disponível.
     */
    public boolean isItemAvailable(int index) {
        MerchantItem item = getMerchantItem(index);
        return item != null && item.isAvailable();
    }
    
    /**
     * Processa a compra de um item.
     * @param index Índice do item a ser comprado.
     * @return Item comprado ou null se a compra falhou.
     */
    public Item purchaseItem(int index) {
        MerchantItem merchantItem = getMerchantItem(index);
        if (merchantItem == null || !merchantItem.isAvailable()) {
            return null;
        }
        
        // Criar o item usando o ItemFactory (apenas para criar o objeto Item)
        Item item = ItemFactory.createItem(merchantItem.getItemId());
        if (item != null) {
            // O preço já está correto no MerchantItem (vem do objects.json)
            merchantItem.reduceStock();
            return item;
        }
        
        return null;
    }
    
    /**
     * Retorna o preço de um item.
     * @param index Índice do item.
     * @return Preço do item ou -1 se índice inválido.
     */
    public int getItemPrice(int index) {
        MerchantItem item = getMerchantItem(index);
        return item != null ? item.getPrice() : -1;
    }
    
    /**
     * Retorna o número total de itens disponíveis.
     * @return Número de itens.
     */
    public int getItemCount() {
        return merchantItems.size();
    }
    
    @Override
    public void interact() {
        // A interação será gerenciada pelo GamePanel
        System.out.println("Comerciante " + merchantName + " interagiu!");
    }
    
    @Override
    public boolean isInteractive() {
        return true;
    }
    
    @Override
    public boolean isAutoInteraction() {
        return false; // Requer interação manual
    }
}
