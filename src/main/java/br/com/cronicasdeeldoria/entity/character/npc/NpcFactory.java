package br.com.cronicasdeeldoria.entity.character.npc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NpcFactory {
    public static List<Npc> loadNpcs(String jsonPath, int tileSize, int playerSize) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> npcList = mapper.readValue(new File(jsonPath), List.class);
        List<Npc> npcs = new ArrayList<>();
        for (Map<String, Object> npcData : npcList) {
            String type = (String) npcData.get("type");
            String name = (String) npcData.get("name");
            boolean isStatic = (Boolean) npcData.get("isStatic");
            String dialog = (String) npcData.get("dialog");
            int tileX = (Integer) npcData.get("tileX");
            int tileY = (Integer) npcData.get("tileY");
            
            int x = tileX * tileSize + (tileSize / 2) - (playerSize / 2);
            int y = tileY * tileSize + (tileSize / 2) - (playerSize / 2);
            
            String skin = (String) npcData.get("skin");
            if ("warrior".equals(type)) {
                npcs.add(new WarriorNpc(name, isStatic, dialog, x, y, skin, playerSize));
            } else {
                npcs.add(new Npc(name, isStatic, dialog, x, y, skin, playerSize));
            }
        }
        return npcs;
    }
}
