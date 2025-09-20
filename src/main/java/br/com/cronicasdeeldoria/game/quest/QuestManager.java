package br.com.cronicasdeeldoria.game.quest;

import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.entity.item.MagicOrb;
import br.com.cronicasdeeldoria.entity.item.QuestItem;
import br.com.cronicasdeeldoria.entity.object.TotemCentral;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.inventory.ItemFactory;
import br.com.cronicasdeeldoria.entity.character.npc.Npc;
import br.com.cronicasdeeldoria.entity.character.npc.SupremeMage;

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
        initializeInitialQuest();
        initializeMainQuest();
        initializeMapTriggers();
        // Não inicializar todas as sub-quests de uma vez - serão criadas conforme necessário
    }

    /**
     * Inicializa a quest inicial "Rumo à Biblioteca".
     */
    private void initializeInitialQuest() {
        Quest initialQuest = new Quest(
            "go_library",
            "Rumo à Biblioteca",
            "Rumores sombrios correm pela vila. Dirija-se à cidade e procure o Sábio Ancião na biblioteca (2º andar)."
        );

        // Adicionar objetivos
        initialQuest.addObjective(new QuestObjective(
            "talk_smart_old_man", "Falar com o Sábio Ancião",
            QuestObjectiveType.TALK_TO_NPC, "smart_old_man"));

        // Definir recompensas
        QuestReward reward = new QuestReward(
            100, 0, // 110 moedas, 0 XP
            new String[]{},
            "Você encontrou o Sábio Ancião! Ele tem informações importantes sobre o equilíbrio do mundo."
        );
        initialQuest.setReward(reward);

        activeQuests.put("go_library", initialQuest);
    }

    /**
     * Inicializa a quest principal das orbes mágicas.
     * Esta quest não é adicionada às quests ativas - será criada quando necessário.
     */
    private void initializeMainQuest() {
        // A quest principal será criada dinamicamente quando o Sábio Ancião for encontrado
        // Não adicionar às activeQuests para não aparecer no início
    }

    /**
     * Cria a quest principal das orbes mágicas dinamicamente.
     */
    private void createMainOrbQuest() {
        if (activeQuests.containsKey("main_orb_quest")) {
            return; // Quest já existe
        }
        
        Quest mainQuest = new Quest(
            "main_orb_quest",
            "As 4 Orbes de Eldoria",
            "O Mago Supremo roubou as 4 orbes mágicas essenciais para o equilíbrio do mundo. " +
            "Você deve recuperar cada orbe individualmente e depositá-la no Totem Central."
        );

        // A quest principal não tem objetivos específicos - eles são gerenciados pelas sub-quests
        // Definir recompensas finais
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
        // Triggers de chegada às dungeons (atualizam objetivos REACH_LOCATION das sub-quests)
        // Não precisamos mais de triggers automáticos para iniciar quests - elas são iniciadas pelos diálogos
        mapQuestTriggers.put("dungeon2", "orb_1");
        mapQuestTriggers.put("dungeon4", "orb_2");
        mapQuestTriggers.put("dungeon3", "orb_3");
        mapQuestTriggers.put("dungeon1", "orb_4");
    }

    /**
     * Chamado quando o jogador fala com um NPC específico.
     * @param npcName Nome do NPC com quem o jogador falou
     */
    public void onPlayerTalkToNpc(String npcName) {
        // Mapear nomes de NPCs para IDs de quest
        String npcId = null;
        if (npcName.equals("Sábio Ancião")) {
            npcId = "smart_old_man";
        }
        
        if (npcId == null) return;
        
        // Verificar se há quests ativas que requerem falar com este NPC
        for (Map.Entry<String, Quest> entry : activeQuests.entrySet()) {
            Quest quest = entry.getValue();
            if (quest.getState() == QuestState.IN_PROGRESS) {
                // Verificar objetivos TALK_TO_NPC
                for (QuestObjective objective : quest.getObjectives()) {
                    if (objective.getType() == QuestObjectiveType.TALK_TO_NPC && 
                        objective.getTargetId().equals(npcId) && 
                        !objective.isCompleted()) {
                        updateQuestObjective(entry.getKey(), objective.getId(), true);
                        
                        // Se for o Sábio Ancião e a quest inicial estiver completa, iniciar a quest das orbes
                        if (npcId.equals("smart_old_man") && entry.getKey().equals("go_library")) {
                            completeInitialQuest();
                        }

                        if(npcId.equals("smart_old_man") && entry.getKey().equals("talk_smart_old_man")) {
                            ArrayList<Npc> npcs = (ArrayList<Npc>) gamePanel.getNpcs();
                            
                            for(Npc npc : npcs) {
                                if(npc.getName().equals("Sábio Ancião")) {
                                    npc.setDialogId(33);
                                }
                            }
                            
                        }
                    }
                }
            }
        }
    }

    /**
     * Chamado quando um NPC é morto.
     * @param npcName Nome do NPC que foi morto
     */
    public void onNpcKilled(String npcName) {
        System.out.println("QuestManager.onNpcKilled() chamado para: " + npcName);
        
        // Verificar se é o Supremo Boss primeiro
        if (npcName.equals("Mago Supremo") || npcName.contains("SupremeMage")) {
            System.out.println("Supremo Boss detectado! Atualizando quest final_boss...");
            
            // Atualizar objetivo da quest final_boss
            updateQuestObjective("final_boss", "kill_supreme_mage", true);
            
            // Chamar método de vitória final
            onSupremeBossDefeated();
            return;
        }
        
        // Mapear nomes de NPCs para IDs de quest
        String npcId = null;
        String orbType = null;
        if (npcName.equals("Orc Boss") || npcName.contains("orcboss")) {
            npcId = "orcboss";
        } else if (npcName.equals("Wolf Boss") || npcName.contains("wolfboss")) {
            npcId = "wolfboss";
            orbType = "fire";
        } else if (npcName.equals("Frostborn Boss") || npcName.contains("frostbornboss")) {
            npcId = "frostbornboss";
            orbType = "water";
        } else if (npcName.equals("Skeleton Boss") || npcName.contains("skeletonboss")) {
            npcId = "skeletonboss";
            orbType = "air";
        }
        
        
        if (npcId == null) {
            return;
        }
        
        // Verificar se há quests ativas que requerem matar este NPC
        for (Map.Entry<String, Quest> entry : activeQuests.entrySet()) {
            Quest quest = entry.getValue();
            
            if (quest.getState() == QuestState.IN_PROGRESS) {
                // Verificar objetivos KILL_NPC
                for (QuestObjective objective : quest.getObjectives()) {
                    if (objective.getType() == QuestObjectiveType.KILL_NPC && 
                        objective.getTargetId().equals(npcId) && 
                        !objective.isCompleted()) {
                        updateQuestObjective(entry.getKey(), objective.getId(), true);
                        
                        // Spawnar orbe correspondente
                        if (orbType != null && gamePanel != null) {
                            spawnOrbAfterBossKill(orbType);
                        }
                    }
                }
            }
        }
    }

    /**
     * Spawna uma orbe após a morte de um boss.
     * @param orbType Tipo da orbe a ser spawnada
     */
    private void spawnOrbAfterBossKill(String orbType) {
        if (gamePanel == null) return;
        
        try {
            // Criar orbe usando ItemFactory
            String orbId = "orb_" + orbType;
            Item item = ItemFactory.createItem(orbId);
            
            if (item instanceof MagicOrb) {
                MagicOrb orb = (MagicOrb) item;
                
                // Adicionar ao inventário do jogador
                if (gamePanel.getInventoryManager() != null) {
                    boolean success = gamePanel.getInventoryManager().addItem(orb);
                    if (success) {
                        // Notificar coleta da orbe
                        onOrbCollected(orb);
                        
                        if (gamePanel.getGameUI() != null) {
                            gamePanel.getGameUI().addMessage(
                                "Orbe de " + orb.getOrbDisplayName() + " apareceu após a morte do boss!", 
                                null, 5000L);
                        }
                    } else {
                        if (gamePanel.getGameUI() != null) {
                            gamePanel.getGameUI().addMessage(
                                "Inventário cheio! A orbe foi perdida.", 
                                null, 5000L);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao spawnar orbe após morte do boss: " + e.getMessage());
        }
    }

    /**
     * Chamado quando um item é coletado.
     * @param itemId ID do item coletado
     */
    public void onItemCollected(String itemId) {
        // Verificar se há quests ativas que requerem coletar este item
        for (Map.Entry<String, Quest> entry : activeQuests.entrySet()) {
            Quest quest = entry.getValue();
            if (quest.getState() == QuestState.IN_PROGRESS) {
                // Verificar objetivos COLLECT_ITEM
                for (QuestObjective objective : quest.getObjectives()) {
                    if (objective.getType() == QuestObjectiveType.COLLECT_ITEM && 
                        objective.getTargetId().equals(itemId) && 
                        !objective.isCompleted()) {
                        updateQuestObjective(entry.getKey(), objective.getId(), true);
                    }
                }
            }
        }
    }

    /**
     * Chamado quando um item é depositado em um local específico.
     * @param itemId ID do item depositado
     * @param locationId ID do local onde foi depositado
     */
    public void onItemDeposited(String itemId, String locationId) {
        // Verificar se há quests ativas que requerem depositar este item neste local
        for (Map.Entry<String, Quest> entry : activeQuests.entrySet()) {
            Quest quest = entry.getValue();
            if (quest.getState() == QuestState.IN_PROGRESS) {
                // Verificar objetivos DEPOSIT_ITEM
                for (QuestObjective objective : quest.getObjectives()) {
                    if (objective.getType() == QuestObjectiveType.DEPOSIT_ITEM && 
                        objective.getTargetId().equals(locationId) && 
                        !objective.isCompleted()) {
                        // Verificar se o item depositado corresponde ao objetivo
                        if (objective.getId().contains(itemId)) {
                            updateQuestObjective(entry.getKey(), objective.getId(), true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Completa a quest inicial e inicia a quest das orbes.
     */
    private void completeInitialQuest() {
        Quest initialQuest = activeQuests.get("go_library");
        if (initialQuest != null && initialQuest.isCompleted()) {
            // Completar a quest inicial
            completeQuest("go_library");
            
            // Criar e iniciar a quest das orbes dinamicamente
            createMainOrbQuest();
            startQuest("main_orb_quest");
            
            if (gamePanel != null && gamePanel.getGameUI() != null) {
                gamePanel.getGameUI().addMessage(
                    "Quest inicial completada! O Sábio Ancião tem informações importantes...", 
                    null, 5000L);
            }
        }
    }
    /**
     * Chamado quando o jogador entra em um mapa específico.
     * @param mapName Nome do mapa onde o jogador entrou
     */
    public void onPlayerEnterMap(String mapName) {
        String questId = mapQuestTriggers.get(mapName);

        if (questId != null) {
            Quest quest = activeQuests.get(questId);

            // Atualiza objetivos reach_<map> quando entrar no mapa target
            if (quest != null && quest.getState() == QuestState.IN_PROGRESS) {
                switch (mapName) {
                    case "dungeon2":
                        updateQuestObjective("orb_1", "reach_dungeon2", true);
                        break;
                    case "dungeon4":
                        updateQuestObjective("orb_2", "reach_dungeon4", true);
                        break;
                    case "dungeon3":
                        updateQuestObjective("orb_3", "reach_dungeon3", true);
                        break;
                    case "dungeon1":
                        updateQuestObjective("orb_4", "reach_dungeon1", true);
                        break;
                }
            }
        }
    }

    /**
     * Cria e inicia a sub-quest para a primeira orbe.
     */
    public void createOrb1Quest() {
        
        if (activeQuests.containsKey("orb_1")) {
            return; // Quest já existe
        }
        
        Quest orb1 = new Quest("orb_1", "Primeira Orbe", "Recupere a primeira orbe e deposite no Totem.");
        orb1.addObjective(new QuestObjective("reach_dungeon2", "Chegar à Dungeon 2", QuestObjectiveType.REACH_LOCATION, "dungeon2"));
        orb1.addObjective(new QuestObjective("kill_orcboss", "Derrotar o Orc Boss", QuestObjectiveType.KILL_NPC, "orcboss"));
        orb1.addObjective(new QuestObjective("collect_orb_earth", "Coletar a Orbe de Terra", QuestObjectiveType.COLLECT_ITEM, "orb_earth"));
        orb1.addObjective(new QuestObjective("deposit_orb_earth_at_totem", "Depositar no Totem", QuestObjectiveType.DEPOSIT_ITEM, "totem"));
        orb1.addObjective(new QuestObjective("talk_smart_old_man", "Falar com o Sábio", QuestObjectiveType.TALK_TO_NPC, "smart_old_man"));
        
        activeQuests.put("orb_1", orb1);
        
        // Iniciar a quest diretamente
        orb1.setState(QuestState.IN_PROGRESS);
        
        if (gamePanel != null && gamePanel.getGameUI() != null) {
            gamePanel.getGameUI().addMessage(
                "Nova quest iniciada: " + orb1.getTitle(), null, 4000L);
        }
    }

    /**
     * Cria e inicia a sub-quest para a segunda orbe.
     */
    public void createOrb2Quest() {
        
        if (activeQuests.containsKey("orb_2")) {
            return; // Quest já existe
        }
        
        Quest orb2 = new Quest("orb_2", "Segunda Orbe", "Recupere a segunda orbe e deposite no Totem.");
        orb2.addObjective(new QuestObjective("reach_dungeon4", "Chegar à Dungeon 4", QuestObjectiveType.REACH_LOCATION, "dungeon4"));
        orb2.addObjective(new QuestObjective("kill_wolfboss", "Derrotar o Wolf Boss", QuestObjectiveType.KILL_NPC, "wolfboss"));
        orb2.addObjective(new QuestObjective("collect_orb_fire", "Coletar a Orbe de Fogo", QuestObjectiveType.COLLECT_ITEM, "orb_fire"));
        orb2.addObjective(new QuestObjective("deposit_orb_fire_at_totem", "Depositar no Totem", QuestObjectiveType.DEPOSIT_ITEM, "totem"));
        orb2.addObjective(new QuestObjective("talk_smart_old_man", "Falar com o Sábio", QuestObjectiveType.TALK_TO_NPC, "smart_old_man"));
        
        activeQuests.put("orb_2", orb2);
        
        // Iniciar a quest diretamente
        orb2.setState(QuestState.IN_PROGRESS);
        
        if (gamePanel != null && gamePanel.getGameUI() != null) {
            gamePanel.getGameUI().addMessage(
                "Nova quest iniciada: " + orb2.getTitle(), null, 4000L);
        }
    }

    /**
     * Cria e inicia a sub-quest para a terceira orbe.
     */
    public void createOrb3Quest() {
        
        if (activeQuests.containsKey("orb_3")) {
            return; // Quest já existe
        }
        
        Quest orb3 = new Quest("orb_3", "Terceira Orbe", "Recupere a terceira orbe e deposite no Totem.");
        orb3.addObjective(new QuestObjective("reach_dungeon3", "Chegar à Dungeon 3", QuestObjectiveType.REACH_LOCATION, "dungeon3"));
        orb3.addObjective(new QuestObjective("kill_frostbornboss", "Derrotar o Frostborn Boss", QuestObjectiveType.KILL_NPC, "frostbornboss"));
        orb3.addObjective(new QuestObjective("collect_orb_water", "Coletar a Orbe de Água", QuestObjectiveType.COLLECT_ITEM, "orb_water"));
        orb3.addObjective(new QuestObjective("deposit_orb_water_at_totem", "Depositar no Totem", QuestObjectiveType.DEPOSIT_ITEM, "totem"));
        orb3.addObjective(new QuestObjective("talk_smart_old_man", "Falar com o Sábio", QuestObjectiveType.TALK_TO_NPC, "smart_old_man"));
        
        activeQuests.put("orb_3", orb3);
        
        // Iniciar a quest diretamente
        orb3.setState(QuestState.IN_PROGRESS);
        
        if (gamePanel != null && gamePanel.getGameUI() != null) {
            gamePanel.getGameUI().addMessage(
                "Nova quest iniciada: " + orb3.getTitle(), null, 4000L);
        }
    }

    /**
     * Cria e inicia a sub-quest para a quarta orbe.
     */
    public void createOrb4Quest() {
        
        if (activeQuests.containsKey("orb_4")) {
            return; // Quest já existe
        }
        
        Quest orb4 = new Quest("orb_4", "Quarta Orbe", "Recupere a quarta orbe e deposite no Totem.");
        orb4.addObjective(new QuestObjective("reach_dungeon1", "Chegar à Dungeon 1", QuestObjectiveType.REACH_LOCATION, "dungeon1"));
        orb4.addObjective(new QuestObjective("kill_skeletonboss", "Derrotar o Skeleton Boss", QuestObjectiveType.KILL_NPC, "skeletonboss"));
        orb4.addObjective(new QuestObjective("collect_orb_air", "Coletar a Orbe de Ar", QuestObjectiveType.COLLECT_ITEM, "orb_air"));
        orb4.addObjective(new QuestObjective("deposit_orb_air_at_totem", "Depositar no Totem", QuestObjectiveType.DEPOSIT_ITEM, "totem"));
        // A última orbe não exige falar com o sábio antes do boss final
        
        activeQuests.put("orb_4", orb4);
        
        // Iniciar a quest diretamente
        orb4.setState(QuestState.IN_PROGRESS);
        
        if (gamePanel != null && gamePanel.getGameUI() != null) {
            gamePanel.getGameUI().addMessage(
                "Nova quest iniciada: " + orb4.getTitle(), null, 4000L);
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
        
        // Criar sub-quests conforme necessário
        switch (questId) {
            case "orb_1":
                createOrb1Quest();
                return;
            case "orb_2":
                createOrb2Quest();
                return;
            case "orb_3":
                createOrb3Quest();
                return;
            case "orb_4":
                createOrb4Quest();
                return;
        }
        
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

            // Atualiza sub-quest específica baseada no tipo/nome da orbe
            switch (orb.getOrbType()) {
                case "fire":
                    updateQuestObjective("orb_2", "collect_orb_fire", true);
                    break;
                case "water":
                    updateQuestObjective("orb_3", "collect_orb_water", true);
                    break;
                case "earth":
                    updateQuestObjective("orb_1", "collect_orb_earth", true);
                    break;
                case "air":
                    updateQuestObjective("orb_4", "collect_orb_air", true);
                    break;
            }

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
                    // Completar a quest principal e spawnar o boss final
                    completeMainQuest();
                    spawnFinalBoss();
                }
            }

            // Atualiza sub-quest de depósito no Totem
            switch (orb.getOrbType()) {
                case "fire":
                    updateQuestObjective("orb_2", "deposit_orb_fire_at_totem", true);
                    break;
                case "water":
                    updateQuestObjective("orb_3", "deposit_orb_water_at_totem", true);
                    break;
                case "earth":
                    updateQuestObjective("orb_1", "deposit_orb_earth_at_totem", true);
                    break;
                case "air":
                    updateQuestObjective("orb_4", "deposit_orb_air_at_totem", true);
                    break;
            }
        }
    }

    /**
     * Completa a quest principal quando todas as orbes são depositadas.
     */
    private void completeMainQuest() {
        Quest mainQuest = activeQuests.get("main_orb_quest");
        if (mainQuest != null) {
            mainQuest.setState(QuestState.COMPLETED);
            completedQuests.put("main_orb_quest", mainQuest);
            activeQuests.remove("main_orb_quest");
            
            if (gamePanel != null && gamePanel.getGameUI() != null) {
                gamePanel.getGameUI().addMessage(
                    "Todas as orbes foram depositadas! O equilíbrio está sendo restaurado...", 
                    null, 5000L);
            }
        }
    }

    /**
     * Spawna o boss final quando todas as orbes são depositadas.
     */
    private void spawnFinalBoss() {
        if (!bossSpawned) {
            bossSpawned = true;

            // Spawnar boss final relativo ao TotemCentral: 0,-4 tiles
            if (totemCentral != null && gamePanel != null) {
                int tileSize = gamePanel.getTileSize();
                int bossX = totemCentral.getWorldX() + 0 * tileSize;
                int bossY = totemCentral.getWorldY() + (-4) * tileSize;

                SupremeMage boss = new SupremeMage(bossX, bossY);
                gamePanel.addNpc(boss);

                // Criar quest final do boss
                createFinalBossQuest();

                if (gamePanel.getGameUI() != null) {
                    gamePanel.getGameUI().addMessage(
                        "O Mago Supremo foi despertado! Prepare-se para a batalha final!",
                        null, 8000L);
                }
            }
        }
    }

    /**
     * Cria a quest final para derrotar o Supremo Boss.
     */
    private void createFinalBossQuest() {
        if (activeQuests.containsKey("final_boss")) {
            return; // Quest já existe
        }
        
        Quest finalBossQuest = new Quest(
            "final_boss",
            "Queda do Mago Supremo",
            "O Mago Supremo foi despertado! Derrote-o para restaurar o equilíbrio do mundo e completar sua jornada."
        );

        // Adicionar objetivo de matar o Supremo Boss
        finalBossQuest.addObjective(new QuestObjective(
            "kill_supreme_mage", 
            "Derrotar o Mago Supremo", 
            QuestObjectiveType.KILL_NPC, 
            "supreme_mage"
        ));

        // Definir recompensas finais épicas
        QuestReward reward = new QuestReward(
            5000, 1000, // 5000 moedas, 1000 XP
            new String[]{"legendary_sword", "magic_armor", "crown_of_eldoria"},
            "Parabéns! Você derrotou o Mago Supremo e restaurou o equilíbrio do mundo!"
        );
        finalBossQuest.setReward(reward);

        activeQuests.put("final_boss", finalBossQuest);
        
        // Iniciar a quest diretamente
        finalBossQuest.setState(QuestState.IN_PROGRESS);
        
        if (gamePanel != null && gamePanel.getGameUI() != null) {
            gamePanel.getGameUI().addMessage(
                "Nova quest iniciada: " + finalBossQuest.getTitle(), null, 4000L);
        }
    }

    /**
     * Chamado quando o Supremo Boss é derrotado.
     */
    public void onSupremeBossDefeated() {
        System.out.println("onSupremeBossDefeated() chamado!");
        
        if (gamePanel != null) {
            System.out.println("Ativando estado de endgame...");
            // Ativar estado de endgame
            gamePanel.gameState = gamePanel.endgameState;
            
            // Reproduzir música de vitória final
            if (gamePanel.getAudioManager() != null) {
                System.out.println("Reproduzindo música de vitória...");
                gamePanel.getAudioManager().changeContext(br.com.cronicasdeeldoria.audio.AudioContext.VICTORY);
            }
            
            // Dar recompensas da quest final_boss
            Quest finalBossQuest = completedQuests.get("final_boss");
            if (finalBossQuest != null && finalBossQuest.getReward() != null) {
                System.out.println("Dando recompensas da quest final_boss...");
                giveQuestRewards(finalBossQuest);
            } else {
                System.out.println("Quest final_boss não encontrada ou sem recompensas!");
            }
            
            System.out.println("Estado de endgame ativado com sucesso!");
        } else {
            System.out.println("ERRO: gamePanel é null!");
        }
    }

    /**
     * Chamado quando o boss final é derrotado.
     * @deprecated Use onSupremeBossDefeated() instead
     */
    public void onBossDefeated() {
        // Redirecionar para o novo método
        onSupremeBossDefeated();
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

            // Verificar se a quest foi completada
            if (quest.isCompleted()) {
                completeQuest(questId);
            }
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
                // TODO: Implementar criação de itens de recompensa
                // gamePanel.getPlayer().getInventoryManager().addItem(ItemFactory.createItem(itemId));
                System.out.println("Item de recompensa: " + itemId); // Evitar warning de variável não utilizada
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
