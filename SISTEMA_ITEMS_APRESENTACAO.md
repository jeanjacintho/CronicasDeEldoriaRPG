# 🎒 Sistema de Items - Apresentação Didática

## 📖 **O que é este sistema?**

Imagine que você está criando um **inventário completo** onde o jogador pode coletar, armazenar e equipar diferentes tipos de itens. O sistema de Items é exatamente isso! Ele permite criar itens com diferentes tipos, raridades, bônus e funcionalidades, além de gerenciar um inventário organizado e sistema de equipamento.

---

## 🎮 **Conceitos Básicos**

### **Item - Objeto do Jogo**
- **O que é?** Qualquer objeto que pode ser coletado e usado
- **Como funciona?** Tem tipo, raridade, descrição e propriedades
- **Exemplo:** Espada, armadura, poção, chave, orbe mágica

### **Inventário - Armazenamento**
- **O que é?** Sistema que guarda os itens coletados
- **Como funciona?** Grid de slots organizados (5x4 = 20 slots)
- **Exemplo:** Jogador coleta espada → vai para slot vazio do inventário

### **Equipamento - Uso de Itens**
- **O que é?** Sistema que aplica bônus dos itens equipados
- **Como funciona?** 4 slots específicos (arma, mão esquerda, armadura, bota)
- **Exemplo:** Equipar espada → +10 força, equipar armadura → +5 defesa

---

## 🏗️ **Como o Sistema Funciona**

### **1. Criação de Items**
```java
// Criar um item simples (para inventário)
Item espada = new Item(
    "sword_iron",                    // ID único
    "Espada de Ferro",              // Nome
    ItemType.WEAPON,                // Tipo
    ItemRarity.COMMON,              // Raridade
    "Uma espada básica de ferro",   // Descrição
    100,                           // Valor em moedas
    false                          // Não empilhável
);

// Criar um item com bônus de equipamento
Item armadura = new Item(
    "armor_leather",               // ID único
    "Armadura de Couro",           // Nome
    0, 0,                          // Posição no mundo (0,0 = inventário)
    ItemType.ARMOR,                // Tipo
    ItemRarity.UNCOMMON,           // Raridade
    "Armadura básica de couro",    // Descrição
    200,                           // Valor
    false,                         // Não empilhável
    1,                             // Max stack size
    null,                          // Sprite definition
    48,                            // Tile size
    Arrays.asList("barbarian", "paladin"), // Classes permitidas
    0,                             // +Força
    5,                             // +Armadura
    20,                            // +Vida
    0                              // +Mana
);
```

**O que acontece aqui?**
- Definimos propriedades básicas do item
- Configuramos tipo e raridade
- Adicionamos bônus de equipamento
- Especificamos classes que podem usar

### **2. Sistema de Tipos**
```java
public enum ItemType {
    WEAPON("Arma", "weapon"),           // Espadas, machados, etc.
    ARMOR("Armadura", "armor"),         // Armaduras, couraças, etc.
    LEFTHAND("Mão Esquerda", "lefthand"), // Escudos, anéis, etc.
    BOOT("Bota", "boot"),               // Botas, sapatos, etc.
    KEY("Chave", "key"),                // Chaves para portas/baús
    CONSUMABLE("Consumível", "consumable"), // Poções, comida, etc.
    QUEST_ITEM("Item de Quest", "quest_item"); // Itens especiais
}
```

**Tipos de Items:**
- **WEAPON:** Aumenta força de ataque
- **ARMOR:** Aumenta defesa e vida
- **LEFTHAND:** Escudos, anéis, objetos especiais
- **BOOT:** Aumenta velocidade ou outros bônus
- **KEY:** Abre portas e baús
- **CONSUMABLE:** Usado uma vez (poções, comida)
- **QUEST_ITEM:** Itens especiais para missões

### **3. Sistema de Raridades**
```java
public enum ItemRarity {
    COMMON("Comum", "common", Color.GRAY),        // Cinza
    UNCOMMON("Incomum", "uncommon", Color.GREEN), // Verde
    RARE("Raro", "rare", Color.BLUE),            // Azul
    EPIC("Épico", "epic", Color.MAGENTA),        // Magenta
    LEGENDARY("Lendário", "legendary", Color.ORANGE); // Laranja
}
```

**Raridades e Cores:**
- **Comum (Cinza):** Itens básicos, fáceis de encontrar
- **Incomum (Verde):** Itens melhores, encontrados ocasionalmente
- **Raro (Azul):** Itens bons, encontrados raramente
- **Épico (Magenta):** Itens excelentes, muito raros
- **Lendário (Laranja):** Itens únicos, extremamente raros

---

## 🎯 **Exemplo Prático: Sistema Completo**

### **Cenário: Jogador Coletando e Equipando Items**

```
🎮 Jogador encontra baú
    ↓ (interage)
📦 Baú contém: Espada de Ferro + Armadura de Couro
    ↓ (coleta)
🎒 Inventário: [Espada] [Armadura] [vazio] [vazio] ...
    ↓ (equipa espada)
⚔️ Slot Arma: [Espada de Ferro] (+10 força)
    ↓ (equipa armadura)
🛡️ Slot Armadura: [Armadura de Couro] (+5 defesa, +20 vida)
    ↓ (atributos atualizados)
📊 Jogador: Força 60, Defesa 15, Vida 220
```

### **Implementação do Sistema**
```java
public class ExemploSistemaItems {
    
    public void demonstrarSistemaCompleto() {
        // 1. Criar jogador
        Player jogador = new Player(100, 100, 3, "down", "Herói", 
            new Barbarian(15), 200, 200, 100, 100, 50, 30, 10);
        
        // 2. Criar inventário
        InventoryManager inventario = new InventoryManager("barbarian", jogador);
        
        // 3. Criar itens
        Item espada = criarEspada();
        Item armadura = criarArmadura();
        Item pocao = criarPocao();
        
        // 4. Adicionar itens ao inventário
        inventario.addItem(espada);
        inventario.addItem(armadura);
        inventario.addItem(pocao);
        
        // 5. Equipar itens
        equiparItem(inventario, espada);
        equiparItem(inventario, armadura);
        
        // 6. Usar consumível
        usarConsumivel(inventario, pocao);
        
        // 7. Mostrar status final
        mostrarStatusJogador(jogador);
    }
    
    private Item criarEspada() {
        return new Item(
            "sword_iron",
            "Espada de Ferro",
            ItemType.WEAPON,
            ItemRarity.COMMON,
            "Uma espada básica de ferro",
            100,
            false
        );
    }
    
    private Item criarArmadura() {
        return new Item(
            "armor_leather",
            "Armadura de Couro",
            0, 0,
            ItemType.ARMOR,
            ItemRarity.UNCOMMON,
            "Armadura básica de couro",
            200,
            false,
            1,
            null,
            48,
            Arrays.asList("barbarian", "paladin"),
            0,  // +Força
            5,  // +Armadura
            20, // +Vida
            0   // +Mana
        );
    }
    
    private Item criarPocao() {
        return new Item(
            "potion_health",
            "Poção de Vida",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            "Restaura 50 pontos de vida",
            25,
            true  // Empilhável
        );
    }
    
    private void equiparItem(InventoryManager inventario, Item item) {
        // Selecionar item no inventário
        inventario.setSelectedItem(item);
        
        // Tentar equipar
        if (inventario.equipSelectedItem()) {
            System.out.println("Item equipado: " + item.getName());
        } else {
            System.out.println("Não foi possível equipar: " + item.getName());
        }
    }
    
    private void usarConsumivel(InventoryManager inventario, Item pocao) {
        // Selecionar poção
        inventario.setSelectedItem(pocao);
        
        // Usar item
        if (inventario.useSelectedItem()) {
            System.out.println("Poção usada! Vida restaurada.");
        }
    }
    
    private void mostrarStatusJogador(Player jogador) {
        System.out.println("=== STATUS DO JOGADOR ===");
        System.out.println("Vida: " + jogador.getAttributeHealth() + "/" + jogador.getAttributeMaxHealth());
        System.out.println("Mana: " + jogador.getAttributeMana() + "/" + jogador.getAttributeMaxMana());
        System.out.println("Força: " + jogador.getAttributeStrength());
        System.out.println("Defesa: " + jogador.getAttributeArmor());
        System.out.println("Agilidade: " + jogador.getAttributeAgility());
    }
}
```

---

## 🎒 **Sistema de Inventário**

### **Gerenciamento de Slots**
```java
public class SistemaInventario {
    
    public void gerenciarInventario() {
        InventoryManager inventario = new InventoryManager("barbarian", player);
        
        // 1. Adicionar item
        Item item = ItemFactory.createItem("sword_iron");
        boolean adicionado = inventario.addItem(item);
        
        if (adicionado) {
            System.out.println("Item adicionado ao inventário!");
        } else {
            System.out.println("Inventário cheio!");
        }
        
        // 2. Verificar se inventário está cheio
        if (inventario.isInventoryFull()) {
            System.out.println("Inventário está cheio!");
        }
        
        // 3. Contar slots ocupados
        int slotsOcupados = inventario.getOccupiedInventorySlots();
        int slotsTotal = inventario.getTotalInventorySlots();
        System.out.println("Slots: " + slotsOcupados + "/" + slotsTotal);
        
        // 4. Remover item
        inventario.removeItem(0); // Remove item do slot 0
        
        // 5. Limpar inventário
        inventario.clearInventory();
    }
    
    public void navegarInventario() {
        InventoryManager inventario = new InventoryManager("barbarian", player);
        
        // Navegar com setas
        inventario.moveSelection("up");    // Move para cima
        inventario.moveSelection("down");  // Move para baixo
        inventario.moveSelection("left");  // Move para esquerda
        inventario.moveSelection("right"); // Move para direita
        
        // Obter item selecionado
        Item itemSelecionado = inventario.getSelectedItem();
        if (itemSelecionado != null) {
            System.out.println("Item selecionado: " + itemSelecionado.getName());
        }
        
        // Alternar entre inventário e equipamento
        inventario.toggleInventoryMode(); // true = inventário, false = equipamento
    }
}
```

### **Sistema de Empilhamento**
```java
public class SistemaEmpilhamento {
    
    public void gerenciarEmpilhamento() {
        // Criar item empilhável
        Item pocao = new Item(
            "potion_health",
            "Poção de Vida",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            "Restaura 50 pontos de vida",
            25,
            true  // Empilhável
        );
        
        // Definir tamanho da pilha
        pocao.setStackSize(5);        // 5 poções na pilha
        pocao.setMaxStackSize(99);    // Máximo 99 por pilha
        
        // Adicionar mais itens à pilha
        Item novaPocao = ItemFactory.createItem("potion_health");
        if (pocao.canStackWith(novaPocao)) {
            int adicionadas = pocao.addToStack(3); // Tenta adicionar 3
            System.out.println("Adicionadas " + adicionadas + " poções à pilha");
        }
        
        // Usar item da pilha
        if (pocao.getStackSize() > 0) {
            pocao.setStackSize(pocao.getStackSize() - 1);
            System.out.println("Poção usada! Restam " + pocao.getStackSize() + " na pilha");
        }
    }
}
```

---

## ⚔️ **Sistema de Equipamento**

### **Slots de Equipamento**
```java
public enum EquipmentSlot {
    WEAPON(0, "Arma", ItemType.WEAPON),           // Slot 0
    LEFTHAND(1, "Mão Esquerda", ItemType.LEFTHAND), // Slot 1
    ARMOR(2, "Armadura", ItemType.ARMOR),        // Slot 2
    BOOT(3, "Bota", ItemType.BOOT);              // Slot 3
}
```

### **Equipar e Desequipar**
```java
public class SistemaEquipamento {
    
    public void gerenciarEquipamento() {
        Equipment equipamento = new Equipment(player);
        
        // 1. Equipar item
        Item espada = ItemFactory.createItem("sword_iron");
        Item itemAnterior = equipamento.equipItem(EquipmentSlot.WEAPON, espada);
        
        if (itemAnterior != null) {
            System.out.println("Item anterior desequipado: " + itemAnterior.getName());
        }
        
        // 2. Verificar item equipado
        Item itemEquipado = equipamento.getEquippedItem(EquipmentSlot.WEAPON);
        if (itemEquipado != null) {
            System.out.println("Arma equipada: " + itemEquipado.getName());
        }
        
        // 3. Desequipar item
        Item itemRemovido = equipamento.unequipItem(EquipmentSlot.WEAPON);
        if (itemRemovido != null) {
            System.out.println("Item desequipado: " + itemRemovido.getName());
        }
        
        // 4. Aplicar bônus automaticamente
        aplicarBonusAutomatico(equipamento);
    }
    
    private void aplicarBonusAutomatico(Equipment equipamento) {
        // O sistema aplica bônus automaticamente ao equipar
        // e remove bônus ao desequipar
        
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Item item = equipamento.getEquippedItem(slot);
            if (item != null) {
                // Bônus já aplicados automaticamente
                System.out.println(slot.getDisplayName() + ": " + item.getName());
                System.out.println("  +Força: " + item.getStrengthFromEquip());
                System.out.println("  +Armadura: " + item.getArmorFromEquip());
                System.out.println("  +Vida: " + item.getHealthFromEquip());
                System.out.println("  +Mana: " + item.getManaFromEquip());
            }
        }
    }
}
```

---

## 🏭 **Sistema de Factory**

### **Criação Automática de Items**
```java
public class ItemFactory {
    
    public static Item createItem(String itemId) {
        // 1. Verificar se é orbe mágica
        if (itemId.startsWith("orb_")) {
            String orbType = itemId.substring(4); // Remove "orb_"
            return new MagicOrb(orbType);
        }
        
        // 2. Buscar no objects.json
        try {
            InputStream is = ItemFactory.class.getResourceAsStream("/objects.json");
            Gson gson = new Gson();
            JsonArray objectsArray = gson.fromJson(new InputStreamReader(is), JsonArray.class);
            
            for (int i = 0; i < objectsArray.size(); i++) {
                JsonObject objectJson = objectsArray.get(i).getAsJsonObject();
                if (objectJson.has("id") && objectJson.get("id").getAsString().equals(itemId)) {
                    return criarItemDoJson(objectJson);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar item " + itemId + ": " + e.getMessage());
        }
        
        return null;
    }
    
    private static Item criarItemDoJson(JsonObject objectJson) {
        String id = objectJson.get("id").getAsString();
        String name = objectJson.get("name").getAsString();
        String itemType = objectJson.get("itemType").getAsString();
        String rarity = objectJson.get("rarity").getAsString();
        String description = objectJson.get("description").getAsString();
        int value = objectJson.get("value").getAsInt();
        
        // Converter strings para enums
        ItemType type = ItemType.fromString(itemType);
        ItemRarity itemRarity = ItemRarity.fromString(rarity);
        
        // Criar item
        return new Item(id, name, type, itemRarity, description, value, false);
    }
}
```

### **Exemplo de Configuração JSON**
```json
{
  "id": "sword_iron",
  "name": "Espada de Ferro",
  "spritePaths": [
    ["/sprites/objects/items/sword_iron.png"]
  ],
  "size": [1, 1],
  "collision": false,
  "interactive": true,
  "autoInteraction": true,
  "itemType": "weapon",
  "rarity": "common",
  "description": "Uma espada básica de ferro",
  "value": 100,
  "allowedClass": ["barbarian", "paladin", "ranger"],
  "strengthFromEquip": 10,
  "armorFromEquip": 0,
  "healthFromEquip": 0,
  "manaFromEquip": 0
}
```

---

## 🎨 **Interface do Usuário**

### **Renderização do Inventário**
```java
public class InterfaceInventario {
    
    public void desenharInventario(Graphics2D g2, InventoryManager inventario) {
        int slotSize = 48;
        int startX = 50;
        int startY = 50;
        
        // Desenhar fundo do inventário
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(startX - 5, startY - 5, 
                   inventario.getInventoryColumns() * slotSize + 10,
                   inventario.getInventoryRows() * slotSize + 10);
        
        // Desenhar slots
        for (int row = 0; row < inventario.getInventoryRows(); row++) {
            for (int col = 0; col < inventario.getInventoryColumns(); col++) {
                int x = startX + col * slotSize;
                int y = startY + row * slotSize;
                
                // Fundo do slot
                g2.setColor(Color.GRAY);
                g2.fillRect(x, y, slotSize, slotSize);
                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, slotSize, slotSize);
                
                // Item no slot
                int slotIndex = row * inventario.getInventoryColumns() + col;
                Item item = inventario.getInventorySlots().get(slotIndex);
                
                if (item != null) {
                    desenharItem(g2, item, x, y, slotSize);
                }
                
                // Destacar slot selecionado
                if (row == inventario.getSelectedRow() && col == inventario.getSelectedColumn()) {
                    g2.setColor(Color.YELLOW);
                    g2.drawRect(x, y, slotSize, slotSize);
                }
            }
        }
    }
    
    private void desenharItem(Graphics2D g2, Item item, int x, int y, int size) {
        // Desenhar sprite do item
        BufferedImage sprite = carregarSpriteItem(item);
        if (sprite != null) {
            g2.drawImage(sprite, x, y, size, size, null);
        }
        
        // Desenhar cor da raridade
        g2.setColor(item.getRarity().getColor());
        g2.drawRect(x, y, size, size);
        
        // Desenhar quantidade se empilhável
        if (item.isStackable() && item.getStackSize() > 1) {
            g2.setColor(Color.WHITE);
            g2.drawString(String.valueOf(item.getStackSize()), x + size - 10, y + size - 5);
        }
    }
}
```

---

## 🔧 **Configuração e Uso**

### **Inicialização do Sistema**
```java
public class InicializacaoSistemaItems {
    
    public void inicializarSistemaItems(Player player) {
        // 1. Criar inventário
        InventoryManager inventario = new InventoryManager("barbarian", player);
        
        // 2. Configurar itens iniciais
        Item itemInicial = ItemFactory.createItem("sword_iron");
        inventario.addItem(itemInicial);
        
        // 3. Configurar interface
        InventoryUI inventarioUI = new InventoryUI(inventario);
        
        // 4. Integrar com outros sistemas
        integrarComSistemas(inventario);
    }
    
    private void integrarComSistemas(InventoryManager inventario) {
        // Integração com sistema de Quest
        inventario.setOnItemCollected((item) -> {
            QuestManager.getInstance().onQuestItemCollected(item);
        });
        
        // Integração com sistema de Batalha
        inventario.setOnItemUsed((item, character) -> {
            if (item.getItemType() == ItemType.CONSUMABLE) {
                usarConsumivel(item, character);
            }
        });
        
        // Integração com sistema de Áudio
        inventario.setOnItemEquipped((item) -> {
            AudioManager.getInstance().playSoundEffect("item_equip");
        });
    }
}
```

### **Integração com Outros Sistemas**
```java
public class IntegracaoSistemas {
    
    // Integração com sistema de Quest
    public void onQuestItemCollected(Item item) {
        if (item instanceof QuestItem) {
            QuestItem questItem = (QuestItem) item;
            QuestManager.getInstance().onQuestItemCollected(questItem);
        }
    }
    
    // Integração com sistema de Batalha
    public void usarConsumivel(Item item, Character character) {
        switch (item.getItemId()) {
            case "potion_health":
                int cura = 50;
                character.heal(cura);
                System.out.println("Vida restaurada: +" + cura);
                break;
                
            case "potion_mana":
                int mana = 30;
                character.restoreMana(mana);
                System.out.println("Mana restaurada: +" + mana);
                break;
        }
    }
    
    // Integração com sistema de Comércio
    public void venderItem(Item item, int preco) {
        player.addCoins(preco);
        inventoryManager.removeItem(item);
        System.out.println("Item vendido por " + preco + " moedas!");
    }
}
```

---

## 🎯 **Casos de Uso Avançados**

### **Sistema de Crafting**
```java
public class SistemaCrafting {
    
    public void criarItem() {
        // Receita: 2 Ferro + 1 Madeira = Espada de Ferro
        List<Item> ingredientes = Arrays.asList(
            ItemFactory.createItem("iron_ore"),
            ItemFactory.createItem("iron_ore"),
            ItemFactory.createItem("wood")
        );
        
        Item resultado = ItemFactory.createItem("sword_iron");
        
        if (temIngredientes(ingredientes)) {
            removerIngredientes(ingredientes);
            adicionarResultado(resultado);
            System.out.println("Item criado: " + resultado.getName());
        } else {
            System.out.println("Ingredientes insuficientes!");
        }
    }
    
    private boolean temIngredientes(List<Item> ingredientes) {
        for (Item ingrediente : ingredientes) {
            if (!inventoryManager.hasItem(ingrediente.getItemId())) {
                return false;
            }
        }
        return true;
    }
}
```

### **Sistema de Enchanting**
```java
public class SistemaEnchanting {
    
    public void encantarItem(Item item, String encantamento) {
        switch (encantamento) {
            case "fire":
                item.setStrengthFromEquip(item.getStrengthFromEquip() + 5);
                item.setName(item.getName() + " [Fogo]");
                break;
                
            case "ice":
                item.setArmorFromEquip(item.getArmorFromEquip() + 3);
                item.setName(item.getName() + " [Gelo]");
                break;
                
            case "lightning":
                item.setManaFromEquip(item.getManaFromEquip() + 10);
                item.setName(item.getName() + " [Raio]");
                break;
        }
        
        // Atualizar raridade
        item.setRarity(ItemRarity.RARE);
    }
}
```

---

## 🏆 **Vantagens do Sistema**

### **✅ Para o Jogador**
- **Organização:** Inventário claro e organizado
- **Progressão:** Itens melhores aumentam poder
- **Variedade:** Diferentes tipos e raridades
- **Estratégia:** Escolhas táticas de equipamento

### **✅ Para o Desenvolvedor**
- **Modular:** Sistema organizado em componentes
- **Extensível:** Fácil de adicionar novos itens
- **Configurável:** Itens definidos via JSON
- **Integrado:** Conecta com todos os sistemas

### **✅ Para o Jogo**
- **Profundidade:** Sistema complexo e rico
- **Variedade:** Muitos tipos de itens diferentes
- **Progressão:** Sensação de evolução constante
- **Replayability:** Diferentes builds de equipamento

---

## 🚀 **Conclusão**

Este sistema de Items oferece:

- **🎒 Inventário Completo:** Sistema organizado de armazenamento
- **⚔️ Equipamento Inteligente:** Bônus automáticos e slots específicos
- **🎨 Raridades Visuais:** Cores e sistema de raridade
- **🏭 Factory Pattern:** Criação automática de itens
- **🔧 Configuração Flexível:** Itens definidos via JSON
- **🎮 Interface Intuitiva:** Navegação fácil e visual

**Resultado:** Um sistema completo de itens que oferece progressão satisfatória, organização clara e integração perfeita com todos os outros sistemas! 🎒✨

---

## 📋 **Resumo dos Conceitos**

| Conceito | O que faz | Exemplo |
|----------|-----------|---------|
| **Item** | Objeto coletável e usável | Espada, armadura, poção |
| **ItemType** | Categoria do item | WEAPON, ARMOR, CONSUMABLE |
| **ItemRarity** | Raridade e cor | COMMON (cinza), LEGENDARY (laranja) |
| **InventoryManager** | Gerencia inventário | 20 slots organizados |
| **Equipment** | Sistema de equipamento | 4 slots com bônus automáticos |
| **ItemFactory** | Cria itens automaticamente | A partir de JSON |

**Este sistema transforma o jogo em uma experiência de coleta e progressão satisfatória!** 🎮🏆

