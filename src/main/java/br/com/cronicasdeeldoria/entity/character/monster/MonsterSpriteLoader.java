package br.com.cronicasdeeldoria.entity.character.monster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Responsável por carregar e fornecer sprites dos Monsters.
 * Versão simplificada sem dependência do Jackson.
 */
public class MonsterSpriteLoader {
  private Map<String, Map<String, List<String>>> spriteMap;

  /**
   * Cria um novo loader de sprites de Monsters.
   * @param jsonPath Caminho para o arquivo JSON de sprites.
   * @throws Exception Se ocorrer erro na inicialização.
   */
  public MonsterSpriteLoader(String jsonPath) throws Exception {
    spriteMap = new HashMap<>();
    loadSpritesFromJson(jsonPath);
  }

  /**
   * Carrega sprites do arquivo JSON.
   */
  private void loadSpritesFromJson(String jsonPath) {
    try {
      java.io.InputStream is = MonsterSpriteLoader.class.getResourceAsStream(jsonPath);
      if (is != null) {
        java.util.Scanner scanner = new java.util.Scanner(is, "UTF-8");
        StringBuilder jsonContent = new StringBuilder();
        while (scanner.hasNextLine()) {
          jsonContent.append(scanner.nextLine());
        }
        scanner.close();

        String json = jsonContent.toString().trim();
        if (json.startsWith("{") && json.endsWith("}")) {
          json = json.substring(1, json.length() - 1);

          int pos = 0;
          while (pos < json.length()) {
            int skinStart = json.indexOf("\"", pos);
            if (skinStart == -1) break;

            int skinEnd = json.indexOf("\"", skinStart + 1);
            if (skinEnd == -1) break;

            String skinName = json.substring(skinStart + 1, skinEnd);

            int braceStart = json.indexOf("{", skinEnd);
            if (braceStart == -1) break;

            int braceCount = 1;
            int braceEnd = braceStart + 1;
            while (braceCount > 0 && braceEnd < json.length()) {
              char c = json.charAt(braceEnd);
              if (c == '{') braceCount++;
              else if (c == '}') braceCount--;
              braceEnd++;
            }

            if (braceCount == 0) {
              String skinContent = json.substring(braceStart + 1, braceEnd - 1);
              Map<String, List<String>> directions = parseDirections(skinContent);
              spriteMap.put(skinName, directions);

            }

            pos = braceEnd;
          }
        }
      } else {
        System.err.println("Arquivo de sprites não encontrado: " + jsonPath);
      }
    } catch (Exception e) {
      System.err.println("Erro ao carregar sprites de Monsters: " + e.getMessage());
      e.printStackTrace();
      // Fallback para sprites básicos
      addFallbackSprites();
    }
  }

  /**
   * Parse das direções de um skin.
   */
  private Map<String, List<String>> parseDirections(String skinContent) {
    Map<String, List<String>> directions = new HashMap<>();

    // Encontrar todas as direções no conteúdo
    int pos = 0;
    while (pos < skinContent.length()) {
      // Encontrar próxima direção
      int dirStart = skinContent.indexOf("\"", pos);
      if (dirStart == -1) break;

      int dirEnd = skinContent.indexOf("\"", dirStart + 1);
      if (dirEnd == -1) break;

      String directionName = skinContent.substring(dirStart + 1, dirEnd);

      // Encontrar array de sprites
      int bracketStart = skinContent.indexOf("[", dirEnd);
      if (bracketStart == -1) break;

      // Encontrar fim do array
      int bracketCount = 1;
      int bracketEnd = bracketStart + 1;
      while (bracketCount > 0 && bracketEnd < skinContent.length()) {
        char c = skinContent.charAt(bracketEnd);
        if (c == '[') bracketCount++;
        else if (c == ']') bracketCount--;
        bracketEnd++;
      }

      if (bracketCount == 0) {
        String spritesContent = skinContent.substring(bracketStart + 1, bracketEnd - 1);
        List<String> sprites = parseSpritesArray(spritesContent);
        directions.put(directionName, sprites);
      }

      pos = bracketEnd;
    }

    return directions;
  }

  /**
   * Parse de um array de sprites.
   */
  private List<String> parseSpritesArray(String spritesContent) {
    List<String> sprites = new ArrayList<>();

    // Encontrar todos os sprites no array
    int pos = 0;
    while (pos < spritesContent.length()) {
      // Encontrar próxima string de sprite
      int spriteStart = spritesContent.indexOf("\"", pos);
      if (spriteStart == -1) break;

      int spriteEnd = spritesContent.indexOf("\"", spriteStart + 1);
      if (spriteEnd == -1) break;

      String spritePath = spritesContent.substring(spriteStart + 1, spriteEnd);
      sprites.add(spritePath);

      pos = spriteEnd + 1;
    }

    return sprites;
  }

  /**
   * Adiciona sprites de fallback caso o carregamento falhe.
   */
  private void addFallbackSprites() {
    // Sprites básicos para fallback
    Map<String, List<String>> guardiaoSprites = new HashMap<>();
    guardiaoSprites.put("down", List.of("player/orc/orc_front.png"));
    guardiaoSprites.put("up", List.of("player/orc/orc_back.png"));
    guardiaoSprites.put("left", List.of("player/orc/orc_left.png"));
    guardiaoSprites.put("right", List.of("player/orc/orc_right.png"));
    spriteMap.put("guardiao", guardiaoSprites);

    Map<String, List<String>> guerreiroSprites = new HashMap<>();
    guerreiroSprites.put("down", List.of("player/breton/breton_front.png"));
    guerreiroSprites.put("up", List.of("player/breton/breton_back.png"));
    guerreiroSprites.put("left", List.of("player/breton/breton_left.png"));
    guerreiroSprites.put("right", List.of("player/breton/breton_right.png"));
    spriteMap.put("guerreiro", guerreiroSprites);
  }

  public List<String> getSprites(String skin, String direction) {
    Map<String, List<String>> skinMap = spriteMap.get(skin);
    if (skinMap != null) {
      List<String> sprites = skinMap.get(direction);

      return sprites;
    }
    return null;
  }
}
