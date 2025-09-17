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
    PLAYER_HOUSE("Casa do Jogador", "Música ambiente da casa do jogador"),
    
    // Contextos de batalha
    BATTLE_NORMAL("Batalha Normal", "Música de batalha contra inimigos comuns"),
    BATTLE_BOSS("Batalha Boss", "Música épica de batalha contra boss"),
    BATTLE_MINIBOSS("Batalha Mini-Boss", "Música de batalha contra mini-boss"),
    BATTLE_FINAL_BOSS("Batalha Boss Final", "Música épica definitiva contra o boss final"),
    
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
        return this == BATTLE_NORMAL || this == BATTLE_BOSS || this == BATTLE_MINIBOSS || this == BATTLE_FINAL_BOSS;
    }
    
    /**
     * Determina se este contexto é de exploração/mapa.
     */
    public boolean isExplorationContext() {
        return this == CITY || this == FOREST || this == DUNGEON1 || this == DUNGEON2 || this == DUNGEON3 || this == DUNGEON4 || this == PLAYER_HOUSE;
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
            }
        }
        
        // Fallback para lógica original se não encontrar no mapeamento
        String lowerMapName = mapName.toLowerCase();
        
        // Verificar contextos específicos primeiro
        if (lowerMapName.contains("forest") || lowerMapName.contains("floresta")) {
            return FOREST;
        } else if (lowerMapName.contains("dungeon1")) {
            return DUNGEON1;
        } else if (lowerMapName.contains("dungeon2")) {
            return DUNGEON2;
        } else if (lowerMapName.contains("dungeon3")) {
            return DUNGEON3;
        } else if (lowerMapName.contains("dungeon4")) {
            return DUNGEON4;
        } else if (lowerMapName.contains("player_house")) {
            return PLAYER_HOUSE;
        } else if (lowerMapName.contains("house")) {
            // Outras casas são parte da cidade, então usar música da cidade
            return CITY;
        } else if (lowerMapName.contains("city")) {
            return CITY;
        } else {
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
        
        // Verificar se é o boss final (Supreme Mage)
        if (lowerNpcName.contains("supreme mage") || lowerNpcName.contains("supreme_mage") ||
            lowerNpcName.contains("mago supremo") || lowerNpcName.contains("supremo")) {
            return BATTLE_FINAL_BOSS;
        }
        
        // Verificar se é um boss conhecido
        if (lowerNpcName.contains("dragon") || lowerNpcName.contains("dragão") ||
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
        
        // Verificar se é um boss específico do jogo (incluindo todos os boss monsters)
        if (lowerNpcName.contains("boss") || lowerNpcName.contains("chefe") ||
            lowerNpcName.contains("wolf boss") || lowerNpcName.contains("skeleton boss") ||
            lowerNpcName.contains("frostborn boss") || lowerNpcName.contains("orc boss") ||
            lowerNpcName.contains("wolfboss") || lowerNpcName.contains("skeletonboss") ||
            lowerNpcName.contains("frostbornboss") || lowerNpcName.contains("orcboss")) {
            return BATTLE_BOSS;
        }
        
        // Todos os outros inimigos (wolf, skeleton, frostborn, orc) são batalhas normais
        return BATTLE_NORMAL;
    }
}
