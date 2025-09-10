package br.com.cronicasdeeldoria.entity.object;

import br.com.cronicasdeeldoria.entity.item.MagicOrb;
import br.com.cronicasdeeldoria.game.quest.QuestManager;
import br.com.cronicasdeeldoria.entity.character.player.Player;
import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.entity.Entity;

import java.util.*;

/**
 * Totem Central onde as orbes mágicas devem ser depositadas.
 * Objeto interativo especial da quest principal.
 */
public class TotemCentral extends MapObject {
    private int depositedOrbsCount;
    private boolean isActivated;
    private GamePanel gamePanel;
    
    /**
     * Construtor para criar o Totem Central.
     * @param worldX Posição X no mundo
     * @param worldY Posição Y no mundo
     */
    public TotemCentral(int worldX, int worldY) {
        super("totem_central", "Totem Central", worldX, worldY, 3, 3, 
              true, true, false, null, 0);
        
        this.depositedOrbsCount = 0;
        this.isActivated = false;
    }
    
    /**
     * Define a referência ao GamePanel.
     * @param gamePanel Referência ao GamePanel
     */
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    
    /**
     * Interage com o Totem Central.
     * Permite depositar orbes mágicas coletadas.
     * @param interactor Entidade que está interagindo
     */
    @Override
    public void interact(Entity interactor) {
        if (!(interactor instanceof Player) || gamePanel == null) return;
        
        QuestManager questManager = QuestManager.getInstance();
        List<MagicOrb> collectedOrbs = questManager.getCollectedOrbs();
        
        if (collectedOrbs.isEmpty()) {
            if (gamePanel.getGameUI() != null) {
                gamePanel.getGameUI().addMessage(
                    "Este é o Totem Central. Traga as 4 orbes mágicas aqui para " +
                    "despertar o Mago Supremo.", null, 5000L);
            }
            return;
        }
        
        // Depositar orbes coletadas
        boolean anyDeposited = false;
        for (MagicOrb orb : new ArrayList<>(collectedOrbs)) {
            if (!orb.isDeposited()) {
                depositOrb(orb);
                anyDeposited = true;
            }
        }
        
        if (anyDeposited && depositedOrbsCount == 4 && !isActivated) {
            activateTotem();
        }
    }
    
    /**
     * Deposita uma orbe no totem.
     * @param orb Orbe a ser depositada
     */
    private void depositOrb(MagicOrb orb) {
        orb.depositInTotem();
        depositedOrbsCount++;
        
        if (gamePanel != null && gamePanel.getGameUI() != null) {
            gamePanel.getGameUI().addMessage(
                "Orbe de " + orb.getOrbDisplayName() + " depositada! " +
                "(" + depositedOrbsCount + "/4)", null, 4000L);
        }
    }
    
    /**
     * Ativa o totem quando todas as orbes são depositadas.
     */
    private void activateTotem() {
        isActivated = true;
        
        if (gamePanel != null && gamePanel.getGameUI() != null) {
            gamePanel.getGameUI().addMessage(
                "O Totem Central está ativado! O Mago Supremo foi despertado!", 
                null, 6000L);
        }
        
        // O QuestManager já cuida do spawn do boss
    }
    
    /**
     * Verifica se o totem está ativado.
     * @return true se todas as orbes foram depositadas
     */
    public boolean isActivated() {
        return isActivated;
    }
    
    /**
     * Obtém o número de orbes depositadas.
     * @return Número de orbes depositadas
     */
    public int getDepositedOrbsCount() {
        return depositedOrbsCount;
    }
    
    /**
     * Obtém o progresso do totem em porcentagem.
     * @return Porcentagem de orbes depositadas (0-100)
     */
    public int getProgressPercentage() {
        return (depositedOrbsCount * 100) / 4;
    }
    
    /**
     * Verifica se todas as orbes foram depositadas.
     * @return true se todas as 4 orbes foram depositadas
     */
    public boolean isComplete() {
        return depositedOrbsCount >= 4;
    }
    
    /**
     * Obtém uma descrição do estado atual do totem.
     * @return Descrição do estado
     */
    public String getStatusDescription() {
        if (isActivated) {
            return "Totem Central ativado - Mago Supremo despertado!";
        } else if (depositedOrbsCount > 0) {
            return "Totem Central parcialmente ativado (" + depositedOrbsCount + "/4 orbes)";
        } else {
            return "Totem Central inativo - Traga as 4 orbes mágicas";
        }
    }
}
