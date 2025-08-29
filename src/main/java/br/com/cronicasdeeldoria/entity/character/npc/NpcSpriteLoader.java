package br.com.cronicasdeeldoria.entity.character.npc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NpcSpriteLoader {
    private Map<String, Map<String, List<String>>> spriteMap;

    public NpcSpriteLoader(String jsonPath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        spriteMap = mapper.readValue(new File(jsonPath), HashMap.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getSprites(String skin, String direction) {
        Map<String, List<String>> skinMap = spriteMap.get(skin);
        if (skinMap != null) {
            return skinMap.get(direction);
        }
        return null;
    }
}
