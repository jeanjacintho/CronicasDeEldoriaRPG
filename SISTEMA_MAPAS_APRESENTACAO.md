# 🗺️ Sistema de Mapas - Apresentação Didática

## 📖 **O que é este sistema?**

Imagine que você está criando um **mundo virtual** onde o jogador pode explorar diferentes lugares como cidades, florestas, dungeons e casas. O sistema de Mapas é exatamente isso! Ele permite criar ambientes complexos com múltiplas camadas, objetos interativos, teleportes e sistema de colisão.

---

## 🎮 **Conceitos Básicos**

### **Tile - Peça do Mapa**
- **O que é?** Uma pequena imagem que forma parte do mapa
- **Como funciona?** Como peças de um quebra-cabeça
- **Exemplo:** Grama, pedra, água, parede

### **Mapa - Ambiente Completo**
- **O que é?** Um ambiente formado por muitos tiles
- **Como funciona?** Múltiplas camadas sobrepostas
- **Exemplo:** Cidade, floresta, dungeon, casa

### **Teleporte - Transporte Instantâneo**
- **O que é?** Sistema que move o jogador entre mapas
- **Como funciona?** Pontos de entrada e saída configurados
- **Exemplo:** Porta da casa → cidade, entrada da dungeon → floresta

---

## 🏗️ **Como o Sistema Funciona**

### **1. Estrutura de um Mapa**
```java
// Estrutura básica de um mapa JSON
{
  "tileSize": 16,           // Tamanho de cada tile em pixels
  "mapWidth": 56,           // Largura do mapa em tiles
  "mapHeight": 54,          // Altura do mapa em tiles
  "message": "Vila de Eldoria", // Mensagem ao entrar no mapa
  "layers": [               // Camadas do mapa
    {
      "name": "Layer_0",    // Nome da camada
      "tiles": [            // Lista de tiles
        {
          "id": "2_37",     // ID do tile
          "x": 33,          // Posição X em tiles
          "y": 2            // Posição Y em tiles
        }
      ],
      "collider": true      // Se a camada tem colisão
    }
  ]
}
```

**O que acontece aqui?**
- Definimos o tamanho e dimensões do mapa
- Criamos camadas (como folhas transparentes)
- Colocamos tiles em posições específicas
- Configuramos colisões

### **2. Sistema de Camadas**
```java
// Múltiplas camadas sobrepostas
public class SistemaCamadas {
    
    public void criarMapaComCamadas() {
        // Camada 0: Fundo (grama, água, etc.)
        MapLayer fundo = new MapLayer("fundo", false);
        fundo.addTile(new MapTile("grass", 10, 10));
        fundo.addTile(new MapTile("water", 15, 15));
        
        // Camada 1: Estruturas (paredes, casas, etc.)
        MapLayer estruturas = new MapLayer("estruturas", true);
        estruturas.addTile(new MapTile("wall", 12, 12));
        estruturas.addTile(new MapTile("house", 20, 20));
        
        // Camada 2: Objetos (mesas, cadeiras, etc.)
        MapLayer objetos = new MapLayer("objetos", true);
        objetos.addTile(new MapTile("table", 25, 25));
        objetos.addTile(new MapTile("chair", 26, 25));
        
        // Camada 3: Teleportes (portas, escadas, etc.)
        MapLayer teleportes = new MapLayer("teleportes", false);
        teleportes.addTile(new MapTile("door", 30, 30, "city", 10, 10));
    }
}
```

**Por que usar camadas?**
- **Organização:** Cada camada tem um propósito
- **Flexibilidade:** Pode mostrar/esconder camadas
- **Colisão:** Apenas algumas camadas têm colisão
- **Renderização:** Ordem de desenho controlada

### **3. Sistema de Teleportes**
```java
// Configuração de teleportes
public class SistemaTeleportes {
    
    public void configurarTeleportes() {
        TeleportManager teleportManager = TeleportManager.getInstance();
        
        // Teleporte da casa para a cidade
        TeleportConfig casaParaCidade = new TeleportConfig();
        casaParaCidade.map = "city";
        casaParaCidade.name = "Cidade de Eldoria";
        casaParaCidade.spawnPoints.put("entrance", new int[]{34, 4});
        
        teleportManager.addTeleport("house_to_city", casaParaCidade);
        
        // Teleporte da cidade para a floresta
        TeleportConfig cidadeParaFloresta = new TeleportConfig();
        cidadeParaFloresta.map = "forest";
        cidadeParaFloresta.name = "Floresta de Eldoria";
        cidadeParaFloresta.spawnPoints.put("entrance", new int[]{9, 60});
        
        teleportManager.addTeleport("city_to_forest", cidadeParaFloresta);
    }
    
    public void executarTeleporte(String teleportId) {
        TeleportManager teleportManager = TeleportManager.getInstance();
        TeleportConfig config = teleportManager.getTeleport(teleportId);
        
        if (config != null) {
            // Carregar novo mapa
            gamePanel.loadMap(config.map);
            
            // Posicionar jogador no ponto de spawn
            int[] spawnPoint = config.getFirstSpawnPoint();
            player.setWorldX(spawnPoint[0] * tileSize);
            player.setWorldY(spawnPoint[1] * tileSize);
            
            // Mostrar mensagem
            gamePanel.showMessage("Você foi teleportado para " + config.name + "!");
        }
    }
}
```

---

## 🎯 **Exemplo Prático: Mapa da Cidade**

### **Cenário: Vila de Eldoria**

```
🏠 Casa do Jogador
    ↓ (teleporte: porta)
🏘️ Vila de Eldoria
    ├── 🏪 Padaria (teleporte: entrada)
    ├── ⚒️ Ferreiro (teleporte: entrada)
    ├── 📚 Biblioteca (teleporte: entrada)
    ├── 🏠 Casas dos Aldeões (teleporte: entrada)
    └── 🌲 Saída para Floresta (teleporte: entrada)
        ↓ (teleporte: entrada)
🌲 Floresta de Eldoria
    ├── 🗡️ Dungeon 2 (teleporte: entrada)
    ├── 🗡️ Dungeon 4 (teleporte: entrada)
    └── 🏛️ Totem Central (teleporte: entrada)
```

### **Implementação do Mapa**
```java
public class MapaVilaEldoria {
    
    public void criarMapaVila() {
        // 1. Configurar dimensões do mapa
        MapJson mapaVila = new MapJson();
        mapaVila.tileSize = 16;
        mapaVila.mapWidth = 56;
        mapaVila.mapHeight = 54;
        mapaVila.message = "Bem-vindo à Vila de Eldoria!";
        
        // 2. Camada de fundo (grama)
        MapLayer fundo = new MapLayer("fundo", false);
        for (int x = 0; x < 56; x++) {
            for (int y = 0; y < 54; y++) {
                fundo.addTile(new MapTile("grass", x, y));
            }
        }
        
        // 3. Camada de estruturas (casas, paredes)
        MapLayer estruturas = new MapLayer("estruturas", true);
        
        // Casa da padaria
        estruturas.addTile(new MapTile("house_wall", 30, 35));
        estruturas.addTile(new MapTile("house_wall", 31, 35));
        estruturas.addTile(new MapTile("house_wall", 32, 35));
        estruturas.addTile(new MapTile("house_wall", 30, 36));
        estruturas.addTile(new MapTile("house_wall", 32, 36));
        estruturas.addTile(new MapTile("house_wall", 30, 37));
        estruturas.addTile(new MapTile("house_wall", 31, 37));
        estruturas.addTile(new MapTile("house_wall", 32, 37));
        
        // Casa do ferreiro
        estruturas.addTile(new MapTile("house_wall", 20, 15));
        estruturas.addTile(new MapTile("house_wall", 21, 15));
        estruturas.addTile(new MapTile("house_wall", 22, 15));
        // ... mais paredes
        
        // 4. Camada de teleportes
        MapLayer teleportes = new MapLayer("teleportes", false);
        
        // Porta da padaria
        teleportes.addTile(new MapTile("door", 31, 36, "bakery", 2, 5));
        
        // Porta do ferreiro
        teleportes.addTile(new MapTile("door", 21, 16, "smith_house", 5, 8));
        
        // Saída para floresta
        teleportes.addTile(new MapTile("forest_exit", 4, 34, "forest", 9, 60));
        
        // 5. Adicionar camadas ao mapa
        mapaVila.layers.add(fundo);
        mapaVila.layers.add(estruturas);
        mapaVila.layers.add(teleportes);
        
        // 6. Salvar mapa
        salvarMapa("city.json", mapaVila);
    }
}
```

---

## 🔄 **Sistema de Colisão**

### **Detecção de Colisões**
```java
public class SistemaColisao {
    
    public void verificarColisoes(Entity entidade) {
        ColisionChecker colisionChecker = new ColisionChecker(gamePanel);
        
        // 1. Verificar colisão com tiles
        colisionChecker.checkTile(entidade);
        
        // 2. Verificar colisão com outras entidades
        colisionChecker.checkEntity(entidade, npcs);
        
        // 3. Verificar colisão com objetos
        colisionChecker.checkObject(entidade);
        
        // 4. Verificar colisão com teleportes
        verificarTeleporte(entidade);
    }
    
    private void verificarTeleporte(Entity entidade) {
        // Obter tile na posição da entidade
        int tileX = entidade.getWorldX() / tileSize;
        int tileY = entidade.getWorldY() / tileSize;
        
        MapTile teleportTile = tileManager.getTeleportTile(tileX, tileY);
        
        if (teleportTile != null && teleportTile.interactive) {
            // Executar teleporte
            executarTeleporte(teleportTile);
        }
    }
}
```

### **Otimizações de Performance**
```java
public class ColisionCheckerOtimizado {
    
    // Cache de hitboxes para evitar criação excessiva
    private Map<Entity, Rectangle> entityHitboxCache = new HashMap<>();
    private Map<String, Boolean> collisionCache = new HashMap<>();
    
    public void checkTile(Entity entity) {
        // Usar cache para evitar recálculos
        String cacheKey = generateCacheKey(entity);
        Boolean cachedResult = collisionCache.get(cacheKey);
        
        if (cachedResult != null) {
            entity.setCollisionOn(cachedResult);
            return;
        }
        
        // Verificar colisão apenas nas bordas da entidade
        int entityLeftCol = (entity.getWorldX() + entity.getHitbox().x) / tileSize;
        int entityRightCol = (entity.getWorldX() + entity.getHitbox().x + entity.getHitbox().width) / tileSize;
        int entityTopRow = (entity.getWorldY() + entity.getHitbox().y) / tileSize;
        int entityBottomRow = (entity.getWorldY() + entity.getHitbox().y + entity.getHitbox().height) / tileSize;
        
        boolean hasCollision = false;
        
        // Verificar apenas tiles relevantes baseado na direção
        switch (entity.getDirection()) {
            case "up":
                for (int col = entityLeftCol; col <= entityRightCol; col++) {
                    if (tileManager.isCollisionAt(col, entityTopRow - 1)) {
                        hasCollision = true;
                        break;
                    }
                }
                break;
            case "down":
                for (int col = entityLeftCol; col <= entityRightCol; col++) {
                    if (tileManager.isCollisionAt(col, entityBottomRow + 1)) {
                        hasCollision = true;
                        break;
                    }
                }
                break;
            // ... outras direções
        }
        
        // Armazenar resultado no cache
        collisionCache.put(cacheKey, hasCollision);
        entity.setCollisionOn(hasCollision);
    }
}
```

---

## 🎨 **Sistema de Renderização**

### **Renderização por Camadas**
```java
public class SistemaRenderizacao {
    
    public void desenharMapa(Graphics2D g2) {
        TileManager tileManager = gamePanel.getTileManager();
        
        // 1. Desenhar camadas de fundo (sem colisão)
        for (MapLayer layer : tileManager.getBackgroundLayers()) {
            desenharCamada(g2, layer);
        }
        
        // 2. Desenhar entidades (jogador, NPCs)
        desenharEntidades(g2);
        
        // 3. Desenhar camadas de primeiro plano
        for (MapLayer layer : tileManager.getForegroundLayers()) {
            desenharCamada(g2, layer);
        }
        
        // 4. Desenhar objetos interativos
        desenharObjetos(g2);
        
        // 5. Desenhar teleportes (se debug ativo)
        if (debugMode) {
            desenharTeleportes(g2);
        }
    }
    
    private void desenharCamada(Graphics2D g2, MapLayer layer) {
        for (MapTile tile : layer.getTiles()) {
            BufferedImage sprite = tileManager.getTileSprite(tile.id);
            if (sprite != null) {
                int screenX = tile.x * tileSize - player.getWorldX() + player.getScreenX();
                int screenY = tile.y * tileSize - player.getWorldY() + player.getScreenY();
                
                // Só desenhar se estiver na tela
                if (isOnScreen(screenX, screenY)) {
                    g2.drawImage(sprite, screenX, screenY, tileSize, tileSize, null);
                }
            }
        }
    }
    
    private void desenharTeleportes(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2));
        
        for (MapTile teleportTile : tileManager.getTeleportTiles()) {
            int screenX = teleportTile.x * tileSize - player.getWorldX() + player.getScreenX();
            int screenY = teleportTile.y * tileSize - player.getWorldY() + player.getScreenY();
            
            if (isOnScreen(screenX, screenY)) {
                g2.drawRect(screenX, screenY, tileSize, tileSize);
                g2.drawString(teleportTile.toMap, screenX, screenY - 5);
            }
        }
    }
}
```

---

## 🔧 **Configuração e Uso**

### **Inicialização do Sistema**
```java
public class InicializacaoMapa {
    
    public void inicializarSistemaMapa(GamePanel gamePanel) {
        // 1. Criar TileManager
        TileManager tileManager = new TileManager(gamePanel);
        
        // 2. Carregar definições de tiles
        tileManager.loadTilesFromJson();
        
        // 3. Carregar mapa inicial
        tileManager.loadMapJson("/maps/houses/player_house.json");
        
        // 4. Configurar sistema de colisão
        ColisionChecker colisionChecker = new ColisionChecker(gamePanel);
        
        // 5. Configurar sistema de teleportes
        TeleportManager teleportManager = TeleportManager.getInstance();
        teleportManager.loadTeleportConfigs();
        
        // 6. Configurar ObjectManager
        ObjectManager objectManager = new ObjectManager(gamePanel, objectSpriteLoader, tileManager.getObjectTiles());
    }
}
```

### **Integração com Outros Sistemas**
```java
public class IntegracaoSistemas {
    
    // Integração com sistema de Quest
    public void onPlayerEnterMap(String mapName) {
        QuestManager questManager = QuestManager.getInstance();
        questManager.onPlayerEnterMap(mapName);
        
        // Atualizar objetivos de "chegar a localização"
        questManager.updateObjective("main_orb_quest", "reach_" + mapName, true);
    }
    
    // Integração com sistema de Áudio
    public void onMapChange(String mapName) {
        AudioManager audioManager = AudioManager.getInstance();
        AudioContext newContext = AudioContext.fromMapName(mapName);
        audioManager.changeContext(newContext);
        
        // Reproduzir efeito sonoro de mudança de mapa
        audioManager.playSoundEffect("teleport");
    }
    
    // Integração com sistema de NPCs
    public void loadNpcsFromMap() {
        List<Npc> mapNpcs = tileManager.getNpcsFromMap();
        for (Npc npc : mapNpcs) {
            npcManager.addNpc(npc);
        }
    }
}
```

---

## 🎯 **Casos de Uso Avançados**

### **Mapa Dinâmico com Eventos**
```java
public class MapaDinamico {
    
    public void criarMapaComEventos() {
        MapJson mapaEvento = new MapJson();
        
        // Configurar eventos baseados em quests
        mapaEvento.setQuestData("required_quest", "main_orb_quest");
        mapaEvento.setQuestData("unlock_condition", "orb_1_completed");
        
        // Adicionar tiles condicionais
        MapLayer tilesCondicionais = new MapLayer("condicionais", true);
        
        // Porta que só abre após completar quest
        MapTile portaBloqueada = new MapTile("locked_door", 25, 25);
        portaBloqueada.setCondition("quest_completed:main_orb_quest");
        tilesCondicionais.addTile(portaBloqueada);
        
        // NPC que só aparece em certas condições
        MapTile npcCondicional = new MapTile("special_npc", 30, 30);
        npcCondicional.setCondition("orb_count:>=2");
        tilesCondicionais.addTile(npcCondicional);
    }
    
    public void atualizarMapaDinamico() {
        QuestManager questManager = QuestManager.getInstance();
        
        // Verificar condições e atualizar tiles
        for (MapTile tile : conditionalTiles) {
            if (verificarCondicao(tile.getCondition())) {
                tile.setVisible(true);
                tile.setCollision(tile.getOriginalCollision());
            } else {
                tile.setVisible(false);
                tile.setCollision(false);
            }
        }
    }
}
```

### **Sistema de Mini-Mapas**
```java
public class SistemaMiniMapa {
    
    public void desenharMiniMapa(Graphics2D g2) {
        int miniMapSize = 200;
        int miniMapX = gamePanel.getWidth() - miniMapSize - 10;
        int miniMapY = 10;
        
        // Fundo do mini-mapa
        g2.setColor(Color.BLACK);
        g2.fillRect(miniMapX, miniMapY, miniMapSize, miniMapSize);
        
        // Calcular escala
        float scaleX = (float) miniMapSize / (mapWidth * tileSize);
        float scaleY = (float) miniMapSize / (mapHeight * tileSize);
        
        // Desenhar tiles importantes
        for (MapTile tile : importantTiles) {
            int miniX = (int) (tile.x * tileSize * scaleX) + miniMapX;
            int miniY = (int) (tile.y * tileSize * scaleY) + miniMapY;
            
            g2.setColor(getTileColor(tile.id));
            g2.fillRect(miniX, miniY, 2, 2);
        }
        
        // Desenhar posição do jogador
        int playerMiniX = (int) (player.getWorldX() * scaleX) + miniMapX;
        int playerMiniY = (int) (player.getWorldY() * scaleY) + miniMapY;
        
        g2.setColor(Color.RED);
        g2.fillOval(playerMiniX - 2, playerMiniY - 2, 4, 4);
    }
}
```

---

## 🏆 **Vantagens do Sistema**

### **✅ Para o Jogador**
- **Exploração Rica:** Múltiplos ambientes para explorar
- **Transições Suaves:** Teleportes funcionais e intuitivos
- **Imersão:** Ambientes detalhados e realistas
- **Progressão:** Desbloqueio de novas áreas

### **✅ Para o Desenvolvedor**
- **Modular:** Sistema organizado em componentes
- **Flexível:** Fácil de criar novos mapas
- **Otimizado:** Performance otimizada com cache
- **Extensível:** Fácil de adicionar novas funcionalidades

### **✅ Para o Jogo**
- **Variedade:** Diferentes tipos de ambientes
- **Profundidade:** Sistema complexo e rico
- **Integração:** Conecta com todos os outros sistemas
- **Escalabilidade:** Suporta mapas grandes e complexos

---

## 🚀 **Conclusão**

Este sistema de Mapas oferece:

- **🗺️ Mundos Complexos:** Múltiplas camadas e ambientes
- **🔄 Teleportes Inteligentes:** Sistema de transporte configurável
- **🚫 Colisão Otimizada:** Detecção eficiente de obstáculos
- **🎨 Renderização Eficiente:** Desenho otimizado por camadas
- **🔧 Configuração Flexível:** Arquivos JSON fáceis de editar
- **🎮 Integração Completa:** Conecta com todos os sistemas

**Resultado:** Um sistema completo de mapas que cria mundos ricos e exploráveis, com transições suaves e performance otimizada! 🗺️✨

---

## 📋 **Resumo dos Conceitos**

| Conceito | O que faz | Exemplo |
|----------|-----------|---------|
| **Tile** | Peça básica do mapa | Grama, pedra, água |
| **Mapa** | Ambiente completo | Cidade, floresta, dungeon |
| **Camada** | Folha transparente com tiles | Fundo, estruturas, objetos |
| **Teleporte** | Transporte entre mapas | Porta, escada, entrada |
| **Colisão** | Detecção de obstáculos | Paredes, objetos sólidos |
| **TileManager** | Controla todos os mapas | Singleton que gerencia tudo |

**Este sistema transforma o jogo em um mundo vasto e explorável com ambientes ricos e detalhados!** 🎮🏆

