package br.com.cronicasdeeldoria.entity.character.npc;

/**
 * Representa um NPC guerreiro, que pode ser contratado ou dispensado.
 */
public class WarriorNpc extends Npc {
    private boolean hired;

    /**
     * Cria um novo WarriorNpc.
     * @param name Nome do NPC.
     * @param isStatic Indica se o NPC é estático.
     * @param dialog Diálogo do NPC.
     * @param x Posição X.
     * @param y Posição Y.
     * @param skin Skin do NPC.
     * @param playerSize Tamanho do jogador (para hitbox).
     */
    public WarriorNpc(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize) {
        super(name, isStatic, dialog, x, y, skin, playerSize);
        this.hired = false;
    }

    public boolean isHired() { return hired; }
    public void hire() { this.hired = true; }
    public void fire() { this.hired = false; }
}
