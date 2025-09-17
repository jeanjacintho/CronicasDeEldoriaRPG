package br.com.cronicasdeeldoria.entity.character.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Responsável por carregar e fornecer sprites de NPCs.
 * Versão simplificada sem dependência do Jackson.
 */
public class NpcSpriteLoader {
    private Map<String, Map<String, List<String>>> spriteMap;

    /**
     * Cria um novo loader de sprites de NPCs.
     * @param jsonPath Caminho para o arquivo JSON de sprites.
     * @throws Exception Se ocorrer erro na inicialização.
     */
    public NpcSpriteLoader(String jsonPath) throws Exception {
        spriteMap = new HashMap<>();
        loadSpritesFromJson(jsonPath);
    }

    /**
     * Carrega sprites do arquivo JSON.
     */
    private void loadSpritesFromJson(String jsonPath) {
        try {
            java.io.InputStream is = NpcSpriteLoader.class.getResourceAsStream(jsonPath);
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
            System.err.println("Erro ao carregar sprites de NPCs: " + e.getMessage());
            e.printStackTrace();
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

    public List<String> getSprites(String skin, String direction) {
        Map<String, List<String>> skinMap = spriteMap.get(skin);
        if (skinMap != null) {
            List<String> sprites = skinMap.get(direction);
            
            return sprites;
        }
        return null;
    }

    /**
     * Obtém apenas o sprite de costas (back) de um NPC.
     * @param skin Nome do skin do NPC
     * @return Lista com o sprite de costas, ou null se não encontrado
     */
    public List<String> getBackSprites(String skin) {
        return getSprites(skin, "up");
    }
    
    /**
     * Obtém apenas o primeiro sprite de costas (back) de um NPC.
     * @param skin Nome do skin do NPC
     * @return Caminho do sprite de costas, ou null se não encontrado
     */
    public String getBackSprite(String skin) {
        List<String> backSprites = getBackSprites(skin);
        if (backSprites != null && !backSprites.isEmpty()) {
            return backSprites.get(0); // Retorna o primeiro sprite (estático)
        }
        return null;
    }
    
    /**
     * Obtém apenas o sprite frontal de um NPC.
     * @param skin Nome do skin do NPC
     * @return Lista com o sprite frontal, ou null se não encontrado
     */
    public List<String> getFrontSprites(String skin) {
        return getSprites(skin, "down");
    }
    
    /**
     * Obtém apenas o primeiro sprite frontal de um NPC.
     * @param skin Nome do skin do NPC
     * @return Caminho do sprite frontal, ou null se não encontrado
     */
    public String getFrontSprite(String skin) {
        List<String> frontSprites = getFrontSprites(skin);
        if (frontSprites != null && !frontSprites.isEmpty()) {
            return frontSprites.get(0); // Retorna o primeiro sprite (estático)
        }
        return null;
    }
    
    /**
     * Obtém apenas o sprite lateral esquerdo de um NPC.
     * @param skin Nome do skin do NPC
     * @return Lista com o sprite lateral esquerdo, ou null se não encontrado
     */
    public List<String> getLeftSprites(String skin) {
        return getSprites(skin, "left");
    }
    
    /**
     * Obtém apenas o primeiro sprite lateral esquerdo de um NPC.
     * @param skin Nome do skin do NPC
     * @return Caminho do sprite lateral esquerdo, ou null se não encontrado
     */
    public String getLeftSprite(String skin) {
        List<String> leftSprites = getLeftSprites(skin);
        if (leftSprites != null && !leftSprites.isEmpty()) {
            return leftSprites.get(0); // Retorna o primeiro sprite (estático)
        }
        return null;
    }
    
    /**
     * Obtém apenas o sprite lateral direito de um NPC.
     * @param skin Nome do skin do NPC
     * @return Lista com o sprite lateral direito, ou null se não encontrado
     */
    public List<String> getRightSprites(String skin) {
        return getSprites(skin, "right");
    }
    
    /**
     * Obtém apenas o primeiro sprite lateral direito de um NPC.
     * @param skin Nome do skin do NPC
     * @return Caminho do sprite lateral direito, ou null se não encontrado
     */
    public String getRightSprite(String skin) {
        List<String> rightSprites = getRightSprites(skin);
        if (rightSprites != null && !rightSprites.isEmpty()) {
            return rightSprites.get(0); // Retorna o primeiro sprite (estático)
        }
        return null;
    }
}
