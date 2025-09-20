# 📚 Sistema de Quests - Documentação Completa da API

## 🎯 **Visão Geral**

Este documento especifica **todas as funcionalidades** disponíveis no sistema de quests do CronicasDeEldoriaRPG, incluindo métodos, classes, enums e exemplos práticos de uso. O sistema foi expandido com recursos avançados de diálogos condicionais, sistema de contratação de NPCs e integração completa com todos os sistemas do jogo.

---

## 🏗️ **Arquitetura do Sistema**

### **Classes Principais**
```
QuestManager (Singleton)     → Gerenciador principal
├── Quest                   → Quest individual
├── QuestObjective          → Objetivo de quest
├── QuestReward             → Recompensas
├── QuestState (Enum)       → Estados da quest
└── QuestObjectiveType (Enum) → Tipos de objetivos
```

### **Classes de Suporte**
```
QuestItem                   → Item especial de quest
├── MagicOrb               → Orbe mágica específica
TotemCentral               → Objeto interativo
SupremeMage                → Boss final
WarriorNpc                 → NPC contratável
```

### **Sistema de Diálogos Condicionais**
```
DialogManager               → Gerenciador de diálogos
├── Dialog                 → Diálogo individual
├── DialogCondition        → Condições para diálogos
└── DialogOption           → Opções de diálogo
```

### **Sistema de Contratação de NPCs**
```
WarriorNpc                 → NPC guerreiro contratável
CompanionManager           → Gerenciador de companheiros
PathfindingAI             → IA de pathfinding
```

---

## 🎮 **QuestManager - Gerenciador Principal**

### **🔧 Inicialização e Configuração**

#### **`getInstance()`**
```java
QuestManager questManager = QuestManager.getInstance();
```
- **Descrição**: Obtém a instância única (Singleton)
- **Retorno**: `QuestManager`
- **Uso**: Sempre use este método para acessar o QuestManager

#### **`initialize(GamePanel gamePanel)`**
```java
questManager.initialize(gamePanel);
```
- **Descrição**: Inicializa o QuestManager com referência ao GamePanel
- **Parâmetros**: `GamePanel gamePanel`
- **Uso**: Chamar uma vez no início do jogo

---

### **🎯 Gerenciamento de Quests**

#### **`startQuest(String questId)`**
```java
questManager.startQuest("main_orb_quest");
```
- **Descrição**: Inicia uma quest específica
- **Parâmetros**: `String questId` - ID da quest
- **Uso**: Iniciar quests manualmente ou via triggers

#### **`getQuest(String questId)`**
```java
Quest quest = questManager.getQuest("main_orb_quest");
if (quest != null) {
    System.out.println("Quest: " + quest.getTitle());
}
```
- **Descrição**: Obtém uma quest específica
- **Parâmetros**: `String questId`
- **Retorno**: `Quest` ou `null`
- **Uso**: Verificar estado de quests específicas

#### **`getActiveQuests()`**
```java
Map<String, Quest> activeQuests = questManager.getActiveQuests();
for (Quest quest : activeQuests.values()) {
    System.out.println("Quest ativa: " + quest.getTitle());
}
```
- **Descrição**: Obtém todas as quests ativas
- **Retorno**: `Map<String, Quest>`
- **Uso**: Listar todas as quests em andamento

#### **`getCompletedQuests()`**
```java
Map<String, Quest> completedQuests = questManager.getCompletedQuests();
for (Quest quest : completedQuests.values()) {
    System.out.println("Quest completada: " + quest.getTitle());
}
```
- **Descrição**: Obtém todas as quests completadas
- **Retorno**: `Map<String, Quest>`
- **Uso**: Verificar histórico de quests

#### **`updateQuestProgress()`**
```java
questManager.updateQuestProgress();
```
- **Descrição**: Atualiza o progresso de todas as quests ativas
- **Uso**: Chamar no loop principal do jogo

---

### **🗺️ Sistema de Triggers de Mapa**

#### **`onPlayerEnterMap(String mapName)`**
```java
questManager.onPlayerEnterMap("totem");
```
- **Descrição**: Chamado automaticamente quando jogador entra em um mapa
- **Parâmetros**: `String mapName` - Nome do mapa
- **Uso**: Sistema interno, chamado pelo GamePanel
- **Funcionalidade**: Atualiza objetivos REACH_LOCATION automaticamente

#### **`onPlayerTalkToNpc(String npcName)`**
```java
questManager.onPlayerTalkToNpc("Sábio Ancião");
```
- **Descrição**: Chamado quando jogador fala com um NPC específico
- **Parâmetros**: `String npcName` - Nome do NPC
- **Uso**: Sistema interno, chamado pelo Npc.interact()
- **Funcionalidade**: Atualiza objetivos TALK_TO_NPC e gerencia diálogos condicionais

#### **`onNpcKilled(String npcName)`**
```java
questManager.onNpcKilled("Orc Boss");
```
- **Descrição**: Chamado quando um NPC é morto
- **Parâmetros**: `String npcName` - Nome do NPC morto
- **Uso**: Sistema interno, chamado pelo sistema de batalha
- **Funcionalidade**: Atualiza objetivos KILL_NPC e spawna orbes após morte de bosses

#### **`onItemCollected(String itemId)`**
```java
questManager.onItemCollected("orb_earth");
```
- **Descrição**: Chamado quando um item é coletado
- **Parâmetros**: `String itemId` - ID do item coletado
- **Uso**: Sistema interno, chamado pelo sistema de inventário
- **Funcionalidade**: Atualiza objetivos COLLECT_ITEM

#### **`onItemDeposited(String itemId, String locationId)`**
```java
questManager.onItemDeposited("orb_earth", "totem");
```
- **Descrição**: Chamado quando um item é depositado em local específico
- **Parâmetros**: 
  - `String itemId` - ID do item depositado
  - `String locationId` - ID do local onde foi depositado
- **Uso**: Sistema interno, chamado pelo TotemCentral
- **Funcionalidade**: Atualiza objetivos DEPOSIT_ITEM

#### **`addMapTrigger(String mapName, String questId)`**
```java
questManager.addMapTrigger("cave", "cave_exploration_quest");
questManager.addMapTrigger("forest", "herb_collection_quest");
```
- **Descrição**: Adiciona trigger para iniciar quest ao entrar no mapa
- **Parâmetros**: 
  - `String mapName` - Nome do mapa
  - `String questId` - ID da quest
- **Uso**: Configurar quests automáticas

#### **`removeMapTrigger(String mapName)`**
```java
questManager.removeMapTrigger("cave");
```
- **Descrição**: Remove trigger de mapa
- **Parâmetros**: `String mapName`
- **Uso**: Desabilitar triggers dinamicamente

---

### **💎 Gerenciamento de Orbes**

#### **`onOrbCollected(MagicOrb orb)`**
```java
// Chamado automaticamente quando orbe é coletada
questManager.onOrbCollected(magicOrb);
```
- **Descrição**: Notifica que uma orbe foi coletada
- **Parâmetros**: `MagicOrb orb`
- **Uso**: Sistema interno, chamado pelo MagicOrb

#### **`onOrbDeposited(MagicOrb orb)`**
```java
// Chamado automaticamente quando orbe é depositada
questManager.onOrbDeposited(magicOrb);
```
- **Descrição**: Notifica que uma orbe foi depositada no totem
- **Parâmetros**: `MagicOrb orb`
- **Uso**: Sistema interno, chamado pelo TotemCentral

#### **`getCollectedOrbs()`**
```java
List<MagicOrb> collectedOrbs = questManager.getCollectedOrbs();
System.out.println("Orbes coletadas: " + collectedOrbs.size());
```
- **Descrição**: Obtém lista de orbes coletadas
- **Retorno**: `List<MagicOrb>`
- **Uso**: Verificar progresso de coleta

#### **`getDepositedOrbs()`**
```java
List<MagicOrb> depositedOrbs = questManager.getDepositedOrbs();
System.out.println("Orbes depositadas: " + depositedOrbs.size());
```
- **Descrição**: Obtém lista de orbes depositadas
- **Retorno**: `List<MagicOrb>`
- **Uso**: Verificar progresso de depósito

---

### **👹 Gerenciamento do Boss**

#### **`isBossSpawned()`**
```java
if (questManager.isBossSpawned()) {
    System.out.println("Boss já foi spawnado!");
}
```
- **Descrição**: Verifica se o boss final foi spawnado
- **Retorno**: `boolean`
- **Uso**: Verificar estado do boss

#### **`onBossDefeated()`**
```java
questManager.onBossDefeated();
```
- **Descrição**: Notifica que o boss foi derrotado
- **Uso**: Chamar quando boss é derrotado na batalha
- **Nota**: Método deprecated, use `onSupremeBossDefeated()` para o boss final

#### **`onSupremeBossDefeated()`**
```java
questManager.onSupremeBossDefeated();
```
- **Descrição**: Chamado quando o Supremo Boss é derrotado
- **Uso**: Sistema interno, chamado automaticamente
- **Funcionalidade**: Ativa estado de endgame, reproduz música de vitória e dá recompensas finais

#### **`createOrb1Quest()`**
```java
questManager.createOrb1Quest();
```
- **Descrição**: Cria e inicia a sub-quest para a primeira orbe
- **Uso**: Chamado automaticamente via `startQuest("orb_1")`
- **Funcionalidade**: Cria quest completa com objetivos de dungeon2, orc boss, coleta e depósito

#### **`createOrb2Quest()`**
```java
questManager.createOrb2Quest();
```
- **Descrição**: Cria e inicia a sub-quest para a segunda orbe
- **Uso**: Chamado automaticamente via `startQuest("orb_2")`
- **Funcionalidade**: Cria quest completa com objetivos de dungeon4, wolf boss, coleta e depósito

#### **`createOrb3Quest()`**
```java
questManager.createOrb3Quest();
```
- **Descrição**: Cria e inicia a sub-quest para a terceira orbe
- **Uso**: Chamado automaticamente via `startQuest("orb_3")`
- **Funcionalidade**: Cria quest completa com objetivos de dungeon3, frostborn boss, coleta e depósito

#### **`createOrb4Quest()`**
```java
questManager.createOrb4Quest();
```
- **Descrição**: Cria e inicia a sub-quest para a quarta orbe
- **Uso**: Chamado automaticamente via `startQuest("orb_4")`
- **Funcionalidade**: Cria quest completa com objetivos de dungeon1, skeleton boss, coleta e depósito

#### **`onQuestItemCollected(QuestItem item)`**
```java
questManager.onQuestItemCollected(magicOrb);
```
- **Descrição**: Chamado quando um item de quest é coletado
- **Parâmetros**: `QuestItem item` - Item de quest coletado
- **Uso**: Sistema interno, chamado pelo sistema de inventário
- **Funcionalidade**: Atualiza objetivos genéricos de coleta

---

### **🏛️ Gerenciamento do Totem**

#### **`setTotemCentral(TotemCentral totem)`**
```java
questManager.setTotemCentral(totemCentral);
```
- **Descrição**: Define referência ao TotemCentral
- **Parâmetros**: `TotemCentral totem`
- **Uso**: Sistema interno, chamado pelo ObjectManager

#### **`getTotemCentral()`**
```java
TotemCentral totem = questManager.getTotemCentral();
if (totem != null) {
    System.out.println("Totem encontrado!");
}
```
- **Descrição**: Obtém referência ao TotemCentral
- **Retorno**: `TotemCentral` ou `null`
- **Uso**: Verificar se totem existe

---

## 🎯 **Quest - Quest Individual**

### **🔧 Criação e Configuração**

#### **Construtor**
```java
Quest quest = new Quest(
    "dungeon_quest",           // ID único
    "Explorar a Caverna",      // Título
    "Explore a caverna e encontre o tesouro perdido." // Descrição
);
```

#### **`addObjective(QuestObjective objective)`**
```java
QuestObjective objective1 = new QuestObjective(
    "explore_rooms", 
    "Explorar 5 salas", 
    QuestObjectiveType.REACH_LOCATION
);
quest.addObjective(objective1);

QuestObjective objective2 = new QuestObjective(
    "find_treasure", 
    "Encontrar tesouro", 
    QuestObjectiveType.COLLECT_ITEM, 
    "treasure_chest"
);
quest.addObjective(objective2);
```
- **Descrição**: Adiciona objetivo à quest
- **Parâmetros**: `QuestObjective objective`
- **Uso**: Definir objetivos da quest

#### **`setReward(QuestReward reward)`**
```java
QuestReward reward = new QuestReward(
    500,                           // Moedas
    200,                           // Experiência
    new String[]{"magic_sword"},   // Itens
    "Parabéns! Você encontrou o tesouro!"
);
quest.setReward(reward);
```
- **Descrição**: Define recompensas da quest
- **Parâmetros**: `QuestReward reward`
- **Uso**: Configurar recompensas

---

### **📊 Verificação de Progresso**

#### **`isCompleted()`**
```java
if (quest.isCompleted()) {
    System.out.println("Quest completada!");
}
```
- **Descrição**: Verifica se todos os objetivos foram completados
- **Retorno**: `boolean`
- **Uso**: Verificar se quest pode ser finalizada

#### **`getProgressPercentage()`**
```java
int progress = quest.getProgressPercentage();
System.out.println("Progresso: " + progress + "%");
```
- **Descrição**: Obtém progresso em porcentagem
- **Retorno**: `int` (0-100)
- **Uso**: Mostrar progresso na UI

#### **`getQuestData(String key)`**
```java
String difficulty = (String) quest.getQuestData("difficulty");
int timeLimit = (Integer) quest.getQuestData("time_limit");
```
- **Descrição**: Obtém dados personalizados da quest
- **Parâmetros**: `String key` - Chave do dado
- **Retorno**: `Object` - Valor armazenado ou null
- **Uso**: Acessar dados customizados da quest

#### **`setQuestData(String key, Object value)`**
```java
quest.setQuestData("difficulty", "hard");
quest.setQuestData("time_limit", 3600);
quest.setQuestData("player_choice", "save_village");
```
- **Descrição**: Define dados personalizados da quest
- **Parâmetros**: 
  - `String key` - Chave do dado
  - `Object value` - Valor a ser armazenado
- **Uso**: Armazenar informações customizadas da quest

#### **`updateObjective(String objectiveId, boolean completed)`**
```java
quest.updateObjective("explore_rooms", true);
quest.updateObjective("find_treasure", false);
```
- **Descrição**: Atualiza estado de um objetivo
- **Parâmetros**: 
  - `String objectiveId` - ID do objetivo
  - `boolean completed` - Se foi completado
- **Uso**: Marcar objetivos como completos

#### **`getObjective(String objectiveId)`**
```java
QuestObjective objective = quest.getObjective("explore_rooms");
if (objective != null) {
    System.out.println("Objetivo: " + objective.getDescription());
}
```
- **Descrição**: Obtém objetivo específico
- **Parâmetros**: `String objectiveId`
- **Retorno**: `QuestObjective` ou `null`
- **Uso**: Verificar estado de objetivos específicos

---

### **📋 Getters e Setters**

#### **Informações Básicas**
```java
String id = quest.getId();                    // ID da quest
String title = quest.getTitle();              // Título
String description = quest.getDescription();  // Descrição
QuestState state = quest.getState();          // Estado atual
```

#### **Objetivos e Recompensas**
```java
List<QuestObjective> objectives = quest.getObjectives();  // Lista de objetivos
QuestReward reward = quest.getReward();                   // Recompensas
```

#### **Estado da Quest**
```java
quest.setState(QuestState.IN_PROGRESS);  // Alterar estado
```

#### **Dados Personalizados**
```java
quest.setQuestData("difficulty", "hard");
quest.setQuestData("time_limit", 3600);

String difficulty = (String) quest.getQuestData("difficulty");
int timeLimit = (Integer) quest.getQuestData("time_limit");
```

---

## 🎯 **QuestObjective - Objetivo de Quest**

### **🔧 Criação**

#### **Construtor Completo**
```java
QuestObjective objective = new QuestObjective(
    "collect_sword",                    // ID único
    "Coletar espada mágica",           // Descrição
    QuestObjectiveType.COLLECT_ITEM,   // Tipo
    "magic_sword"                      // ID do alvo
);
```

#### **Construtor Simplificado**
```java
QuestObjective objective = new QuestObjective(
    "reach_village",                   // ID único
    "Chegar à vila",                  // Descrição
    QuestObjectiveType.REACH_LOCATION  // Tipo (sem alvo)
);
```

---

### **📊 Verificação de Estado**

#### **`isCompleted()`**
```java
if (objective.isCompleted()) {
    System.out.println("Objetivo completado!");
}
```
- **Descrição**: Verifica se objetivo foi completado
- **Retorno**: `boolean`

#### **`setCompleted(boolean completed)`**
```java
objective.setCompleted(true);   // Marcar como completo
objective.setCompleted(false); // Marcar como incompleto
```
- **Descrição**: Define estado do objetivo
- **Parâmetros**: `boolean completed`

---

### **📋 Getters e Setters**

```java
String id = objective.getId();                    // ID do objetivo
String description = objective.getDescription();  // Descrição
QuestObjectiveType type = objective.getType();    // Tipo do objetivo
String targetId = objective.getTargetId();        // ID do alvo
objective.setTargetId("new_target");             // Alterar alvo
```

---

## 🏆 **QuestReward - Recompensas**

### **🔧 Criação**

#### **Construtor Completo**
```java
QuestReward reward = new QuestReward(
    1000,                                    // Moedas
    500,                                     // Experiência
    new String[]{"sword", "armor", "potion"}, // Itens
    "Parabéns! Você completou a quest!"      // Mensagem
);
```

#### **Construtor Simplificado**
```java
QuestReward reward = new QuestReward(1000, 500); // Apenas moedas e XP
```

#### **Construtor com Mensagem Personalizada**
```java
QuestReward reward = new QuestReward(
    2000, 800, 
    new String[]{"magic_sword", "healing_potion"}, 
    "Parabéns! Você completou a quest épica!"
);
```

---

### **📋 Getters**

```java
int money = reward.getMoney();           // Quantidade de moedas
int experience = reward.getExperience(); // Quantidade de experiência
String[] items = reward.getItemIds();    // IDs dos itens
String message = reward.getMessage();    // Mensagem de recompensa
reward.setMessage("Nova mensagem!");    // Alterar mensagem
```

---

## 📊 **Enums**

### **QuestState - Estados da Quest**
```java
QuestState.NOT_STARTED  // Quest não iniciada
QuestState.IN_PROGRESS  // Quest em andamento
QuestState.COMPLETED    // Quest completada
QuestState.FAILED       // Quest falhou
```

### **QuestObjectiveType - Tipos de Objetivos**
```java
QuestObjectiveType.COLLECT_ITEM    // Coletar item específico
QuestObjectiveType.KILL_NPC        // Derrotar NPC específico
QuestObjectiveType.TALK_TO_NPC     // Falar com NPC específico
QuestObjectiveType.REACH_LOCATION  // Chegar a localização
QuestObjectiveType.DEPOSIT_ITEM    // Depositar item em local específico
QuestObjectiveType.DELIVER_ITEM    // Entregar item para NPC
```

---

## 🎭 **Sistema de Diálogos Condicionais**

### **DialogCondition - Condições para Diálogos**

#### **Criação de Condições**
```java
DialogCondition condition = new DialogCondition(
    "quest_status",           // Tipo da condição
    "main_orb_quest",         // ID da quest
    QuestState.COMPLETED      // Status requerido
);
```

#### **Tipos de Condições Suportadas**
```java
// Verificar status específico de uma quest
DialogCondition questStatus = new DialogCondition(
    "quest_status", "orb_1", QuestState.IN_PROGRESS
);

// Verificar se quest foi completada
DialogCondition questCompleted = new DialogCondition(
    "quest_completed", "orb_1", null
);

// Verificar se quest não foi iniciada
DialogCondition questNotStarted = new DialogCondition(
    "quest_not_started", "orb_2", null
);

// Verificar se quest está em andamento
DialogCondition questInProgress = new DialogCondition(
    "quest_in_progress", "orb_3", null
);
```

#### **Verificação de Condições**
```java
DialogCondition condition = new DialogCondition(
    "quest_status", "main_orb_quest", QuestState.COMPLETED
);

QuestManager questManager = QuestManager.getInstance();
if (condition.isMet(questManager)) {
    System.out.println("Condição atendida!");
}
```

### **DialogManager - Diálogos Condicionais**

#### **`findAppropriateDialog(String npcName)`**
```java
int dialogId = dialogManager.findAppropriateDialog("Sábio Ancião");
if (dialogId != -1) {
    dialogManager.startDialog(dialogId);
}
```
- **Descrição**: Encontra diálogo apropriado baseado no status das quests
- **Parâmetros**: `String npcName` - Nome do NPC
- **Retorno**: `int` - ID do diálogo ou -1 se não encontrado
- **Uso**: Sistema interno para diálogos condicionais

#### **`startAppropriateDialog(String npcName)`**
```java
boolean started = dialogManager.startAppropriateDialog("Sábio Ancião");
if (started) {
    System.out.println("Diálogo condicional iniciado!");
}
```
- **Descrição**: Inicia diálogo apropriado baseado nas condições
- **Parâmetros**: `String npcName` - Nome do NPC
- **Retorno**: `boolean` - true se diálogo foi iniciado
- **Uso**: Chamado automaticamente pelo sistema de interação

#### **`startQuest(String questId)`**
```java
dialogManager.startQuest("orb_1");
```
- **Descrição**: Inicia uma quest específica via diálogo
- **Parâmetros**: `String questId` - ID da quest
- **Uso**: Chamado via ações de diálogo (`actionType: "start_quest"`)

### **Configuração de Diálogos Condicionais**

#### **dialogs.json - Estrutura com Condições**
```json
{
  "id": 32,
  "speakerName": "Sábio Ancião",
  "text": "Agora que você me encontrou, preciso que você recupere as 4 orbes mágicas...",
  "condition": {
    "type": "quest_completed",
    "questId": "go_library",
    "status": "COMPLETED"
  },
  "options": [
    {
      "text": "Entendi, vou ajudar!",
      "actionType": "start_quest",
      "actionData": "orb_1"
    }
  ]
}
```

#### **Tipos de Condições no JSON**
```json
// Quest completada
"condition": {
  "type": "quest_completed",
  "questId": "orb_1",
  "status": "COMPLETED"
}

// Quest em andamento
"condition": {
  "type": "quest_in_progress",
  "questId": "orb_2",
  "status": "IN_PROGRESS"
}

// Quest não iniciada
"condition": {
  "type": "quest_not_started",
  "questId": "orb_3",
  "status": "NOT_STARTED"
}
```

---

## 👥 **Sistema de Contratação de NPCs**

### **WarriorNpc - NPC Contratável**

#### **Criação de NPC Contratável**
```java
WarriorNpc warrior = new WarriorNpc(
    "Guerreiro Mercenário",    // Nome
    false,                     // Não é estático
    "",                        // Diálogo inicial
    1200, 800,                 // Posição X, Y
    "barbarian",               // Skin
    48,                        // Tamanho
    true,                      // Interativo
    false,                     // Sem auto-interação
    20                         // ID do diálogo
);
```

#### **Métodos de Contratação**
```java
// Verificar se está contratado
if (warrior.isHired()) {
    System.out.println("Guerreiro já está contratado!");
}

// Contratar guerreiro
warrior.hire();

// Dispensar guerreiro
warrior.fire();
```

#### **Integração com Sistema de Diálogos**
```java
// No diálogo de contratação
case "hire_warrior":
    if (player.getPlayerMoney().getMoney() >= 100) {
        player.getPlayerMoney().removeMoney(100);
        warrior.hire();
        gamePanel.getGameUI().addMessage(
            "Guerreiro contratado por 100 moedas!", null, 3000L);
    } else {
        gamePanel.getGameUI().addMessage(
            "Moedas insuficientes! Você precisa de 100 moedas.", null, 3000L);
    }
    break;
```

### **Configuração de NPCs Contratáveis**

#### **npcs.json - NPC Contratável**
```json
{
  "id": "hireable_warrior_001",
  "name": "Guerreiro Mercenário",
  "skin": "barbarian",
  "x": 1200,
  "y": 800,
  "isStatic": false,
  "interactive": true,
  "autoInteraction": false,
  "dialogId": 20,
  "hireable": true,
  "hireCost": 100,
  "companionType": "warrior",
  "contractDuration": 10
}
```

#### **character_config.json - Tipos de Companheiros**
```json
{
  "companionTypes": {
    "warrior": {
      "attackBonus": 15,
      "defenseBonus": 10,
      "specialBonus": 5,
      "description": "Guerreiro experiente com bônus de ataque"
    },
    "mage": {
      "attackBonus": 5,
      "defenseBonus": 5,
      "specialBonus": 20,
      "description": "Mago poderoso com habilidades especiais"
    },
    "archer": {
      "attackBonus": 12,
      "defenseBonus": 8,
      "specialBonus": 15,
      "description": "Arqueiro ágil com ataques à distância"
    }
  }
}
```

---

## 🎮 **Exemplos Práticos de Uso**

### **Exemplo 1: Quest de Exploração**
```java
// Criar quest de exploração
Quest explorationQuest = new Quest(
    "cave_exploration",
    "Explorar a Caverna Perdida",
    "Explore a caverna misteriosa e encontre o tesouro antigo."
);

// Adicionar objetivos
explorationQuest.addObjective(new QuestObjective(
    "explore_5_rooms", 
    "Explorar 5 salas da caverna", 
    QuestObjectiveType.REACH_LOCATION
));

explorationQuest.addObjective(new QuestObjective(
    "find_treasure", 
    "Encontrar tesouro perdido", 
    QuestObjectiveType.COLLECT_ITEM, 
    "ancient_treasure"
));

explorationQuest.addObjective(new QuestObjective(
    "defeat_guardian", 
    "Derrotar guardião do tesouro", 
    QuestObjectiveType.KILL_NPC, 
    "treasure_guardian"
));

// Definir recompensas
QuestReward reward = new QuestReward(
    2000, 
    800, 
    new String[]{"ancient_sword", "magic_ring"}, 
    "Você encontrou o tesouro perdido!"
);
explorationQuest.setReward(reward);

// Registrar quest no QuestManager
QuestManager questManager = QuestManager.getInstance();
questManager.getActiveQuests().put("cave_exploration", explorationQuest);

// Configurar trigger de mapa
questManager.addMapTrigger("cave", "cave_exploration");

// Iniciar quest
questManager.startQuest("cave_exploration");
```

### **Exemplo 2: Quest de Coleta**
```java
// Criar quest de coleta
Quest collectionQuest = new Quest(
    "herb_collection",
    "Coleta de Ervas Medicinais",
    "Colete ervas medicinais para o herbalista da vila."
);

// Adicionar objetivos
collectionQuest.addObjective(new QuestObjective(
    "collect_healing_herbs", 
    "Coletar 10 ervas de cura", 
    QuestObjectiveType.COLLECT_ITEM, 
    "healing_herb"
));

collectionQuest.addObjective(new QuestObjective(
    "collect_mana_herbs", 
    "Coletar 5 ervas de mana", 
    QuestObjectiveType.COLLECT_ITEM, 
    "mana_herb"
));

collectionQuest.addObjective(new QuestObjective(
    "deliver_herbs", 
    "Entregar ervas ao herbalista", 
    QuestObjectiveType.DELIVER_ITEM, 
    "herbalist_npc"
));

// Definir recompensas
QuestReward reward = new QuestReward(
    500, 
    300, 
    new String[]{"healing_potion", "mana_potion"}, 
    "Obrigado pelas ervas! Aqui estão suas recompensas."
);
collectionQuest.setReward(reward);

// Configurar trigger de mapa
questManager.addMapTrigger("forest", "herb_collection");
```

### **Exemplo 3: Quest de Resgate**
```java
// Criar quest de resgate
Quest rescueQuest = new Quest(
    "princess_rescue",
    "Resgate da Princesa",
    "A princesa foi sequestrada! Resgate-a do castelo do dragão."
);

// Adicionar objetivos
rescueQuest.addObjective(new QuestObjective(
    "reach_castle", 
    "Chegar ao castelo do dragão", 
    QuestObjectiveType.REACH_LOCATION
));

rescueQuest.addObjective(new QuestObjective(
    "defeat_dragon", 
    "Derrotar o dragão", 
    QuestObjectiveType.KILL_NPC, 
    "dragon_boss"
));

rescueQuest.addObjective(new QuestObjective(
    "talk_to_princess", 
    "Falar com a princesa", 
    QuestObjectiveType.TALK_TO_NPC, 
    "princess_npc"
));

rescueQuest.addObjective(new QuestObjective(
    "escape_castle", 
    "Escapar do castelo", 
    QuestObjectiveType.REACH_LOCATION
));

// Definir recompensas
QuestReward reward = new QuestReward(
    5000, 
    1500, 
    new String[]{"dragon_scale_armor", "royal_crown"}, 
    "Você salvou a princesa! O reino está em dívida com você!"
);
rescueQuest.setReward(reward);

// Configurar trigger de mapa
questManager.addMapTrigger("dragon_castle", "princess_rescue");
```

### **Exemplo 4: Quest com Objetivos Condicionais**
```java
// Criar quest com objetivos condicionais
Quest conditionalQuest = new Quest(
    "merchant_quest",
    "Missão do Comerciante",
    "Ajude o comerciante com suas tarefas."
);

// Adicionar objetivos
conditionalQuest.addObjective(new QuestObjective(
    "talk_to_merchant", 
    "Falar com o comerciante", 
    QuestObjectiveType.TALK_TO_NPC, 
    "merchant_npc"
));

// Objetivo condicional baseado no nível do jogador
if (gamePanel.getPlayer().getLevel() >= 10) {
    conditionalQuest.addObjective(new QuestObjective(
        "deliver_rare_items", 
        "Entregar itens raros", 
        QuestObjectiveType.DELIVER_ITEM, 
        "rare_items"
    ));
} else {
    conditionalQuest.addObjective(new QuestObjective(
        "deliver_common_items", 
        "Entregar itens comuns", 
        QuestObjectiveType.DELIVER_ITEM, 
        "common_items"
    ));
}

// Definir recompensas baseadas no nível
int baseReward = gamePanel.getPlayer().getLevel() * 100;
QuestReward reward = new QuestReward(
    baseReward, 
    baseReward / 2, 
    new String[]{"merchant_token"}, 
    "Obrigado pela ajuda!"
);
conditionalQuest.setReward(reward);
```

### **Exemplo 5: Quest Principal das Orbes (Sistema Atual)**
```java
// O sistema atual cria automaticamente as quests das orbes
QuestManager questManager = QuestManager.getInstance();

// Quest inicial é criada automaticamente na inicialização
Quest initialQuest = questManager.getQuest("go_library");
if (initialQuest != null) {
    System.out.println("Quest inicial: " + initialQuest.getTitle());
}

// Sub-quests são criadas dinamicamente via diálogos
questManager.startQuest("orb_1"); // Cria automaticamente a quest da primeira orbe
questManager.startQuest("orb_2"); // Cria automaticamente a quest da segunda orbe
questManager.startQuest("orb_3"); // Cria automaticamente a quest da terceira orbe
questManager.startQuest("orb_4"); // Cria automaticamente a quest da quarta orbe

// Quest final do boss é criada automaticamente quando todas as orbes são depositadas
```

### **Exemplo 6: Sistema de Diálogos Condicionais**
```java
// Criar diálogo condicional baseado no status da quest
DialogCondition condition = new DialogCondition(
    "quest_completed", 
    "orb_1", 
    null
);

// Verificar se condição é atendida
QuestManager questManager = QuestManager.getInstance();
if (condition.isMet(questManager)) {
    // Mostrar diálogo de próxima etapa
    dialogManager.startDialog(33);
} else {
    // Mostrar diálogo de instruções
    dialogManager.startDialog(32);
}

// Diálogo com ação de iniciar quest
Dialog questDialog = new Dialog(
    32, 
    "Sábio Ancião", 
    "Agora que você me encontrou, preciso que você recupere as 4 orbes mágicas..."
);

questDialog.addOption(new DialogOption(
    "Entendi, vou ajudar!",
    "start_quest",
    "orb_1"
));
```

### **Exemplo 7: Sistema de Contratação de NPCs**
```java
// Criar NPC contratável
WarriorNpc mercenary = new WarriorNpc(
    "Guerreiro Mercenário",
    false, "", 1200, 800, "barbarian", 48, true, false, 20
);

// Verificar se jogador tem dinheiro suficiente
if (player.getPlayerMoney().getMoney() >= 100) {
    // Contratar guerreiro
    player.getPlayerMoney().removeMoney(100);
    mercenary.hire();
    
    // Adicionar como companheiro
    if (gamePanel.getCompanionManager() != null) {
        gamePanel.getCompanionManager().addCompanion(mercenary);
    }
    
    gamePanel.getGameUI().addMessage(
        "Guerreiro contratado por 100 moedas!", null, 3000L);
} else {
    gamePanel.getGameUI().addMessage(
        "Moedas insuficientes! Você precisa de 100 moedas.", null, 3000L);
}
```

### **Exemplo 8: Quest com Dados Personalizados**
```java
// Criar quest com dados customizados
Quest customQuest = new Quest(
    "timed_challenge",
    "Desafio Contra o Tempo",
    "Complete a missão antes do tempo acabar!"
);

// Definir dados personalizados
customQuest.setQuestData("time_limit", 300); // 5 minutos
customQuest.setQuestData("start_time", System.currentTimeMillis());
customQuest.setQuestData("difficulty", "hard");
customQuest.setQuestData("player_choice", "none");

// Verificar timeout durante o jogo
long startTime = (Long) customQuest.getQuestData("start_time");
long timeLimit = (Integer) customQuest.getQuestData("time_limit") * 1000;
if (System.currentTimeMillis() - startTime > timeLimit) {
    customQuest.setState(QuestState.FAILED);
    gamePanel.getGameUI().addMessage(
        "Tempo esgotado! Quest falhou!", null, 5000L);
}

// Atualizar escolha do jogador
customQuest.setQuestData("player_choice", "save_village");

// Criar objetivos baseados na escolha
String choice = (String) customQuest.getQuestData("player_choice");
if (choice.equals("save_village")) {
    customQuest.addObjective(new QuestObjective(
        "save_village", 
        "Salvar a vila dos bandidos", 
        QuestObjectiveType.REACH_LOCATION
    ));
} else if (choice.equals("save_princess")) {
    customQuest.addObjective(new QuestObjective(
        "save_princess", 
        "Resgatar a princesa", 
        QuestObjectiveType.REACH_LOCATION
    ));
}
```

---

## 🔧 **Integração com Outros Sistemas**

### **Com Sistema de Diálogos Condicionais**
```java
// No DialogManager, verificar condições antes de iniciar diálogo
public boolean startDialog(int dialogId) {
    Dialog dialog = dialogs.get(dialogId);
    if (dialog == null) return false;
    
    // Verificar se a condição do diálogo é atendida
    QuestManager questManager = QuestManager.getInstance();
    if (!dialog.isConditionMet(questManager)) {
        return false;
    }
    
    // Iniciar diálogo
    this.currentDialog = dialog;
    this.isDialogActive = true;
    return true;
}

// Ações de diálogo para iniciar quests
case "start_quest":
    QuestManager.getInstance().startQuest(actionData);
    break;
```

### **Com Sistema de Contratação de NPCs**
```java
// Verificar se NPC é contratável
if (npc instanceof WarriorNpc) {
    WarriorNpc warrior = (WarriorNpc) npc;
    if (!warrior.isHired()) {
        // Mostrar opções de contratação
        showHireOptions(warrior);
    } else {
        // Mostrar status do contrato
        showContractStatus(warrior);
    }
}

// Integração com sistema de dinheiro
case "hire_warrior":
    WarriorNpc warrior = (WarriorNpc) currentNpc;
    int cost = warrior.getHireCost();
    if (player.getPlayerMoney().getMoney() >= cost) {
        player.getPlayerMoney().removeMoney(cost);
        warrior.hire();
        // Adicionar como companheiro ativo
    }
    break;
```

### **Com Sistema de Inventário**
```java
// Verificar se jogador tem itens necessários
if (player.getInventoryManager().hasItem("magic_sword")) {
    quest.updateObjective("collect_sword", true);
}

// Dar itens de recompensa
for (String itemId : reward.getItemIds()) {
    Item rewardItem = ItemFactory.createItem(itemId);
    player.getInventoryManager().addItem(rewardItem);
}
```

### **Com Sistema de Batalha**
```java
// Quando NPC é derrotado
if (npc.getId().equals("dragon_boss")) {
    QuestManager.getInstance().getQuest("princess_rescue")
        .updateObjective("defeat_dragon", true);
}

// Spawnar orbes após morte de bosses
if (npcName.equals("Orc Boss")) {
    QuestManager.getInstance().onNpcKilled("Orc Boss");
    // Sistema automaticamente spawna orbe de terra
}
```

### **Com Sistema de Teleportes**
```java
// Quando jogador chega em localização
if (currentMap.equals("dragon_castle")) {
    QuestManager.getInstance().getQuest("princess_rescue")
        .updateObjective("reach_castle", true);
}

// Sistema automático de triggers de mapa
QuestManager.getInstance().onPlayerEnterMap("dungeon2");
// Automaticamente atualiza objetivo "reach_dungeon2"
```

### **Com Sistema de Áudio**
```java
// Reproduzir música de vitória quando boss final é derrotado
public void onSupremeBossDefeated() {
    if (gamePanel.getAudioManager() != null) {
        gamePanel.getAudioManager().changeContext(AudioContext.VICTORY);
    }
}
```

### **Com Sistema de Estados do Jogo**
```java
// Ativar estado de endgame quando boss final é derrotado
public void onSupremeBossDefeated() {
    gamePanel.gameState = gamePanel.endgameState;
    // Dar recompensas finais
    giveQuestRewards(finalBossQuest);
}
```

---

## 🎯 **Casos de Uso Avançados**

### **Quest com Múltiplas Fases**
```java
// Fase 1: Coleta
Quest phase1 = new Quest("phase1", "Coleta de Materiais", "...");
phase1.addObjective(new QuestObjective("collect_materials", "Coletar materiais", QuestObjectiveType.COLLECT_ITEM));

// Fase 2: Criação
Quest phase2 = new Quest("phase2", "Criação do Item", "...");
phase2.addObjective(new QuestObjective("create_item", "Criar item especial", QuestObjectiveType.COLLECT_ITEM));

// Fase 3: Entrega
Quest phase3 = new Quest("phase3", "Entrega Final", "...");
phase3.addObjective(new QuestObjective("deliver_item", "Entregar item", QuestObjectiveType.DELIVER_ITEM));
```

### **Quest com Timeout**
```java
Quest timedQuest = new Quest("timed_quest", "Missão Urgente", "...");
timedQuest.setQuestData("time_limit", 300); // 5 minutos
timedQuest.setQuestData("start_time", System.currentTimeMillis());

// Verificar timeout
long startTime = (Long) timedQuest.getQuestData("start_time");
long timeLimit = (Integer) timedQuest.getQuestData("time_limit") * 1000;
if (System.currentTimeMillis() - startTime > timeLimit) {
    timedQuest.setState(QuestState.FAILED);
}
```

### **Quest com Escolhas**
```java
Quest choiceQuest = new Quest("choice_quest", "Escolha Difícil", "...");
choiceQuest.setQuestData("player_choice", "none");

// Atualizar baseado na escolha
String choice = (String) choiceQuest.getQuestData("player_choice");
if (choice.equals("save_village")) {
    choiceQuest.addObjective(new QuestObjective("save_village", "Salvar a vila", QuestObjectiveType.REACH_LOCATION));
} else if (choice.equals("save_princess")) {
    choiceQuest.addObjective(new QuestObjective("save_princess", "Salvar a princesa", QuestObjectiveType.REACH_LOCATION));
}
```

### **Quest com Diálogos Condicionais**
```java
// Criar diálogo que só aparece se quest específica foi completada
DialogCondition condition = new DialogCondition(
    "quest_completed", 
    "orb_1", 
    null
);

Dialog conditionalDialog = new Dialog(
    33, 
    "Sábio Ancião", 
    "Excelente! Você recuperou a primeira orbe. Agora vá para a segunda dungeon..."
);
conditionalDialog.setCondition(condition);

// O diálogo só será exibido se a quest "orb_1" estiver completada
```

### **Quest com Sistema de Contratação**
```java
// Quest que requer contratar um guerreiro
Quest escortQuest = new Quest(
    "escort_merchant",
    "Escolta do Comerciante",
    "Contrate um guerreiro para escoltar o comerciante através da floresta perigosa."
);

escortQuest.addObjective(new QuestObjective(
    "hire_warrior", 
    "Contratar um guerreiro", 
    QuestObjectiveType.TALK_TO_NPC, 
    "hireable_warrior"
));

escortQuest.addObjective(new QuestObjective(
    "escort_merchant", 
    "Escoltar comerciante até a cidade", 
    QuestObjectiveType.REACH_LOCATION, 
    "city"
));

// Verificar se guerreiro foi contratado
WarriorNpc warrior = findHiredWarrior();
if (warrior != null && warrior.isHired()) {
    escortQuest.updateObjective("hire_warrior", true);
}
```

### **Quest com Sistema de Orbes (Sistema Atual)**
```java
// O sistema atual implementa automaticamente:
// 1. Quest inicial "go_library" - encontrar o Sábio Ancião
// 2. Quest principal "main_orb_quest" - recuperar as 4 orbes
// 3. Sub-quests "orb_1", "orb_2", "orb_3", "orb_4" - cada orbe individual
// 4. Quest final "final_boss" - derrotar o Mago Supremo

// Cada sub-quest tem objetivos específicos:
// - Chegar à dungeon correspondente (REACH_LOCATION)
// - Derrotar o boss da dungeon (KILL_NPC)
// - Coletar a orbe (COLLECT_ITEM)
// - Depositar no totem (DEPOSIT_ITEM)
// - Falar com o Sábio Ancião (TALK_TO_NPC)

// O sistema gerencia automaticamente:
// - Spawn de orbes após morte de bosses
// - Atualização de objetivos via eventos
// - Criação da quest final quando todas as orbes são depositadas
// - Spawn do Mago Supremo
// - Ativação do estado de endgame
```

---

## 📋 **Checklist de Implementação**

### **✅ Configuração Básica**
- [ ] QuestManager inicializado
- [ ] Quest criada com objetivos
- [ ] Recompensas definidas
- [ ] Trigger de mapa configurado
- [ ] Sistema de diálogos condicionais configurado
- [ ] NPCs contratáveis configurados

### **✅ Integração**
- [ ] Sistema de diálogos condicionais integrado
- [ ] Sistema de inventário integrado
- [ ] Sistema de batalha integrado
- [ ] Sistema de teleportes integrado
- [ ] Sistema de contratação de NPCs integrado
- [ ] Sistema de áudio integrado
- [ ] Sistema de estados do jogo integrado

### **✅ Testes**
- [ ] Quest inicia corretamente
- [ ] Objetivos são atualizados automaticamente
- [ ] Recompensas são dadas
- [ ] Quest é completada
- [ ] Diálogos condicionais funcionam
- [ ] Sistema de contratação funciona
- [ ] Sistema de orbes funciona completamente
- [ ] Boss final é spawnado corretamente
- [ ] Estado de endgame é ativado

### **✅ Funcionalidades Avançadas**
- [ ] Sistema de dados personalizados da quest
- [ ] Sistema de timeout de quests
- [ ] Sistema de escolhas do jogador
- [ ] Sistema de diálogos condicionais
- [ ] Sistema de contratação de NPCs
- [ ] Sistema automático de spawn de orbes
- [ ] Sistema automático de triggers de mapa
- [ ] Sistema de música de vitória
- [ ] Sistema de estados de endgame

---

## 🚀 **Conclusão**

Este sistema de quests oferece:

- ✅ **Flexibilidade Total**: Suporte a todos os tipos de objetivos e funcionalidades avançadas
- ✅ **Integração Perfeita**: Funciona com todos os sistemas existentes (diálogos, inventário, batalha, áudio, estados)
- ✅ **Facilidade de Uso**: API simples e intuitiva com criação automática de quests
- ✅ **Extensibilidade**: Fácil adicionar novos tipos de quests e funcionalidades
- ✅ **Manutenibilidade**: Código organizado e documentado
- ✅ **Sistema Automático**: Criação dinâmica de quests, spawn automático de orbes e bosses
- ✅ **Diálogos Condicionais**: Sistema avançado de diálogos baseados no status das quests
- ✅ **Contratação de NPCs**: Sistema completo de contratação e gerenciamento de companheiros
- ✅ **Dados Personalizados**: Suporte a dados customizados e escolhas do jogador
- ✅ **Sistema de Orbes**: Implementação completa da campanha principal com 4 orbes e boss final
- ✅ **Estados de Jogo**: Integração com sistema de estados incluindo endgame
- ✅ **Áudio Dinâmico**: Música de vitória e mudanças de contexto baseadas em eventos

**O sistema está pronto para criar qualquer tipo de quest que você imaginar, desde quests simples até campanhas épicas completas!** 🎮✨

### **Recursos Implementados**
- 🎯 **Campanha Principal Completa**: Sistema de 4 orbes + boss final
- 🎭 **Diálogos Condicionais**: Diálogos que mudam baseados no progresso das quests
- 👥 **Contratação de NPCs**: Sistema de contratação de guerreiros e companheiros
- ⏰ **Quests com Timeout**: Sistema de tempo limite para quests
- 🎲 **Escolhas do Jogador**: Sistema de decisões que afetam o progresso
- 📊 **Dados Personalizados**: Armazenamento de informações customizadas nas quests
- 🎵 **Áudio Dinâmico**: Música e efeitos sonoros baseados em eventos
- 🏆 **Sistema de Recompensas**: Moedas, experiência e itens automáticos
- 🗺️ **Triggers Automáticos**: Sistema automático de atualização de objetivos
- 🎮 **Estados de Jogo**: Integração completa com sistema de estados
