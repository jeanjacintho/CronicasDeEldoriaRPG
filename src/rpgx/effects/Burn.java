package rpgx.effects;

import rpgx.core.Actor;
import rpgx.events.EventBus;
import rpgx.events.Events;

public class Burn implements StatusEffect {
    @Override public String name() { return "BURN"; }
    @Override public void onApply(Actor target, EventBus bus) { }
    @Override public void onTurnStart(Actor target, EventBus bus) {
        int dot = Math.max(1, (int)Math.round(target.getStats().maxHP * 0.04));
        target.trueDamage(dot);
        bus.publish(Events.dmg(target, target, dot, "Queimadura"));
        if (!target.isAlive()) bus.publish(Events.death(target));
    }
    @Override public void onTurnEnd(Actor target, EventBus bus) { }
    @Override public boolean blocksAction() { return false; }
}
