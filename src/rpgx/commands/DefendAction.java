package rpgx.commands;

import rpgx.battle.Battle;
import rpgx.core.Actor;
import rpgx.effects.ActiveStatus;
import rpgx.effects.Guard;
import rpgx.events.Events;

public class DefendAction implements CombatAction {
    private final Actor src;
    public DefendAction(Actor src) { this.src = src; }
    @Override public void execute(Battle b) {
        src.applyStatus(new ActiveStatus(new Guard(), 1), b.getBus());
        src.getStats().gainSP(3);
        b.getBus().publish(Events.status(src, src, "GUARD", 1));
    }
    @Override public String describe() { return src.getName() + " assume GUARDA"; }
}
