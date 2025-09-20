# 🗺️ Sistema de Mapa - Documentação Completa da API

## 🎯 **Visão Geral**

Este documento especifica **todas as funcionalidades** disponíveis no sistema de mapa do CronicasDeEldoriaRPG, incluindo métodos, classes, estruturas de dados e exemplos práticos de uso. O sistema implementa mapas baseados em tiles com múltiplas camadas, sistema de colisão, teleportes e objetos interativos.

---

## 🏗️ **Arquitetura do Sistema**

### **Classes Principais**
```
TileManager              → Gerenciador principal de tiles e mapas
├── Tile                → Tile individual com imagem e colisão
├── MapLayer            → Camada do mapa com tiles
├── MapTile             → Tile posicionado no mapa
└── MapJson             → Estrutura JSON do mapa

ObjectManager           → Gerenciador de objetos do mapa
├── MapObject           → Objeto interativo do mapa
├── ObjectSpriteLoader  → Carregador de sprites de objetos
└── TotemCentral        → Objeto especial (Totem Central)

TeleportManager         → Gerenciador de teleportes
├── TeleportConfig      → Configuração de teleporte
└── TeleportManager     → Singleton para gerenciar teleportes

ColisionChecker         → Sistema de detecção de colisão
└── ColisionChecker     → Verificador de colisões
```

### **Estruturas de Dados**
```
MapJson                 → Arquivo JSON do mapa
├── tileSize           → Tamanho dos tiles
├── mapWidth           → Largura do mapa em tiles
├── mapHeight          → Altura do mapa em tiles
├── message            → Mensagem ao entrar no mapa
└── layers             → Lista de camadas

MapLayer                → Camada do mapa
├── name               → Nome da camada
├── tiles              → Lista de tiles da camada
└── collider           → Se a camada tem colisão

MapTile                 → Tile posicionado
├── id                 → ID do tile
├── x                  → Posição X em tiles
├── y                  → Posição Y em tiles
├── interactive        → Se é interativo
├── toMap              → Mapa de destino (teleportes)
├── toX                → Posição X de destino
└── toY                → Posição Y de destino
```

---

## 🗺️ **TileManager - Gerenciador Principal**

### **🔧 Inicialização e Configuração**

#### **Construtor**
```java
TileManager tileManager = new TileManager(gamePanel);
```
- **Descrição**: Cria uma nova instância do gerenciador de tiles
- **Parâmetros**: `GamePanel gamePanel` - Referência ao painel do jogo
- **Funcionalidade**: 
  - Carrega tiles do arquivo `tiles.json`
  - Carrega mapa inicial (`player_house.json`)
  - Configura spritesheet e definições de tiles

#### **`loadTilesFromJson()`**
```java
// Chamado automaticamente no construtor
tileManager.loadTilesFromJson();
```
- **Descrição**: Carrega definições de tiles do arquivo JSON
- **Arquivo**: `/tiles.json`
- **Funcionalidade**:
  - Carrega spritesheet de tiles
  - Cria mapa de tiles com IDs
  - Configura propriedades de colisão
  - Usa tileSize escalado do GamePanel

#### **`loadMapJson(String path)`**
```java
tileManager.loadMapJson("/maps/city.json");
tileManager.loadMapJson("/maps/houses/player_house.json");
```
- **Descrição**: Carrega um novo mapa do arquivo JSON
- **Parâmetros**: `String path` - Caminho para o arquivo JSON do mapa
- **Funcionalidade**:
  - Carrega estrutura do mapa
  - Define dimensões do mapa
  - Carrega todas as camadas
  - Exibe mensagem de boas-vindas se presente

---

### **🎨 Sistema de Renderização**

#### **`draw(Graphics2D g2)`**
```java
tileManager.draw(graphics2D);
```
- **Descrição**: Renderiza todas as camadas do mapa
- **Parâmetros**: `Graphics2D g2` - Contexto gráfico
- **Funcionalidade**:
  - Renderiza camadas de fundo
  - Renderiza o jogador
  - Renderiza camadas de overlay
  - Otimiza renderização por área visível

#### **`drawBackgroundLayers(Graphics2D g2)`**
```java
tileManager.drawBackgroundLayers(graphics2D);
```
- **Descrição**: Renderiza apenas as camadas de fundo
- **Parâmetros**: `Graphics2D g2` - Contexto gráfico
- **Funcionalidade**:
  - Renderiza camadas normais (fundo)
  - Renderiza objetos
  - Renderiza NPCs se existirem
  - Renderiza monstros se existirem
  - Não renderiza camadas overlay

#### **`drawOverlayLayers(Graphics2D g2)`**
```java
tileManager.drawOverlayLayers(graphics2D);
```
- **Descrição**: Renderiza apenas as camadas de overlay
- **Parâmetros**: `Graphics2D g2` - Contexto gráfico
- **Funcionalidade**:
  - Renderiza camadas com "overlay" no nome
  - Renderiza acima do jogador
  - Usado para elementos que ficam por cima

#### **`renderLayer(Graphics2D g2, MapLayer layer, ...)`**
```java
// Chamado internamente pelos métodos de renderização
tileManager.renderLayer(graphics2D, layer, firstCol, lastCol, firstRow, lastRow, playerWorldX, playerWorldY, screenX, screenY);
```
- **Descrição**: Renderiza uma camada específica
- **Parâmetros**: 
  - `Graphics2D g2` - Contexto gráfico
  - `MapLayer layer` - Camada a ser renderizada
  - `int firstCol, lastCol` - Colunas visíveis
  - `int firstRow, lastRow` - Linhas visíveis
  - `int playerWorldX, playerWorldY` - Posição do jogador
  - `int screenX, screenY` - Posição na tela
- **Funcionalidade**:
  - Renderiza apenas tiles visíveis
  - Calcula posições de desenho
  - Aplica escala de tiles

---

### **🔍 Sistema de Colisão**

#### **`isCollisionAt(int x, int y)`**
```java
boolean hasCollision = tileManager.isCollisionAt(10, 15);
if (hasCollision) {
    System.out.println("Posição tem colisão!");
}
```
- **Descrição**: Verifica se há colisão em uma posição específica
- **Parâmetros**: 
  - `int x` - Posição X em tiles
  - `int y` - Posição Y em tiles
- **Retorno**: `boolean` - true se há colisão
- **Funcionalidade**:
  - Verifica todas as camadas com `collider = true`
  - Retorna true se encontrar tile na posição

---

### **📊 Informações do Mapa**

#### **`getMapWidth()`**
```java
int width = tileManager.getMapWidth();
System.out.println("Largura do mapa: " + width + " tiles");
```
- **Descrição**: Retorna a largura do mapa em tiles
- **Retorno**: `int` - Largura do mapa

#### **`getMapHeight()`**
```java
int height = tileManager.getMapHeight();
System.out.println("Altura do mapa: " + height + " tiles");
```
- **Descrição**: Retorna a altura do mapa em tiles
- **Retorno**: `int` - Altura do mapa

#### **`getTileSize()`**
```java
int tileSize = tileManager.getTileSize();
System.out.println("Tamanho do tile: " + tileSize + " pixels");
```
- **Descrição**: Retorna o tamanho dos tiles em pixels
- **Retorno**: `int` - Tamanho do tile

---

### **🎯 Filtros de Tiles**

#### **`getNpcTiles()`**
```java
List<MapTile> npcTiles = tileManager.getNpcTiles();
for (MapTile tile : npcTiles) {
    System.out.println("NPC: " + tile.id + " em (" + tile.x + ", " + tile.y + ")");
}
```
- **Descrição**: Retorna todos os tiles de NPCs
- **Retorno**: `List<MapTile>` - Lista de tiles de NPCs
- **Funcionalidade**: Filtra camadas com "npcs" ou "npc" no nome

#### **`getMonsterTiles()`**
```java
List<MapTile> monsterTiles = tileManager.getMonsterTiles();
for (MapTile tile : monsterTiles) {
    System.out.println("Monstro: " + tile.id + " em (" + tile.x + ", " + tile.y + ")");
}
```
- **Descrição**: Retorna todos os tiles de monstros
- **Retorno**: `List<MapTile>` - Lista de tiles de monstros
- **Funcionalidade**: Filtra camadas com "monsters" ou "monster" no nome

#### **`getTeleportTiles()`**
```java
List<MapTile> teleportTiles = tileManager.getTeleportTiles();
for (MapTile tile : teleportTiles) {
    System.out.println("Teleporte: " + tile.id + " em (" + tile.x + ", " + tile.y + ")");
}
```
- **Descrição**: Retorna todos os tiles de teleporte
- **Retorno**: `List<MapTile>` - Lista de tiles de teleporte
- **Funcionalidade**: Filtra camadas com "teleport" no nome

#### **`getObjectTiles()`**
```java
List<MapTile> objectTiles = tileManager.getObjectTiles();
for (MapTile tile : objectTiles) {
    System.out.println("Objeto: " + tile.id + " em (" + tile.x + ", " + tile.y + ")");
}
```
- **Descrição**: Retorna todos os tiles de objetos
- **Retorno**: `List<MapTile>` - Lista de tiles de objetos
- **Funcionalidade**: Filtra camadas com "objetos", "objects" ou "items" no nome

---

## 🎮 **ObjectManager - Gerenciador de Objetos**

### **🔧 Inicialização**

#### **Construtor**
```java
ObjectManager objectManager = new ObjectManager(gamePanel, objectSpriteLoader, objectTiles);
```
- **Descrição**: Cria um novo gerenciador de objetos
- **Parâmetros**: 
  - `GamePanel gamePanel` - Referência ao painel do jogo
  - `ObjectSpriteLoader objectSpriteLoader` - Carregador de sprites
  - `List<MapTile> objectTiles` - Tiles brutos dos objetos
- **Funcionalidade**: 
  - Gerencia objetos ativos baseado na proximidade do jogador
  - Otimiza performance carregando apenas objetos próximos

#### **`updateActiveObjects(int playerTileX, int playerTileY)`**
```java
objectManager.updateActiveObjects(playerTileX, playerTileY);
```
- **Descrição**: Atualiza lista de objetos ativos baseado na posição do jogador
- **Parâmetros**: 
  - `int playerTileX` - Tile X do jogador
  - `int playerTileY` - Tile Y do jogador
- **Funcionalidade**:
  - Adiciona objetos próximos à lista ativa
  - Remove objetos distantes da lista ativa
  - Usa buffer de 2 tiles para otimização

#### **`instantiateMapObject(MapTile raw)`**
```java
MapObject mapObject = objectManager.instantiateMapObject(rawTile);
```
- **Descrição**: Cria instância de objeto do mapa
- **Parâmetros**: `MapTile raw` - Tile bruto do objeto
- **Retorno**: `MapObject` - Objeto instanciado ou null
- **Funcionalidade**:
  - Cria objetos especiais (ex: TotemCentral)
  - Cria MapObjects comuns
  - Configura propriedades de interação

### **📋 Getters**

#### **`getActiveObjects()`**
```java
List<MapObject> activeObjects = objectManager.getActiveObjects();
for (MapObject obj : activeObjects) {
    System.out.println("Objeto ativo: " + obj.getName());
}
```
- **Descrição**: Retorna lista de objetos ativos
- **Retorno**: `List<MapObject>` - Lista de objetos ativos

#### **`getRawObjectTiles()`**
```java
List<MapTile> rawTiles = objectManager.getRawObjectTiles();
System.out.println("Total de objetos no mapa: " + rawTiles.size());
```
- **Descrição**: Retorna lista de tiles brutos de objetos
- **Retorno**: `List<MapTile>` - Lista de tiles brutos

---

## 🎯 **MapObject - Objeto do Mapa**

### **🔧 Criação**

#### **Construtor**
```java
MapObject mapObject = new MapObject(
    "chest_01",                    // ID do objeto
    "Baú de Tesouro",              // Nome
    worldX, worldY,                // Posição no mundo
    width, height,                 // Tamanho em tiles
    true,                          // Tem colisão
    true,                          // É interativo
    false,                         // Tem auto-interação
    objectDefinition,              // Definição do objeto
    tileSize                       // Tamanho do tile
);
```

### **📊 Propriedades**

#### **Getters Básicos**
```java
String objectId = mapObject.getObjectId();           // ID do objeto
String name = mapObject.getName();                   // Nome do objeto
boolean collision = mapObject.hasCollision();        // Tem colisão
boolean interactive = mapObject.isInteractive();     // É interativo
boolean autoInteraction = mapObject.isAutoInteraction(); // Tem auto-interação
int width = mapObject.getWidth();                    // Largura
int height = mapObject.getHeight();                  // Altura
boolean active = mapObject.isActive();               // Está ativo
```

#### **Setters**
```java
mapObject.setActive(true);                          // Ativar/desativar
mapObject.setCollision(true);                       // Definir colisão
mapObject.setInteractive(true);                     // Definir interatividade
mapObject.setAutoInteraction(false);                // Definir auto-interação
```

### **🎨 Renderização**

#### **`draw(Graphics2D g, ObjectSpriteLoader spriteLoader, ...)`**
```java
mapObject.draw(graphics2D, spriteLoader, tileSize, player, playerScreenX, playerScreenY);
```
- **Descrição**: Desenha o objeto na tela
- **Parâmetros**: 
  - `Graphics2D g` - Contexto gráfico
  - `ObjectSpriteLoader spriteLoader` - Carregador de sprites
  - `int tileSize` - Tamanho do tile
  - `Player player` - Jogador
  - `int playerScreenX, playerScreenY` - Posição do jogador na tela
- **Funcionalidade**:
  - Calcula posição de desenho
  - Desenha sprite do objeto
  - Só desenha se estiver ativo

### **🎮 Interação**

#### **`interact(Player player)`**
```java
mapObject.interact(player);
```
- **Descrição**: Executa interação com o objeto
- **Parâmetros**: `Player player` - Jogador interagindo
- **Funcionalidade**:
  - Executa ação específica do objeto
  - Pode dar itens, abrir diálogos, etc.
  - Implementado por subclasses específicas

---

## 🚀 **TeleportManager - Sistema de Teleportes**

### **🔧 Inicialização**

#### **Singleton Pattern**
```java
TeleportManager teleportManager = TeleportManager.getInstance();
```
- **Descrição**: Obtém instância única do gerenciador
- **Retorno**: `TeleportManager` - Instância singleton
- **Funcionalidade**: Carrega configurações do arquivo `teleports.json`

### **📋 Consultas**

#### **`getTeleport(String teleportId)`**
```java
TeleportConfig config = teleportManager.getTeleport("city_to_dungeon1");
if (config != null) {
    System.out.println("Teleporte para: " + config.name);
}
```
- **Descrição**: Obtém configuração de teleporte por ID
- **Parâmetros**: `String teleportId` - ID do teleporte
- **Retorno**: `TeleportConfig` - Configuração ou null

#### **`getQuickTeleport(String quickId)`**
```java
String teleportString = teleportManager.getQuickTeleport("home");
if (teleportString != null) {
    System.out.println("Teleporte rápido: " + teleportString);
}
```
- **Descrição**: Obtém teleporte rápido por ID
- **Parâmetros**: `String quickId` - ID do teleporte rápido
- **Retorno**: `String` - String no formato "mapa,x,y" ou null

#### **`hasTeleport(String teleportId)`**
```java
if (teleportManager.hasTeleport("city_to_dungeon1")) {
    System.out.println("Teleporte existe!");
}
```
- **Descrição**: Verifica se teleporte existe
- **Parâmetros**: `String teleportId` - ID do teleporte
- **Retorno**: `boolean` - true se existe

#### **`hasQuickTeleport(String quickId)`**
```java
if (teleportManager.hasQuickTeleport("home")) {
    System.out.println("Teleporte rápido existe!");
}
```
- **Descrição**: Verifica se teleporte rápido existe
- **Parâmetros**: `String quickId` - ID do teleporte rápido
- **Retorno**: `boolean` - true se existe

### **📊 Listas Completas**

#### **`getAllTeleports()`**
```java
Map<String, TeleportConfig> allTeleports = teleportManager.getAllTeleports();
for (String id : allTeleports.keySet()) {
    TeleportConfig config = allTeleports.get(id);
    System.out.println(id + ": " + config.name);
}
```
- **Descrição**: Retorna todos os teleportes disponíveis
- **Retorno**: `Map<String, TeleportConfig>` - Mapa com todos os teleportes

#### **`getAllQuickTeleports()`**
```java
Map<String, String> allQuickTeleports = teleportManager.getAllQuickTeleports();
for (String id : allQuickTeleports.keySet()) {
    String teleportString = allQuickTeleports.get(id);
    System.out.println(id + ": " + teleportString);
}
```
- **Descrição**: Retorna todos os teleportes rápidos disponíveis
- **Retorno**: `Map<String, String>` - Mapa com todos os teleportes rápidos

---

## 🎯 **TeleportConfig - Configuração de Teleporte**

### **📊 Propriedades**

#### **Getters Básicos**
```java
String map = config.map;                    // Nome do mapa de destino
String name = config.name;                  // Nome do teleporte
String description = config.description;    // Descrição do teleporte
Map<String, int[]> spawnPoints = config.spawnPoints; // Pontos de spawn
```

### **🎯 Métodos de Spawn**

#### **`getSpawnPoint(String spawnPoint)`**
```java
int[] coords = config.getSpawnPoint("entrance");
if (coords != null) {
    System.out.println("Spawn em: (" + coords[0] + ", " + coords[1] + ")");
}
```
- **Descrição**: Obtém coordenadas de um ponto de spawn específico
- **Parâmetros**: `String spawnPoint` - Nome do ponto de spawn
- **Retorno**: `int[]` - Array [x, y] ou null

#### **`getFirstSpawnPoint()`**
```java
int[] coords = config.getFirstSpawnPoint();
if (coords != null) {
    System.out.println("Primeiro spawn: (" + coords[0] + ", " + coords[1] + ")");
}
```
- **Descrição**: Obtém o primeiro ponto de spawn disponível
- **Retorno**: `int[]` - Array [x, y] ou null

### **🔗 Geração de Strings**

#### **`generateTeleportString(String spawnPoint)`**
```java
String teleportString = config.generateTeleportString("entrance");
if (teleportString != null) {
    System.out.println("String de teleporte: " + teleportString);
}
```
- **Descrição**: Gera string de teleporte para ponto específico
- **Parâmetros**: `String spawnPoint` - Nome do ponto de spawn
- **Retorno**: `String` - String no formato "mapa,x,y" ou null

#### **`generateTeleportString()`**
```java
String teleportString = config.generateTeleportString();
if (teleportString != null) {
    System.out.println("String de teleporte: " + teleportString);
}
```
- **Descrição**: Gera string de teleporte para primeiro ponto disponível
- **Retorno**: `String` - String no formato "mapa,x,y" ou null

---

## 🔍 **ColisionChecker - Sistema de Colisão**

### **🔧 Verificação de Colisão**

#### **`checkObject(Entity entity)`**
```java
colisionChecker.checkObject(player);
if (player.isCollisionOn()) {
    System.out.println("Jogador colidiu com objeto!");
}
```
- **Descrição**: Verifica colisão com objetos do mapa
- **Parâmetros**: `Entity entity` - Entidade para verificar
- **Funcionalidade**:
  - Verifica colisão com objetos ativos
  - Usa cache para otimização
  - Considera direção do movimento
  - Ativa flag de colisão na entidade

#### **`isPlayerCollidingWithObject(Player player, MapObject obj)`**
```java
boolean colliding = colisionChecker.isPlayerCollidingWithObject(player, mapObject);
if (colliding) {
    System.out.println("Jogador colidiu com objeto!");
}
```
- **Descrição**: Verifica colisão específica entre jogador e objeto
- **Parâmetros**: 
  - `Player player` - Jogador
  - `MapObject obj` - Objeto
- **Retorno**: `boolean` - true se há colisão

#### **`isPlayerNearObject(Player player, int objX, int objY)`**
```java
boolean near = colisionChecker.isPlayerNearObject(player, objectX, objectY);
if (near) {
    System.out.println("Jogador está próximo do objeto!");
}
```
- **Descrição**: Verifica se jogador está próximo de um objeto
- **Parâmetros**: 
  - `Player player` - Jogador
  - `int objX, objY` - Posição do objeto
- **Retorno**: `boolean` - true se está próximo

---

## 🎮 **GamePanel - Integração com Sistema de Mapa**

### **🗺️ Carregamento de Mapas**

#### **`loadMap(String mapName)`**
```java
gamePanel.loadMap("city");
gamePanel.loadMap("dungeon1");
gamePanel.loadMap("houses/player_house");
```
- **Descrição**: Carrega um novo mapa
- **Parâmetros**: `String mapName` - Nome do mapa (sem extensão .json)
- **Funcionalidade**:
  - Carrega mapa do arquivo JSON
  - Recarrega NPCs e objetos
  - Notifica QuestManager sobre mudança
  - Atualiza contexto de áudio

#### **`updateAudioContextForMap(String mapName)`**
```java
// Chamado automaticamente ao carregar mapa
gamePanel.updateAudioContextForMap("city");
```
- **Descrição**: Atualiza contexto de áudio baseado no mapa
- **Parâmetros**: `String mapName` - Nome do mapa
- **Funcionalidade**:
  - Muda música baseada no mapa
  - Reproduz efeito sonoro de teleporte
  - Atualiza contexto do AudioManager

### **🚀 Sistema de Teleportes**

#### **`checkAutomaticTeleports()`**
```java
// Chamado automaticamente no loop do jogo
gamePanel.checkAutomaticTeleports();
```
- **Descrição**: Verifica teleportes automáticos
- **Funcionalidade**:
  - Verifica colisão com teleportes automáticos
  - Executa teleporte se necessário
  - Usa apenas TeleportManager para validação

#### **`performTeleport(MapTile teleportTile)`**
```java
// Chamado internamente pelo sistema
gamePanel.performTeleport(teleportTile);
```
- **Descrição**: Executa teleporte do jogador
- **Parâmetros**: `MapTile teleportTile` - Tile de teleporte
- **Funcionalidade**:
  - Carrega novo mapa
  - Posiciona jogador no ponto de spawn
  - Exibe mensagem de confirmação

#### **`performInteractiveTeleport(MapTile teleportTile)`**
```java
// Chamado quando jogador interage com teleporte
gamePanel.performInteractiveTeleport(teleportTile);
```
- **Descrição**: Executa teleporte interativo
- **Parâmetros**: `MapTile teleportTile` - Tile de teleporte
- **Funcionalidade**:
  - Similar ao teleporte automático
  - Usado para teleportes que requerem interação

### **🎯 Verificação de Colisão**

#### **`isPlayerCollidingWithTeleport(Player player, int teleportWorldX, int teleportWorldY)`**
```java
boolean colliding = gamePanel.isPlayerCollidingWithTeleport(player, teleportX, teleportY);
if (colliding) {
    System.out.println("Jogador colidiu com teleporte!");
}
```
- **Descrição**: Verifica colisão com teleporte
- **Parâmetros**: 
  - `Player player` - Jogador
  - `int teleportWorldX, teleportWorldY` - Posição do teleporte
- **Retorno**: `boolean` - true se há colisão
- **Funcionalidade**: Usa distância entre centros para detecção

---

## 🎮 **Exemplos Práticos de Uso**

### **Exemplo 1: Carregamento de Mapa**
```java
// Carregar novo mapa
TileManager tileManager = gamePanel.getTileManager();
tileManager.loadMapJson("/maps/city.json");

// Verificar dimensões do mapa
int mapWidth = tileManager.getMapWidth();
int mapHeight = tileManager.getMapHeight();
int tileSize = tileManager.getTileSize();

System.out.println("Mapa carregado: " + mapWidth + "x" + mapHeight + " tiles");
System.out.println("Tamanho do tile: " + tileSize + " pixels");
```

### **Exemplo 2: Sistema de Colisão**
```java
// Verificar colisão em posição específica
boolean hasCollision = tileManager.isCollisionAt(10, 15);
if (hasCollision) {
    System.out.println("Posição (10, 15) tem colisão!");
}

// Verificar colisão com objetos
ColisionChecker collisionChecker = gamePanel.getColisionChecker();
collisionChecker.checkObject(player);

if (player.isCollisionOn()) {
    System.out.println("Jogador colidiu com algo!");
}
```

### **Exemplo 3: Sistema de Teleportes**
```java
// Verificar se teleporte existe
TeleportManager teleportManager = TeleportManager.getInstance();
if (teleportManager.hasTeleport("city_to_dungeon1")) {
    TeleportConfig config = teleportManager.getTeleport("city_to_dungeon1");
    System.out.println("Teleporte para: " + config.name);
    
    // Obter ponto de spawn
    int[] spawnCoords = config.getSpawnPoint("entrance");
    if (spawnCoords != null) {
        System.out.println("Spawn em: (" + spawnCoords[0] + ", " + spawnCoords[1] + ")");
    }
}

// Usar teleporte rápido
String quickTeleport = teleportManager.getQuickTeleport("home");
if (quickTeleport != null) {
    System.out.println("Teleporte rápido: " + quickTeleport);
}
```

### **Exemplo 4: Objetos do Mapa**
```java
// Obter objetos ativos
ObjectManager objectManager = gamePanel.getObjectManager();
List<MapObject> activeObjects = objectManager.getActiveObjects();

for (MapObject obj : activeObjects) {
    System.out.println("Objeto ativo: " + obj.getName());
    System.out.println("Posição: (" + obj.getWorldX() + ", " + obj.getWorldY() + ")");
    System.out.println("Interativo: " + obj.isInteractive());
    System.out.println("Tem colisão: " + obj.hasCollision());
}

// Atualizar objetos ativos baseado na posição do jogador
int playerTileX = player.getWorldX() / tileSize;
int playerTileY = player.getWorldY() / tileSize;
objectManager.updateActiveObjects(playerTileX, playerTileY);
```

### **Exemplo 5: Filtros de Tiles**
```java
// Obter tiles de NPCs
List<MapTile> npcTiles = tileManager.getNpcTiles();
for (MapTile tile : npcTiles) {
    System.out.println("NPC: " + tile.id + " em (" + tile.x + ", " + tile.y + ")");
}

// Obter tiles de monstros
List<MapTile> monsterTiles = tileManager.getMonsterTiles();
for (MapTile tile : monsterTiles) {
    System.out.println("Monstro: " + tile.id + " em (" + tile.x + ", " + tile.y + ")");
}

// Obter tiles de teleporte
List<MapTile> teleportTiles = tileManager.getTeleportTiles();
for (MapTile tile : teleportTiles) {
    System.out.println("Teleporte: " + tile.id + " em (" + tile.x + ", " + tile.y + ")");
    if (tile.interactive != null && tile.interactive) {
        System.out.println("  - Teleporte interativo");
    }
}

// Obter tiles de objetos
List<MapTile> objectTiles = tileManager.getObjectTiles();
for (MapTile tile : objectTiles) {
    System.out.println("Objeto: " + tile.id + " em (" + tile.x + ", " + tile.y + ")");
}
```

### **Exemplo 6: Renderização Personalizada**
```java
// Renderizar apenas camadas de fundo
Graphics2D g2 = (Graphics2D) graphics;
tileManager.drawBackgroundLayers(g2);

// Renderizar jogador
player.draw(g2);

// Renderizar camadas de overlay
tileManager.drawOverlayLayers(g2);
```

### **Exemplo 7: Sistema de Interação**
```java
// Verificar interação com objetos
ObjectManager objectManager = gamePanel.getObjectManager();
for (MapObject obj : objectManager.getActiveObjects()) {
    if (obj.isInteractive() && obj.isActive()) {
        boolean shouldInteract = false;
        
        if (obj.isAutoInteraction()) {
            // Verificar colisão real para auto-interação
            shouldInteract = isPlayerCollidingWithObject(player, obj);
        } else {
            // Verificar proximidade para interação manual
            shouldInteract = isPlayerNearObject(player, obj.getWorldX(), obj.getWorldY());
        }
        
        if (shouldInteract) {
            obj.interact(player);
            break; // Só interage com um objeto por vez
        }
    }
}
```

### **Exemplo 8: Sistema de Teleportes Avançado**
```java
// Verificar teleportes automáticos
List<MapTile> teleportTiles = tileManager.getTeleportTiles();
for (MapTile teleportTile : teleportTiles) {
    if (teleportTile.id != null && teleportManager.hasTeleport(teleportTile.id)) {
        int teleportWorldX = teleportTile.x * tileSize;
        int teleportWorldY = teleportTile.y * tileSize;
        
        if (isPlayerCollidingWithTeleport(player, teleportWorldX, teleportWorldY)) {
            // Executar teleporte
            performTeleport(teleportTile);
            break; // Só executa um teleporte por vez
        }
    }
}

// Verificar teleportes interativos
for (MapTile teleportTile : teleportTiles) {
    if (teleportTile.interactive != null && teleportTile.interactive) {
        int teleportWorldX = teleportTile.x * tileSize;
        int teleportWorldY = teleportTile.y * tileSize;
        
        if (isPlayerNearObject(player, teleportWorldX, teleportWorldY)) {
            // Executar teleporte interativo
            performInteractiveTeleport(teleportTile);
            break;
        }
    }
}
```

---

## 🔧 **Integração com Outros Sistemas**

### **Com Sistema de Quest**
```java
// Notificar mudança de mapa
QuestManager questManager = QuestManager.getInstance();
questManager.onPlayerEnterMap(mapName);

// Verificar objetivos de localização
if (questManager.hasActiveQuest()) {
    Quest activeQuest = questManager.getActiveQuest();
    // Objetivos REACH_LOCATION são atualizados automaticamente
}
```

### **Com Sistema de Áudio**
```java
// Atualizar contexto de áudio baseado no mapa
AudioManager audioManager = AudioManager.getInstance();
AudioContext newContext = AudioContext.fromMapName(mapName);
audioManager.changeContext(newContext);

// Reproduzir efeito sonoro de teleporte
audioManager.playSoundEffect("teleport");
```

### **Com Sistema de UI**
```java
// Exibir mensagem de boas-vindas do mapa
if (mapJson.message != null && !mapJson.message.isEmpty()) {
    gameUI.showCenterMessage(mapJson.message, 4000);
}

// Exibir mensagem de teleporte
gameUI.addMessage("Você foi teleportado para " + config.name + "!", null, 3000L);
```

### **Com Sistema de NPCs**
```java
// Carregar NPCs do mapa
List<MapTile> npcTiles = tileManager.getNpcTiles();
for (MapTile tile : npcTiles) {
    // Criar NPC baseado no tile
    Npc npc = createNpcFromTile(tile);
    if (npc != null) {
        npcs.add(npc);
    }
}
```

### **Com Sistema de Objetos**
```java
// Carregar objetos do mapa
List<MapTile> objectTiles = tileManager.getObjectTiles();
ObjectSpriteLoader objectSpriteLoader = new ObjectSpriteLoader("/objects.json");
ObjectManager objectManager = new ObjectManager(gamePanel, objectSpriteLoader, objectTiles);

// Atualizar objetos ativos
int playerTileX = player.getWorldX() / tileSize;
int playerTileY = player.getWorldY() / tileSize;
objectManager.updateActiveObjects(playerTileX, playerTileY);
```

---

## 🎯 **Casos de Uso Avançados**

### **Sistema de Múltiplas Camadas**
```java
// Renderizar camadas em ordem específica
List<MapLayer> layers = tileManager.getMapLayers();
for (MapLayer layer : layers) {
    String layerName = layer.name.toLowerCase();
    
    if (layerName.contains("background")) {
        // Renderizar fundo primeiro
        renderLayer(g2, layer, ...);
    } else if (layerName.contains("objects")) {
        // Renderizar objetos
        renderLayer(g2, layer, ...);
    } else if (layerName.contains("npcs")) {
        // Renderizar NPCs
        renderLayer(g2, layer, ...);
    } else if (layerName.contains("overlay")) {
        // Renderizar overlay por último
        renderLayer(g2, layer, ...);
    }
}
```

### **Sistema de Cache de Colisão**
```java
// Usar cache para otimizar verificação de colisão
ColisionChecker collisionChecker = gamePanel.getColisionChecker();

// Verificar colisão com cache
collisionChecker.checkObject(player);

// Limpar cache periodicamente
collisionChecker.clearCacheIfNeeded();
```

### **Sistema de Teleportes Condicionais**
```java
// Verificar condições antes de teleportar
TeleportManager teleportManager = TeleportManager.getInstance();
if (teleportManager.hasTeleport("city_to_dungeon1")) {
    TeleportConfig config = teleportManager.getTeleport("city_to_dungeon1");
    
    // Verificar se jogador tem item necessário
    if (player.getInventoryManager().hasItem("dungeon_key")) {
        // Executar teleporte
        performTeleport(teleportTile);
    } else {
        gameUI.addMessage("Você precisa da chave do calabouço!", null, 3000L);
    }
}
```

### **Sistema de Objetos Dinâmicos**
```java
// Criar objetos dinamicamente
ObjectManager objectManager = gamePanel.getObjectManager();
ObjectSpriteLoader spriteLoader = new ObjectSpriteLoader("/objects.json");

// Criar novo objeto
MapTile newTile = new MapTile();
newTile.id = "chest_01";
newTile.x = 10;
newTile.y = 15;
newTile.interactive = true;

// Instanciar objeto
MapObject newObject = objectManager.instantiateMapObject(newTile);
if (newObject != null) {
    objectManager.getActiveObjects().add(newObject);
}
```

### **Sistema de Múltiplos Pontos de Spawn**
```java
// Usar pontos de spawn específicos
TeleportConfig config = teleportManager.getTeleport("city");
if (config != null) {
    // Verificar pontos de spawn disponíveis
    for (String spawnPoint : config.spawnPoints.keySet()) {
        int[] coords = config.getSpawnPoint(spawnPoint);
        System.out.println("Ponto de spawn '" + spawnPoint + "': (" + coords[0] + ", " + coords[1] + ")");
    }
    
    // Usar ponto específico
    int[] entranceCoords = config.getSpawnPoint("entrance");
    if (entranceCoords != null) {
        player.setWorldX(entranceCoords[0] * tileSize);
        player.setWorldY(entranceCoords[1] * tileSize);
    }
}
```

---

## 📋 **Checklist de Implementação**

### **✅ Configuração Básica**
- [ ] TileManager inicializado com GamePanel
- [ ] Arquivo tiles.json configurado
- [ ] Spritesheet de tiles carregada
- [ ] Mapa inicial carregado

### **✅ Sistema de Mapas**
- [ ] Arquivos JSON de mapas criados
- [ ] Múltiplas camadas configuradas
- [ ] Sistema de colisão funcionando
- [ ] Renderização otimizada

### **✅ Sistema de Objetos**
- [ ] ObjectManager inicializado
- [ ] Arquivo objects.json configurado
- [ ] Objetos interativos funcionando
- [ ] Sistema de proximidade implementado

### **✅ Sistema de Teleportes**
- [ ] TeleportManager configurado
- [ ] Arquivo teleports.json criado
- [ ] Teleportes automáticos funcionando
- [ ] Teleportes interativos funcionando
- [ ] Múltiplos pontos de spawn configurados

### **✅ Integração**
- [ ] Sistema de quest integrado
- [ ] Sistema de áudio integrado
- [ ] Sistema de UI integrado
- [ ] Sistema de NPCs integrado
- [ ] Sistema de colisão integrado

### **✅ Testes**
- [ ] Mapas carregam corretamente
- [ ] Colisão funciona
- [ ] Teleportes funcionam
- [ ] Objetos interativos funcionam
- [ ] Renderização está otimizada
- [ ] Sistema de cache funciona

### **✅ Funcionalidades Avançadas**
- [ ] Múltiplas camadas
- [ ] Sistema de cache de colisão
- [ ] Teleportes condicionais
- [ ] Objetos dinâmicos
- [ ] Múltiplos pontos de spawn
- [ ] Sistema de proximidade
- [ ] Renderização otimizada

---

## 🚀 **Conclusão**

Este sistema de mapa oferece:

- ✅ **Mapas Baseados em Tiles**: Sistema completo com múltiplas camadas
- ✅ **Sistema de Colisão**: Detecção eficiente com cache
- ✅ **Sistema de Teleportes**: Automáticos e interativos com múltiplos pontos
- ✅ **Sistema de Objetos**: Objetos interativos com proximidade
- ✅ **Renderização Otimizada**: Apenas área visível é renderizada
- ✅ **Integração Completa**: Funciona com todos os sistemas do jogo
- ✅ **Performance**: Cache e otimizações para mapas grandes
- ✅ **Flexibilidade**: Suporte a múltiplas camadas e tipos de tiles
- ✅ **Extensibilidade**: Fácil adicionar novos tipos de objetos e teleportes

### **Recursos Implementados**
- 🗺️ **Mapas Baseados em Tiles**: Sistema completo com múltiplas camadas
- 🎯 **Sistema de Colisão**: Detecção eficiente com cache
- 🚀 **Sistema de Teleportes**: Automáticos e interativos
- 🎮 **Sistema de Objetos**: Objetos interativos com proximidade
- 🎨 **Renderização Otimizada**: Apenas área visível renderizada
- 🔍 **Filtros de Tiles**: NPCs, monstros, teleportes, objetos
- 📊 **Informações do Mapa**: Dimensões, tamanho de tiles
- 🎵 **Integração de Áudio**: Contexto baseado no mapa
- 🎮 **Integração Total**: Funciona com quests, NPCs, UI, áudio

**O sistema está pronto para criar mundos imersivos e interativos!** 🗺️✨


