package br.com.cronicasdeeldoria.entity.character.npc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fábrica de NPCs responsável por carregar instâncias de NPCs a partir de um arquivo JSON.
 */
public class NpcFactory {
    /**
     * Carrega uma lista de NPCs a partir de um arquivo JSON.
     *
     * @param jsonPath   Caminho para o arquivo JSON com os dados dos NPCs.
     * @param tileSize   Tamanho do tile para posicionamento dos NPCs.
     * @param playerSize Tamanho do jogador, usado para centralizar o NPC no tile.
     * @return Lista de NPCs instanciados.
     * @throws Exception Se ocorrer erro na leitura ou parsing do arquivo.
     */
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
