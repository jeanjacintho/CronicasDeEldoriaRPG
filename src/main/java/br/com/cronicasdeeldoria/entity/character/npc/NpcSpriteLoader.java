package br.com.cronicasdeeldoria.entity.character.npc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsável por carregar e fornecer sprites de NPCs a partir de um arquivo JSON.
 */
public class NpcSpriteLoader {
    private Map<String, Map<String, List<String>>> spriteMap;

    /**
     * Cria um novo loader de sprites de NPCs.
     * @param jsonPath Caminho para o arquivo JSON de sprites.
     * @throws Exception Se ocorrer erro na leitura ou parsing do arquivo.
     */
    public NpcSpriteLoader(String jsonPath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        spriteMap = mapper.readValue(new File(jsonPath), HashMap.class);
    }

    public List<String> getSprites(String skin, String direction) {
        Map<String, List<String>> skinMap = spriteMap.get(skin);
        if (skinMap != null) {
            return skinMap.get(direction);
        }
        return null;
    }
}
