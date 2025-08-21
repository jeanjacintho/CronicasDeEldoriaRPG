package rpgx.effects;

import rpgx.core.Actor;
import rpgx.events.EventBus;

public interface StatusEffect {
    String name();
    void onApply(Actor target, EventBus bus);
    void onTurnStart(Actor target, EventBus bus);
    void onTurnEnd(Actor target, EventBus bus);
    boolean blocksAction(); // ex. Freeze, Stun
}
