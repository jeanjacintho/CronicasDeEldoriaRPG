package br.com.cronicasdeeldoria.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CharacterConfigLoader {
  private static CharacterConfigLoader instance;
  private Map<String, Map<String, Object>> characterConfigs;

  private CharacterConfigLoader() {
    loadConfigurations();
  }

  public static CharacterConfigLoader getInstance() {
    if(instance == null) {
      instance = new CharacterConfigLoader();
    }
    return instance;
  }

  private void loadConfigurations() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream("character_config.json");

      if(inputStream == null) {
        System.err.println("Arquivo character_config.json não encontrado!");
        return;
      }

      JsonNode rootNode = mapper.readTree(inputStream);
      characterConfigs = new HashMap<>();

      rootNode.fieldNames().forEachRemaining(className -> {
        JsonNode classNode = rootNode.get(className);
        Map<String, Object> attributes = new HashMap<>();

        classNode.fieldNames().forEachRemaining(attribute -> {
          JsonNode valueNode = classNode.get(attribute);

          if(valueNode.isInt()) {
            attributes.put(attribute, valueNode.asInt());
          } else if(valueNode.isDouble()) {
            attributes.put(attribute, valueNode.asDouble());
          } else {
            attributes.put(attribute, valueNode.asText());
          }
        });
        characterConfigs.put(className, attributes);
      });
    } catch (Exception e) {
      System.err.println("Erro ao carregar configurações: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public Map<String, Object> getCharacterConfig(String playerClass) {
    Map<String, Object> config = characterConfigs.get(playerClass.toLowerCase());
    if (config == null) {
      System.out.println("Configuração não encontrada para classe: " + playerClass + ". Usando padrão.");
      config = characterConfigs.get("default");
    }
    return config != null ? new HashMap<>(config) : new HashMap<>();
  }

  public int getIntAttribute(String playerClass, String attribute, int defaultValue) {
    Map<String, Object> config = getCharacterConfig(playerClass);
    Object value = config.get(attribute);
    return value instanceof Integer ? (Integer) value : defaultValue;
  }

  public String getStringAttribute(String playerClass, String attribute, String defaultValue) {
    Map<String, Object> config = getCharacterConfig(playerClass);
    Object value = config.get(attribute);
    return value instanceof String ? (String) value : defaultValue;
  }
}
