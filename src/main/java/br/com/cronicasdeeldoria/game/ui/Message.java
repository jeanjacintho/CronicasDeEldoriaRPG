package br.com.cronicasdeeldoria.game.ui;

import java.awt.Image;

/**
 * Representa uma mensagem exibida na interface do jogo.
 */
public class Message {
    private final String text;
    private final Image image;
    private final long expirationTime;

    /**
     * Cria uma nova mensagem.
     * @param text Texto da mensagem.
     * @param image Imagem opcional.
     * @param durationMillis Duração em milissegundos.
     */
    public Message(String text, Image image, long durationMillis) {
        this.text = text;
        this.image = image;
        this.expirationTime = System.currentTimeMillis() + durationMillis;
    }

    /**
     * Retorna o texto da mensagem.
     * @return Texto da mensagem.
     */
    public String getText() { return text; }
    /**
     * Retorna a imagem associada à mensagem.
     * @return Imagem da mensagem.
     */
    public Image getImage() { return image; }
    /**
     * Verifica se a mensagem expirou.
     * @return true se expirada, false caso contrário.
     */
    public boolean isExpired() { return System.currentTimeMillis() > expirationTime; }
}
