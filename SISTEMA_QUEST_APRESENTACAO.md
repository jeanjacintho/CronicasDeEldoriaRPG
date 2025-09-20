# 📚 Sistema de Quest - Apresentação Didática

## 📖 **O que é este sistema?**

Imagine que você está criando uma **história interativa** onde o jogador precisa completar missões para progredir na aventura. O sistema de Quest é exatamente isso! Ele permite criar uma narrativa estruturada com objetivos claros, recompensas e progressão lógica.

---

## 🎮 **Conceitos Básicos**

### **Quest - Missão Principal**
- **O que é?** Uma missão completa com objetivos específicos
- **Como funciona?** Tem título, descrição, objetivos e recompensas
- **Exemplo:** "As 4 Orbes de Eldoria" - coletar 4 orbes mágicas

### **QuestObjective - Objetivo Individual**
- **O que é?** Uma tarefa específica dentro de uma quest
- **Como funciona?** Cada objetivo tem um tipo e um alvo
- **Exemplo:** "Derrotar o Orc Boss" ou "Coletar a Orbe de Fogo"

### **QuestState - Estado da Quest**
- **O que é?** O status atual da quest
- **Estados possíveis:**
  - `NOT_STARTED` - Quest não iniciada
  - `IN_PROGRESS` - Quest em andamento
  - `COMPLETED` - Quest completada
  - `FAILED` - Quest falhou

---

## 🏗️ **Como o Sistema Funciona**

### **1. Criação de Quest**
```java
// Criar uma nova quest
Quest questPrincipal = new Quest(
    "main_orb_quest",                    // ID único
    "As 4 Orbes de Eldoria",            // Título
    "Recupere as 4 orbes mágicas roubadas pelo Mago Supremo" // Descrição
);

// Adicionar objetivos
questPrincipal.addObjective(new QuestObjective(
    "reach_dungeon2",                   // ID do objetivo
    "Chegar à Dungeon 2",              // Descrição
    QuestObjectiveType.REACH_LOCATION,  // Tipo
    "dungeon2"                          // Alvo
));

questPrincipal.addObjective(new QuestObjective(
    "kill_orc_boss",                     // ID do objetivo
    "Derrotar o Orc Boss",             // Descrição
    QuestObjectiveType.KILL_NPC,        // Tipo
    "orcboss"                           // Alvo
));
```

**O que acontece aqui?**
- Criamos uma quest com informações básicas
- Adicionamos objetivos específicos
- Cada objetivo tem um tipo e um alvo

### **2. Gerenciamento de Quest**
```java
// Obter o gerenciador de quests (Singleton)
QuestManager questManager = QuestManager.getInstance();

// Iniciar uma quest
questManager.startQuest("main_orb_quest");

// Verificar progresso
Quest quest = questManager.getActiveQuest("main_orb_quest");
int progresso = quest.getProgressPercentage(); // 0-100%

// Completar um objetivo
questManager.updateObjective("main_orb_quest", "kill_orc_boss", true);
```

**O que acontece aqui?**
- O QuestManager controla todas as quests
- Podemos iniciar, atualizar e completar quests
- O sistema calcula automaticamente o progresso

### **3. Tipos de Objetivos**
```java
// Diferentes tipos de objetivos que uma quest pode ter
QuestObjectiveType.COLLECT_ITEM;    // Coletar item específico
QuestObjectiveType.KILL_NPC;       // Derrotar NPC específico
QuestObjectiveType.TALK_TO_NPC;    // Falar com NPC específico
QuestObjectiveType.REACH_LOCATION; // Chegar a localização
QuestObjectiveType.DEPOSIT_ITEM;    // Depositar item em local
QuestObjectiveType.DELIVER_ITEM;    // Entregar item para NPC
```

---

## 🎯 **Exemplo Prático: Quest das Orbes**

### **Cenário: História Principal do Jogo**

```
🏠 Casa do Jogador
    ↓ (Quest: "Rumo à Biblioteca")
📚 Biblioteca - Sábio Ancião
    ↓ (Quest: "As 4 Orbes de Eldoria")
🗡️ Dungeon 2 - Orc Boss → 🔥 Orbe de Fogo
    ↓
🗡️ Dungeon 4 - Wolf Boss → 💧 Orbe de Água
    ↓
🗡️ Dungeon 3 - Frost Boss → 🌍 Orbe de Terra
    ↓
🗡️ Dungeon 1 - Skeleton Boss → 💨 Orbe de Ar
    ↓
🏛️ Totem Central (depositar todas as orbes)
    ↓
👹 Mago Supremo (boss final)
```

### **Implementação da Quest**
```java
public class ExemploQuestOrbes {
    
    public void criarQuestPrincipal() {
        // 1. Quest inicial - ir à biblioteca
        Quest questBiblioteca = new Quest(
            "go_library",
            "Rumo à Biblioteca",
            "Encontre o Sábio Ancião na biblioteca da cidade"
        );
        
        questBiblioteca.addObjective(new QuestObjective(
            "reach_library",
            "Chegar à biblioteca",
            QuestObjectiveType.REACH_LOCATION,
            "city_library_2f"
        ));
        
        questBiblioteca.addObjective(new QuestObjective(
            "talk_sage",
            "Falar com o Sábio Ancião",
            QuestObjectiveType.TALK_TO_NPC,
            "smart_old_man"
        ));
        
        // 2. Quest principal - coletar orbes
        Quest questOrbes = new Quest(
            "main_orb_quest",
            "As 4 Orbes de Eldoria",
            "Recupere as 4 orbes mágicas roubadas pelo Mago Supremo"
        );
        
        // Objetivos para cada orbe
        adicionarObjetivosOrbe(questOrbes, "orb_1", "dungeon2", "orcboss");
        adicionarObjetivosOrbe(questOrbes, "orb_2", "dungeon4", "wolfboss");
        adicionarObjetivosOrbe(questOrbes, "orb_3", "dungeon3", "frostbornboss");
        adicionarObjetivosOrbe(questOrbes, "orb_4", "dungeon1", "skeletonboss");
        
        // Objetivo final - depositar no totem
        questOrbes.addObjective(new QuestObjective(
            "deposit_all_orbs",
            "Depositar todas as orbes no Totem Central",
            QuestObjectiveType.DEPOSIT_ITEM,
            "totem_central"
        ));
        
        // 3. Quest final - derrotar boss
        Quest questFinal = new Quest(
            "final_boss",
            "Queda do Mago Supremo",
            "Derrote o Mago Supremo e restaure o equilíbrio"
        );
        
        questFinal.addObjective(new QuestObjective(
            "kill_supreme_mage",
            "Derrotar o Mago Supremo",
            QuestObjectiveType.KILL_NPC,
            "supreme_mage"
        ));
    }
    
    private void adicionarObjetivosOrbe(Quest quest, String orbId, String dungeon, String boss) {
        // Chegar à dungeon
        quest.addObjective(new QuestObjective(
            "reach_" + dungeon,
            "Chegar à " + dungeon,
            QuestObjectiveType.REACH_LOCATION,
            dungeon
        ));
        
        // Derrotar o boss
        quest.addObjective(new QuestObjective(
            "kill_" + boss,
            "Derrotar o " + boss,
            QuestObjectiveType.KILL_NPC,
            boss
        ));
        
        // Coletar a orbe
        quest.addObjective(new QuestObjective(
            "collect_" + orbId,
            "Coletar a " + orbId,
            QuestObjectiveType.COLLECT_ITEM,
            orbId
        ));
        
        // Depositar no totem
        quest.addObjective(new QuestObjective(
            "deposit_" + orbId,
            "Depositar " + orbId + " no Totem Central",
            QuestObjectiveType.DEPOSIT_ITEM,
            "totem_central"
        ));
    }
}
```

---

## 🔄 **Sistema de Eventos Automáticos**

### **Detecção Automática de Progresso**
```java
public class SistemaEventosQuest {
    
    // Quando jogador entra em um mapa
    public void onPlayerEnterMap(String mapName) {
        QuestManager questManager = QuestManager.getInstance();
        
        // Verificar se há objetivo de chegar a este mapa
        questManager.updateObjective("main_orb_quest", "reach_" + mapName, true);
    }
    
    // Quando jogador derrota um NPC
    public void onNpcKilled(String npcId) {
        QuestManager questManager = QuestManager.getInstance();
        
        // Verificar se há objetivo de derrotar este NPC
        questManager.updateObjective("main_orb_quest", "kill_" + npcId, true);
        
        // Se for um boss, spawnar a orbe
        if (npcId.equals("orcboss")) {
            spawnOrb("fire", getBossPosition());
        }
    }
    
    // Quando jogador coleta um item
    public void onItemCollected(Item item) {
        if (item instanceof MagicOrb) {
            MagicOrb orb = (MagicOrb) item;
            QuestManager.getInstance().onOrbCollected(orb);
        }
    }
    
    // Quando jogador deposita no totem
    public void onOrbDeposited(MagicOrb orb) {
        QuestManager questManager = QuestManager.getInstance();
        
        // Atualizar objetivo de depósito
        questManager.updateObjective("main_orb_quest", "deposit_" + orb.getId(), true);
        
        // Verificar se todas as orbes foram depositadas
        if (questManager.getDepositedOrbs().size() == 4) {
            spawnSupremeMage();
            questManager.startQuest("final_boss");
        }
    }
}
```

---

## 🎁 **Sistema de Recompensas**

### **Recompensas Automáticas**
```java
public class SistemaRecompensas {
    
    public void darRecompensasQuest(Quest quest) {
        if (quest.getId().equals("go_library")) {
            // Recompensa: 100 moedas para comprar itens
            player.addCoins(100);
            showMessage("Você recebeu 100 moedas!");
        }
        
        if (quest.getId().equals("main_orb_quest")) {
            // Recompensa: Acesso ao boss final
            unlockSupremeMage();
            showMessage("O Mago Supremo foi despertado!");
        }
        
        if (quest.getId().equals("final_boss")) {
            // Recompensa: Final do jogo
            showEnding();
            unlockNewGamePlus();
        }
    }
}
```

---

## 🎨 **Interface do Usuário**

### **Exibição de Quest**
```java
public class QuestUI {
    
    public void desenharQuestAtiva(Graphics2D g, Quest quest) {
        // Título da quest
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(quest.getTitle(), 20, 30);
        
        // Descrição
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString(quest.getDescription(), 20, 50);
        
        // Objetivos
        int y = 70;
        for (QuestObjective objetivo : quest.getObjectives()) {
            String status = objetivo.isCompleted() ? "✓" : "○";
            String texto = status + " " + objetivo.getDescription();
            
            g.setColor(objetivo.isCompleted() ? Color.GREEN : Color.YELLOW);
            g.drawString(texto, 20, y);
            y += 20;
        }
        
        // Barra de progresso
        int progresso = quest.getProgressPercentage();
        g.setColor(Color.GRAY);
        g.fillRect(20, y + 10, 200, 10);
        g.setColor(Color.BLUE);
        g.fillRect(20, y + 10, (200 * progresso) / 100, 10);
        
        g.setColor(Color.WHITE);
        g.drawString("Progresso: " + progresso + "%", 20, y + 35);
    }
}
```

---

## 🔧 **Configuração e Uso**

### **Inicialização do Sistema**
```java
public class InicializacaoQuest {
    
    public void inicializarSistemaQuest(GamePanel gamePanel) {
        // 1. Obter o QuestManager
        QuestManager questManager = QuestManager.getInstance();
        
        // 2. Inicializar com referência ao GamePanel
        questManager.initialize(gamePanel);
        
        // 3. Criar e configurar quests
        criarQuestsPrincipais();
        
        // 4. Iniciar quest inicial
        questManager.startQuest("go_library");
        
        // 5. Configurar triggers de mapa
        questManager.setMapQuestTrigger("city_library_2f", "go_library");
        questManager.setMapQuestTrigger("dungeon2", "main_orb_quest");
    }
    
    private void criarQuestsPrincipais() {
        // Implementação das quests principais
        // (código mostrado anteriormente)
    }
}
```

### **Integração com Outros Sistemas**
```java
public class IntegracaoSistemas {
    
    // Integração com sistema de batalha
    public void onBattleEnd(String npcId, boolean playerWon) {
        if (playerWon) {
            QuestManager.getInstance().updateObjective("main_orb_quest", "kill_" + npcId, true);
        }
    }
    
    // Integração com sistema de inventário
    public void onItemAdded(Item item) {
        if (item instanceof QuestItem) {
            QuestItem questItem = (QuestItem) item;
            QuestManager.getInstance().onQuestItemCollected(questItem);
        }
    }
    
    // Integração com sistema de diálogos
    public void onDialogAction(String actionType, String actionData) {
        if (actionType.equals("start_quest")) {
            QuestManager.getInstance().startQuest(actionData);
        }
    }
}
```

---

## 🎯 **Casos de Uso Avançados**

### **Quest com Múltiplos Caminhos**
```java
public class QuestMultiplosCaminhos {
    
    public void criarQuestEscolha() {
        Quest questEscolha = new Quest(
            "moral_choice",
            "Escolha Moral",
            "Escolha entre salvar a vila ou o tesouro"
        );
        
        // Objetivo condicional baseado na escolha do jogador
        questEscolha.addObjective(new QuestObjective(
            "save_village",
            "Salvar a vila dos bandidos",
            QuestObjectiveType.KILL_NPC,
            "bandit_leader"
        ));
        
        questEscolha.addObjective(new QuestObjective(
            "save_treasure",
            "Proteger o tesouro da vila",
            QuestObjectiveType.REACH_LOCATION,
            "treasure_room"
        ));
        
        // Apenas um dos objetivos precisa ser completado
        questEscolha.setQuestData("require_all_objectives", false);
    }
}
```

### **Quest com Time Limit**
```java
public class QuestTempoLimitado {
    
    public void criarQuestUrgente() {
        Quest questUrgente = new Quest(
            "urgent_rescue",
            "Resgate Urgente",
            "Salve o prisioneiro antes que seja tarde!"
        );
        
        questUrgente.setQuestData("time_limit", 300000); // 5 minutos
        questUrgente.setQuestData("start_time", System.currentTimeMillis());
        
        // Verificar tempo restante
        long tempoRestante = questUrgente.getQuestData("time_limit") - 
                           (System.currentTimeMillis() - questUrgente.getQuestData("start_time"));
        
        if (tempoRestante <= 0) {
            questUrgente.setState(QuestState.FAILED);
        }
    }
}
```

---

## 🏆 **Vantagens do Sistema**

### **✅ Para o Jogador**
- **Progressão Clara:** Objetivos bem definidos
- **Feedback Visual:** Progresso sempre visível
- **Recompensas:** Motivação para continuar
- **História Coesa:** Narrativa estruturada

### **✅ Para o Desenvolvedor**
- **Fácil de Usar:** API simples e intuitiva
- **Flexível:** Suporta diferentes tipos de objetivos
- **Automático:** Detecção automática de progresso
- **Extensível:** Fácil de adicionar novos tipos

### **✅ Para o Jogo**
- **Imersão:** História envolvente
- **Variedade:** Diferentes tipos de missões
- **Replayability:** Múltiplas escolhas possíveis
- **Profundidade:** Sistema rico e complexo

---

## 🚀 **Conclusão**

Este sistema de Quest oferece:

- **📚 Narrativa Estruturada:** Histórias bem organizadas
- **🎯 Objetivos Claros:** Missões com propósito
- **🔄 Progressão Automática:** Sistema inteligente
- **🎁 Sistema de Recompensas:** Motivação para o jogador
- **🎨 Interface Intuitiva:** Feedback visual claro
- **🔧 Fácil Configuração:** API simples de usar

**Resultado:** Um sistema completo de quests que transforma o jogo em uma experiência narrativa envolvente! 📚✨

---

## 📋 **Resumo dos Conceitos**

| Conceito | O que faz | Exemplo |
|----------|-----------|---------|
| **Quest** | Missão completa com objetivos | "As 4 Orbes de Eldoria" |
| **QuestObjective** | Tarefa específica dentro da quest | "Derrotar o Orc Boss" |
| **QuestState** | Status atual da quest | IN_PROGRESS, COMPLETED |
| **QuestManager** | Controla todas as quests | Singleton que gerencia tudo |
| **MagicOrb** | Item especial da quest | Orbe de Fogo, Água, Terra, Ar |
| **TotemCentral** | Local para depositar orbes | Objeto interativo especial |

**Este sistema transforma o jogo em uma aventura épica com objetivos claros e progressão satisfatória!** 🎮🏆
