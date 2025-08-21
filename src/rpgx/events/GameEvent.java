package rpgx.events;

import rpgx.core.Actor;

public class GameEvent {
    public enum Type { TURN_START, TURN_END, DAMAGE, HEAL, DEATH, STATUS_APPLIED }

    public final Type type;
    public final Actor actor;
    public final Actor target;
    public final int amount;
    public final String detail;

    public GameEvent(Type type, Actor actor, Actor target, int amount, String detail) {
        this.type = type;
        this.actor = actor;
        this.target = target;
        this.amount = amount;
        this.detail = detail;
    }
}
