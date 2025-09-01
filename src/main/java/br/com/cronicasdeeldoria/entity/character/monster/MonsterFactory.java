package br.com.cronicasdeeldoria.entity.character.monster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.cronicasdeeldoria.tile.TileManager.MapTile;

/**
 * Fábrica de Monsters responsável por carregar instâncias de Monster
 * s a partir de um arquivo JSON.
 */
public class MonsterFactory {

  /**
   * Carrega Monsters a partir de MapTiles (novo formato).
   */
  public static List<Monster> loadMonstersFromTiles(List<MapTile> monsterTiles, int tileSize, int playerSize) {
    List<Monster> monsters = new ArrayList<>();

    // Carregar definições do JSON
    Map<String, Map<String, Object>> monsterDefinitions = loadMonsterDefinitionsFromJson();

    for (MapTile monsterTile : monsterTiles) {
      Map<String, Object> monsterData = monsterDefinitions.get(monsterTile.id);
      if (monsterData != null) {
        String type = (String) monsterData.get("type");
        String name = (String) monsterData.get("name");
        boolean isStatic = (Boolean) monsterData.get("isStatic");
        String dialog = (String) monsterData.get("dialog");
        String skin = (String) monsterData.get("skin");

        int x = monsterTile.x * tileSize + (tileSize / 2) - (playerSize / 2);
        int y = monsterTile.y * tileSize + (tileSize / 2) - (playerSize / 2);

        if ("monster".equals(type)) {
          monsters.add(new OrcMonster(name, isStatic, dialog, x, y, skin, playerSize));
        }
//        else {
//          monsters.add(new Npc(name, isStatic, dialog, x, y, skin, playerSize));
//        }
      } else {
        System.err.println("Definição não encontrada para Monster ID: " + monsterTile.id);
      }
    }

    return monsters;
  }

  /**
   * Carrega as definições de Monsters do arquivo JSON.
   */
  private static Map<String, Map<String, Object>> loadMonsterDefinitionsFromJson() {
    Map<String, Map<String, Object>> definitions = new java.util.HashMap<>();

    try {
      java.io.InputStream is = br.com.cronicasdeeldoria.entity.character.monster.MonsterFactory.class.getResourceAsStream("/monsters.json");
      if (is != null) {
        // Usar Gson para parse mais confiável
        com.google.gson.Gson gson = new com.google.gson.Gson();
        com.google.gson.JsonArray jsonArray = gson.fromJson(new java.io.InputStreamReader(is), com.google.gson.JsonArray.class);

        for (int i = 0; i < jsonArray.size(); i++) {
          com.google.gson.JsonObject monsterJson = jsonArray.get(i).getAsJsonObject();

          Map<String, Object> monsterData = new java.util.HashMap<>();
          monsterData.put("id", monsterJson.get("id").getAsString());
          monsterData.put("name", monsterJson.get("name").getAsString());
          monsterData.put("isStatic", monsterJson.get("isStatic").getAsBoolean());
          monsterData.put("dialog", monsterJson.get("dialog").getAsString());
          monsterData.put("type", monsterJson.get("type").getAsString());
          monsterData.put("skin", monsterJson.get("skin").getAsString());

          String id = monsterData.get("id").toString();
          definitions.put(id, monsterData);
        }
      } else {
        System.err.println("Arquivo monsters.json não encontrado!");
      }
    } catch (Exception e) {
      System.err.println("Erro ao carregar definições de Monsters do JSON: " + e.getMessage());
      e.printStackTrace();
    }

    return definitions;
  }
}
