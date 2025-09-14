package br.com.cronicasdeeldoria.audio;

/**
 * Enumeração dos diferentes contextos de áudio no jogo.
 * Define quando cada tipo de música deve ser reproduzida.
 */
public enum AudioContext {
    
    // Contextos de mapa/exploração
    CITY("Cidade", "Música ambiente da cidade"),
    FOREST("Floresta", "Música ambiente da floresta"),
    DUNGEON1("Masmorra", "Música ambiente de masmorra"),
    DUNGEON2("Masmorra", "Música ambiente de masmorra"),
    DUNGEON3("Masmorra", "Música ambiente de masmorra"),
    DUNGEON4("Masmorra", "Música ambiente de masmorra"),
    CAVE("Caverna", "Música ambiente de caverna"),
    CASTLE("Castelo", "Música ambiente do castelo"),
    PLAYER_HOUSE("Casa do Jogador", "Música ambiente da casa do jogador"),
    
    // Contextos de batalha
    BATTLE_NORMAL("Batalha Normal", "Música de batalha contra inimigos comuns"),
    BATTLE_BOSS("Batalha Boss", "Música épica de batalha contra boss"),
    BATTLE_MINIBOSS("Batalha Mini-Boss", "Música de batalha contra mini-boss"),
    
    // Contextos especiais
    MENU("Menu", "Música do menu principal"),
    VICTORY("Vitória", "Música de vitória"),
    DEFEAT("Derrota", "Música de derrota"),
    SILENCE("Silêncio", "Sem música de fundo");
    
    private final String displayName;
    private final String description;
    
    AudioContext(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Determina se este contexto é de batalha.
     */
    public boolean isBattleContext() {
        return this == BATTLE_NORMAL || this == BATTLE_BOSS || this == BATTLE_MINIBOSS;
    }
    
    /**
     * Determina se este contexto é de exploração/mapa.
     */
    public boolean isExplorationContext() {
        return this == CITY || this == FOREST || this == DUNGEON1 || this == DUNGEON2 || this == DUNGEON3 || this == DUNGEON4 || this == CAVE || this == CASTLE || this == PLAYER_HOUSE;
    }
    
    /**
     * Determina se este contexto é especial (menu, vitória, etc.).
     */
    public boolean isSpecialContext() {
        return this == MENU || this == VICTORY || this == DEFEAT || this == SILENCE;
    }
    
    /**
     * Obtém o contexto de áudio baseado no nome do mapa.
     * Agora usa o AudioConfigLoader para mapeamento mais preciso.
     */
    public static AudioContext fromMapName(String mapName) {
        if (mapName == null) {
            return SILENCE;
        }
        
        // Primeiro tentar usar o AudioConfigLoader para mapeamento mais preciso
        AudioConfigLoader configLoader = AudioConfigLoader.getInstance();
        String mappedContext = configLoader.getMapAudioContext(mapName);
        
        if (mappedContext != null) {
            try {
                return AudioContext.valueOf(mappedContext.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Contexto inválido no mapeamento: " + mappedContext);
            }
        }
        
        // Fallback para lógica original se não encontrar no mapeamento
        String lowerMapName = mapName.toLowerCase();
        
        // Verificar contextos específicos primeiro
        if (lowerMapName.contains("forest") || lowerMapName.contains("floresta")) {
            return FOREST;
        } else if (lowerMapName.contains("dungeon1") || lowerMapName.contains("masmorra")) {
            return DUNGEON1;
        } else if (lowerMapName.contains("dungeon2") || lowerMapName.contains("masmorra")) {
            return DUNGEON2;
        } else if (lowerMapName.contains("dungeon3") || lowerMapName.contains("masmorra")) {
            return DUNGEON3;
        } else if (lowerMapName.contains("dungeon4") || lowerMapName.contains("masmorra")) {
            return DUNGEON4;
        } else if (lowerMapName.contains("cave") || lowerMapName.contains("caverna")) {
            return CAVE;
        } else if (lowerMapName.contains("castle") || lowerMapName.contains("castelo")) {
            return CASTLE;
        } else if (lowerMapName.contains("player_house") || lowerMapName.contains("casa_do_jogador")) {
            return PLAYER_HOUSE;
        } else if (lowerMapName.contains("house") || lowerMapName.contains("casa") || 
                   lowerMapName.contains("houses") || lowerMapName.contains("casas")) {
            // Outras casas são parte da cidade, então usar música da cidade
            return CITY;
        } else if (lowerMapName.contains("city") || lowerMapName.contains("cidade")) {
            return CITY;
        } else {
            // Para mapas não identificados, usar floresta como padrão
            System.out.println("Mapa não identificado: " + mapName + " - usando contexto FOREST como padrão");
            return FOREST;
        }
    }
    
    /**
     * Obtém o contexto de batalha baseado no tipo de inimigo.
     */
    public static AudioContext fromEnemyType(String enemyType) {
        if (enemyType == null) {
            return BATTLE_NORMAL;
        }
        
        String lowerEnemyType = enemyType.toLowerCase();
        
        if (lowerEnemyType.contains("boss") || lowerEnemyType.contains("chefe")) {
            return BATTLE_BOSS;
        } else if (lowerEnemyType.contains("mini") || lowerEnemyType.contains("elite")) {
            return BATTLE_MINIBOSS;
        } else {
            return BATTLE_NORMAL;
        }
    }
    
    /**
     * Obtém o contexto de batalha baseado no nome do NPC.
     */
    public static AudioContext fromNpcName(String npcName) {
        if (npcName == null) {
            return BATTLE_NORMAL;
        }
        
        String lowerNpcName = npcName.toLowerCase();
        
        // Verificar se é um boss conhecido
        if (lowerNpcName.contains("supreme") || lowerNpcName.contains("supremo") ||
            lowerNpcName.contains("dragon") || lowerNpcName.contains("dragão") ||
            lowerNpcName.contains("lich") || lowerNpcName.contains("lich") ||
            lowerNpcName.contains("demon") || lowerNpcName.contains("demônio")) {
            return BATTLE_BOSS;
        }
        
        // Verificar se é um mini-boss
        if (lowerNpcName.contains("elite") || lowerNpcName.contains("guardian") ||
            lowerNpcName.contains("guardião") || lowerNpcName.contains("captain") ||
            lowerNpcName.contains("capitão")) {
            return BATTLE_MINIBOSS;
        }
        
        return BATTLE_NORMAL;
    }
}
