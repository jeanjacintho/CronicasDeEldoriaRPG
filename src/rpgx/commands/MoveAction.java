package rpgx.commands;

import rpgx.battle.Battle;
import rpgx.core.Actor;
import rpgx.core.Position;

public class MoveAction implements CombatAction {
    private final Actor src;
    private final Position dest;

    public MoveAction(Actor src, Position dest) { this.src = src; this.dest = dest; }

    @Override
    public void execute(Battle b) {
        if (src.hasRoot()) { b.getRenderer().println(src.getName()+" está enraizado!"); return; }
        if (src.getStats().sp < 2) { b.getRenderer().println("Stamina insuficiente para mover."); return; }
        src.getStats().spendSP(2);
        src.setPosition(dest);
        b.getRenderer().println(src.getName() + " move para " + dest);
    }

    @Override public String describe() { return src.getName() + " move -> " + dest; }
}
