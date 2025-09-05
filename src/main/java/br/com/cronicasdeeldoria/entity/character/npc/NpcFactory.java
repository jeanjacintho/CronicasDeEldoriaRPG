package br.com.cronicasdeeldoria.entity.character.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.com.cronicasdeeldoria.tile.TileManager.MapTile;

/**
 * Fábrica de NPCs responsável por carregar instâncias de NPCs a partir de um arquivo JSON.
 */
public class NpcFactory {

    /**
     * Carrega NPCs a partir de MapTiles (novo formato).
     */
    public static List<Npc> loadNpcsFromTiles(List<MapTile> npcTiles, int tileSize, int playerSize) {
        List<Npc> npcs = new ArrayList<>();

        // Carregar definições do JSON
        Map<String, Map<String, Object>> npcDefinitions = loadNpcDefinitionsFromJson();

        for (MapTile npcTile : npcTiles) {
            Map<String, Object> npcData = npcDefinitions.get(npcTile.id);
            if (npcData != null) {
                String type = (String) npcData.get("type");
                String name = (String) npcData.get("name");
                boolean isStatic = (Boolean) npcData.get("isStatic");
                String dialog = (String) npcData.get("dialog");
                String skin = (String) npcData.get("skin");
                boolean interactive = npcData.containsKey("interactive") ? (Boolean) npcData.get("interactive") : true;
                boolean autoInteraction = npcData.containsKey("autoInteraction") ? (Boolean) npcData.get("autoInteraction") : false;

                int x = npcTile.x * tileSize + (tileSize / 2) - (playerSize / 2);
                int y = npcTile.y * tileSize + (tileSize / 2) - (playerSize / 2);

                if ("barbarian".equals(type)) {
                    npcs.add(new WarriorNpc(name, isStatic, dialog, x, y, skin, playerSize, interactive, autoInteraction));
                }
                else if ("enemy".equals(type)) {
                  npcs.add(new WolfMonster(name, isStatic, dialog, x, y, skin, playerSize, interactive, autoInteraction));
                } else {
                    npcs.add(new Npc(name, isStatic, dialog, x, y, skin, playerSize, interactive, autoInteraction));
                }
            } else {
                System.err.println("Definição não encontrada para NPC ID: " + npcTile.id);
            }
        }

        return npcs;
    }

    /**
     * Carrega as definições de NPCs do arquivo JSON.
     */
    private static Map<String, Map<String, Object>> loadNpcDefinitionsFromJson() {
        Map<String, Map<String, Object>> definitions = new java.util.HashMap<>();

        try {
            java.io.InputStream is = NpcFactory.class.getResourceAsStream("/npcs.json");
            if (is != null) {

                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(new java.io.InputStreamReader(is), JsonArray.class);

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject npcJson = jsonArray.get(i).getAsJsonObject();

                    Map<String, Object> npcData = new HashMap<>();
                    npcData.put("id", npcJson.get("id").getAsString());
                    npcData.put("name", npcJson.get("name").getAsString());
                    npcData.put("isStatic", npcJson.get("isStatic").getAsBoolean());
                    npcData.put("dialog", npcJson.get("dialog").getAsString());
                    npcData.put("type", npcJson.get("type").getAsString());
                    npcData.put("skin", npcJson.get("skin").getAsString());

                    if (npcJson.has("interactive")) {
                        npcData.put("interactive", npcJson.get("interactive").getAsBoolean());
                    } else {
                        npcData.put("interactive", true);
                    }

                    if (npcJson.has("autoInteraction")) {
                        npcData.put("autoInteraction", npcJson.get("autoInteraction").getAsBoolean());
                    } else {
                        npcData.put("autoInteraction", false);
                    }

                    String id = npcData.get("id").toString();
                    definitions.put(id, npcData);
                }
            } else {
                System.err.println("Arquivo npcs.json não encontrado!");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar definições de NPCs do JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return definitions;
    }
}
