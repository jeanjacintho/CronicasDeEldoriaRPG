package br.com.cronicasdeeldoria.game.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Carregador de mapeamentos de teclado a partir de arquivo JSON
 */
public class KeyboardMappingLoader {
    
    private Map<String, KeyMapping> mappings;
    private static final String CONFIG_FILE = "/keyboard_mappings.json";
    
    public KeyboardMappingLoader() {
        this.mappings = new HashMap<>();
        loadMappings();
    }
    
    /**
     * Carrega os mapeamentos do arquivo JSON
     */
    private void loadMappings() {
        try {
            
            InputStream inputStream = getClass().getResourceAsStream(CONFIG_FILE);
            if (inputStream == null) {
                System.err.println("Arquivo de configuração não encontrado: " + CONFIG_FILE);
                return;
            }
            
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);
            JsonNode mappingsArray = rootNode.get("keyboard_mappings");
            
            if (mappingsArray != null && mappingsArray.isArray()) {
                
                for (JsonNode mappingNode : mappingsArray) {
                    String key = mappingNode.get("key").asText();
                    String imagePath = mappingNode.get("imagePath").asText();
                    String description = mappingNode.get("description").asText();
                    
                    
                    KeyMapping mapping = new KeyMapping(key, imagePath, description);
                    mappings.put(key, mapping);
                }
                
            } else {
                System.err.println("Array 'keyboard_mappings' não encontrado ou não é um array");
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar mapeamentos de teclado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtém todos os mapeamentos carregados
     * @return Map com todos os mapeamentos
     */
    public Map<String, KeyMapping> getAllMappings() {
        return new HashMap<>(mappings);
    }
    
    /**
     * Obtém um mapeamento específico por nome da tecla
     * @param keyName Nome da tecla
     * @return Mapeamento da tecla ou null se não encontrado
     */
    public KeyMapping getMapping(String keyName) {
        return mappings.get(keyName);
    }
    
    /**
     * Verifica se uma tecla está mapeada
     * @param keyName Nome da tecla
     * @return true se a tecla está mapeada
     */
    public boolean hasMapping(String keyName) {
        return mappings.containsKey(keyName);
    }
    
    /**
     * Classe interna para representar um mapeamento de tecla
     */
    public static class KeyMapping {
        private String key;
        private String imagePath;
        private String description;
        
        public KeyMapping(String key, String imagePath, String description) {
            this.key = key;
            this.imagePath = imagePath;
            this.description = description;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getImagePath() {
            return imagePath;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

