package br.com.cronicasdeeldoria.game.dialog;

/**
 * Representa uma opção de diálogo que o jogador pode escolher.
 */
public class DialogOption {
    private String text;
    private int nextDialogId;
    private boolean hasAction;
    private String actionType;
    private String actionData;
    private boolean isAvailable;
    private String requirementType;
    private String requirementData;
    
    // Campos para dar itens
    private boolean givesItem;
    private String itemId;
    private int itemQuantity;

    /**
     * Cria uma nova opção de diálogo.
     * @param text Texto da opção
     * @param nextDialogId ID do próximo diálogo
     */
    public DialogOption(String text, int nextDialogId) {
        this.text = text;
        this.nextDialogId = nextDialogId;
        this.hasAction = false;
        this.isAvailable = true;
        this.givesItem = false;
        this.itemQuantity = 1;
    }

    /**
     * Cria uma nova opção de diálogo com ação.
     * @param text Texto da opção
     * @param nextDialogId ID do próximo diálogo
     * @param actionType Tipo da ação
     * @param actionData Dados da ação
     */
    public DialogOption(String text, int nextDialogId, String actionType, String actionData) {
        this.text = text;
        this.nextDialogId = nextDialogId;
        this.hasAction = true;
        this.actionType = actionType;
        this.actionData = actionData;
        this.isAvailable = true;
        this.givesItem = false;
        this.itemQuantity = 1;
    }

    /**
     * Cria uma nova opção de diálogo com requisitos.
     * @param text Texto da opção
     * @param nextDialogId ID do próximo diálogo
     * @param actionType Tipo da ação
     * @param actionData Dados da ação
     * @param requirementType Tipo do requisito
     * @param requirementData Dados do requisito
     */
    public DialogOption(String text, int nextDialogId, String actionType, String actionData, 
                       String requirementType, String requirementData) {
        this.text = text;
        this.nextDialogId = nextDialogId;
        this.hasAction = true;
        this.actionType = actionType;
        this.actionData = actionData;
        this.requirementType = requirementType;
        this.requirementData = requirementData;
        this.isAvailable = true;
        this.givesItem = false;
        this.itemQuantity = 1;
    }

    /**
     * Cria uma nova opção de diálogo que dá um item.
     * @param text Texto da opção
     * @param nextDialogId ID do próximo diálogo
     * @param itemId ID do item a ser dado
     * @param itemQuantity Quantidade do item
     */
    public DialogOption(String text, int nextDialogId, String itemId, int itemQuantity) {
        this.text = text;
        this.nextDialogId = nextDialogId;
        this.hasAction = false;
        this.isAvailable = true;
        this.givesItem = true;
        this.itemId = itemId;
        this.itemQuantity = itemQuantity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNextDialogId() {
        return nextDialogId;
    }

    public void setNextDialogId(int nextDialogId) {
        this.nextDialogId = nextDialogId;
    }

    public boolean hasAction() {
        return hasAction;
    }

    public void setHasAction(boolean hasAction) {
        this.hasAction = hasAction;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public String getRequirementData() {
        return requirementData;
    }

    public void setRequirementData(String requirementData) {
        this.requirementData = requirementData;
    }

    // Getters e setters para campos de item
    public boolean givesItem() {
        return givesItem;
    }

    public void setGivesItem(boolean givesItem) {
        this.givesItem = givesItem;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
}
