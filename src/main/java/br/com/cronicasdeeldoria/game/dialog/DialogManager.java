package br.com.cronicasdeeldoria.game.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.io.IOException;

import br.com.cronicasdeeldoria.entity.item.QuestItem;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.entity.character.npc.MerchantNpc;
import br.com.cronicasdeeldoria.entity.character.npc.Npc;
import br.com.cronicasdeeldoria.entity.item.Item;
import br.com.cronicasdeeldoria.game.inventory.ItemFactory;
import br.com.cronicasdeeldoria.game.teleport.TeleportManager;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.quest.QuestManager;

/**
 * Gerencia o sistema de diálogos do jogo, incluindo carregamento de diálogos
 * de arquivos JSON e execução de ações baseadas nas escolhas do jogador.
 */
public class DialogManager {
    private Map<Integer, Dialog> dialogs;
    private Dialog currentDialog;
    private boolean isDialogActive;
    private int selectedOptionIndex;
    private GamePanel gamePanel;
    private Player player;
    
    // Sistema de paginação
    private List<String> textPages;
    private int currentPageIndex;

    /**
     * Cria um novo gerenciador de diálogos.
     * @param gamePanel Painel do jogo
     * @param player Jogador
     */
    public DialogManager(GamePanel gamePanel, Player player) {
        this.gamePanel = gamePanel;
        this.player = player;
        this.dialogs = new HashMap<>();
        this.isDialogActive = false;
        this.selectedOptionIndex = 0;
        this.textPages = new ArrayList<>();
        this.currentPageIndex = 0;
        loadDialogsFromJson();
    }

    /**
     * Carrega os diálogos do arquivo JSON.
     */
    private void loadDialogsFromJson() {
        try {
            InputStream is = getClass().getResourceAsStream("/dialogs.json");
            if (is == null) {
                System.err.println("Arquivo dialogs.json não encontrado!");
                return;
            }

            Gson gson = new Gson();
            JsonArray dialogsArray = gson.fromJson(new String(is.readAllBytes()), JsonArray.class);

            for (JsonElement dialogElement : dialogsArray) {
                JsonObject dialogObj = dialogElement.getAsJsonObject();

                int id = dialogObj.get("id").getAsInt();
                String speakerName = dialogObj.get("speakerName").getAsString();
                String text = dialogObj.get("text").getAsString();
                // Carrega o sprite do retrato do npc se existir
                String portraitSprite = dialogObj.has("portraitSprite") ?
                    dialogObj.get("portraitSprite").getAsString() : null;

                Dialog dialog = new Dialog(id, speakerName, text, portraitSprite);

                // Carregar condição se existir
                if (dialogObj.has("condition")) {
                    JsonObject conditionObj = dialogObj.getAsJsonObject("condition");
                    String conditionType = conditionObj.get("type").getAsString();
                    String questId = conditionObj.get("questId").getAsString();
                    String statusStr = conditionObj.get("status").getAsString();
                    
                    br.com.cronicasdeeldoria.game.quest.QuestState requiredStatus = 
                        br.com.cronicasdeeldoria.game.quest.QuestState.valueOf(statusStr.toUpperCase());
                    
                    DialogCondition condition = new DialogCondition(conditionType, questId, requiredStatus);
                    dialog.setCondition(condition);
                }

                // Carregar opções se existirem
                if (dialogObj.has("options")) {
                    JsonArray optionsArray = dialogObj.getAsJsonArray("options");
                    for (JsonElement optionElement : optionsArray) {
                        JsonObject optionObj = optionElement.getAsJsonObject();

                        String optionText = optionObj.get("text").getAsString();
                        int nextDialogId = optionObj.has("nextDialogId") ?
                            optionObj.get("nextDialogId").getAsInt() : -1;

                        DialogOption option = new DialogOption(optionText, nextDialogId);

                        // Adicionar ação se existir
                        if (optionObj.has("actionType")) {
                            String actionType = optionObj.get("actionType").getAsString();
                            String actionData = optionObj.has("actionData") ?
                                optionObj.get("actionData").getAsString() : "";
                            option.setHasAction(true);
                            option.setActionType(actionType);
                            option.setActionData(actionData);
                        }

                        // Adicionar requisitos se existirem
                        if (optionObj.has("requirementType")) {
                            String requirementType = optionObj.get("requirementType").getAsString();
                            String requirementData = optionObj.has("requirementData") ?
                                optionObj.get("requirementData").getAsString() : "";
                            option.setRequirementType(requirementType);
                            option.setRequirementData(requirementData);
                        }

                        // Adicionar campos de item se existirem
                        if (optionObj.has("givesItem") && optionObj.get("givesItem").getAsBoolean()) {
                            String itemId = optionObj.has("itemId") ?
                                optionObj.get("itemId").getAsString() : "";
                            int itemQuantity = optionObj.has("itemQuantity") ?
                                optionObj.get("itemQuantity").getAsInt() : 1;
                            option.setGivesItem(true);
                            option.setItemId(itemId);
                            option.setItemQuantity(itemQuantity);
                        }

                        dialog.addOption(option);
                    }
                }

                // Verificar se é diálogo de fim
                if (dialogObj.has("isEndDialog") && dialogObj.get("isEndDialog").getAsBoolean()) {
                    dialog.setAsEndDialog();
                }

                dialogs.put(id, dialog);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar diálogos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Encontra o diálogo apropriado para um NPC baseado nas condições das quests.
     * @param npcName Nome do NPC
     * @return ID do diálogo apropriado ou -1 se nenhum for encontrado
     */
    public int findAppropriateDialog(String npcName) {
        QuestManager questManager = QuestManager.getInstance();
        if (questManager == null) {
            return -1;
        }

        // Para o Sábio Ancião, verificar o status das quests
        if (npcName.equals("Sábio Ancião")) {
            // Verificar se a quest inicial foi completada
            if (questManager.getCompletedQuests().containsKey("go_library")) {
                // Verificar status das orbes
                if (questManager.getCompletedQuests().containsKey("orb_1")) {
                    if (questManager.getCompletedQuests().containsKey("orb_2")) {
                        if (questManager.getCompletedQuests().containsKey("orb_3")) {
                            if (questManager.getCompletedQuests().containsKey("orb_4")) {
                                return 36; // Todas as orbes completadas
                            } else {
                                return 35; // Orbe 3 completada
                            }
                        } else {
                            return 34; // Orbe 2 completada
                        }
                    } else {
                        return 33; // Orbe 1 completada
                    }
                } else {
                    // Quest inicial completada, mas nenhuma orbe ainda
                    return 32; // Instruções para primeira orbe
                }
            } else {
                // Quest inicial não completada
                return 30; // Diálogo inicial
            }
        }

        return -1; // NPC não reconhecido ou sem diálogo condicional
    }

    /**
     * Inicia um diálogo apropriado para um NPC baseado nas condições.
     * @param npcName Nome do NPC
     * @return true se o diálogo foi iniciado com sucesso
     */
    public boolean startAppropriateDialog(String npcName) {
        int dialogId = findAppropriateDialog(npcName);
        if (dialogId != -1) {
            return startDialog(dialogId);
        }
        return false;
    }
    public boolean startDialog(int dialogId) {
        
        Dialog dialog = dialogs.get(dialogId);
        if (dialog == null) {
            System.err.println("Diálogo com ID " + dialogId + " não encontrado!");
            return false;
        }

        // Verificar se a condição do diálogo é atendida
        QuestManager questManager = QuestManager.getInstance();
        if (!dialog.isConditionMet(questManager)) {
            return false;
        }

        this.currentDialog = dialog;
        this.isDialogActive = true;
        this.selectedOptionIndex = 0;
        this.currentPageIndex = 0;

        // Resetar paginação da UI para novo diálogo
        if (gamePanel != null && gamePanel.getDialogUI() != null) {
            gamePanel.getDialogUI().resetPagination();
        }

        // Processar texto em páginas (mantido para compatibilidade)
        processTextIntoPages(dialog.getText());

        // Verificar disponibilidade das opções
        updateOptionAvailability();

        return true;
    }

    /**
     * Processa o texto do diálogo em páginas baseado no espaço disponível.
     * @param text Texto completo do diálogo
     */
    private void processTextIntoPages(String text) {
        textPages.clear();
        
        // Configurações de paginação - ajustadas para caixa menor
        int maxWidth = 300; 
        int maxHeight = 100; 
        int lineHeight = 18; // Altura aproximada de uma linha
        int maxLinesPerPage = maxHeight / lineHeight; // Aproximadamente 5-6 linhas por página
        
        String[] words = text.split(" ");
        StringBuilder currentPage = new StringBuilder();
        int currentLines = 0;
        
        for (String word : words) {
            String testText = currentPage.length() > 0 ? 
                currentPage + " " + word : word;
            
            // Estimativa mais precisa do comprimento da linha (baseada na fonte de 16px)
            int estimatedLength = testText.length() * 9;
            
            if (estimatedLength > maxWidth && currentPage.length() > 0) {
                // Verificar se ainda cabe mais linhas nesta página
                if (currentLines >= maxLinesPerPage) {
                    // Finalizar página atual e começar nova
                    textPages.add(currentPage.toString().trim());
                    currentPage = new StringBuilder(word);
                    currentLines = 1;
                } else {
                    // Quebrar linha
                    currentPage.append("\n").append(word);
                    currentLines++;
                }
            } else {
                // Adicionar palavra à linha atual
                if (currentPage.length() > 0) {
                    currentPage.append(" ");
                }
                currentPage.append(word);
            }
        }
        
        // Adicionar última página se não estiver vazia
        if (currentPage.length() > 0) {
            textPages.add(currentPage.toString().trim());
        }
        
        // Se não há páginas, criar uma página vazia
        if (textPages.isEmpty()) {
            textPages.add("");
        }
    }

    /**
     * Avança para a próxima página do diálogo.
     * @return true se conseguiu avançar, false se já está na última página
     */
    public boolean nextPage() {
        if (currentPageIndex < textPages.size() - 1) {
            currentPageIndex++;
            return true;
        }
        return false;
    }

    /**
     * Volta para a página anterior do diálogo.
     * @return true se conseguiu voltar, false se já está na primeira página
     */
    public boolean previousPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            return true;
        }
        return false;
    }

    /**
     * Obtém o texto da página atual.
     * @return Texto da página atual
     */
    public String getCurrentPageText() {
        if (textPages.isEmpty() || currentPageIndex >= textPages.size()) {
            return "";
        }
        return textPages.get(currentPageIndex);
    }

    /**
     * Verifica se o diálogo tem múltiplas páginas.
     * @return true se tem mais de uma página
     */
    public boolean hasMultiplePages() {
        return textPages.size() > 1;
    }

    /**
     * Verifica se está na última página.
     * @return true se está na última página
     */
    public boolean isOnLastPage() {
        return currentPageIndex >= textPages.size() - 1;
    }

    /**
     * Verifica se está na primeira página.
     * @return true se está na primeira página
     */
    public boolean isOnFirstPage() {
        return currentPageIndex <= 0;
    }

    /**
     * Obtém o índice da página atual.
     * @return Índice da página atual (0-based)
     */
    public int getCurrentPage() {
        return currentPageIndex;
    }

    /**
     * Obtém o número total de páginas.
     * @return Número total de páginas
     */
    public int getTotalPages() {
        return textPages.size();
    }

    /**
     * Atualiza a disponibilidade das opções baseada nos requisitos.
     */
    private void updateOptionAvailability() {
        if (currentDialog == null) return;

        for (DialogOption option : currentDialog.getOptions()) {
            boolean available = true;

            if (option.getRequirementType() != null) {
                switch (option.getRequirementType()) {
                    case "level":
                        int requiredLevel = Integer.parseInt(option.getRequirementData());
                        available = player.getCurrentLevel() >= requiredLevel;
                        break;
                    case "item":
                        // Implementar verificação de itens futuramente
                        available = true;
                        break;
                    case "money":
                        int requiredMoney = Integer.parseInt(option.getRequirementData());
                        available = player.getPlayerMoney().getCurrentMoney() >= requiredMoney;
                        break;
                    case "quest":
                        // Implementar sistema de quests futuramente
                        available = true;
                        break;
                }
            }

            option.setAvailable(available);
        }
    }

    /**
     * Seleciona a próxima opção.
     */
    public void selectNextOption() {
        if (!isDialogActive || currentDialog == null) return;

        List<DialogOption> availableOptions = currentDialog.getAvailableOptions();
        if (availableOptions.isEmpty()) return;

        selectedOptionIndex = (selectedOptionIndex + 1) % availableOptions.size();
    }

    /**
     * Seleciona a opção anterior.
     */
    public void selectPreviousOption() {
        if (!isDialogActive || currentDialog == null) return;

        List<DialogOption> availableOptions = currentDialog.getAvailableOptions();
        if (availableOptions.isEmpty()) return;

        selectedOptionIndex = selectedOptionIndex > 0 ?
            selectedOptionIndex - 1 : availableOptions.size() - 1;
    }

    /**
     * Confirma a seleção atual e executa a ação.
     * NOTA: Este método só deve ser chamado quando se está na última página!
     */
    public void confirmSelection() {

        if (!isDialogActive || currentDialog == null) {
            return;
        }

        List<DialogOption> availableOptions = currentDialog.getAvailableOptions();
        
        if (availableOptions.isEmpty()) {
            endDialog();
            return;
        }

        DialogOption selectedOption = availableOptions.get(selectedOptionIndex);
        // Dar item se a opção especificar
        if (selectedOption.givesItem()) {
            giveItemToPlayer(selectedOption.getItemId(), selectedOption.getItemQuantity());
        }

        // Executar ação se existir
        if (selectedOption.hasAction()) {
            executeAction(selectedOption.getActionType(), selectedOption.getActionData());
        }

        // Continuar para o próximo diálogo ou encerrar
        if (selectedOption.getNextDialogId() > 0) {
            startDialog(selectedOption.getNextDialogId());
        } else {
            endDialog();
        }
    }

    /**
     * Dá um item ao jogador.
     * @param itemId ID do item
     * @param quantity Quantidade do item
     */
    public void giveItemToPlayer(String itemId, int quantity) {
        try {
            // Criar item a partir do ID usando ItemFactory
            Item item = ItemFactory.createItem(itemId);
            if (item == null) {
                System.err.println("Não foi possível criar item com ID: " + itemId);
                return;
            }

            // Definir quantidade se necessário
            if (quantity > 1) {
                item.setStackSize(quantity);
            }

            // Usar o InventoryManager para adicionar o item
            if (gamePanel.getInventoryManager() != null) {
              // Se o item coletado for uma Orb, chama a quest
              if (item instanceof QuestItem) {
                ((QuestItem) item).onCollect();
              }
              boolean success = gamePanel.getInventoryManager().addItem(item);
              if (success) {
                // Notificar QuestManager sobre coleta de item
                if (gamePanel.getQuestManager() != null) {
                  gamePanel.getQuestManager().onItemCollected(itemId);
                }
                gamePanel.getGameUI().addMessage("Você recebeu " + quantity + "x " + item.getName() + "!", null, 3500L);
              } else {
                gamePanel.getGameUI().addMessage("Inventário cheio! Não foi possível receber o item.", null, 3500L);
              }
            } else {
                System.err.println("InventoryManager não disponível para dar item: " + itemId);
            }
        } catch (Exception e) {
            System.err.println("Erro ao dar item ao jogador: " + e.getMessage());
        }
    }

    /**
     * Executa ação de teleporte com suporte a mudança de mapa.
     * @param actionData Dados da ação no formato: "mapa,x,y", "x,y", "teleportId:spawnPoint", ou "quickId"
     */
    private void performTeleportAction(String actionData) {
        try {
            TeleportManager teleportManager = TeleportManager.getInstance();

            // Verificar se é um teleporte rápido
            if (teleportManager.hasQuickTeleport(actionData)) {
                String teleportString = teleportManager.getQuickTeleport(actionData);
                executeTeleportString(teleportString, "Teleporte rápido executado!");
                return;
            }

            // Verificar se é um teleporte configurado (formato: "teleportId:spawnPoint")
            if (actionData.contains(":")) {
                String[] parts = actionData.split(":", 2);
                String teleportId = parts[0].trim();
                String spawnPoint = parts[1].trim();

                TeleportManager.TeleportConfig config = teleportManager.getTeleport(teleportId);
                if (config != null) {
                    String teleportString = config.generateTeleportString(spawnPoint);
                    if (teleportString != null) {
                        executeTeleportString(teleportString, "Você foi teleportado para " + config.name + "!");
                        return;
                    } else {
                        gamePanel.getGameUI().addMessage("Ponto de spawn '" + spawnPoint + "' não encontrado!", null, 3500L);
                        return;
                    }
                } else {
                    gamePanel.getGameUI().addMessage("Teleporte '" + teleportId + "' não encontrado!", null, 3500L);
                    return;
                }
            }

            // Teleporte direto por coordenadas
            executeTeleportString(actionData, "Você foi teleportado!");

        } catch (Exception e) {
            System.err.println("Erro no teleporte: " + e.getMessage());
            gamePanel.getGameUI().addMessage("Erro no teleporte!", null, 3500L);
        }
    }

    /**
     * Executa uma string de teleporte no formato "mapa,x,y" ou "x,y".
     * @param teleportString String de teleporte.
     * @param message Mensagem a ser exibida.
     */
    private void executeTeleportString(String teleportString, String message) {
        try {
            String[] parts = teleportString.split(",");

            if (parts.length == 2) {
                // Teleporte no mapa atual: "x,y"
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                player.setWorldX(x);
                player.setWorldY(y);
                gamePanel.getGameUI().addMessage(message, null, 3500L);

            } else if (parts.length == 3) {
                // Teleporte para outro mapa: "mapa,x,y"
                String mapName = parts[0].trim();
                int x = Integer.parseInt(parts[1].trim());
                int y = Integer.parseInt(parts[2].trim());

                // Carregar novo mapa
                loadMap(mapName);

                // Posicionar jogador na nova posição
                player.setWorldX(x);
                player.setWorldY(y);

                gamePanel.getGameUI().addMessage(message, null, 3500L);

            } else {
                System.err.println("Formato de teleporte inválido: " + teleportString);
                gamePanel.getGameUI().addMessage("Erro no formato do teleporte!", null, 3500L);
            }

        } catch (NumberFormatException e) {
            System.err.println("Erro ao parsear coordenadas de teleporte: " + teleportString);
            gamePanel.getGameUI().addMessage("Erro nas coordenadas do teleporte!", null, 3500L);
        }
    }

    /**
     * Carrega um novo mapa.
     * @param mapName Nome do mapa (sem extensão .json)
     */
    private void loadMap(String mapName) {
        try {
            // Usar reflexão para acessar o método privado loadMap do GamePanel
            java.lang.reflect.Method loadMapMethod = gamePanel.getClass().getDeclaredMethod("loadMap", String.class);
            loadMapMethod.setAccessible(true);
            loadMapMethod.invoke(gamePanel, mapName);

        } catch (Exception e) {
            System.err.println("Erro ao carregar mapa " + mapName + ": " + e.getMessage());

            // Fallback: tentar carregar diretamente via TileManager
            try {
                gamePanel.getTileManager().loadMapJson("/maps/" + mapName + ".json");
                // Usar reflexão para acessar método privado
                Method loadNpcsMethod = gamePanel.getClass().getDeclaredMethod("loadNpcsFromMap");
                loadNpcsMethod.setAccessible(true);
                loadNpcsMethod.invoke(gamePanel);
            } catch (Exception fallbackError) {
                System.err.println("Erro no fallback de carregamento de mapa: " + fallbackError.getMessage());
            }
        }
    }

    /**
     * Encontra o comerciante mais próximo do jogador.
     * @return MerchantNpc mais próximo ou null se não encontrado
     */
    private MerchantNpc findNearestMerchant() {
        if (gamePanel.getNpcs() == null) return null;

        MerchantNpc nearestMerchant = null;
        double minDistance = Double.MAX_VALUE;

        for (Npc npc : gamePanel.getNpcs()) {
            if (npc instanceof MerchantNpc) {
                double distance = Math.sqrt(
                    Math.pow(npc.getWorldX() - player.getWorldX(), 2) +
                    Math.pow(npc.getWorldY() - player.getWorldY(), 2)
                );

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestMerchant = (MerchantNpc) npc;
                }
            }
        }

        return nearestMerchant;
    }

    /**
     * Executa uma ação baseada no tipo e dados fornecidos.
     * @param actionType Tipo da ação
     * @param actionData Dados da ação
     */
    private void executeAction(String actionType, String actionData) {
        switch (actionType) {
            case "give_item":
                // Implementar sistema de itens futuramente
                gamePanel.getGameUI().addMessage("Você recebeu: " + actionData, null, 3500L);
                break;
            case "give_money":
                int amount = Integer.parseInt(actionData);
                player.getPlayerMoney().addMoney(amount);
                gamePanel.getGameUI().addMessage("Você recebeu " + amount + " moedas!", null, 3500L);
                break;
            case "take_money":
                int cost = Integer.parseInt(actionData);
                if (player.getPlayerMoney().getCurrentMoney() >= cost) {
                    player.getPlayerMoney().removeMoney(cost);
                    gamePanel.getGameUI().addMessage("Você pagou " + cost + " moedas.", null, 3500L);
                }
                break;
            case "heal":
                int healAmount = Integer.parseInt(actionData);
                player.heal(healAmount);
                gamePanel.getGameUI().addMessage("Você foi curado em " + healAmount + " HP!", null, 3500L);
                break;
            case "teleport":
                performTeleportAction(actionData);
                break;
            case "open_merchant":
                // Abrir interface de comércio
                if (gamePanel.getMerchantManager() != null) {
                    // Encontrar o comerciante mais próximo
                    MerchantNpc nearestMerchant = findNearestMerchant();
                    if (nearestMerchant != null) {
                        gamePanel.getMerchantManager().openMerchant(nearestMerchant);
                        gamePanel.gameState = gamePanel.merchantState;
                    }
                }
                break;
            case "start_battle":
                // Implementar início de batalha
                break;
            case "start_quest":
                startQuest(actionData);
                break;
            case "complete_quest":
                // Implementar sistema de quests
                break;
        }
    }

    /**
     * Inicia uma quest específica.
     * @param questId ID da quest a ser iniciada
     */
    private void startQuest(String questId) {
        QuestManager questManager = QuestManager.getInstance();
        if (questManager != null) {
            questManager.startQuest(questId);

            if (gamePanel != null && gamePanel.getGameUI() != null) {
                gamePanel.getGameUI().addMessage(
                    "Nova quest iniciada: " + questManager.getQuest(questId).getTitle(),
                    null, 4000L);
            }
        }
    }

    /**
     * Encerra o diálogo atual e reseta o estado.
     */
    public void endDialog() {
        this.isDialogActive = false;
        this.currentDialog = null;
        this.selectedOptionIndex = 0;
        this.currentPageIndex = 0;
        this.textPages.clear();
    }

    /**
     * Verifica se há um diálogo ativo.
     * @return true se há diálogo ativo
     */
    public boolean isDialogActive() {
        return isDialogActive;
    }

    /**
     * Obtém o diálogo atual.
     * @return Diálogo atual ou null
     */
    public Dialog getCurrentDialog() {
        return currentDialog;
    }

    /**
     * Obtém o índice da opção selecionada.
     * @return Índice da opção selecionada
     */
    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    /**
     * Obtém todas as opções disponíveis do diálogo atual.
     * @return Lista de opções disponíveis
     */
    public List<DialogOption> getCurrentAvailableOptions() {
        if (currentDialog == null) return new ArrayList<>();
        return currentDialog.getAvailableOptions();
    }
}
