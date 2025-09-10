package br.com.cronicasdeeldoria.game.quest;

import java.util.*;

/**
 * Representa uma quest no sistema de quests.
 * Contém objetivos, estado atual e recompensas.
 */
public class Quest {
    private String id;
    private String title;
    private String description;
    private List<QuestObjective> objectives;
    private QuestState state;
    private QuestReward reward;
    private Map<String, Object> questData;
    
    /**
     * Construtor para criar uma nova quest.
     * @param id ID único da quest
     * @param title Título da quest
     * @param description Descrição da quest
     */
    public Quest(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.objectives = new ArrayList<>();
        this.state = QuestState.NOT_STARTED;
        this.questData = new HashMap<>();
    }
    
    /**
     * Adiciona um objetivo à quest.
     * @param objective Objetivo a ser adicionado
     */
    public void addObjective(QuestObjective objective) {
        objectives.add(objective);
    }
    
    /**
     * Verifica se a quest está completa.
     * @return true se todos os objetivos foram completados
     */
    public boolean isCompleted() {
        return objectives.stream().allMatch(QuestObjective::isCompleted);
    }
    
    /**
     * Atualiza o estado de um objetivo específico.
     * @param objectiveId ID do objetivo
     * @param completed Se o objetivo foi completado
     */
    public void updateObjective(String objectiveId, boolean completed) {
        objectives.stream()
            .filter(obj -> obj.getId().equals(objectiveId))
            .findFirst()
            .ifPresent(obj -> obj.setCompleted(completed));
    }
    
    /**
     * Obtém um objetivo específico pelo ID.
     * @param objectiveId ID do objetivo
     * @return Objetivo encontrado ou null
     */
    public QuestObjective getObjective(String objectiveId) {
        return objectives.stream()
            .filter(obj -> obj.getId().equals(objectiveId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Obtém o progresso da quest em porcentagem.
     * @return Porcentagem de objetivos completados
     */
    public int getProgressPercentage() {
        if (objectives.isEmpty()) return 0;
        
        long completedCount = objectives.stream()
            .mapToLong(obj -> obj.isCompleted() ? 1 : 0)
            .sum();
            
        return (int) ((completedCount * 100) / objectives.size());
    }
    
    // Getters e setters
    public String getId() { 
        return id; 
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public List<QuestObjective> getObjectives() { 
        return new ArrayList<>(objectives); 
    }
    
    public QuestState getState() { 
        return state; 
    }
    
    public void setState(QuestState state) { 
        this.state = state; 
    }
    
    public QuestReward getReward() { 
        return reward; 
    }
    
    public void setReward(QuestReward reward) { 
        this.reward = reward; 
    }
    
    public Map<String, Object> getQuestData() {
        return questData;
    }
    
    public void setQuestData(String key, Object value) {
        questData.put(key, value);
    }
    
    public Object getQuestData(String key) {
        return questData.get(key);
    }
}

