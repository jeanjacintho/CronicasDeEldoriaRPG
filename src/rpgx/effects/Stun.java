package rpgx.effects;

import rpgx.core.Actor;
import rpgx.events.EventBus;

public class Stun implements StatusEffect {
    @Override public String name() { return "STUN"; }
    @Override public void onApply(Actor target, EventBus bus) { }
    @Override public void onTurnStart(Actor target, EventBus bus) { }
    @Override public void onTurnEnd(Actor target, EventBus bus) { }
    @Override public boolean blocksAction() { return true; }
}
