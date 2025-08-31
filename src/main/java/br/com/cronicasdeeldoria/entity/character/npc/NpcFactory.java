package br.com.cronicasdeeldoria.entity.character.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                
                int x = npcTile.x * tileSize + (tileSize / 2) - (playerSize / 2);
                int y = npcTile.y * tileSize + (tileSize / 2) - (playerSize / 2);
                
                if ("warrior".equals(type)) {
                    npcs.add(new WarriorNpc(name, isStatic, dialog, x, y, skin, playerSize));
                } else {
                    npcs.add(new Npc(name, isStatic, dialog, x, y, skin, playerSize));
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
                // Usar Gson para parse mais confiável
                com.google.gson.Gson gson = new com.google.gson.Gson();
                com.google.gson.JsonArray jsonArray = gson.fromJson(new java.io.InputStreamReader(is), com.google.gson.JsonArray.class);
                
                for (int i = 0; i < jsonArray.size(); i++) {
                    com.google.gson.JsonObject npcJson = jsonArray.get(i).getAsJsonObject();
                    
                    Map<String, Object> npcData = new java.util.HashMap<>();
                    npcData.put("id", npcJson.get("id").getAsString());
                    npcData.put("name", npcJson.get("name").getAsString());
                    npcData.put("isStatic", npcJson.get("isStatic").getAsBoolean());
                    npcData.put("dialog", npcJson.get("dialog").getAsString());
                    npcData.put("type", npcJson.get("type").getAsString());
                    npcData.put("skin", npcJson.get("skin").getAsString());
                    
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
