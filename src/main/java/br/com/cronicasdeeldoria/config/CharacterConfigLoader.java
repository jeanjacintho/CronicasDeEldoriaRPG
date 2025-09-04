package br.com.cronicasdeeldoria.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterConfigLoader {
  private static CharacterConfigLoader instance;
  private Map<String, CharacterConfig> characterConfigs;

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
      characterConfigs = new HashMap<>();

      java.io.InputStream is = getClass().getResourceAsStream("/character_config.json");
      if (is == null) {
        System.err.println("ERRO: character_config.json não encontrado!");
        throw new RuntimeException("character_config.json não encontrado!");
      }

      java.util.Scanner scanner = new java.util.Scanner(is);
      scanner.useDelimiter("\\A");
      String json = scanner.hasNext() ? scanner.next() : "";
      scanner.close();

      Gson gson = new Gson();
      TypeToken<List<CharacterConfig>> token = new TypeToken<List<CharacterConfig>>(){};
      List<CharacterConfig> configs = gson.fromJson(json, token.getType());

      if (configs != null) {
        for (CharacterConfig config : configs) {
          if (config != null && config.id != null) {
            characterConfigs.put(config.id.toLowerCase(), config);
          }
        }
      }

    } catch (Exception e) {
      System.err.println("Erro ao carregar configurações: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public CharacterConfig getCharacterConfig(String playerClass) {
    CharacterConfig config = characterConfigs.get(playerClass.toLowerCase());
    if (config == null) {
      config = characterConfigs.get("archer"); // Usar archer como padrão
    }
    return config;
  }

  public int getIntAttribute(String playerClass, String attribute, int defaultValue) {
    CharacterConfig config = getCharacterConfig(playerClass);
    if (config == null) return defaultValue;

    switch (attribute.toLowerCase()) {
      case "speed": return config.speed;
      case "health": return config.health;
      case "mana": return config.mana;
      case "strength": return config.strength;
      case "armor": return config.armor;
      case "agility": return config.agility;
      case "dexterity": return config.dexterity != null ? config.dexterity : defaultValue;
      case "willpower": return config.willpower != null ? config.willpower : defaultValue;
      case "endurance": return config.endurance != null ? config.endurance : defaultValue;
      case "magicpower": return config.magicPower != null ? config.magicPower : defaultValue;
      case "rage": return config.rage != null ? config.rage : defaultValue;
      case "luck": return config.luck != null ? config.luck : defaultValue;
      default: return defaultValue;
    }
  }

  public String getStringAttribute(String playerClass, String attribute, String defaultValue) {
    CharacterConfig config = getCharacterConfig(playerClass);
    if (config == null) return defaultValue;

    switch (attribute.toLowerCase()) {
      case "direction": return config.direction;
      case "name": return config.name;
      default: return defaultValue;
    }
  }

  public static class CharacterConfig {
    public String id;
    public String name;
    public int speed;
    public String direction;
    public int health;
    public int mana;
    public int strength;
    public int armor;
    public int agility;
    public Integer dexterity;
    public Integer willpower;
    public Integer endurance;
    public Integer magicPower;
    public Integer rage;
    public Integer luck;
  }
}
