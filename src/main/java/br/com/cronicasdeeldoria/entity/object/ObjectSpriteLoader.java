package br.com.cronicasdeeldoria.entity.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.cronicasdeeldoria.entity.character.AttributeType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Responsável por carregar e fornecer sprites de objetos.
 * Usando Gson para simplicidade.
 */
public class ObjectSpriteLoader {
    private Map<String, ObjectDefinition> objectDefinitions;

    /**
     * Cria um novo loader de sprites de objetos.
     * @param jsonPath Caminho para o arquivo JSON de objetos.
     * @throws Exception Se ocorrer erro na inicialização.
     */
    public ObjectSpriteLoader(String jsonPath) throws Exception {
        objectDefinitions = new HashMap<>();
        loadObjectsFromJson(jsonPath);
    }

    /**
     * Carrega objetos do arquivo JSON.
     */
    private void loadObjectsFromJson(String jsonPath) throws Exception {
        java.io.InputStream is = getClass().getResourceAsStream(jsonPath);
        if (is == null) {
            throw new Exception("Arquivo não encontrado: " + jsonPath);
        }

        java.util.Scanner scanner = new java.util.Scanner(is);
        scanner.useDelimiter("\\A");
        String json = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        // Usar Gson para parse direto
        Gson gson = new Gson();
        TypeToken<List<ObjectDefinition>> token = new TypeToken<List<ObjectDefinition>>(){};
        List<ObjectDefinition> objects = gson.fromJson(json, token.getType());

        if (objects != null) {
            for (ObjectDefinition objDef : objects) {
                if (objDef != null && objDef.id != null) {
                    objectDefinitions.put(objDef.id, objDef);
                  }
            }
        }
    }

    /**
     * Obtém a definição de um objeto por ID.
     */
    public ObjectDefinition getObjectDefinition(String objectId) {
        ObjectDefinition def = objectDefinitions.get(objectId);
        if (def == null) {
            System.err.println("DEBUG: Objeto não encontrado no ObjectSpriteLoader: " + objectId);
            System.err.println("DEBUG: Objetos disponíveis: " + objectDefinitions.keySet());
        }
        return def;
    }

    /**
     * Obtém todos os IDs de objetos disponíveis.
     */
    public java.util.Set<String> getAvailableObjectIds() {
        return objectDefinitions.keySet();
    }

    /**
     * Classe interna para representar a definição de um objeto.
     */
    public static class ObjectDefinition {
        public String id;
        public String name;
        public boolean collision;
        public Boolean interactive; // null significa usar padrão (true)
        public Boolean autoInteraction; // null significa usar padrão (false)
        public int[] size;
        public List<List<String>> spritePaths;

        // Campos adicionais para itens
        public String itemType;
        public String rarity;
        public String description;
        public int value;
        public List<String> allowedClass;
        //public Map<AttributeType, Integer> bonusAttributes = new HashMap<>();

      // Campos adicionais para quests
        public String questItem;
        public String orbType;
    }
}
