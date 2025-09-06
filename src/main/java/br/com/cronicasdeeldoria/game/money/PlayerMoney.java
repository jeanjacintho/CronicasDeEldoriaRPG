package br.com.cronicasdeeldoria.game.money;

/**
 * Classe responsável por gerenciar o dinheiro do jogador.
 */
public class PlayerMoney {
    private int currentMoney;
    
    public PlayerMoney() {
        this.currentMoney = 0; // Dinheiro inicial
    }
    
    public PlayerMoney(int initialMoney) {
        this.currentMoney = Math.max(0, initialMoney);
    }
    
    /**
     * Adiciona dinheiro ao jogador.
     * @param amount Quantidade a ser adicionada.
     */
    public void addMoney(int amount) {
        if (amount > 0) {
            this.currentMoney += amount;
        }
    }
    
    /**
     * Remove dinheiro do jogador.
     * @param amount Quantidade a ser removida.
     * @return true se a operação foi bem-sucedida, false se não há dinheiro suficiente.
     */
    public boolean removeMoney(int amount) {
        if (amount <= 0) {
            return true;
        }
        
        if (hasEnoughMoney(amount)) {
            this.currentMoney -= amount;
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica se o jogador tem dinheiro suficiente.
     * @param amount Quantidade necessária.
     * @return true se tem dinheiro suficiente.
     */
    public boolean hasEnoughMoney(int amount) {
        return this.currentMoney >= amount;
    }
    
    /**
     * Retorna a quantidade atual de dinheiro.
     * @return Quantidade de dinheiro atual.
     */
    public int getCurrentMoney() {
        return currentMoney;
    }
    
    /**
     * Define a quantidade de dinheiro.
     * @param amount Nova quantidade de dinheiro.
     */
    public void setMoney(int amount) {
        this.currentMoney = Math.max(0, amount);
    }
    
    /**
     * Retorna o dinheiro formatado para exibição.
     * @return String formatada com o dinheiro.
     */
    public String getFormattedMoney() {
        return String.format("%,d", currentMoney) + " moedas";
    }
    
    /**
     * Retorna apenas o número do dinheiro para exibição simples.
     * @return String com apenas o número.
     */
    public String getMoneyDisplay() {
        return String.valueOf(currentMoney);
    }
}


