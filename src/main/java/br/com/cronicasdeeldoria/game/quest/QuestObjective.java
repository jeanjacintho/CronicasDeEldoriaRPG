package br.com.cronicasdeeldoria.game.quest;

/**
 * Representa um objetivo individual dentro de uma quest.
 * Cada quest pode ter múltiplos objetivos que devem ser completados.
 */
public class QuestObjective {
    private String id;
    private String description;
    private boolean completed;
    private QuestObjectiveType type;
    private String targetId; // ID do item/NPC/objeto alvo
    
    /**
     * Construtor para criar um objetivo de quest.
     * @param id ID único do objetivo
     * @param description Descrição do objetivo
     * @param type Tipo do objetivo
     * @param targetId ID do alvo (item, NPC, etc.)
     */
    public QuestObjective(String id, String description, QuestObjectiveType type, String targetId) {
        this.id = id;
        this.description = description;
        this.completed = false;
        this.type = type;
        this.targetId = targetId;
    }
    
    /**
     * Construtor simplificado para objetivos sem alvo específico.
     * @param id ID único do objetivo
     * @param description Descrição do objetivo
     * @param type Tipo do objetivo
     */
    public QuestObjective(String id, String description, QuestObjectiveType type) {
        this(id, description, type, null);
    }
    
    // Getters e setters
    public String getId() { 
        return id; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public boolean isCompleted() { 
        return completed; 
    }
    
    public void setCompleted(boolean completed) { 
        this.completed = completed; 
    }
    
    public QuestObjectiveType getType() { 
        return type; 
    }
    
    public String getTargetId() { 
        return targetId; 
    }
    
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}

