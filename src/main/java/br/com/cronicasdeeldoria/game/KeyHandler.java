package br.com.cronicasdeeldoria.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Gerencia os eventos de teclado do jogador.
 */
public class KeyHandler implements KeyListener {
    public boolean actionPressed, upPressed, downPressed, leftPressed, rightPressed, xPressed, zPressed, qPressed;
    public boolean rPressed, fPressed, gPressed, hPressed, tPressed, specialPressed, attackPressed, defendPressed, escapePressed;
    public boolean healthPressed, manaPressed, waterOrbPressed, fireOrbPressed;
    public boolean inventoryPressed, tabPressed, escapeKeyPressed, debugPressed, jPressed, lPressed;
    public boolean upArrowPressed, downArrowPressed; // Teclas de seta para scroll

    // Sistema anti-repeat para teclas importantes
    private boolean actionKeyDown = false;
    private boolean leftKeyDown = false;
    private boolean rightKeyDown = false;

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
            case KeyEvent.VK_A:
                if (!leftKeyDown) {
                    leftPressed = true;
                    leftKeyDown = true;
                }
                break;
            case KeyEvent.VK_D:
                if (!rightKeyDown) {
                    rightPressed = true;
                    rightKeyDown = true;
                }
                break;
            case KeyEvent.VK_E:
                if (!actionKeyDown) {
                    actionPressed = true;
                    actionKeyDown = true;
                }
                break;
            case KeyEvent.VK_X: xPressed = true; break;
            case KeyEvent.VK_Z: zPressed = true; break;
            case KeyEvent.VK_Q: qPressed = true; break;
            case KeyEvent.VK_R: rPressed = true; break;
            case KeyEvent.VK_F: fPressed = true; break;
            case KeyEvent.VK_G: gPressed = true; break;
            case KeyEvent.VK_H: hPressed = true; break;
            //case KeyEvent.VK_T: tPressed = true; break;
            case KeyEvent.VK_I: inventoryPressed = true; break;
            case KeyEvent.VK_TAB: tabPressed = true; break;
            case KeyEvent.VK_ESCAPE: escapeKeyPressed = true; break;
            case KeyEvent.VK_P: debugPressed = true; break;
            case KeyEvent.VK_J: jPressed = true; break;
            case KeyEvent.VK_L: lPressed = true; break;
            case KeyEvent.VK_UP: upArrowPressed = true; break;
            case KeyEvent.VK_DOWN: downArrowPressed = true; break;

            // Key de batalha
            case KeyEvent.VK_1: specialPressed = true; break;
            case KeyEvent.VK_2: attackPressed = true; break;
            case KeyEvent.VK_3: defendPressed = true; break;
            case KeyEvent.VK_4: escapePressed = true; break;
            case KeyEvent.VK_6: healthPressed = true; break;
            case KeyEvent.VK_7: manaPressed = true; break;

            // Efeitos das orbes
            case KeyEvent.VK_0: waterOrbPressed = true; break;
            case KeyEvent.VK_9: fireOrbPressed = true; break;

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
            case KeyEvent.VK_A:
                leftPressed = false;
                leftKeyDown = false;
                break;
            case KeyEvent.VK_D:
                rightPressed = false;
                rightKeyDown = false;
                break;
            case KeyEvent.VK_E:
                actionPressed = false;
                actionKeyDown = false;
                break;
            case KeyEvent.VK_X: xPressed = false; break;
            case KeyEvent.VK_Z: zPressed = false; break;
            case KeyEvent.VK_Q: qPressed = false; break;
            case KeyEvent.VK_R: rPressed = false; break;
            case KeyEvent.VK_F: fPressed = false; break;
            case KeyEvent.VK_G: gPressed = false; break;
            case KeyEvent.VK_H: hPressed = false; break;
            //case KeyEvent.VK_T: tPressed = false; break;
            case KeyEvent.VK_I: inventoryPressed = false; break;
            case KeyEvent.VK_TAB: tabPressed = false; break;
            case KeyEvent.VK_ESCAPE: escapeKeyPressed = false; break;
            case KeyEvent.VK_P: debugPressed = false; break;
            case KeyEvent.VK_J: jPressed = false; break;
            case KeyEvent.VK_L: lPressed = false; break;
            case KeyEvent.VK_UP: upArrowPressed = false; break;
            case KeyEvent.VK_DOWN: downArrowPressed = false; break;


          // Batalha
          case KeyEvent.VK_1: specialPressed = false; break;
          case KeyEvent.VK_2: attackPressed = false; break;
          case KeyEvent.VK_3: defendPressed = false; break;
          case KeyEvent.VK_4: escapePressed = false; break;
          case KeyEvent.VK_6: healthPressed = false; break;
          case KeyEvent.VK_7: manaPressed = false; break;

          // Efeitos das orbes
          case KeyEvent.VK_0: waterOrbPressed = false; break;
          case KeyEvent.VK_9: fireOrbPressed = false; break;
        }
    }
}
