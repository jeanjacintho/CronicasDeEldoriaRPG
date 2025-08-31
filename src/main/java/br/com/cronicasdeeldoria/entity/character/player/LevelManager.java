package br.com.cronicasdeeldoria.entity.character.player;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Gerenciador de níveis do jogador.
 */
public class LevelManager {
    private static LevelManager instance;
    private List<LevelDefinition> levels;
    
    private LevelManager() {
        loadLevels();
    }
    
    public static LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }
    
    private void loadLevels() {
        try {
            java.io.InputStream is = LevelManager.class.getResourceAsStream("/levels.json");
            if (is != null) {
                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(new java.io.InputStreamReader(is), JsonArray.class);
                
                levels = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject levelJson = jsonArray.get(i).getAsJsonObject();
                    
                    LevelDefinition level = new LevelDefinition();
                    level.level = levelJson.get("level").getAsInt();
                    level.xpRequired = levelJson.get("xpRequired").getAsInt();
                    level.healthBonus = levelJson.get("healthBonus").getAsInt();
                    level.manaBonus = levelJson.get("manaBonus").getAsInt();
                    level.strengthBonus = levelJson.get("strengthBonus").getAsInt();
                    level.agilityBonus = levelJson.get("agilityBonus").getAsInt();
                    level.luckBonus = levelJson.get("luckBonus").getAsInt();
                    
                    levels.add(level);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar níveis: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Calcula o nível baseado no XP total.
     */
    public int calculateLevel(int totalXp) {
        int currentLevel = 1;
        
        for (LevelDefinition level : levels) {
            if (totalXp >= level.xpRequired) {
                currentLevel = level.level;
            } else {
                break;
            }
        }
        
        return currentLevel;
    }
    
    /**
     * Calcula o XP necessário para o próximo nível.
     */
    public int getXpForNextLevel(int currentLevel) {
        for (LevelDefinition level : levels) {
            if (level.level == currentLevel + 1) {
                return level.xpRequired;
            }
        }
        return -1; // Nível máximo atingido
    }
    
    /**
     * Retorna os bônus de atributos para um nível específico.
     */
    public LevelDefinition getLevelDefinition(int level) {
        for (LevelDefinition levelDef : levels) {
            if (levelDef.level == level) {
                return levelDef;
            }
        }
        return null;
    }
    
    /**
     * Retorna o XP necessário para o nível atual.
     */
    public int getXpForCurrentLevel(int currentLevel) {
        for (LevelDefinition level : levels) {
            if (level.level == currentLevel) {
                return level.xpRequired;
            }
        }
        return 0;
    }
    
    /**
     * Calcula o progresso do XP para o próximo nível (0.0 a 1.0).
     */
    public double getXpProgress(int currentXp, int currentLevel) {
        int xpForCurrent = getXpForCurrentLevel(currentLevel);
        int xpForNext = getXpForNextLevel(currentLevel);
        
        if (xpForNext == -1) {
            return 1.0; // Nível máximo
        }
        
        int xpInLevel = currentXp - xpForCurrent;
        int xpNeeded = xpForNext - xpForCurrent;
        
        return (double) xpInLevel / xpNeeded;
    }
    
    public static class LevelDefinition {
        public int level;
        public int xpRequired;
        public int healthBonus;
        public int manaBonus;
        public int strengthBonus;
        public int agilityBonus;
        public int luckBonus;
    }
}
