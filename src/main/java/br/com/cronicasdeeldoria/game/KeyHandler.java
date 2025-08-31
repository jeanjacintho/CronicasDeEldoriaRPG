package br.com.cronicasdeeldoria.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Gerencia os eventos de teclado do jogador.
 */
public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean actionPressed;
    public boolean xPressed, zPressed;

    /**
     * Evento chamado quando uma tecla é digitada.
     * @param e Evento de tecla.
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Evento chamado quando uma tecla é pressionada.
     * @param e Evento de tecla.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W:
                upPressed = true;
                break;
            case KeyEvent.VK_S:
                downPressed = true;
                break;
            case KeyEvent.VK_A:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
                rightPressed = true;
                break;
            case KeyEvent.VK_E:
                actionPressed = true;
                break;
            case KeyEvent.VK_X:
                xPressed = true;
                break;
            case KeyEvent.VK_Z:
                zPressed = true;
                break;
        }
    }

    /**
     * Evento chamado quando uma tecla é liberada.
     * @param e Evento de tecla.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W:
                upPressed = false;
                break;
            case KeyEvent.VK_S:
                downPressed = false;
                break;
            case KeyEvent.VK_A:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
                rightPressed = false;
                break;
            case KeyEvent.VK_E:
                actionPressed = false;
                break;
            case KeyEvent.VK_X:
                xPressed = false;
                break;
            case KeyEvent.VK_Z:
                zPressed = false;
                break;
        }
    }
}
