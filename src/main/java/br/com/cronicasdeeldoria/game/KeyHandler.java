package br.com.cronicasdeeldoria.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Gerencia os eventos de teclado do jogador.
 */
public class KeyHandler implements KeyListener {
    public boolean actionPressed, upPressed, downPressed, leftPressed, rightPressed, xPressed, zPressed, qPressed;
    public boolean rPressed, fPressed, gPressed, hPressed, tPressed, magicPressed, attackPressed, defendPressed, escapePressed;

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
            case KeyEvent.VK_W: upPressed = true; break;
            case KeyEvent.VK_S: downPressed = true; break;
            case KeyEvent.VK_A: leftPressed = true; break;
            case KeyEvent.VK_D: rightPressed = true; break;
            case KeyEvent.VK_E: actionPressed = true; break;
            case KeyEvent.VK_X: xPressed = true; break;
            case KeyEvent.VK_Z: zPressed = true; break;
            case KeyEvent.VK_Q: qPressed = true; break;
            case KeyEvent.VK_R: rPressed = true; break;
            case KeyEvent.VK_F: fPressed = true; break;
            case KeyEvent.VK_G: gPressed = true; break;
            case KeyEvent.VK_H: hPressed = true; break;
            //case KeyEvent.VK_T: tPressed = true; break;

            // Key de batalha
            case KeyEvent.VK_4: magicPressed = true; break;
            case KeyEvent.VK_3: attackPressed = true; break;
            case KeyEvent.VK_2: defendPressed = true; break;
            case KeyEvent.VK_1: escapePressed = true; break;

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
            case KeyEvent.VK_W: upPressed = false; break;
            case KeyEvent.VK_S: downPressed = false; break;
            case KeyEvent.VK_A: leftPressed = false; break;
            case KeyEvent.VK_D: rightPressed = false; break;
            case KeyEvent.VK_E: actionPressed = false; break;
            case KeyEvent.VK_X: xPressed = false; break;
            case KeyEvent.VK_Z: zPressed = false; break;
            case KeyEvent.VK_Q: qPressed = false; break;
            case KeyEvent.VK_R: rPressed = false; break;
            case KeyEvent.VK_F: fPressed = false; break;
            case KeyEvent.VK_G: gPressed = false; break;
            case KeyEvent.VK_H: hPressed = false; break;
            //case KeyEvent.VK_T: tPressed = false; break;

          case KeyEvent.VK_4: magicPressed = false; break;
          case KeyEvent.VK_3: attackPressed = false; break;
          case KeyEvent.VK_2: defendPressed = false; break;
          case KeyEvent.VK_1: escapePressed = false; break;
        }
    }
}
