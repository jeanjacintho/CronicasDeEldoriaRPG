# 🎒 Sistema de Itens e Poderes - Documentação Completa da API

## 🎯 **Visão Geral**

Este documento especifica **todas as funcionalidades** disponíveis no sistema de itens e poderes do CronicasDeEldoriaRPG, incluindo métodos, classes, enums e exemplos práticos de uso. O sistema implementa inventário completo, equipamentos com bônus, raridades, empilhamento e itens especiais de quest.

---

## 🏗️ **Arquitetura do Sistema**

### **Classes Principais**
```
Item                    → Classe base para todos os itens
├── QuestItem          → Item especial para quests
└── MagicOrb           → Orbe mágica específica

ItemType               → Enum com tipos de itens
ItemRarity             → Enum com raridades e cores

InventoryManager        → Gerenciador de inventário
├── Equipment          → Sistema de equipamento
└── ItemFactory        → Factory para criar itens

EquipmentSlot          → Enum com slots de equipamento
```

### **Sistema de Tipos**
```
ItemType (Enum)
├── WEAPON            → Arma
├── ARMOR             → Armadura
├── LEFTHAND          → Mão esquerda (anel, escudo)
├── BOOT              → Bota
├── KEY               → Chave
├── CONSUMABLE        → Consumível
└── QUEST_ITEM        → Item de quest
```

### **Sistema de Raridades**
```
ItemRarity (Enum)
├── COMMON            → Comum (Cinza)
├── UNCOMMON          → Incomum (Verde)
├── RARE              → Raro (Azul)
├── EPIC              → Épico (Magenta)
└── LEGENDARY         → Lendário (Laranja)
```

### **Sistema de Equipamento**
```
EquipmentSlot (Enum)
├── WEAPON            → Slot 0 - Arma
├── LEFTHAND          → Slot 1 - Mão esquerda
├── ARMOR             → Slot 2 - Armadura
└── BOOT              → Slot 3 - Bota
```

---

## 🎒 **Item - Classe Base**

### **🔧 Criação de Itens**

#### **Construtor Simples (Inventário)**
```java
Item item = new Item(
    "healing_potion",           // ID único
    "Poção de Cura",            // Nome
    ItemType.CONSUMABLE,        // Tipo
    ItemRarity.COMMON,          // Raridade
    "Restaura 50 HP",           // Descrição
    25,                         // Valor em moedas
    true                        // Empilhável
);
```

#### **Construtor Completo (Mundo)**
```java
Item item = new Item(
    "magic_sword",              // ID único
    "Espada Mágica",            // Nome
    worldX, worldY,             // Posição no mundo
    ItemType.WEAPON,            // Tipo
    ItemRarity.RARE,            // Raridade
    "Uma espada encantada",     // Descrição
    500,                        // Valor
    false,                      // Empilhável
    1,                          // Tamanho máximo da pilha
    objectDefinition,           // Definição visual
    tileSize,                   // Tamanho do tile
    Arrays.asList("barbarian", "paladin"), // Classes permitidas
    15,                         // Bônus de força
    5,                          // Bônus de armadura
    0,                          // Bônus de vida
    10                          // Bônus de mana
);
```

### **📊 Propriedades Básicas**

#### **Getters Básicos**
```java
String itemId = item.getItemId();                    // ID único
String name = item.getName();                        // Nome
String description = item.getDescription();          // Descrição
ItemType type = item.getItemType();                  // Tipo
ItemRarity rarity = item.getRarity();               // Raridade
int value = item.getValue();                         // Valor em moedas
boolean stackable = item.isStackable();             // Empilhável
int stackSize = item.getStackSize();                // Tamanho da pilha
int maxStackSize = item.getMaxStackSize();          // Tamanho máximo da pilha
```

#### **Setters**
```java
item.setItemId("new_id");
item.setName("Novo Nome");
item.setDescription("Nova descrição");
item.setItemType(ItemType.WEAPON);
item.setRarity(ItemRarity.RARE);
item.setValue(1000);
item.setStackable(true);
item.setStackSize(5);
item.setMaxStackSize(99);
```

### **⚔️ Sistema de Equipamento**

#### **Bônus de Equipamento**
```java
int strength = item.getStrengthFromEquip();         // Bônus de força
int armor = item.getArmorFromEquip();               // Bônus de armadura
int health = item.getHealthFromEquip();             // Bônus de vida
int mana = item.getManaFromEquip();                 // Bônus de mana

item.setStrengthFromEquip(20);
item.setArmorFromEquip(10);
item.setHealthFromEquip(50);
item.setManaFromEquip(25);
```

#### **Classes Permitidas**
```java
List<String> allowedClasses = item.getAllowedClass();
if (allowedClasses != null) {
    System.out.println("Classes permitidas: " + allowedClasses);
}

item.setAllowedClass(Arrays.asList("barbarian", "paladin"));

// Verificar se classe pode equipar
boolean canEquip = item.canBeEquippedBy("barbarian");
if (canEquip) {
    System.out.println("Barbarian pode equipar este item!");
}
```

### **🔍 Verificações**

#### **`isEquipable()`**
```java
if (item.isEquipable()) {
    System.out.println("Item pode ser equipado!");
}
```
- **Descrição**: Verifica se o item pode ser equipado
- **Retorno**: `boolean` - true se equipável
- **Funcionalidade**: Verifica se o tipo do item é equipável

#### **`canStackWith(Item otherItem)`**
```java
if (item.canStackWith(otherItem)) {
    System.out.println("Itens podem ser empilhados!");
}
```
- **Descrição**: Verifica se dois itens podem ser empilhados
- **Parâmetros**: `Item otherItem` - Outro item
- **Retorno**: `boolean` - true se podem ser empilhados
- **Condições**:
  - Ambos devem ser empilháveis
  - Mesmo ID de item
  - Espaço disponível na pilha

#### **`stackItem(Item otherItem)`**
```java
int stacked = item.stackItem(otherItem);
System.out.println("Empilhados: " + stacked + " itens");
```
- **Descrição**: Tenta empilhar outro item neste item
- **Parâmetros**: `Item otherItem` - Item a ser empilhado
- **Retorno**: `int` - Quantidade empilhada
- **Funcionalidade**: 
  - Empilha até o limite máximo
  - Atualiza tamanhos das pilhas
  - Retorna quantidade empilhada

#### **`copy()`**
```java
Item itemCopy = item.copy();
System.out.println("Cópia criada: " + itemCopy.getName());
```
- **Descrição**: Cria uma cópia do item
- **Retorno**: `Item` - Nova instância do item
- **Funcionalidade**: Cria cópia independente com mesmas propriedades

---

## 🎯 **ItemType - Tipos de Itens**

### **📋 Tipos Disponíveis**

#### **Tipos Equipáveis**
```java
ItemType.WEAPON        // Arma
ItemType.ARMOR         // Armadura
ItemType.LEFTHAND      // Mão esquerda (anel, escudo)
ItemType.BOOT          // Bota
```

#### **Tipos Não-Equipáveis**
```java
ItemType.KEY           // Chave
ItemType.CONSUMABLE    // Consumível
ItemType.QUEST_ITEM    // Item de quest
```

### **🔧 Métodos**

#### **`getDisplayName()`**
```java
String displayName = ItemType.WEAPON.getDisplayName();
System.out.println("Tipo: " + displayName); // "Arma"
```
- **Descrição**: Retorna nome em português para exibição
- **Retorno**: `String` - Nome de exibição

#### **`getJsonValue()`**
```java
String jsonValue = ItemType.WEAPON.getJsonValue();
System.out.println("Valor JSON: " + jsonValue); // "weapon"
```
- **Descrição**: Retorna valor usado em arquivos JSON
- **Retorno**: `String` - Valor JSON

#### **`fromString(String value)`**
```java
ItemType type = ItemType.fromString("weapon");
if (type != null) {
    System.out.println("Tipo encontrado: " + type.getDisplayName());
}
```
- **Descrição**: Converte string para ItemType
- **Parâmetros**: `String value` - Valor a converter
- **Retorno**: `ItemType` - Tipo correspondente ou null

#### **`isEquipable()`**
```java
if (ItemType.WEAPON.isEquipable()) {
    System.out.println("Arma é equipável!");
}
```
- **Descrição**: Verifica se o tipo é equipável
- **Retorno**: `boolean` - true se equipável

---

## 💎 **ItemRarity - Raridades**

### **📋 Raridades Disponíveis**

```java
ItemRarity.COMMON       // Comum (Cinza)
ItemRarity.UNCOMMON     // Incomum (Verde)
ItemRarity.RARE         // Raro (Azul)
ItemRarity.EPIC         // Épico (Magenta)
ItemRarity.LEGENDARY    // Lendário (Laranja)
```

### **🔧 Métodos**

#### **`getDisplayName()`**
```java
String displayName = ItemRarity.RARE.getDisplayName();
System.out.println("Raridade: " + displayName); // "Raro"
```

#### **`getJsonValue()`**
```java
String jsonValue = ItemRarity.RARE.getJsonValue();
System.out.println("Valor JSON: " + jsonValue); // "rare"
```

#### **`getColor()`**
```java
Color color = ItemRarity.RARE.getColor();
System.out.println("Cor: " + color); // java.awt.Color[r=0,g=0,b=255]
```
- **Descrição**: Retorna cor associada à raridade
- **Retorno**: `Color` - Cor para exibição

#### **`fromString(String value)`**
```java
ItemRarity rarity = ItemRarity.fromString("rare");
if (rarity != null) {
    System.out.println("Raridade: " + rarity.getDisplayName());
}
```

---

## 🎒 **InventoryManager - Gerenciador de Inventário**

### **🔧 Inicialização**

#### **Construtor**
```java
InventoryManager inventory = new InventoryManager("barbarian", player);
```
- **Descrição**: Cria novo gerenciador de inventário
- **Parâmetros**: 
  - `String playerClassName` - Classe do jogador
  - `Player player` - Referência ao jogador
- **Funcionalidade**: 
  - Cria inventário 5x4 (20 slots)
  - Inicializa sistema de equipamento
  - Configura slots vazios

### **📦 Gerenciamento de Itens**

#### **`addItem(Item item)`**
```java
boolean success = inventory.addItem(item);
if (success) {
    System.out.println("Item adicionado com sucesso!");
} else {
    System.out.println("Inventário cheio!");
}
```
- **Descrição**: Adiciona item ao inventário
- **Parâmetros**: `Item item` - Item a adicionar
- **Retorno**: `boolean` - true se adicionado
- **Funcionalidade**:
  - Tenta empilhar com itens existentes
  - Procura slot vazio se não empilhável
  - Retorna false se inventário cheio

#### **`removeItem(int slotIndex)`**
```java
Item removedItem = inventory.removeItem(5);
if (removedItem != null) {
    System.out.println("Item removido: " + removedItem.getName());
}
```
- **Descrição**: Remove item do slot específico
- **Parâmetros**: `int slotIndex` - Índice do slot (0-19)
- **Retorno**: `Item` - Item removido ou null

#### **`getItem(int slotIndex)`**
```java
Item item = inventory.getItem(5);
if (item != null) {
    System.out.println("Item no slot 5: " + item.getName());
}
```
- **Descrição**: Obtém item do slot específico
- **Parâmetros**: `int slotIndex` - Índice do slot
- **Retorno**: `Item` - Item no slot ou null

### **🔍 Consultas**

#### **`hasItemById(String itemId)`**
```java
if (inventory.hasItemById("healing_potion")) {
    System.out.println("Tem poção de cura!");
}
```
- **Descrição**: Verifica se tem item pelo ID
- **Parâmetros**: `String itemId` - ID do item
- **Retorno**: `boolean` - true se encontrado

#### **`countItemById(String itemId)`**
```java
int count = inventory.countItemById("healing_potion");
System.out.println("Poções de cura: " + count);
```
- **Descrição**: Conta quantidade total do item
- **Parâmetros**: `String itemId` - ID do item
- **Retorno**: `int` - Quantidade total

#### **`consumeItem(String itemId)`**
```java
boolean consumed = inventory.consumeItem("healing_potion");
if (consumed) {
    System.out.println("Poção consumida!");
}
```
- **Descrição**: Consome uma unidade do item
- **Parâmetros**: `String itemId` - ID do item
- **Retorno**: `boolean` - true se consumido
- **Funcionalidade**: Remove 1 unidade da pilha

### **🎮 Controle de Interface**

#### **Navegação**
```java
inventory.moveUp();        // Move cursor para cima
inventory.moveDown();      // Move cursor para baixo
inventory.moveLeft();      // Move cursor para esquerda
inventory.moveRight();     // Move cursor para direita
```

#### **Seleção**
```java
int selectedRow = inventory.getSelectedRow();        // Linha selecionada
int selectedColumn = inventory.getSelectedColumn();  // Coluna selecionada
int selectedSlot = inventory.getSelectedSlot();      // Slot selecionado (0-19)
Item selectedItem = inventory.getSelectedItem();     // Item selecionado
```

#### **Modos**
```java
boolean inInventoryMode = inventory.isInInventoryMode(); // true = inventário, false = equipamento
inventory.setInInventoryMode(true);  // Alternar para modo inventário
inventory.setInInventoryMode(false); // Alternar para modo equipamento
```

#### **Visibilidade**
```java
boolean visible = inventory.isVisible();
inventory.setVisible(true);  // Mostrar inventário
inventory.setVisible(false); // Esconder inventário
```

### **📊 Informações**

#### **`getTotalSlots()`**
```java
int totalSlots = inventory.getTotalSlots();
System.out.println("Total de slots: " + totalSlots); // 20
```

#### **`getEmptySlots()`**
```java
int emptySlots = inventory.getEmptySlots();
System.out.println("Slots vazios: " + emptySlots);
```

#### **`isFull()`**
```java
if (inventory.isFull()) {
    System.out.println("Inventário cheio!");
}
```

#### **`isEmpty()`**
```java
if (inventory.isEmpty()) {
    System.out.println("Inventário vazio!");
}
```

---

## ⚔️ **Equipment - Sistema de Equipamento**

### **🔧 Inicialização**

#### **Construtor**
```java
Equipment equipment = new Equipment(player);
```
- **Descrição**: Cria sistema de equipamento
- **Parâmetros**: `Player player` - Referência ao jogador
- **Funcionalidade**: 
  - Cria 4 slots de equipamento
  - Configura slots específicos por tipo

### **🎯 Slots de Equipamento**

#### **EquipmentSlot Enum**
```java
EquipmentSlot.WEAPON     // Slot 0 - Arma
EquipmentSlot.LEFTHAND   // Slot 1 - Mão esquerda
EquipmentSlot.ARMOR      // Slot 2 - Armadura
EquipmentSlot.BOOT       // Slot 3 - Bota
```

#### **Métodos do Enum**
```java
int index = EquipmentSlot.WEAPON.getIndex();                    // 0
String displayName = EquipmentSlot.WEAPON.getDisplayName();    // "Arma"
ItemType requiredType = EquipmentSlot.WEAPON.getRequiredType(); // ItemType.WEAPON

EquipmentSlot slot = EquipmentSlot.fromIndex(0); // EquipmentSlot.WEAPON
```

### **⚔️ Equipamento**

#### **`equipItem(EquipmentSlot slot, Item item)`**
```java
Item previousItem = equipment.equipItem(EquipmentSlot.WEAPON, sword);
if (previousItem != null) {
    System.out.println("Item anterior: " + previousItem.getName());
}
```
- **Descrição**: Equipa item no slot específico
- **Parâmetros**: 
  - `EquipmentSlot slot` - Slot de equipamento
  - `Item item` - Item a equipar
- **Retorno**: `Item` - Item que estava equipado anteriormente
- **Funcionalidade**:
  - Verifica compatibilidade do item
  - Remove bônus do item anterior
  - Aplica bônus do novo item
  - Reproduz som de equipamento

#### **`unequipItem(EquipmentSlot slot)`**
```java
Item unequippedItem = equipment.unequipItem(EquipmentSlot.WEAPON);
if (unequippedItem != null) {
    System.out.println("Item desequipado: " + unequippedItem.getName());
}
```
- **Descrição**: Remove item do slot
- **Parâmetros**: `EquipmentSlot slot` - Slot de equipamento
- **Retorno**: `Item` - Item removido
- **Funcionalidade**: Remove bônus do item

### **🔍 Consultas**

#### **`getEquippedItem(EquipmentSlot slot)`**
```java
Item equippedItem = equipment.getEquippedItem(EquipmentSlot.WEAPON);
if (equippedItem != null) {
    System.out.println("Arma equipada: " + equippedItem.getName());
}
```

#### **`getEquippedItem(int index)`**
```java
Item equippedItem = equipment.getEquippedItem(0); // Slot 0 (Arma)
```

#### **`isSlotEmpty(EquipmentSlot slot)`**
```java
if (equipment.isSlotEmpty(EquipmentSlot.WEAPON)) {
    System.out.println("Slot de arma vazio!");
}
```

#### **`isSlotEmpty(int index)`**
```java
if (equipment.isSlotEmpty(0)) {
    System.out.println("Slot 0 vazio!");
}
```

### **🎮 Controle de Interface**

#### **Navegação**
```java
equipment.moveUp();    // Move cursor para cima
equipment.moveDown();  // Move cursor para baixo
```

#### **Seleção**
```java
int selectedSlot = equipment.getSelectedSlot();                    // Slot selecionado (0-3)
Item selectedItem = equipment.getSelectedItem();                  // Item selecionado
EquipmentSlot selectedEquipmentSlot = equipment.getSelectedEquipmentSlot(); // Slot selecionado
```

#### **`setSelectedSlot(int slot)`**
```java
equipment.setSelectedSlot(2); // Selecionar slot 2 (Armadura)
```

### **📊 Informações**

#### **`getTotalSlots()`**
```java
int totalSlots = equipment.getTotalSlots();
System.out.println("Total de slots: " + totalSlots); // 4
```

#### **`getOccupiedSlots()`**
```java
int occupiedSlots = equipment.getOccupiedSlots();
System.out.println("Slots ocupados: " + occupiedSlots);
```

#### **`hasAnyItemEquipped()`**
```java
if (equipment.hasAnyItemEquipped()) {
    System.out.println("Tem itens equipados!");
}
```

#### **`getAllEquippedItems()`**
```java
Item[] allItems = equipment.getAllEquippedItems();
for (int i = 0; i < allItems.length; i++) {
    if (allItems[i] != null) {
        System.out.println("Slot " + i + ": " + allItems[i].getName());
    }
}
```

---

## 🏭 **ItemFactory - Factory de Itens**

### **🔧 Criação de Itens**

#### **`createItem(String itemId)`**
```java
Item item = ItemFactory.createItem("healing_potion");
if (item != null) {
    System.out.println("Item criado: " + item.getName());
}
```
- **Descrição**: Cria item pelo ID usando objects.json
- **Parâmetros**: `String itemId` - ID do item
- **Retorno**: `Item` - Item criado ou null
- **Funcionalidade**: 
  - Busca dados no objects.json
  - Cria item com propriedades corretas
  - Suporte especial para orbes mágicas

#### **`createMagicOrb(String orbType)`**
```java
MagicOrb orb = ItemFactory.createMagicOrb("fire");
System.out.println("Orbe criada: " + orb.getName()); // "Orbe de Fogo"
```
- **Descrição**: Cria orbe mágica específica
- **Parâmetros**: `String orbType` - Tipo da orbe (fire, water, earth, air)
- **Retorno**: `MagicOrb` - Orbe criada

---

## 🎯 **QuestItem - Item de Quest**

### **🔧 Criação**

#### **Construtor com Posição**
```java
QuestItem questItem = new QuestItem(
    "ancient_key",              // ID único
    "Chave Antiga",             // Nome
    worldX, worldY,             // Posição no mundo
    ItemType.KEY,               // Tipo
    ItemRarity.RARE,            // Raridade
    "Uma chave antiga e misteriosa", // Descrição
    0,                          // Valor (quest items não têm valor)
    false,                      // Empilhável
    "ancient_temple_quest"     // ID da quest
);
```

#### **Construtor sem Posição**
```java
QuestItem questItem = new QuestItem(
    "ancient_key",              // ID único
    "Chave Antiga",             // Nome
    ItemType.KEY,               // Tipo
    ItemRarity.RARE,            // Raridade
    "Uma chave antiga e misteriosa", // Descrição
    0,                          // Valor
    false,                      // Empilhável
    "ancient_temple_quest"      // ID da quest
);
```

### **🎮 Funcionalidades**

#### **`onCollect()`**
```java
questItem.onCollect();
```
- **Descrição**: Chamado quando item é coletado
- **Funcionalidade**: Notifica QuestManager sobre coleta

#### **Getters**
```java
boolean isQuestItem = questItem.isQuestItem();        // true
String questId = questItem.getQuestId();             // "ancient_temple_quest"
questItem.setQuestId("new_quest_id");                // Alterar ID da quest
```

---

## 🔮 **MagicOrb - Orbe Mágica**

### **🔧 Criação**

#### **Construtor com Posição**
```java
MagicOrb orb = new MagicOrb("fire", worldX, worldY);
```

#### **Construtor sem Posição**
```java
MagicOrb orb = new MagicOrb("fire");
```

### **🎮 Funcionalidades**

#### **`depositInTotem()`**
```java
orb.depositInTotem();
```
- **Descrição**: Deposita orbe no Totem Central
- **Funcionalidade**: Notifica QuestManager sobre depósito

#### **Getters**
```java
String orbType = orb.getOrbType();                   // "fire"
int orbPower = orb.getOrbPower();                    // 25
String displayName = orb.getOrbDisplayName();       // "Fogo"
boolean deposited = orb.isDeposited();               // false
```

#### **`isOrbType(String type)`**
```java
if (orb.isOrbType("fire")) {
    System.out.println("É uma orbe de fogo!");
}
```

### **📊 Tipos de Orbes**

```java
MagicOrb fireOrb = new MagicOrb("fire");    // Poder: 25
MagicOrb waterOrb = new MagicOrb("water");  // Poder: 20
MagicOrb earthOrb = new MagicOrb("earth");  // Poder: 30
MagicOrb airOrb = new MagicOrb("air");      // Poder: 15
```

---

## 🎮 **Exemplos Práticos de Uso**

### **Exemplo 1: Criação de Itens**
```java
// Criar poção de cura
Item healingPotion = new Item(
    "healing_potion",
    "Poção de Cura",
    ItemType.CONSUMABLE,
    ItemRarity.COMMON,
    "Restaura 50 HP",
    25,
    true
);

// Criar espada mágica
Item magicSword = new Item(
    "magic_sword",
    "Espada Mágica",
    ItemType.WEAPON,
    ItemRarity.RARE,
    "Uma espada encantada",
    500,
    false
);

// Criar usando factory
Item item = ItemFactory.createItem("healing_potion");
```

### **Exemplo 2: Sistema de Inventário**
```java
// Adicionar itens ao inventário
InventoryManager inventory = new InventoryManager("barbarian", player);

boolean success = inventory.addItem(healingPotion);
if (success) {
    System.out.println("Poção adicionada!");
}

// Verificar se tem item
if (inventory.hasItemById("healing_potion")) {
    System.out.println("Tem poção de cura!");
}

// Contar quantidade
int count = inventory.countItemById("healing_potion");
System.out.println("Poções: " + count);

// Consumir item
boolean consumed = inventory.consumeItem("healing_potion");
if (consumed) {
    System.out.println("Poção consumida!");
}
```

### **Exemplo 3: Sistema de Equipamento**
```java
// Equipar item
Equipment equipment = new Equipment(player);
Item previousItem = equipment.equipItem(EquipmentSlot.WEAPON, magicSword);

if (previousItem != null) {
    System.out.println("Item anterior: " + previousItem.getName());
}

// Verificar item equipado
Item equippedWeapon = equipment.getEquippedItem(EquipmentSlot.WEAPON);
if (equippedWeapon != null) {
    System.out.println("Arma equipada: " + equippedWeapon.getName());
}

// Desequipar item
Item unequippedItem = equipment.unequipItem(EquipmentSlot.WEAPON);
if (unequippedItem != null) {
    System.out.println("Item desequipado: " + unequippedItem.getName());
}
```

### **Exemplo 4: Sistema de Empilhamento**
```java
// Criar itens empilháveis
Item potion1 = new Item("healing_potion", "Poção", ItemType.CONSUMABLE, ItemRarity.COMMON, "Cura", 25, true);
Item potion2 = new Item("healing_potion", "Poção", ItemType.CONSUMABLE, ItemRarity.COMMON, "Cura", 25, true);

// Verificar se podem ser empilhados
if (potion1.canStackWith(potion2)) {
    System.out.println("Podem ser empilhados!");
    
    // Empilhar
    int stacked = potion1.stackItem(potion2);
    System.out.println("Empilhados: " + stacked + " itens");
    System.out.println("Pilha atual: " + potion1.getStackSize());
}
```

### **Exemplo 5: Sistema de Raridades**
```java
// Criar itens com diferentes raridades
Item commonItem = new Item("iron_sword", "Espada de Ferro", ItemType.WEAPON, ItemRarity.COMMON, "Espada comum", 100, false);
Item rareItem = new Item("magic_sword", "Espada Mágica", ItemType.WEAPON, ItemRarity.RARE, "Espada rara", 500, false);
Item legendaryItem = new Item("excalibur", "Excalibur", ItemType.WEAPON, ItemRarity.LEGENDARY, "Espada lendária", 2000, false);

// Obter informações de raridade
System.out.println("Raridade: " + rareItem.getRarity().getDisplayName());
System.out.println("Cor: " + rareItem.getRarity().getColor());
System.out.println("Valor JSON: " + rareItem.getRarity().getJsonValue());
```

### **Exemplo 6: Sistema de Classes**
```java
// Criar item com restrição de classe
Item barbarianSword = new Item(
    "barbarian_sword",
    "Espada do Bárbaro",
    ItemType.WEAPON,
    ItemRarity.RARE,
    "Espada para bárbaros",
    500,
    false
);

// Definir classes permitidas
barbarianSword.setAllowedClass(Arrays.asList("barbarian"));

// Verificar se classe pode equipar
if (barbarianSword.canBeEquippedBy("barbarian")) {
    System.out.println("Bárbaro pode equipar!");
}

if (!barbarianSword.canBeEquippedBy("mage")) {
    System.out.println("Mago não pode equipar!");
}
```

### **Exemplo 7: Sistema de Bônus**
```java
// Criar item com bônus
Item powerRing = new Item(
    "power_ring",
    "Anel do Poder",
    ItemType.LEFTHAND,
    ItemRarity.EPIC,
    "Aumenta atributos",
    1000,
    false
);

// Definir bônus
powerRing.setStrengthFromEquip(10);
powerRing.setArmorFromEquip(5);
powerRing.setHealthFromEquip(25);
powerRing.setManaFromEquip(20);

// Equipar e verificar bônus
Equipment equipment = new Equipment(player);
equipment.equipItem(EquipmentSlot.LEFTHAND, powerRing);

// Verificar atributos do jogador
System.out.println("Força: " + player.getAttributeStrength());
System.out.println("Armadura: " + player.getAttributeArmor());
System.out.println("Vida máxima: " + player.getAttributeMaxHealth());
System.out.println("Mana máxima: " + player.getAttributeMaxMana());
```

### **Exemplo 8: Sistema de Quest Items**
```java
// Criar item de quest
QuestItem questKey = new QuestItem(
    "ancient_key",
    "Chave Antiga",
    ItemType.KEY,
    ItemRarity.RARE,
    "Uma chave antiga e misteriosa",
    0,
    false,
    "ancient_temple_quest"
);

// Coletar item
questKey.onCollect(); // Notifica QuestManager

// Verificar propriedades
System.out.println("É item de quest: " + questKey.isQuestItem());
System.out.println("ID da quest: " + questKey.getQuestId());
```

### **Exemplo 9: Sistema de Orbes Mágicas**
```java
// Criar orbes mágicas
MagicOrb fireOrb = new MagicOrb("fire");
MagicOrb waterOrb = new MagicOrb("water");
MagicOrb earthOrb = new MagicOrb("earth");
MagicOrb airOrb = new MagicOrb("air");

// Verificar propriedades
System.out.println("Orbe de fogo: " + fireOrb.getOrbDisplayName());
System.out.println("Poder: " + fireOrb.getOrbPower());

// Depositar no totem
fireOrb.depositInTotem();
System.out.println("Depositada: " + fireOrb.isDeposited());

// Verificar tipo
if (fireOrb.isOrbType("fire")) {
    System.out.println("É uma orbe de fogo!");
}
```

---

## 🔧 **Integração com Outros Sistemas**

### **Com Sistema de Quest**
```java
// Notificar coleta de item de quest
QuestItem questItem = new QuestItem("ancient_key", "Chave Antiga", ItemType.KEY, ItemRarity.RARE, "Chave antiga", 0, false, "ancient_quest");
questItem.onCollect(); // Notifica QuestManager

// Notificar depósito de orbe
MagicOrb orb = new MagicOrb("fire");
orb.depositInTotem(); // Notifica QuestManager
```

### **Com Sistema de Batalha**
```java
// Aplicar bônus de equipamento
Equipment equipment = new Equipment(player);
Item weapon = equipment.getEquippedItem(EquipmentSlot.WEAPON);

if (weapon != null) {
    int strengthBonus = weapon.getStrengthFromEquip();
    int armorBonus = weapon.getArmorFromEquip();
    
    // Bônus são aplicados automaticamente ao equipar
    System.out.println("Bônus de força: " + strengthBonus);
    System.out.println("Bônus de armadura: " + armorBonus);
}
```

### **Com Sistema de Áudio**
```java
// Som de equipamento é reproduzido automaticamente
Equipment equipment = new Equipment(player);
equipment.equipItem(EquipmentSlot.WEAPON, sword); // Reproduz som de equipamento
```

### **Com Sistema de UI**
```java
// Controlar visibilidade do inventário
InventoryManager inventory = new InventoryManager("barbarian", player);
inventory.setVisible(true);  // Mostrar inventário
inventory.setVisible(false); // Esconder inventário

// Navegar no inventário
inventory.moveUp();
inventory.moveDown();
inventory.moveLeft();
inventory.moveRight();

// Obter item selecionado
Item selectedItem = inventory.getSelectedItem();
if (selectedItem != null) {
    System.out.println("Item selecionado: " + selectedItem.getName());
}
```

---

## 🎯 **Casos de Uso Avançados**

### **Sistema de Loot Dinâmico**
```java
// Criar sistema de loot baseado em raridade
public Item generateRandomLoot() {
    Random random = new Random();
    ItemRarity rarity = ItemRarity.COMMON;
    
    int roll = random.nextInt(100);
    if (roll < 50) rarity = ItemRarity.COMMON;
    else if (roll < 75) rarity = ItemRarity.UNCOMMON;
    else if (roll < 90) rarity = ItemRarity.RARE;
    else if (roll < 98) rarity = ItemRarity.EPIC;
    else rarity = ItemRarity.LEGENDARY;
    
    // Criar item baseado na raridade
    return createItemByRarity(rarity);
}
```

### **Sistema de Upgrade de Itens**
```java
// Melhorar item existente
public Item upgradeItem(Item item, int upgradeLevel) {
    Item upgradedItem = item.copy();
    
    // Aumentar bônus baseado no nível
    int strengthBonus = upgradedItem.getStrengthFromEquip() + (upgradeLevel * 2);
    int armorBonus = upgradedItem.getArmorFromEquip() + (upgradeLevel * 1);
    
    upgradedItem.setStrengthFromEquip(strengthBonus);
    upgradedItem.setArmorFromEquip(armorBonus);
    
    // Atualizar nome
    upgradedItem.setName(item.getName() + " +" + upgradeLevel);
    
    return upgradedItem;
}
```

### **Sistema de Conjuntos**
```java
// Verificar se jogador tem conjunto completo
public boolean hasCompleteSet(Equipment equipment, String setName) {
    Item[] equippedItems = equipment.getAllEquippedItems();
    int setPieces = 0;
    
    for (Item item : equippedItems) {
        if (item != null && item.getName().contains(setName)) {
            setPieces++;
        }
    }
    
    return setPieces >= 4; // Conjunto completo
}
```

### **Sistema de Durabilidade**
```java
// Adicionar sistema de durabilidade
public class DurableItem extends Item {
    private int durability;
    private int maxDurability;
    
    public DurableItem(String itemId, String name, ItemType itemType, ItemRarity rarity, String description, int value, boolean stackable, int maxDurability) {
        super(itemId, name, itemType, rarity, description, value, stackable);
        this.maxDurability = maxDurability;
        this.durability = maxDurability;
    }
    
    public void reduceDurability(int amount) {
        durability = Math.max(0, durability - amount);
        if (durability <= 0) {
            // Item quebrado
            System.out.println("Item quebrado: " + getName());
        }
    }
    
    public boolean isBroken() {
        return durability <= 0;
    }
}
```

---

## 📋 **Checklist de Implementação**

### **✅ Configuração Básica**
- [ ] ItemFactory configurado
- [ ] Arquivo objects.json configurado
- [ ] Sistema de raridades implementado
- [ ] Sistema de tipos implementado

### **✅ Sistema de Inventário**
- [ ] InventoryManager inicializado
- [ ] Sistema de empilhamento funcionando
- [ ] Navegação no inventário funcionando
- [ ] Sistema de consumo implementado

### **✅ Sistema de Equipamento**
- [ ] Equipment inicializado
- [ ] Sistema de bônus funcionando
- [ ] Slots específicos configurados
- [ ] Sistema de desequipamento funcionando

### **✅ Sistema de Quest Items**
- [ ] QuestItem implementado
- [ ] MagicOrb implementado
- [ ] Integração com QuestManager
- [ ] Sistema de depósito funcionando

### **✅ Integração**
- [ ] Sistema de quest integrado
- [ ] Sistema de batalha integrado
- [ ] Sistema de áudio integrado
- [ ] Sistema de UI integrado

### **✅ Testes**
- [ ] Itens são criados corretamente
- [ ] Inventário funciona
- [ ] Equipamento funciona
- [ ] Bônus são aplicados
- [ ] Sistema de empilhamento funciona
- [ ] Quest items funcionam

### **✅ Funcionalidades Avançadas**
- [ ] Sistema de raridades
- [ ] Sistema de classes
- [ ] Sistema de bônus
- [ ] Sistema de empilhamento
- [ ] Sistema de quest items
- [ ] Sistema de orbes mágicas
- [ ] Sistema de loot dinâmico
- [ ] Sistema de upgrade

---

## 🚀 **Conclusão**

Este sistema de itens e poderes oferece:

- ✅ **Sistema Completo de Itens**: Criação, gerenciamento e uso de itens
- ✅ **Sistema de Inventário**: 20 slots com empilhamento e navegação
- ✅ **Sistema de Equipamento**: 4 slots com bônus automáticos
- ✅ **Sistema de Raridades**: 5 raridades com cores específicas
- ✅ **Sistema de Classes**: Restrições de classe para equipamentos
- ✅ **Sistema de Bônus**: Bônus automáticos ao equipar
- ✅ **Sistema de Quest Items**: Itens especiais para quests
- ✅ **Sistema de Orbes**: Orbes mágicas com poderes especiais
- ✅ **Integração Total**: Funciona com todos os sistemas do jogo

### **Recursos Implementados**
- 🎒 **Sistema Completo de Itens**: Criação, gerenciamento e uso
- 📦 **Sistema de Inventário**: 20 slots com empilhamento
- ⚔️ **Sistema de Equipamento**: 4 slots com bônus automáticos
- 💎 **Sistema de Raridades**: 5 raridades com cores
- 🎯 **Sistema de Classes**: Restrições de classe
- 🔮 **Sistema de Quest Items**: Itens especiais para quests
- 🌟 **Sistema de Orbes**: Orbes mágicas com poderes
- 🎮 **Integração Total**: Funciona com quests, batalha, áudio, UI

**O sistema está pronto para criar itens épicos e poderosos!** 🎒✨


