# ⚔️ Sistema de Batalha - Documentação Completa da API

## 🎯 **Visão Geral**

Este documento especifica **todas as funcionalidades** disponíveis no sistema de batalha do CronicasDeEldoriaRPG, incluindo métodos, classes, enums e exemplos práticos de uso. O sistema implementa batalhas por turnos baseadas em agilidade, com sistema completo de buffs, habilidades especiais e integração com todos os sistemas do jogo.

---

## 🏗️ **Arquitetura do Sistema**

### **Classes Principais**
```
Battle                    → Controlador principal de batalha
├── Character            → Classe base para personagens
├── Player               → Personagem jogável
├── Npc                  → Personagem não-jogável (inimigo)
├── CharacterClass       → Interface para classes de personagem
├── Buff                 → Sistema de buffs/debuffs
└── Equipment            → Sistema de equipamentos
```

### **Classes de Personagem**
```
CharacterClass (Interface)
├── Barbarian           → Guerreiro com Fúria de Batalha
├── Mage                → Mago com Ataque Mágico
├── Paladin             → Paladino com Ataque Sagrado
├── Ranger              → Arqueiro com Flecha Certeira
└── Orc                 → Orc (NPC)
```

### **Sistema de Buffs**
```
Buff                     → Buff/Debuff individual
├── ARMOR               → Buff de armadura
├── STRENGTH            → Buff de força
├── HOT                 → Cura ao longo do tempo
└── DOT                 → Dano ao longo do tempo
```

### **Sistema de Efeitos**
```
BattleEffectManager     → Gerenciador de efeitos visuais
├── triggerForPlayer    → Efeitos para jogador
├── triggerForMonster   → Efeitos para monstro
└── triggerForMonsterFromPlayer → Efeitos de jogador para monstro
```

---

## ⚔️ **Battle - Controlador Principal**

### **🔧 Inicialização e Configuração**

#### **Construtor**
```java
Battle battle = new Battle(gamePanel);
```
- **Descrição**: Cria uma nova instância de batalha
- **Parâmetros**: `GamePanel gamePanel` - Referência ao painel do jogo
- **Uso**: Instanciar o sistema de batalha

#### **`startBattle(Player player, Npc monster)`**
```java
battle.startBattle(player, wolfMonster);
```
- **Descrição**: Inicia uma nova batalha entre jogador e monstro
- **Parâmetros**: 
  - `Player player` - Personagem jogável
  - `Npc monster` - Monstro inimigo
- **Funcionalidade**: 
  - Determina ordem de turnos por agilidade
  - Inicializa estado da batalha
  - Processa primeiro turno automaticamente se for do monstro

---

### **🎮 Processamento de Ações**

#### **`processPlayerAction(String action)`**
```java
battle.processPlayerAction("ATTACK");
battle.processPlayerAction("DEFEND");
battle.processPlayerAction("SPECIAL");
battle.processPlayerAction("FLEE");
```
- **Descrição**: Processa ação escolhida pelo jogador
- **Parâmetros**: `String action` - Ação a ser executada
- **Ações Disponíveis**:
  - `"ATTACK"` - Ataque básico
  - `"DEFEND"` - Defender (buff de armadura)
  - `"SPECIAL"` - Habilidade especial da classe
  - `"FLEE"` - Tentar fugir da batalha
  - `"REGEN"` - Usar orbe de água (cura)
  - `"DAMAGEOVERTIME"` - Usar orbe de fogo (dano)
  - `"HEALTH"` - Usar poção de vida
  - `"MANA"` - Usar poção de mana

#### **`processMonsterTurn()`**
```java
// Chamado automaticamente pelo sistema
battle.processMonsterTurn();
```
- **Descrição**: Processa turno do monstro automaticamente
- **IA do Monstro**:
  - 80% chance de atacar
  - 20% chance de defender
- **Uso**: Sistema interno, não chamar manualmente

---

### **⚔️ Sistema de Combate**

#### **`calculateDamage(Character attacker, Character target)`**
```java
int damage = Battle.calculateDamage(player, monster);
System.out.println("Dano causado: " + damage);
```
- **Descrição**: Calcula dano baseado nos atributos dos personagens
- **Parâmetros**: 
  - `Character attacker` - Personagem atacante
  - `Character target` - Personagem alvo
- **Retorno**: `int` - Quantidade de dano
- **Fórmula**: `max(1, força_efetiva - (armadura_efetiva / 2)) + variação_40%`

#### **`attack(Character attacker, Character target)`**
```java
battle.attack(player, monster);
```
- **Descrição**: Executa ataque entre dois personagens
- **Parâmetros**: 
  - `Character attacker` - Personagem atacante
  - `Character target` - Personagem alvo
- **Funcionalidade**:
  - Calcula dano usando `calculateDamage()`
  - Aplica dano ao alvo
  - Reproduz efeitos sonoros
  - Dispara animações de efeito

#### **`defend(Character character)`**
```java
battle.defend(player);
```
- **Descrição**: Personagem se defende, ganhando buff de armadura
- **Parâmetros**: `Character character` - Personagem defendendo
- **Funcionalidade**:
  - Aplica buff ARMOR com 120% da armadura base
  - Duração: 4 turnos
  - Cooldown: 4 turnos
  - Dispara animação de escudo

#### **`flee(Character character)`**
```java
boolean success = battle.flee(player);
if (success) {
    System.out.println("Fuga bem-sucedida!");
}
```
- **Descrição**: Tenta fugir da batalha
- **Parâmetros**: `Character character` - Personagem tentando fugir
- **Retorno**: `boolean` - true se fuga foi bem-sucedida
- **Funcionalidade**:
  - 50% chance de sucesso
  - Não funciona contra Mago Supremo
  - Move jogador para longe do monstro

---

### **🔮 Habilidades Especiais**

#### **`specialAttack(Character attacker, Character target, int countTurn)`**
```java
battle.specialAttack(player, monster, battle.getCountTurn());
```
- **Descrição**: Executa habilidade especial da classe do personagem
- **Parâmetros**: 
  - `Character attacker` - Personagem usando habilidade
  - `Character target` - Alvo da habilidade
  - `int countTurn` - Número do turno atual
- **Funcionalidade**: Chama `getSpecialAbility()` da classe do personagem

#### **Habilidades por Classe**

##### **Barbarian - Fúria de Batalha**
```java
// Custo: 15 mana
// Efeito: Buff de força por 6 turnos
int bonus = (int)(attacker.getAttributeStrength() * 1.2);
Buff strBuff = new Buff("STRENGTH", bonus, 6, 6, attacker);
attacker.applyBuff(strBuff);
```

##### **Mage - Ataque Mágico**
```java
// Custo: 15 mana
// Efeito: Dano mágico 2.8x maior que ataque normal
int magicDamage = (int) (Battle.calculateDamage(attacker, target) * 2.8);
target.setAttributeHealth(target.getAttributeHealth() - magicDamage);
```

##### **Paladin - Ataque Sagrado**
```java
// Custo: 15 mana
// Efeito: Dano 2x + cura 80% do dano causado
int damage = (int) (Battle.calculateDamage(attacker, target) * 2);
int heal = (int) (damage * 0.8);
attacker.setAttributeHealth(attacker.getAttributeHealth() + heal);
```

##### **Ranger - Flecha Certeira**
```java
// Custo: 15 mana
// Efeito: Dano 2x que ignora armadura do alvo
int baseDamage = attacker.getEffectiveStrength();
int finalDamage = baseDamage + variation;
int damage = (int)(finalDamage * 2);
```

---

### **🧪 Sistema de Poções e Orbes**

#### **`healthPotion(Character character)`**
```java
battle.healthPotion(player);
```
- **Descrição**: Usa poção de vida
- **Parâmetros**: `Character character` - Personagem usando poção
- **Funcionalidade**:
  - Cura base: 50 HP
  - Variação: ±40%
  - Não excede vida máxima
  - Reproduz som de cura

#### **`manaPotion(Character character)`**
```java
battle.manaPotion(player);
```
- **Descrição**: Usa poção de mana
- **Parâmetros**: `Character character` - Personagem usando poção
- **Funcionalidade**:
  - Restaura base: 35 MP
  - Variação: ±30%
  - Não excede mana máxima
  - Reproduz som de cura

#### **`waterOrb(Character character)`**
```java
battle.waterOrb(player);
```
- **Descrição**: Usa orbe de água para cura ao longo do tempo
- **Parâmetros**: `Character character` - Personagem usando orbe
- **Funcionalidade**:
  - Cura: 3.5% da vida máxima por turno
  - Duração: 99 turnos
  - Aplica buff HOT

#### **`fireOrb(Character monster)`**
```java
battle.fireOrb(monster);
```
- **Descrição**: Usa orbe de fogo para dano ao longo do tempo
- **Parâmetros**: `Character monster` - Monstro alvo
- **Funcionalidade**:
  - Dano: 4% da vida máxima por turno (3% para Mago Supremo)
  - Duração: 99 turnos
  - Aplica buff DOT

---

### **📊 Controle de Turnos**

#### **`determineTurnOrder()`**
```java
// Chamado automaticamente no início da batalha
battle.determineTurnOrder();
```
- **Descrição**: Determina ordem de turnos baseada na agilidade
- **Funcionalidade**:
  - Ordena personagens por agilidade (maior primeiro)
  - Cria lista `turnOrder`
  - Define primeiro personagem a agir

#### **`nextTurn()`**
```java
// Chamado automaticamente após cada ação
battle.nextTurn();
```
- **Descrição**: Avança para o próximo turno
- **Funcionalidade**:
  - Incrementa contador de turnos
  - Atualiza buffs de todos os personagens
  - Alterna para próximo personagem na ordem

#### **`checkBattleEnd()`**
```java
// Chamado automaticamente após cada ação
boolean ended = battle.checkBattleEnd();
```
- **Descrição**: Verifica se a batalha terminou
- **Retorno**: `boolean` - true se batalha terminou
- **Condições de Fim**:
  - Jogador com vida ≤ 0 → Derrota
  - Monstro com vida ≤ 0 → Vitória
  - Limpa buffs do jogador na vitória

---

### **🎵 Sistema de Áudio**

#### **Efeitos Sonoros Automáticos**
```java
// Reproduzidos automaticamente durante batalha
audioManager.playSoundEffect("player_attack");    // Ataque do jogador
audioManager.playSoundEffect("player_block");     // Bloqueio do jogador
audioManager.playSoundEffect("player_flee");      // Tentativa de fuga
audioManager.playSoundEffect("potion_heal");      // Uso de poção
```

---

### **🎨 Sistema de Efeitos Visuais**

#### **`deriveMonsterKey(Npc npc)`**
```java
String monsterKey = battle.deriveMonsterKey(monster);
```
- **Descrição**: Determina chave do monstro para efeitos visuais
- **Parâmetros**: `Npc npc` - Monstro
- **Retorno**: `String` - Chave do monstro
- **Chaves Suportadas**:
  - `"orcboss"`, `"orc"`
  - `"frostbornboss"`, `"frostborn"`
  - `"wolfboss"`, `"wolf"`
  - `"skeletonboss"`, `"skeleton"`
  - `"suprememage"`

#### **Efeitos Visuais Automáticos**
```java
// Disparados automaticamente durante ações
battleEffectManager.triggerForPlayer(playerClass, "attack");     // Ataque
battleEffectManager.triggerForPlayer(playerClass, "shield");     // Defesa
battleEffectManager.triggerForPlayer(playerClass, "special");    // Habilidade
battleEffectManager.triggerForPlayer(playerClass, "heal");       // Cura
battleEffectManager.triggerForMonster(monsterKey, "attack");    // Ataque do monstro
battleEffectManager.triggerForMonster(monsterKey, "dot");        // Dano ao longo do tempo
```

---

## 🧬 **Character - Classe Base**

### **📊 Atributos Básicos**

#### **Vida e Mana**
```java
int health = character.getAttributeHealth();        // Vida atual
int maxHealth = character.getAttributeMaxHealth();  // Vida máxima
int mana = character.getAttributeMana();           // Mana atual
int maxMana = character.getAttributeMaxMana();     // Mana máxima

character.setAttributeHealth(100);                 // Definir vida
character.setAttributeMana(50);                    // Definir mana
```

#### **Atributos de Combate**
```java
int strength = character.getAttributeStrength();    // Força
int agility = character.getAttributeAgility();     // Agilidade
int armor = character.getAttributeArmor();         // Armadura
int force = character.getAttribueForce();          // Força (legado)
int defence = character.getAttributeDefence();     // Defesa (legado)
int stamina = character.getAttributeStamina();     // Resistência (legado)
```

### **🔮 Sistema de Buffs**

#### **`hasActiveBuff(String type)`**
```java
if (character.hasActiveBuff("ARMOR")) {
    System.out.println("Personagem está defendendo!");
}
```
- **Descrição**: Verifica se personagem tem buff ativo
- **Parâmetros**: `String type` - Tipo do buff
- **Retorno**: `boolean` - true se buff está ativo

#### **`canApplyBuff(String type)`**
```java
if (character.canApplyBuff("STRENGTH")) {
    // Pode aplicar buff de força
}
```
- **Descrição**: Verifica se pode aplicar buff
- **Parâmetros**: `String type` - Tipo do buff
- **Retorno**: `boolean` - true se pode aplicar
- **Limitações**:
  - Máximo 3 buffs diferentes ativos
  - Não pode aplicar mesmo tipo se ativo ou em cooldown

#### **`applyBuff(Buff buff)`**
```java
Buff armorBuff = new Buff("ARMOR", 20, 4, 4, character);
character.applyBuff(armorBuff);
```
- **Descrição**: Aplica buff ao personagem
- **Parâmetros**: `Buff buff` - Buff a ser aplicado
- **Funcionalidade**: Adiciona buff à lista se possível

#### **`updateBuffs(int countTurn, GamePanel gp)`**
```java
character.updateBuffs(turnCount, gamePanel);
```
- **Descrição**: Atualiza todos os buffs ativos
- **Parâmetros**: 
  - `int countTurn` - Número do turno atual
  - `GamePanel gp` - Referência ao painel do jogo
- **Funcionalidade**:
  - Processa efeitos de HOT e DOT
  - Decrementa duração dos buffs
  - Remove buffs expirados

### **⚔️ Atributos Efetivos**

#### **`getEffectiveArmor()`**
```java
int totalArmor = character.getEffectiveArmor();
```
- **Descrição**: Retorna armadura total incluindo buffs
- **Retorno**: `int` - Armadura base + bônus de buffs ARMOR

#### **`getEffectiveStrength()`**
```java
int totalStrength = character.getEffectiveStrength();
```
- **Descrição**: Retorna força total incluindo buffs
- **Retorno**: `int` - Força base + bônus de buffs STRENGTH

### **🧹 Limpeza de Buffs**

#### **`cleanActiveBuffs()`**
```java
character.cleanActiveBuffs();
```
- **Descrição**: Remove todos os buffs ativos
- **Uso**: Chamado quando monstro é derrotado

---

## 🔮 **Buff - Sistema de Buffs/Debuffs**

### **🔧 Criação de Buffs**

#### **Construtor**
```java
Buff buff = new Buff(
    "ARMOR",           // Tipo do buff
    20,                // Valor do bônus
    4,                 // Duração em turnos
    4,                 // Cooldown em turnos
    character          // Personagem que aplicou
);
```

### **📊 Propriedades do Buff**

#### **Getters Básicos**
```java
String type = buff.getType();           // Tipo do buff
int bonus = buff.getBonus();            // Valor do bônus
int turnsLeft = buff.getTurnsLeft();    // Turnos restantes
Character caster = buff.getCaster();    // Personagem que aplicou
```

#### **Verificação de Estado**
```java
boolean isActive = buff.isActive();         // Buff está ativo?
boolean onCooldown = buff.isOnCooldown();   // Buff está em cooldown?
```

### **⏰ Gerenciamento de Duração**

#### **`decrementDuration(Character character)`**
```java
buff.decrementDuration(character);
```
- **Descrição**: Decrementa duração do buff
- **Parâmetros**: `Character character` - Personagem dono do buff
- **Funcionalidade**:
  - Decrementa turnos restantes
  - Inicia cooldown quando expira
  - Decrementa cooldown restante

### **📋 Tipos de Buff Suportados**

#### **ARMOR - Buff de Armadura**
```java
Buff armorBuff = new Buff("ARMOR", 20, 4, 4, character);
// +20 armadura por 4 turnos, cooldown 4 turnos
```

#### **STRENGTH - Buff de Força**
```java
Buff strengthBuff = new Buff("STRENGTH", 15, 6, 6, character);
// +15 força por 6 turnos, cooldown 6 turnos
```

#### **HOT - Cura ao Longo do Tempo**
```java
Buff hotBuff = new Buff("HOT", 10, 99, 0, character);
// +10 vida a cada 2 turnos por 99 turnos
```

#### **DOT - Dano ao Longo do Tempo**
```java
Buff dotBuff = new Buff("DOT", 5, 99, 0, character);
// -5 vida a cada 2 turnos por 99 turnos
```

---

## 🎭 **CharacterClass - Classes de Personagem**

### **🔧 Interface CharacterClass**

#### **Métodos Obrigatórios**
```java
String className = characterClass.getCharacterClassName();        // Nome da classe
String specialAttr = characterClass.getSpecialAttributeName();    // Nome do atributo especial
int specialValue = characterClass.getSpecialAttributeValue();     // Valor do atributo especial
String abilityName = characterClass.getSpecialAbilityName();      // Nome da habilidade especial
characterClass.getSpecialAbility(attacker, target, turn, gp);     // Executa habilidade especial
```

### **⚔️ Implementações das Classes**

#### **Barbarian - Guerreiro**
```java
Barbarian barbarian = new Barbarian(15); // 15 de força de vontade

// Atributos especiais
String className = barbarian.getCharacterClassName();     // "Barbarian"
String specialAttr = barbarian.getSpecialAttributeName(); // "willpower"
int willpower = barbarian.getSpecialAttributeValue();     // 15
String abilityName = barbarian.getSpecialAbilityName();  // "Furia de Batalha"

// Habilidade especial: Fúria de Batalha
// Custo: 15 mana
// Efeito: Buff de força por 6 turnos
barbarian.getSpecialAbility(attacker, target, turnCount, gamePanel);
```

#### **Mage - Mago**
```java
Mage mage = new Mage(20); // 20 de poder mágico

// Atributos especiais
String className = mage.getCharacterClassName();     // "Mage"
String specialAttr = mage.getSpecialAttributeName();  // "magicPower"
int magicPower = mage.getSpecialAttributeValue();    // 20
String abilityName = mage.getSpecialAbilityName();    // "Ataque Mágico"

// Habilidade especial: Ataque Mágico
// Custo: 15 mana
// Efeito: Dano 2.8x maior que ataque normal
mage.getSpecialAbility(attacker, target, turnCount, gamePanel);
```

#### **Paladin - Paladino**
```java
Paladin paladin = new Paladin(18); // 18 de resistência

// Atributos especiais
String className = paladin.getCharacterClassName();     // "Paladin"
String specialAttr = paladin.getSpecialAttributeName(); // "endurance"
int endurance = paladin.getSpecialAttributeValue();      // 18
String abilityName = paladin.getSpecialAbilityName();   // "Ataque Sagrado"

// Habilidade especial: Ataque Sagrado
// Custo: 15 mana
// Efeito: Dano 2x + cura 80% do dano causado
paladin.getSpecialAbility(attacker, target, turnCount, gamePanel);
```

#### **Ranger - Arqueiro**
```java
Ranger ranger = new Ranger(22); // 22 de destreza

// Atributos especiais
String className = ranger.getCharacterClassName();     // "Ranger"
String specialAttr = ranger.getSpecialAttributeName(); // "dexterity"
int dexterity = ranger.getSpecialAttributeValue();     // 22
String abilityName = ranger.getSpecialAbilityName();   // "Flecha Certeira"

// Habilidade especial: Flecha Certeira
// Custo: 15 mana
// Efeito: Dano 2x que ignora armadura do alvo
ranger.getSpecialAbility(attacker, target, turnCount, gamePanel);
```

---

## 🎮 **Exemplos Práticos de Uso**

### **Exemplo 1: Batalha Básica**
```java
// Criar instância de batalha
Battle battle = new Battle(gamePanel);

// Iniciar batalha
battle.startBattle(player, wolfMonster);

// Processar ações do jogador
battle.processPlayerAction("ATTACK");   // Atacar
battle.processPlayerAction("DEFEND");  // Defender
battle.processPlayerAction("SPECIAL"); // Habilidade especial
battle.processPlayerAction("FLEE");    // Tentar fugir

// Verificar estado da batalha
if (battle.isInBattle()) {
    System.out.println("Batalha em andamento!");
    if (battle.isPlayerTurn()) {
        System.out.println("Turno do jogador!");
    }
}
```

### **Exemplo 2: Sistema de Buffs**
```java
// Criar buff de armadura
Buff armorBuff = new Buff("ARMOR", 25, 6, 4, player);

// Verificar se pode aplicar
if (player.canApplyBuff("ARMOR")) {
    player.applyBuff(armorBuff);
    System.out.println("Buff de armadura aplicado!");
}

// Verificar buff ativo
if (player.hasActiveBuff("ARMOR")) {
    int totalArmor = player.getEffectiveArmor();
    System.out.println("Armadura total: " + totalArmor);
}

// Atualizar buffs no turno
player.updateBuffs(turnCount, gamePanel);
```

### **Exemplo 3: Habilidades Especiais**
```java
// Barbarian usando Fúria de Batalha
Barbarian barbarian = new Barbarian(15);
if (player.getAttributeMana() >= 15) {
    barbarian.getSpecialAbility(player, monster, turnCount, gamePanel);
    System.out.println("Fúria de Batalha ativada!");
}

// Mage usando Ataque Mágico
Mage mage = new Mage(20);
if (player.getAttributeMana() >= 15) {
    mage.getSpecialAbility(player, monster, turnCount, gamePanel);
    System.out.println("Ataque Mágico executado!");
}

// Paladin usando Ataque Sagrado
Paladin paladin = new Paladin(18);
if (player.getAttributeMana() >= 15) {
    paladin.getSpecialAbility(player, monster, turnCount, gamePanel);
    System.out.println("Ataque Sagrado executado!");
}
```

### **Exemplo 4: Sistema de Poções**
```java
// Usar poção de vida
if (player.getAttributeHealth() < player.getAttributeMaxHealth()) {
    battle.healthPotion(player);
    System.out.println("Poção de vida usada!");
}

// Usar poção de mana
if (player.getAttributeMana() < player.getAttributeMaxMana()) {
    battle.manaPotion(player);
    System.out.println("Poção de mana usada!");
}

// Usar orbe de água (cura ao longo do tempo)
battle.waterOrb(player);
System.out.println("Orbe de água ativada!");

// Usar orbe de fogo (dano ao longo do tempo)
battle.fireOrb(monster);
System.out.println("Orbe de fogo ativada!");
```

### **Exemplo 5: Cálculo de Dano**
```java
// Calcular dano entre personagens
int damage = Battle.calculateDamage(player, monster);
System.out.println("Dano calculado: " + damage);

// Verificar atributos efetivos
int playerStrength = player.getEffectiveStrength();
int monsterArmor = monster.getEffectiveArmor();

System.out.println("Força efetiva do jogador: " + playerStrength);
System.out.println("Armadura efetiva do monstro: " + monsterArmor);

// Executar ataque
battle.attack(player, monster);
```

### **Exemplo 6: Sistema de Turnos**
```java
// Verificar ordem de turnos
List<Character> turnOrder = battle.getTurnOrder();
for (int i = 0; i < turnOrder.size(); i++) {
    Character character = turnOrder.get(i);
    System.out.println((i+1) + ". " + character.getName() + 
                      " (Agilidade: " + character.getAttributeAgility() + ")");
}

// Verificar turno atual
Character currentCharacter = battle.getCurrentCharacter();
if (currentCharacter instanceof Player) {
    System.out.println("Turno do jogador!");
} else {
    System.out.println("Turno do monstro!");
}

// Avançar turno
battle.nextTurn();
```

### **Exemplo 7: Sistema de Fuga**
```java
// Tentar fugir da batalha
boolean fleeSuccess = battle.flee(player);
if (fleeSuccess) {
    System.out.println("Fuga bem-sucedida!");
    battle.endBattle();
} else {
    System.out.println("Falha na fuga!");
}

// Verificar se pode fugir (não funciona contra Mago Supremo)
if (!monster.getName().equals("Mago Supremo")) {
    // Tentar fugir
    battle.flee(player);
} else {
    System.out.println("Não é possível fugir do Mago Supremo!");
}
```

### **Exemplo 8: Sistema de Efeitos Visuais**
```java
// Disparar efeitos visuais para jogador
String playerClass = player.getCharacterClass().getCharacterClassName();
battleEffectManager.triggerForPlayer(playerClass, "attack");
battleEffectManager.triggerForPlayer(playerClass, "shield");
battleEffectManager.triggerForPlayer(playerClass, "special");
battleEffectManager.triggerForPlayer(playerClass, "heal");

// Disparar efeitos visuais para monstro
String monsterKey = battle.deriveMonsterKey(monster);
battleEffectManager.triggerForMonster(monsterKey, "attack");
battleEffectManager.triggerForMonster(monsterKey, "dot");
```

---

## 🔧 **Integração com Outros Sistemas**

### **Com Sistema de Quest**
```java
// Quando monstro é derrotado
if (monster.getAttributeHealth() <= 0) {
    QuestManager questManager = QuestManager.getInstance();
    questManager.onNpcKilled(monster.getName());
    // Atualiza objetivos de quest automaticamente
}
```

### **Com Sistema de Inventário**
```java
// Verificar se jogador tem poções
if (player.getInventoryManager().hasItem("health_potion")) {
    battle.healthPotion(player);
    player.getInventoryManager().removeItem("health_potion");
}
```

### **Com Sistema de Áudio**
```java
// Reproduzir música de batalha
audioManager.changeContext(AudioContext.BATTLE);

// Reproduzir efeitos sonoros
audioManager.playSoundEffect("player_attack");
audioManager.playSoundEffect("player_block");
audioManager.playSoundEffect("player_flee");
```

### **Com Sistema de UI**
```java
// Mostrar dano na tela
gameUI.showDamage(target, damage, null);

// Mostrar cura na tela
gameUI.showHeal(character, healAmount, "POTION");

// Mostrar mana restaurada
gameUI.showMana(character, manaAmount);
```

### **Com Sistema de Efeitos**
```java
// Disparar efeitos visuais
battleEffectManager.triggerForPlayer(playerClass, "attack");
battleEffectManager.triggerForMonster(monsterKey, "attack");
```

---

## 🎯 **Casos de Uso Avançados**

### **Batalha com Múltiplos Buffs**
```java
// Aplicar múltiplos buffs
Buff armorBuff = new Buff("ARMOR", 20, 4, 4, player);
Buff strengthBuff = new Buff("STRENGTH", 15, 6, 6, player);
Buff hotBuff = new Buff("HOT", 10, 99, 0, player);

if (player.canApplyBuff("ARMOR")) {
    player.applyBuff(armorBuff);
}
if (player.canApplyBuff("STRENGTH")) {
    player.applyBuff(strengthBuff);
}
if (player.canApplyBuff("HOT")) {
    player.applyBuff(hotBuff);
}

// Verificar todos os buffs ativos
for (Buff buff : player.getActiveBuffs()) {
    if (buff.isActive()) {
        System.out.println("Buff ativo: " + buff.getType() + 
                          " (+" + buff.getBonus() + ")");
    }
}
```

### **Sistema de Cooldown**
```java
// Verificar cooldown de buffs
for (Buff buff : player.getActiveBuffs()) {
    if (buff.isOnCooldown()) {
        System.out.println("Buff em cooldown: " + buff.getType());
    }
}

// Aplicar buff apenas se não estiver em cooldown
if (player.canApplyBuff("ARMOR")) {
    Buff armorBuff = new Buff("ARMOR", 20, 4, 4, player);
    player.applyBuff(armorBuff);
} else {
    System.out.println("Buff de armadura em cooldown!");
}
```

### **Sistema de Dano ao Longo do Tempo**
```java
// Aplicar DOT ao monstro
Buff dotBuff = new Buff("DOT", 5, 99, 0, player);
monster.applyBuff(dotBuff);

// Verificar DOT ativo
if (monster.hasActiveBuff("DOT")) {
    System.out.println("Monstro está sofrendo dano ao longo do tempo!");
}

// Atualizar DOT a cada turno
monster.updateBuffs(turnCount, gamePanel);
```

### **Sistema de Cura ao Longo do Tempo**
```java
// Aplicar HOT ao jogador
Buff hotBuff = new Buff("HOT", 10, 99, 0, player);
player.applyBuff(hotBuff);

// Verificar HOT ativo
if (player.hasActiveBuff("HOT")) {
    System.out.println("Jogador está se curando ao longo do tempo!");
}

// Atualizar HOT a cada turno
player.updateBuffs(turnCount, gamePanel);
```

### **Sistema de Habilidades Especiais Avançado**
```java
// Verificar mana antes de usar habilidade
int manaCost = 15;
if (player.getAttributeMana() >= manaCost) {
    // Usar habilidade especial
    player.getCharacterClass().getSpecialAbility(player, monster, turnCount, gamePanel);
    
    // Verificar efeitos pós-habilidade
    if (player.hasActiveBuff("STRENGTH")) {
        System.out.println("Buff de força ativo!");
    }
} else {
    System.out.println("Mana insuficiente para usar habilidade especial!");
}
```

---

## 📋 **Checklist de Implementação**

### **✅ Configuração Básica**
- [ ] Battle inicializado com GamePanel
- [ ] Personagens criados com atributos corretos
- [ ] Classes de personagem implementadas
- [ ] Sistema de buffs configurado

### **✅ Integração**
- [ ] Sistema de quest integrado
- [ ] Sistema de inventário integrado
- [ ] Sistema de áudio integrado
- [ ] Sistema de UI integrado
- [ ] Sistema de efeitos visuais integrado

### **✅ Testes**
- [ ] Batalha inicia corretamente
- [ ] Ordem de turnos funciona
- [ ] Ações são processadas
- [ ] Buffs são aplicados e atualizados
- [ ] Habilidades especiais funcionam
- [ ] Sistema de fuga funciona
- [ ] Batalha termina corretamente

### **✅ Funcionalidades Avançadas**
- [ ] Sistema de buffs/debuffs
- [ ] Sistema de cooldown
- [ ] Sistema de dano ao longo do tempo
- [ ] Sistema de cura ao longo do tempo
- [ ] Sistema de efeitos visuais
- [ ] Sistema de áudio dinâmico
- [ ] Sistema de poções e orbes
- [ ] Sistema de habilidades especiais

---

## 🚀 **Conclusão**

Este sistema de batalha oferece:

- ✅ **Batalhas por Turnos**: Sistema baseado em agilidade com ordem dinâmica
- ✅ **Classes Diversas**: 4 classes com habilidades especiais únicas
- ✅ **Sistema de Buffs**: Buffs/debuffs com duração e cooldown
- ✅ **Habilidades Especiais**: Cada classe tem habilidade única
- ✅ **Sistema de Poções**: Poções de vida e mana com variação
- ✅ **Sistema de Orbes**: Efeitos de cura e dano ao longo do tempo
- ✅ **Sistema de Fuga**: Mecânica de fuga com restrições
- ✅ **Efeitos Visuais**: Animações para todas as ações
- ✅ **Sistema de Áudio**: Efeitos sonoros dinâmicos
- ✅ **Integração Completa**: Funciona com todos os sistemas do jogo

### **Recursos Implementados**
- ⚔️ **Batalhas por Turnos**: Sistema baseado em agilidade
- 🎭 **4 Classes de Personagem**: Barbarian, Mage, Paladin, Ranger
- 🔮 **Sistema de Buffs**: ARMOR, STRENGTH, HOT, DOT
- ⚡ **Habilidades Especiais**: Cada classe com habilidade única
- 🧪 **Sistema de Poções**: Vida e mana com variação
- 💎 **Sistema de Orbes**: Efeitos de longo prazo
- 🏃 **Sistema de Fuga**: Mecânica de escape
- 🎨 **Efeitos Visuais**: Animações para todas as ações
- 🎵 **Sistema de Áudio**: Efeitos sonoros dinâmicos
- 🎮 **Integração Total**: Funciona com quests, inventário, UI

**O sistema está pronto para criar batalhas épicas e estratégicas!** ⚔️✨
