package br.com.cronicasdeeldoria.game.teleport;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Gerenciador de teleportes do jogo.
 * Carrega configurações de teleportes do arquivo teleports.json.
 */
public class TeleportManager {
    
    private static TeleportManager instance;
    private Map<String, TeleportConfig> teleports;
    private Map<String, String> quickTeleports;
    
    private TeleportManager() {
        this.teleports = new HashMap<>();
        this.quickTeleports = new HashMap<>();
        loadTeleportConfigs();
    }
    
    public static TeleportManager getInstance() {
        if (instance == null) {
            instance = new TeleportManager();
        }
        return instance;
    }
    
    /**
     * Carrega configurações de teleportes do arquivo JSON.
     */
    private void loadTeleportConfigs() {
        try {
            InputStream is = TeleportManager.class.getResourceAsStream("/teleports.json");
            if (is != null) {
                Gson gson = new Gson();
                JsonObject config = gson.fromJson(new InputStreamReader(is), JsonObject.class);
                
                // Carregar teleportes normais
                JsonObject teleportsObj = config.getAsJsonObject("teleports");
                if (teleportsObj != null) {
                    for (String key : teleportsObj.keySet()) {
                        JsonObject teleportObj = teleportsObj.getAsJsonObject(key);
                        TeleportConfig teleportConfig = gson.fromJson(teleportObj, TeleportConfig.class);
                        teleports.put(key, teleportConfig);
                    }
                }
                
                // Carregar teleportes rápidos
                JsonObject quickTeleportsObj = config.getAsJsonObject("quickTeleports");
                if (quickTeleportsObj != null) {
                    for (String key : quickTeleportsObj.keySet()) {
                        String value = quickTeleportsObj.get(key).getAsString();
                        quickTeleports.put(key, value);
                    }
                }
            } else {
                System.err.println("Arquivo teleports.json não encontrado!");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar configurações de teleporte: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtém um teleporte por ID.
     * @param teleportId ID do teleporte.
     * @return Configuração do teleporte ou null se não encontrado.
     */
    public TeleportConfig getTeleport(String teleportId) {
        return teleports.get(teleportId);
    }
    
    /**
     * Obtém um teleporte rápido por ID.
     * @param quickId ID do teleporte rápido.
     * @return String de teleporte no formato "mapa,x,y" ou null se não encontrado.
     */
    public String getQuickTeleport(String quickId) {
        return quickTeleports.get(quickId);
    }
    
    /**
     * Obtém todos os teleportes disponíveis.
     * @return Map com todos os teleportes.
     */
    public Map<String, TeleportConfig> getAllTeleports() {
        return new HashMap<>(teleports);
    }
    
    /**
     * Obtém todos os teleportes rápidos disponíveis.
     * @return Map com todos os teleportes rápidos.
     */
    public Map<String, String> getAllQuickTeleports() {
        return new HashMap<>(quickTeleports);
    }
    
    /**
     * Verifica se um teleporte existe.
     * @param teleportId ID do teleporte.
     * @return true se o teleporte existe.
     */
    public boolean hasTeleport(String teleportId) {
        return teleports.containsKey(teleportId);
    }
    
    /**
     * Verifica se um teleporte rápido existe.
     * @param quickId ID do teleporte rápido.
     * @return true se o teleporte rápido existe.
     */
    public boolean hasQuickTeleport(String quickId) {
        return quickTeleports.containsKey(quickId);
    }
    
    /**
     * Classe para configuração de teleporte.
     */
    public static class TeleportConfig {
        public String map;
        public String name;
        public String description;
        public Map<String, int[]> spawnPoints;
        
        public TeleportConfig() {
            this.spawnPoints = new HashMap<>();
        }
        
        /**
         * Obtém coordenadas de um ponto de spawn.
         * @param spawnPoint Nome do ponto de spawn.
         * @return Array [x, y] ou null se não encontrado.
         */
        public int[] getSpawnPoint(String spawnPoint) {
            return spawnPoints.get(spawnPoint);
        }
        
        /**
         * Obtém o primeiro ponto de spawn disponível.
         * @return Array [x, y] ou null se não houver pontos.
         */
        public int[] getFirstSpawnPoint() {
            if (spawnPoints.isEmpty()) {
                return null;
            }
            return spawnPoints.values().iterator().next();
        }
        
        /**
         * Gera string de teleporte para um ponto específico.
         * @param spawnPoint Nome do ponto de spawn.
         * @return String no formato "mapa,x,y" ou null se não encontrado.
         */
        public String generateTeleportString(String spawnPoint) {
            int[] coords = getSpawnPoint(spawnPoint);
            if (coords != null && coords.length == 2) {
                return map + "," + coords[0] + "," + coords[1];
            }
            return null;
        }
        
        /**
         * Gera string de teleporte para o primeiro ponto disponível.
         * @return String no formato "mapa,x,y" ou null se não houver pontos.
         */
        public String generateTeleportString() {
            int[] coords = getFirstSpawnPoint();
            if (coords != null && coords.length == 2) {
                return map + "," + coords[0] + "," + coords[1];
            }
            return null;
        }
    }
}
