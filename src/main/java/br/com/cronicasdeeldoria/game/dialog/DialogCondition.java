package br.com.cronicasdeeldoria.game.dialog;

import br.com.cronicasdeeldoria.game.quest.QuestManager;
import br.com.cronicasdeeldoria.game.quest.QuestState;

/**
 * Representa uma condição para exibir um diálogo baseada no status de uma quest.
 */
public class DialogCondition {
    private String type;
    private String questId;
    private QuestState requiredStatus;
    
    /**
     * Construtor para criar uma condição de diálogo.
     * @param type Tipo da condição (ex: "quest_status")
     * @param questId ID da quest a ser verificada
     * @param requiredStatus Status requerido da quest
     */
    public DialogCondition(String type, String questId, QuestState requiredStatus) {
        this.type = type;
        this.questId = questId;
        this.requiredStatus = requiredStatus;
    }
    
    /**
     * Verifica se a condição é atendida.
     * @param questManager Gerenciador de quests para verificar o status
     * @return true se a condição for atendida
     */
    public boolean isMet(QuestManager questManager) {
        if (questManager == null) {
            return false;
        }
        
        switch (type) {
            case "quest_status":
                return checkQuestStatus(questManager);
            case "quest_completed":
                return questManager.getCompletedQuests().containsKey(questId);
            case "quest_not_started":
                return !questManager.getActiveQuests().containsKey(questId) && 
                       !questManager.getCompletedQuests().containsKey(questId);
            case "quest_in_progress":
                return questManager.getActiveQuests().containsKey(questId) &&
                       questManager.getQuest(questId).getState() == QuestState.IN_PROGRESS;
            default:
                return false;
        }
    }
    
    /**
     * Verifica o status específico de uma quest.
     * @param questManager Gerenciador de quests
     * @return true se o status da quest corresponde ao requerido
     */
    private boolean checkQuestStatus(QuestManager questManager) {
        // Verificar se a quest está ativa
        if (questManager.getActiveQuests().containsKey(questId)) {
            return questManager.getQuest(questId).getState() == requiredStatus;
        }
        
        // Verificar se a quest está completada
        if (questManager.getCompletedQuests().containsKey(questId)) {
            return requiredStatus == QuestState.COMPLETED;
        }
        
        // Se a quest não existe, só retorna true se o status requerido for NOT_STARTED
        return requiredStatus == QuestState.NOT_STARTED;
    }
    
    // Getters
    public String getType() {
        return type;
    }
    
    public String getQuestId() {
        return questId;
    }
    
    public QuestState getRequiredStatus() {
        return requiredStatus;
    }
    
    // Setters
    public void setType(String type) {
        this.type = type;
    }
    
    public void setQuestId(String questId) {
        this.questId = questId;
    }
    
    public void setRequiredStatus(QuestState requiredStatus) {
        this.requiredStatus = requiredStatus;
    }
    
    @Override
    public String toString() {
        return "DialogCondition{" +
                "type='" + type + '\'' +
                ", questId='" + questId + '\'' +
                ", requiredStatus=" + requiredStatus +
                '}';
    }
}
