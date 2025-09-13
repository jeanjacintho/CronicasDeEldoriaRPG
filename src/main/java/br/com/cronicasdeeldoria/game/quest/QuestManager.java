package br.com.cronicasdeeldoria.game.quest;

import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.entity.item.MagicOrb;
import br.com.cronicasdeeldoria.entity.item.QuestItem;
import br.com.cronicasdeeldoria.entity.object.TotemCentral;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.entity.character.npc.SupremeMage;
import br.com.cronicasdeeldoria.game.inventory.InventoryManager;

import java.util.*;

/**
 * Gerenciador principal do sistema de quests.
 * Controla todas as quests ativas, progresso e eventos relacionados.
 */
public class QuestManager {
    private static QuestManager instance;
    private Map<String, Quest> activeQuests;
    private Map<String, Quest> completedQuests;
    private List<MagicOrb> collectedOrbs;
    private List<MagicOrb> depositedOrbs;
    private TotemCentral totemCentral;
    private boolean bossSpawned;
    private GamePanel gamePanel;
    private Map<String, String> mapQuestTriggers; // mapa -> questId

    /**
     * Construtor privado para implementar Singleton.
     */
    private QuestManager() {
        this.activeQuests = new HashMap<>();
        this.completedQuests = new HashMap<>();
        this.collectedOrbs = new ArrayList<>();
        this.depositedOrbs = new ArrayList<>();
        this.bossSpawned = false;
        this.mapQuestTriggers = new HashMap<>();
    }

    /**
     * Obtém a instância única do QuestManager (Singleton).
     * @return Instância do QuestManager
     */
    public static QuestManager getInstance() {
        if (instance == null) {
            instance = new QuestManager();
        }
        return instance;
    }

    /**
     * Inicializa o QuestManager com referência ao GamePanel.
     * @param gamePanel Referência ao GamePanel
     */
    public void initialize(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        initializeMainQuest();
        initializeMapTriggers();
    }

    /**
     * Inicializa a quest principal das orbes mágicas.
     */
    private void initializeMainQuest() {
        Quest mainQuest = new Quest(
            "main_orb_quest",
            "Recuperar as Orbes Mágicas",
            "O Mago Supremo roubou as 4 orbes mágicas essenciais para o equilíbrio do mundo. " +
            "Você deve recuperar todas as orbes e levá-las ao Totem Central."
        );

        // Adicionar objetivos
        mainQuest.addObjective(new QuestObjective(
            "collect_fire_orb", "Coletar Orbe de Fogo",
            QuestObjectiveType.COLLECT_ITEM, "orb_fire"));
        mainQuest.addObjective(new QuestObjective(
            "collect_water_orb", "Coletar Orbe de Água",
            QuestObjectiveType.COLLECT_ITEM, "orb_water"));
        mainQuest.addObjective(new QuestObjective(
            "collect_earth_orb", "Coletar Orbe de Terra",
            QuestObjectiveType.COLLECT_ITEM, "orb_earth"));
        mainQuest.addObjective(new QuestObjective(
            "collect_air_orb", "Coletar Orbe de Ar",
            QuestObjectiveType.COLLECT_ITEM, "orb_air"));
        mainQuest.addObjective(new QuestObjective(
            "deposit_all_orbs", "Depositar todas as orbes no Totem Central",
            QuestObjectiveType.DEPOSIT_ITEM, "totem_central"));
        mainQuest.addObjective(new QuestObjective(
            "defeat_final_boss", "Derrotar o Mago Supremo",
            QuestObjectiveType.KILL_NPC, "supreme_mage"));

        // Definir recompensas
        QuestReward reward = new QuestReward(
            1000, 500,
            new String[]{"legendary_sword", "magic_armor"},
            "Parabéns! Você restaurou o equilíbrio do mundo!"
        );
        mainQuest.setReward(reward);

        activeQuests.put("main_orb_quest", mainQuest);
    }

    /**
     * Inicializa os triggers de mapa para quests.
     */
    private void initializeMapTriggers() {
        // Configurar triggers de mapa
        // Exemplo: quando entrar no mapa "totem", iniciar quest das orbes
        mapQuestTriggers.put("totem", "main_orb_quest");
    }

    /**
     * Chamado quando o jogador entra em um novo mapa.
     * Verifica se há alguma quest que deve ser iniciada neste mapa.
     * @param mapName Nome do mapa
     */
    public void onPlayerEnterMap(String mapName) {
        String questId = mapQuestTriggers.get(mapName);

        if (questId != null) {
            Quest quest = activeQuests.get(questId);

            // Verificar se a quest existe e ainda não foi iniciada
            if (quest != null && quest.getState() == QuestState.NOT_STARTED) {
                startQuest(questId);

                if (gamePanel != null && gamePanel.getGameUI() != null) {
                    gamePanel.getGameUI().addMessage(
                        "Quest automática iniciada: " + quest.getTitle(), null, 4000L);
                }
            }
        }
    }

    /**
     * Adiciona um trigger de mapa para uma quest.
     * @param mapName Nome do mapa
     * @param questId ID da quest a ser iniciada
     */
    public void addMapTrigger(String mapName, String questId) {
        mapQuestTriggers.put(mapName, questId);
    }

    /**
     * Remove um trigger de mapa.
     * @param mapName Nome do mapa
     */
    public void removeMapTrigger(String mapName) {
        mapQuestTriggers.remove(mapName);
    }

    /**
     * Inicia uma quest específica.
     * @param questId ID da quest a ser iniciada
     */
    public void startQuest(String questId) {
        Quest quest = activeQuests.get(questId);
        if (quest != null && quest.getState() == QuestState.NOT_STARTED) {
            quest.setState(QuestState.IN_PROGRESS);
            if (gamePanel != null && gamePanel.getGameUI() != null) {
                gamePanel.getGameUI().addMessage(
                    "Nova quest iniciada: " + quest.getTitle(), null, 4000L);
            }
        }
    }

    /**
     * Chamado quando um item de quest é coletado.
     * @param item Item de quest coletado
     */
    public void onQuestItemCollected(QuestItem item) {
        if (item instanceof MagicOrb) {
            onOrbCollected((MagicOrb) item);
        }

        // Atualizar objetivo genérico
        String objectiveId = "collect_" + item.getItemId();
        updateQuestObjective(item.getQuestId(), objectiveId, true);
    }

    /**
     * Chamado quando uma orbe mágica é coletada.
     * @param orb Orbe coletada
     */
    public void onOrbCollected(MagicOrb orb) {

        if (!collectedOrbs.contains(orb)) {
            collectedOrbs.add(orb);

            String objectiveId = "collect_" + orb.getOrbType() + "_orb";
            updateQuestObjective("main_orb_quest", objectiveId, true);

            if (gamePanel != null && gamePanel.getGameUI() != null) {
                gamePanel.getGameUI().addMessage(
                    "Orbe de " + orb.getOrbDisplayName() + " coletada! " +
                    "(" + collectedOrbs.size() + "/4)", null, 5000L);

                if (collectedOrbs.size() == 4) {
                    gamePanel.getGameUI().addMessage(
                        "Todas as orbes foram coletadas! Vá ao Totem Central!", null, 7000L);
                }
            }
        }
    }

    /**
     * Chamado quando uma orbe é depositada no totem.
     * @param orb Orbe depositada
     */
    public void onOrbDeposited(MagicOrb orb) {
        if (!depositedOrbs.contains(orb)) {
            depositedOrbs.add(orb);

            if (gamePanel != null && gamePanel.getGameUI() != null) {
                gamePanel.getGameUI().addMessage(
                    "Orbe de " + orb.getOrbDisplayName() + " depositada no Totem Central! " +
                    "(" + depositedOrbs.size() + "/4)", null, 5000L);

                if (depositedOrbs.size() == 4) {
                    spawnFinalBoss();
                }
            }
        }
    }

    /**
     * Spawna o boss final quando todas as orbes são depositadas.
     */
    private void spawnFinalBoss() {
        if (!bossSpawned) {
            bossSpawned = true;

            updateQuestObjective("main_orb_quest", "deposit_all_orbs", true);

            // Spawnar boss final relativo ao TotemCentral: 0,-4 tiles
            if (totemCentral != null && gamePanel != null) {
                int tileSize = gamePanel.getTileSize();
                int bossX = totemCentral.getWorldX() + 0 * tileSize;
                int bossY = totemCentral.getWorldY() + (-4) * tileSize;

                SupremeMage boss = new SupremeMage(bossX, bossY);
                gamePanel.addNpc(boss);

                if (gamePanel.getGameUI() != null) {
                    gamePanel.getGameUI().addMessage(
                        "O Mago Supremo foi despertado! Prepare-se para a batalha final!",
                        null, 8000L);
                }
            }
        }
    }

    /**
     * Chamado quando o boss final é derrotado.
     */
    public void onBossDefeated() {
        updateQuestObjective("main_orb_quest", "defeat_final_boss", true);
        completeQuest("main_orb_quest");

        if (gamePanel != null && gamePanel.getGameUI() != null) {
            gamePanel.getGameUI().addMessage(
                "Parabéns! Você derrotou o Mago Supremo e restaurou o equilíbrio do mundo!",
                null, 10000L);
        }
    }

    /**
     * Atualiza o estado de um objetivo específico.
     * @param questId ID da quest
     * @param objectiveId ID do objetivo
     * @param completed Se o objetivo foi completado
     */
    private void updateQuestObjective(String questId, String objectiveId, boolean completed) {
        Quest quest = activeQuests.get(questId);
        if (quest != null) {
            quest.updateObjective(objectiveId, completed);
        }
    }

    /**
     * Completa uma quest e dá as recompensas.
     * @param questId ID da quest a ser completada
     */
    private void completeQuest(String questId) {
        Quest quest = activeQuests.remove(questId);
        if (quest != null) {
            quest.setState(QuestState.COMPLETED);
            completedQuests.put(questId, quest);

            // Dar recompensas
            giveQuestRewards(quest);
        }
    }

    /**
     * Dá as recompensas de uma quest completada.
     * @param quest Quest completada
     */
    private void giveQuestRewards(Quest quest) {
        if (quest.getReward() != null && gamePanel != null) {
            QuestReward reward = quest.getReward();

            // Dar moedas
            if (reward.getMoney() > 0) {
                gamePanel.getPlayer().getPlayerMoney().addMoney(reward.getMoney());
            }

            // Dar experiência
            if (reward.getExperience() > 0) {
                gamePanel.getPlayer().gainXp(reward.getExperience());
            }

            // Dar itens
            for (String itemId : reward.getItemIds()) {
                // Implementar criação de itens de recompensa
                // gamePanel.getPlayer().getInventoryManager().addItem(ItemFactory.createItem(itemId));
                System.out.println("Item de recompensa: " + itemId);
            }

            if (gamePanel.getGameUI() != null) {
                String message = reward.getMessage();
                if (message == null || message.isEmpty()) {
                    message = "Quest completada!";
                }
                gamePanel.getGameUI().addMessage(message, null, 5000L);
            }
        }
    }

    /**
     * Atualiza o progresso das quests ativas.
     */
    public void updateQuestProgress() {
        // Verificar se alguma quest foi completada
        List<String> completedQuestIds = new ArrayList<>();
        for (Map.Entry<String, Quest> entry : activeQuests.entrySet()) {
            Quest quest = entry.getValue();
            if (quest.isCompleted() && quest.getState() == QuestState.IN_PROGRESS) {
                completedQuestIds.add(entry.getKey());
            }
        }

        // Completar quests que foram finalizadas
        for (String questId : completedQuestIds) {
            completeQuest(questId);
        }
    }

    // Getters
    public List<MagicOrb> getCollectedOrbs() {
        return new ArrayList<>(collectedOrbs);
    }

    public List<MagicOrb> getDepositedOrbs() {
        return new ArrayList<>(depositedOrbs);
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }

    public void setTotemCentral(TotemCentral totem) {
        this.totemCentral = totem;
    }

    public Quest getQuest(String questId) {
        return activeQuests.get(questId);
    }

    public Map<String, Quest> getActiveQuests() {
        return new HashMap<>(activeQuests);
    }

    public Map<String, Quest> getCompletedQuests() {
        return new HashMap<>(completedQuests);
    }

    public TotemCentral getTotemCentral() {
        return totemCentral;
    }
}
