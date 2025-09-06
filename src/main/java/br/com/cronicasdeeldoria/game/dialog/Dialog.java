package br.com.cronicasdeeldoria.game.dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um diálogo com múltiplas opções e ações possíveis.
 */
public class Dialog {
    private int id;
    private String speakerName;
    private String text;
    private List<DialogOption> options;
    private boolean isEndDialog;
    private String portraitSprite;
    private int portraitFrame;

    /**
     * Cria um novo diálogo.
     * @param id ID único do diálogo
     * @param speakerName Nome do personagem que fala
     * @param text Texto do diálogo
     */
    public Dialog(int id, String speakerName, String text) {
        this.id = id;
        this.speakerName = speakerName;
        this.text = text;
        this.options = new ArrayList<>();
        this.isEndDialog = false;
        this.portraitFrame = 0;
    }

    /**
     * Cria um novo diálogo com sprite de retrato.
     * @param id ID único do diálogo
     * @param speakerName Nome do personagem que fala
     * @param text Texto do diálogo
     * @param portraitSprite Sprite do retrato
     */
    public Dialog(int id, String speakerName, String text, String portraitSprite) {
        this.id = id;
        this.speakerName = speakerName;
        this.text = text;
        this.options = new ArrayList<>();
        this.isEndDialog = false;
        this.portraitSprite = portraitSprite;
        this.portraitFrame = 0;
    }

    /**
     * Adiciona uma opção ao diálogo.
     * @param option Opção a ser adicionada
     */
    public void addOption(DialogOption option) {
        this.options.add(option);
    }

    /**
     * Adiciona uma opção simples ao diálogo.
     * @param text Texto da opção
     * @param nextDialogId ID do próximo diálogo
     */
    public void addOption(String text, int nextDialogId) {
        this.options.add(new DialogOption(text, nextDialogId));
    }

    /**
     * Adiciona uma opção com ação ao diálogo.
     * @param text Texto da opção
     * @param nextDialogId ID do próximo diálogo
     * @param actionType Tipo da ação
     * @param actionData Dados da ação
     */
    public void addOption(String text, int nextDialogId, String actionType, String actionData) {
        this.options.add(new DialogOption(text, nextDialogId, actionType, actionData));
    }

    /**
     * Adiciona uma opção de fim de diálogo.
     * @param text Texto da opção
     */
    public void addEndOption(String text) {
        DialogOption endOption = new DialogOption(text, -1);
        this.options.add(endOption);
    }

    /**
     * Marca este diálogo como fim de conversa.
     */
    public void setAsEndDialog() {
        this.isEndDialog = true;
    }

    /**
     * Verifica se o diálogo tem opções disponíveis.
     * @return true se tem opções disponíveis
     */
    public boolean hasAvailableOptions() {
        return options.stream().anyMatch(DialogOption::isAvailable);
    }

    /**
     * Obtém todas as opções disponíveis.
     * @return Lista de opções disponíveis
     */
    public List<DialogOption> getAvailableOptions() {
        List<DialogOption> availableOptions = new ArrayList<>();
        for (DialogOption option : options) {
            if (option.isAvailable()) {
                availableOptions.add(option);
            }
        }
        return availableOptions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<DialogOption> getOptions() {
        return options;
    }

    public void setOptions(List<DialogOption> options) {
        this.options = options;
    }

    public boolean isEndDialog() {
        return isEndDialog;
    }

    public void setEndDialog(boolean endDialog) {
        isEndDialog = endDialog;
    }

    public String getPortraitSprite() {
        return portraitSprite;
    }

    public void setPortraitSprite(String portraitSprite) {
        this.portraitSprite = portraitSprite;
    }

    public int getPortraitFrame() {
        return portraitFrame;
    }

    public void setPortraitFrame(int portraitFrame) {
        this.portraitFrame = portraitFrame;
    }
}
