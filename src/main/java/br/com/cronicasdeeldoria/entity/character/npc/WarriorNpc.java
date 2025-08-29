package br.com.cronicasdeeldoria.entity.character.npc;

public class WarriorNpc extends Npc {
    private boolean hired;

    public WarriorNpc(String name, boolean isStatic, String dialog, int x, int y, String skin, int playerSize) {
        super(name, isStatic, dialog, x, y, skin, playerSize);
        this.hired = false;
    }

    public boolean isHired() { return hired; }
    public void hire() { this.hired = true; }
    public void fire() { this.hired = false; }
}
