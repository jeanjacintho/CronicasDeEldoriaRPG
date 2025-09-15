package br.com.cronicasdeeldoria.audio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Carregador de configuração de áudio do jogo.
 * Lê o arquivo audio_config.json e fornece configurações para o AudioManager.
 */
public class AudioConfigLoader {
    
    private static AudioConfigLoader instance;
    private final ObjectMapper objectMapper;
    private JsonNode configRoot;
    private final Map<String, String> mapAudioMapping;
    private final Map<String, String> enemyAudioMapping;
    
    private AudioConfigLoader() {
        this.objectMapper = new ObjectMapper();
        this.mapAudioMapping = new HashMap<>();
        this.enemyAudioMapping = new HashMap<>();
        loadConfig();
    }
    
    public static synchronized AudioConfigLoader getInstance() {
        if (instance == null) {
            instance = new AudioConfigLoader();
        }
        return instance;
    }
    
    /**
     * Carrega a configuração de áudio do arquivo JSON.
     */
    private void loadConfig() {
        try {
            InputStream configStream = getClass().getResourceAsStream("/audio_config.json");
            if (configStream != null) {
                configRoot = objectMapper.readTree(configStream);
                loadMapAudioMapping();
                loadEnemyAudioMapping();
            } else {
                System.err.println("Audio configuration file not found: /audio_config.json");
                createDefaultConfig();
            }
        } catch (Exception e) {
            System.err.println("Error loading audio configuration: " + e.getMessage());
            createDefaultConfig();
        }
    }
    
    /**
     * Cria configuração padrão caso o arquivo não seja encontrado.
     */
    private void createDefaultConfig() {
        configRoot = objectMapper.createObjectNode();
    }
    
    /**
     * Carrega o mapeamento de mapas para contextos de áudio.
     */
    private void loadMapAudioMapping() {
        if (configRoot != null && configRoot.has("audio_config")) {
            JsonNode mapMapping = configRoot.get("audio_config").get("map_audio_mapping");
            if (mapMapping != null) {
                mapMapping.fields().forEachRemaining(entry -> {
                    mapAudioMapping.put(entry.getKey(), entry.getValue().asText());
                });
            }
        }
    }
    
    /**
     * Carrega o mapeamento de inimigos para contextos de áudio.
     */
    private void loadEnemyAudioMapping() {
        if (configRoot != null && configRoot.has("audio_config")) {
            JsonNode enemyMapping = configRoot.get("audio_config").get("enemy_audio_mapping");
            if (enemyMapping != null) {
                // Carregar palavras-chave de boss
                JsonNode bossKeywords = enemyMapping.get("boss_keywords");
                if (bossKeywords != null && bossKeywords.isArray()) {
                    bossKeywords.forEach(keyword -> {
                        enemyAudioMapping.put(keyword.asText().toLowerCase(), "boss");
                    });
                }
                
                // Carregar palavras-chave de mini-boss
                JsonNode minibossKeywords = enemyMapping.get("miniboss_keywords");
                if (minibossKeywords != null && minibossKeywords.isArray()) {
                    minibossKeywords.forEach(keyword -> {
                        enemyAudioMapping.put(keyword.asText().toLowerCase(), "miniboss");
                    });
                }
            }
        }
    }
    
    /**
     * Obtém o arquivo de música para um contexto específico.
     */
    public String getMusicFile(String context) {
        if (configRoot == null) return null;
        
        try {
            JsonNode musicContexts = configRoot.get("audio_config").get("music").get("contexts").get(context);
            if (musicContexts != null && musicContexts.has("file")) {
                return musicContexts.get("file").asText();
            }
        } catch (Exception e) {
            System.err.println("Error getting music file for context " + context + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtém o volume para um contexto de música específico.
     */
    public float getMusicVolume(String context) {
        if (configRoot == null) return 0.6f;
        
        try {
            JsonNode musicContexts = configRoot.get("audio_config").get("music").get("contexts").get(context);
            if (musicContexts != null && musicContexts.has("volume")) {
                return (float) musicContexts.get("volume").asDouble();
            }
        } catch (Exception e) {
            System.err.println("Error getting music volume for context " + context + ": " + e.getMessage());
        }
        
        return 0.6f;
    }
    
    /**
     * Obtém o arquivo de efeito sonoro.
     */
    public String getSoundEffectFile(String category, String soundName) {
        if (configRoot == null) return null;
        
        try {
            JsonNode sfxCategory = configRoot.get("audio_config").get("sound_effects").get(category);
            if (sfxCategory != null && sfxCategory.has(soundName)) {
                JsonNode soundConfig = sfxCategory.get(soundName);
                if (soundConfig.has("file")) {
                    return soundConfig.get("file").asText();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting sound effect file for " + category + "." + soundName + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtém o volume para um efeito sonoro específico.
     */
    public float getSoundEffectVolume(String category, String soundName) {
        if (configRoot == null) return 0.8f;
        
        try {
            JsonNode sfxCategory = configRoot.get("audio_config").get("sound_effects").get(category);
            if (sfxCategory != null && sfxCategory.has(soundName)) {
                JsonNode soundConfig = sfxCategory.get(soundName);
                if (soundConfig.has("volume")) {
                    return (float) soundConfig.get("volume").asDouble();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting sound effect volume for " + category + "." + soundName + ": " + e.getMessage());
        }
        
        return 0.8f;
    }
    
    /**
     * Obtém o contexto de áudio baseado no nome do mapa.
     */
    public String getMapAudioContext(String mapName) {
        if (mapName == null) return "forest";
        
        String lowerMapName = mapName.toLowerCase();
        String mappedContext = mapAudioMapping.get(lowerMapName);

        
        String result = mappedContext != null ? mappedContext : "forest";
        
        return result;
    }
    
    /**
     * Obtém o contexto de áudio baseado no nome do inimigo.
     */
    public String getEnemyAudioContext(String enemyName) {
        if (enemyName == null) return "normal";
        
        String lowerEnemyName = enemyName.toLowerCase();
        
        // Verificar palavras-chave específicas
        for (Map.Entry<String, String> entry : enemyAudioMapping.entrySet()) {
            if (lowerEnemyName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return "normal";
    }
    
    /**
     * Obtém configurações gerais de áudio.
     */
    public AudioSettings getAudioSettings() {
        AudioSettings settings = new AudioSettings();
        
        if (configRoot != null) {
            try {
                JsonNode settingsNode = configRoot.get("audio_config").get("settings");
                if (settingsNode != null) {
                    settings.masterVolume = (float) settingsNode.get("master_volume").asDouble();
                    settings.musicVolume = (float) settingsNode.get("music_volume").asDouble();
                    settings.sfxVolume = (float) settingsNode.get("sfx_volume").asDouble();
                    settings.maxConcurrentSfx = settingsNode.get("max_concurrent_sfx").asInt();
                    settings.fadeDurationMs = settingsNode.get("fade_duration_ms").asInt();
                }
            } catch (Exception e) {
                System.err.println("Error loading audio settings: " + e.getMessage());
            }
        }
        
        return settings;
    }
    
    /**
     * Classe para armazenar configurações de áudio.
     */
    public static class AudioSettings {
        public float masterVolume = 0.7f;
        public float musicVolume = 0.6f;
        public float sfxVolume = 0.8f;
        public int maxConcurrentSfx = 10;
        public int fadeDurationMs = 2000;
    }
}
