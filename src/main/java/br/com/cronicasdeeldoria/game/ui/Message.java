package br.com.cronicasdeeldoria.game.ui;

import java.awt.Image;

public class Message {
    private final String text;
    private final Image image;
    private final long expirationTime;

    public Message(String text, Image image, long durationMillis) {
        this.text = text;
        this.image = image;
        this.expirationTime = System.currentTimeMillis() + durationMillis;
    }

    public String getText() { return text; }
    public Image getImage() { return image; }
    public boolean isExpired() { return System.currentTimeMillis() > expirationTime; }
}
