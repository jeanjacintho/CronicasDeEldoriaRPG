# ⚔️ Sistema de Batalha - Apresentação Didática

## 📖 **O que é este sistema?**

Imagine que você está criando um **combate estratégico** onde o jogador enfrenta monstros em turnos, usando diferentes habilidades e estratégias. O sistema de Batalha é exatamente isso! Ele permite criar combates dinâmicos com sistema de turnos, buffs, habilidades especiais e efeitos visuais.

---

## 🎮 **Conceitos Básicos**

### **Batalha por Turnos**
- **O que é?** Sistema onde cada personagem age em sua vez
- **Como funciona?** Ordem baseada na agilidade dos personagens
- **Exemplo:** Jogador ataca → Monstro ataca → Jogador defende → Monstro ataca

### **Buffs e Debuffs**
- **O que é?** Efeitos temporários que modificam atributos
- **Como funciona?** Duram alguns turnos e depois entram em cooldown
- **Exemplo:** Buff de força (+20% dano por 3 turnos), Buff de armadura (+20% defesa por 2 turnos)

### **Habilidades Especiais**
- **O que é?** Ataques únicos de cada classe de personagem
- **Como funciona?** Consomem mana e têm efeitos especiais
- **Exemplo:** Barbarian usa "Fúria de Batalha", Mage usa "Ataque Mágico"

---

## 🏗️ **Como o Sistema Funciona**

### **1. Início da Batalha**
```java
// Iniciar uma batalha entre jogador e monstro
Battle battle = new Battle(gamePanel);
battle.startBattle(player, wolfMonster);

// O sistema automaticamente:
// 1. Determina ordem de turnos por agilidade
// 2. Inicia o primeiro turno
// 3. Se for turno do monstro, processa automaticamente
```

**O que acontece aqui?**
- Sistema cria lista de turnos baseada na agilidade
- Personagem com mais agilidade age primeiro
- Batalha fica em estado "ativa"
- Interface aguarda input do jogador

### **2. Processamento de Ações**
```java
// Jogador escolhe uma ação
battle.processPlayerAction("ATTACK");    // Ataque básico
battle.processPlayerAction("DEFEND");    // Defender (buff de armadura)
battle.processPlayerAction("SPECIAL");   // Habilidade especial
battle.processPlayerAction("FLEE");      // Tentar fugir
battle.processPlayerAction("REGEN");     // Usar orbe de água (cura)
battle.processPlayerAction("HEALTH");    // Usar poção de vida
```

**Ações Disponíveis:**
- **ATTACK:** Ataque básico com dano calculado
- **DEFEND:** Aplica buff de armadura por 2 turnos
- **SPECIAL:** Habilidade especial da classe (consome mana)
- **FLEE:** Tenta fugir da batalha
- **REGEN:** Aplica cura ao longo do tempo
- **DAMAGEOVERTIME:** Aplica dano ao longo do tempo no inimigo
- **HEALTH/MANA:** Usa poções do inventário

### **3. Sistema de Turnos**
```java
public class SistemaTurnos {
    
    public void processarTurno() {
        // 1. Obter personagem atual
        Character personagemAtual = getCurrentCharacter();
        
        // 2. Se for jogador, aguardar input
        if (personagemAtual instanceof Player) {
            waitingForPlayerInput = true;
            return;
        }
        
        // 3. Se for monstro, processar IA automaticamente
        if (personagemAtual instanceof Npc) {
            processarTurnoMonstro((Npc) personagemAtual);
        }
        
        // 4. Verificar se batalha terminou
        if (checkBattleEnd()) return;
        
        // 5. Próximo turno
        nextTurn();
        
        // 6. Atualizar buffs de todos os personagens
        atualizarBuffs();
    }
    
    private void processarTurnoMonstro(Npc monstro) {
        Random random = new Random();
        int escolha = random.nextInt(100);
        
        if (escolha < 80) {        // 80% chance de atacar
            attack(monstro, player);
        } else if (escolha < 100) { // 20% chance de defender
            defend(monstro);
        }
    }
}
```

---

## ⚔️ **Sistema de Combate**

### **Cálculo de Dano**
```java
public static int calculateDamage(Character attacker, Character target) {
    // 1. Dano base = Força do atacante - (Armadura do alvo / 2)
    int baseDamage = Math.max(1, attacker.getEffectiveStrength() - (target.getEffectiveArmor() / 2));
    
    // 2. Variação de ±40% para tornar combate menos previsível
    int variation = Math.max(1, (int)(baseDamage * 0.4));
    int finalDamage = baseDamage + (int)(Math.random() * variation * 2) - variation;
    
    // 3. Dano mínimo de 1
    return Math.max(1, finalDamage);
}
```

**Exemplo Prático:**
```
Jogador (Força: 50) ataca Monstro (Armadura: 20)
Dano base = 50 - (20/2) = 40
Variação = ±16 (40% de 40)
Dano final = 24 a 56 (média: 40)
```

### **Sistema de Buffs**
```java
public class SistemaBuffs {
    
    public void aplicarBuff(Character personagem, String tipo, int bonus, int duracao) {
        // Verificar se pode aplicar o buff
        if (personagem.canApplyBuff(tipo)) {
            Buff buff = new Buff(tipo, bonus, duracao, cooldownTime, personagem);
            personagem.applyBuff(buff);
            
            // Mostrar efeito visual
            mostrarEfeitoVisual(personagem, tipo);
        }
    }
    
    public void atualizarBuffs(Character personagem, int turnoAtual) {
        for (Buff buff : personagem.getActiveBuffs()) {
            // Aplicar efeitos de buffs ativos
            switch (buff.getType()) {
                case "HOT": // Heal Over Time
                    if (turnoAtual % 2 == 0) { // A cada 2 turnos
                        int cura = Math.min(buff.getBonus(), personagem.getMissingHealth());
                        personagem.heal(cura);
                        mostrarCura(personagem, cura);
                    }
                    break;
                    
                case "DOT": // Damage Over Time
                    if (turnoAtual % 2 == 0) { // A cada 2 turnos
                        personagem.takeDamage(buff.getBonus());
                        mostrarDano(personagem, buff.getBonus());
                    }
                    break;
            }
            
            // Decrementar duração
            buff.decrementDuration(personagem);
        }
    }
}
```

**Tipos de Buffs:**
- **ARMOR:** Aumenta armadura (reduz dano recebido)
- **STRENGTH:** Aumenta força (aumenta dano causado)
- **HOT:** Cura ao longo do tempo (Heal Over Time)
- **DOT:** Dano ao longo do tempo (Damage Over Time)

---

## 🎯 **Exemplo Prático: Batalha Completa**

### **Cenário: Jogador vs Orc Boss**

```
🎮 Turno 1: Jogador (Agilidade: 30)
   Ação: ATTACK
   Dano: 45 (Força: 50 - Armadura: 10)
   Orc Boss HP: 455/500

👹 Turno 2: Orc Boss (Agilidade: 25)
   IA: 80% chance de atacar
   Ação: ATTACK
   Dano: 35 (Força: 40 - Armadura: 10)
   Jogador HP: 165/200

🎮 Turno 3: Jogador
   Ação: DEFEND
   Buff aplicado: ARMOR +20% por 2 turnos
   Armadura: 10 → 12

👹 Turno 4: Orc Boss
   Ação: ATTACK
   Dano: 30 (reduzido pela armadura)
   Jogador HP: 135/200

🎮 Turno 5: Jogador
   Ação: SPECIAL (Fúria de Batalha)
   Mana: 100 → 85
   Buff aplicado: STRENGTH +60% por 3 turnos
   Força: 50 → 80

👹 Turno 6: Orc Boss
   Ação: ATTACK
   Dano: 30
   Jogador HP: 105/200

🎮 Turno 7: Jogador
   Ação: ATTACK (com buff de força)
   Dano: 72 (Força: 80 - Armadura: 10)
   Orc Boss HP: 383/500

👹 Turno 8: Orc Boss
   Ação: ATTACK
   Dano: 30
   Jogador HP: 75/200

🎮 Turno 9: Jogador
   Ação: REGEN (Orbe de Água)
   Buff aplicado: HOT +7 HP por turno por 99 turnos
   Cura: +7 HP
   Jogador HP: 82/200

👹 Turno 10: Orc Boss
   Ação: ATTACK
   Dano: 30
   Jogador HP: 52/200

🎮 Turno 11: Jogador
   Ação: ATTACK
   Dano: 72
   HOT: +7 HP
   Orc Boss HP: 311/500
   Jogador HP: 59/200

...continua até alguém morrer...
```

### **Implementação da Batalha**
```java
public class ExemploBatalhaCompleta {
    
    public void executarBatalha() {
        // 1. Criar personagens
        Player jogador = new Player(100, 100, 3, "down", "Herói", 
            new Barbarian(15), 200, 200, 100, 100, 50, 30, 10);
        
        Npc orcBoss = new Npc(200, 200, 2, "left", "Orc Boss", 
            new Orc(20), 500, 500, 50, 50, 40, 25, 10);
        
        // 2. Iniciar batalha
        Battle battle = new Battle(gamePanel);
        battle.startBattle(jogador, orcBoss);
        
        // 3. Simular ações do jogador
        simularAcoesJogador(battle);
    }
    
    private void simularAcoesJogador(Battle battle) {
        // Turno 1: Ataque básico
        battle.processPlayerAction("ATTACK");
        
        // Turno 3: Defender
        battle.processPlayerAction("DEFEND");
        
        // Turno 5: Habilidade especial
        battle.processPlayerAction("SPECIAL");
        
        // Turno 7: Ataque com buff
        battle.processPlayerAction("ATTACK");
        
        // Turno 9: Usar orbe de cura
        battle.processPlayerAction("REGEN");
        
        // Continuar até batalha terminar...
    }
}
```

---

## 🎨 **Sistema de Efeitos Visuais**

### **Efeitos de Batalha**
```java
public class SistemaEfeitosVisuais {
    
    public void mostrarEfeitoAtaque(Character atacante, Character alvo) {
        BattleEffectManager effectManager = gamePanel.getBattleEffectManager();
        
        if (atacante instanceof Player) {
            // Efeito no jogador
            String classe = atacante.getCharacterClass().getCharacterClassName();
            effectManager.triggerForPlayer(classe, "attack");
            
            // Efeito de dano no alvo
            effectManager.triggerForMonsterFromPlayer("damage");
        } else if (atacante instanceof Npc) {
            // Efeito no monstro
            String tipoMonstro = derivarTipoMonstro((Npc) atacante);
            effectManager.triggerForMonster(tipoMonstro, "attack");
            
            // Efeito de dano no jogador
            effectManager.triggerForPlayer("damage");
        }
    }
    
    public void mostrarEfeitoBuff(Character personagem, String tipoBuff) {
        BattleEffectManager effectManager = gamePanel.getBattleEffectManager();
        
        if (personagem instanceof Player) {
            String classe = personagem.getCharacterClass().getCharacterClassName();
            effectManager.triggerForPlayer(classe, tipoBuff);
        } else if (personagem instanceof Npc) {
            String tipoMonstro = derivarTipoMonstro((Npc) personagem);
            effectManager.triggerForMonster(tipoMonstro, tipoBuff);
        }
    }
}
```

### **Configuração de Efeitos**
```json
{
  "player": {
    "barbarian": {
      "attack": { "imagePath": "/sprites/effects/swordHit1time.gif", "durationMs": 700 },
      "shield": { "imagePath": "/sprites/effects/shield.gif", "durationMs": 800 },
      "special": { "imagePath": "/sprites/effects/fury.gif", "durationMs": 900 },
      "heal": { "imagePath": "/sprites/effects/heal.gif", "durationMs": 700 }
    }
  },
  "monster": {
    "orcboss": {
      "attack": { "imagePath": "/sprites/effects/swordHit1time.gif", "durationMs": 800 },
      "damage": { "imagePath": "/sprites/effects/swordHit1time.gif", "durationMs": 600 },
      "shield": { "imagePath": "/sprites/effects/shield.gif", "durationMs": 800 }
    }
  }
}
```

---

## 🎭 **Classes de Personagem**

### **Sistema de Classes**
```java
public interface CharacterClass {
    String getCharacterClassName();
    String getSpecialAttributeName();
    int getSpecialAttributeValue();
    String getSpecialAbilityName();
    void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp);
}
```

### **Exemplo: Classe Barbarian**
```java
public class Barbarian implements CharacterClass {
    
    @Override
    public String getCharacterClassName() {
        return "Barbarian";
    }
    
    @Override
    public String getSpecialAbilityName() {
        return "Fúria de Batalha";
    }
    
    @Override
    public void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp) {
        int manaCost = 15;
        
        if (attacker.getAttributeMana() >= manaCost) {
            // Consumir mana
            attacker.setAttributeMana(attacker.getAttributeMana() - manaCost);
            
            // Aplicar buff de força
            int bonus = (int)(attacker.getAttributeStrength() * 1.2);
            Buff strBuff = new Buff("STRENGTH", bonus, 6, 6, attacker);
            attacker.applyBuff(strBuff);
            
            // Mostrar efeito visual
            gp.getBattleEffectManager().triggerForPlayer("barbarian", "special");
        }
    }
}
```

### **Outras Classes**
```java
// Mago - Ataque Mágico
public class Mage implements CharacterClass {
    public void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp) {
        // Ataque mágico que ignora armadura
        int magicDamage = attacker.getAttributeStrength() + attacker.getAttributeMana() / 2;
        target.takeDamage(magicDamage);
    }
}

// Paladino - Ataque Sagrado
public class Paladin implements CharacterClass {
    public void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp) {
        // Ataque sagrado + cura para o jogador
        int holyDamage = attacker.getAttributeStrength() * 2;
        target.takeDamage(holyDamage);
        attacker.heal(holyDamage / 2);
    }
}

// Ranger - Flecha Certeira
public class Ranger implements CharacterClass {
    public void getSpecialAbility(Character attacker, Character target, int countTurn, GamePanel gp) {
        // Ataque que sempre acerta com dano crítico
        int criticalDamage = attacker.getAttributeStrength() * 3;
        target.takeDamage(criticalDamage);
    }
}
```

---

## 🔧 **Configuração e Uso**

### **Inicialização do Sistema**
```java
public class InicializacaoBatalha {
    
    public void inicializarSistemaBatalha(GamePanel gamePanel) {
        // 1. Criar sistema de batalha
        Battle battle = new Battle(gamePanel);
        
        // 2. Configurar efeitos visuais
        BattleEffectManager effectManager = new BattleEffectManager();
        effectManager.loadConfig("/battle_effects.json");
        
        // 3. Configurar sistema de áudio
        AudioManager audioManager = AudioManager.getInstance();
        audioManager.loadBattleSounds();
        
        // 4. Integrar com outros sistemas
        integrarComSistemas(gamePanel, battle);
    }
    
    private void integrarComSistemas(GamePanel gamePanel, Battle battle) {
        // Integração com sistema de Quest
        battle.setOnBattleEnd((victory, defeatedEnemy) -> {
            if (victory) {
                QuestManager.getInstance().onEnemyDefeated(defeatedEnemy);
            }
        });
        
        // Integração com sistema de Inventário
        battle.setOnItemUsed((item, character) -> {
            inventoryManager.removeItem(item);
        });
        
        // Integração com sistema de Experiência
        battle.setOnBattleEnd((victory, defeatedEnemy) -> {
            if (victory) {
                player.gainExperience(defeatedEnemy.getExperienceReward());
            }
        });
    }
}
```

### **Integração com Outros Sistemas**
```java
public class IntegracaoSistemas {
    
    // Integração com sistema de Quest
    public void onEnemyDefeated(Npc enemy) {
        QuestManager questManager = QuestManager.getInstance();
        questManager.updateObjective("main_orb_quest", "kill_" + enemy.getId(), true);
        
        // Spawnar orbe se for boss
        if (enemy.getName().contains("Boss")) {
            spawnOrbAfterBossDefeat(enemy);
        }
    }
    
    // Integração com sistema de Inventário
    public void onItemUsed(Item item, Character character) {
        if (item.getType() == ItemType.HEALTH_POTION) {
            int healAmount = item.getHealValue();
            character.heal(healAmount);
            inventoryManager.removeItem(item);
        }
    }
    
    // Integração com sistema de Áudio
    public void onBattleAction(String action, Character character) {
        AudioManager audioManager = AudioManager.getInstance();
        
        switch (action) {
            case "ATTACK":
                audioManager.playSoundEffect("attack");
                break;
            case "DEFEND":
                audioManager.playSoundEffect("shield");
                break;
            case "SPECIAL":
                audioManager.playSoundEffect("special_ability");
                break;
        }
    }
}
```

---

## 🎯 **Casos de Uso Avançados**

### **Batalha com Múltiplos Inimigos**
```java
public class BatalhaMultipla {
    
    public void iniciarBatalhaGrupo(Player player, List<Npc> inimigos) {
        Battle battle = new Battle(gamePanel);
        
        // Adicionar todos os inimigos à ordem de turnos
        battle.addEnemies(inimigos);
        
        // Determinar ordem baseada na agilidade
        battle.determineTurnOrder();
        
        // Iniciar batalha
        battle.startBattle(player, inimigos.get(0));
    }
    
    public void processarTurnoGrupo() {
        Character personagemAtual = getCurrentCharacter();
        
        if (personagemAtual instanceof Player) {
            // Aguardar input do jogador
            waitingForPlayerInput = true;
        } else if (personagemAtual instanceof Npc) {
            // Processar IA do monstro
            processarIAGrupo((Npc) personagemAtual);
        }
    }
    
    private void processarIAGrupo(Npc monstro) {
        // IA mais inteligente para grupos
        if (monstro.getAttributeHealth() < monstro.getAttributeMaxHealth() * 0.3) {
            // Se com pouca vida, 50% chance de defender
            if (Math.random() < 0.5) {
                defend(monstro);
                return;
            }
        }
        
        // Ataque normal
        attack(monstro, player);
    }
}
```

### **Sistema de Status Effects**
```java
public class SistemaStatusEffects {
    
    public void aplicarStatusEffect(Character personagem, String effect, int duracao) {
        switch (effect) {
            case "POISON":
                Buff poison = new Buff("DOT", 5, duracao, 0, null);
                personagem.applyBuff(poison);
                break;
                
            case "STUN":
                personagem.setStunned(true);
                personagem.setStunDuration(duracao);
                break;
                
            case "FREEZE":
                personagem.setFrozen(true);
                personagem.setFreezeDuration(duracao);
                break;
                
            case "BURN":
                Buff burn = new Buff("DOT", 8, duracao, 0, null);
                personagem.applyBuff(burn);
                break;
        }
    }
    
    public void atualizarStatusEffects(Character personagem) {
        if (personagem.isStunned()) {
            personagem.decrementStunDuration();
            if (personagem.getStunDuration() <= 0) {
                personagem.setStunned(false);
            }
        }
        
        if (personagem.isFrozen()) {
            personagem.decrementFreezeDuration();
            if (personagem.getFreezeDuration() <= 0) {
                personagem.setFrozen(false);
            }
        }
    }
}
```

---

## 🏆 **Vantagens do Sistema**

### **✅ Para o Jogador**
- **Estratégia:** Decisões táticas em cada turno
- **Variedade:** Diferentes classes e habilidades
- **Progressão:** Sistema de buffs e debuffs
- **Feedback Visual:** Efeitos claros e satisfatórios

### **✅ Para o Desenvolvedor**
- **Modular:** Sistema organizado em componentes
- **Extensível:** Fácil de adicionar novas classes
- **Configurável:** Efeitos visuais via JSON
- **Integrado:** Conecta com todos os sistemas

### **✅ Para o Jogo**
- **Profundidade:** Sistema complexo e estratégico
- **Variedade:** Diferentes tipos de combate
- **Imersão:** Efeitos visuais e sonoros
- **Replayability:** Múltiplas estratégias possíveis

---

## 🚀 **Conclusão**

Este sistema de Batalha oferece:

- **⚔️ Combate Estratégico:** Sistema de turnos baseado em agilidade
- **🎭 Classes Diversas:** Cada classe com habilidades únicas
- **🔮 Sistema de Buffs:** Efeitos temporários que modificam combate
- **🎨 Efeitos Visuais:** Animações e feedback visual claro
- **🔊 Integração de Áudio:** Sons e música de batalha
- **🎮 Interface Intuitiva:** Controles simples e responsivos

**Resultado:** Um sistema completo de batalha que oferece combates estratégicos, variados e visualmente impressionantes! ⚔️✨

---

## 📋 **Resumo dos Conceitos**

| Conceito | O que faz | Exemplo |
|----------|-----------|---------|
| **Battle** | Controlador principal da batalha | Gerencia turnos e ações |
| **Turn Order** | Ordem baseada na agilidade | Jogador (30) → Monstro (25) |
| **Buff** | Efeito temporário | +20% força por 3 turnos |
| **Special Ability** | Habilidade única da classe | Fúria de Batalha (Barbarian) |
| **BattleEffect** | Efeito visual | Animação de ataque |
| **CharacterClass** | Classe do personagem | Barbarian, Mage, Paladin |

**Este sistema transforma o jogo em uma experiência de combate estratégica e envolvente!** 🎮🏆

