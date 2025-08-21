package rpgx.events;

import rpgx.core.Actor;

public class Events {
    public static GameEvent dmg(Actor src, Actor tgt, int amount, String detail) {
        return new GameEvent(GameEvent.Type.DAMAGE, src, tgt, amount, detail);
    }
    public static GameEvent heal(Actor src, Actor tgt, int amount, String detail) {
        return new GameEvent(GameEvent.Type.HEAL, src, tgt, amount, detail);
    }
    public static GameEvent death(Actor tgt) {
        return new GameEvent(GameEvent.Type.DEATH, tgt, tgt, 0, tgt.getName() + " caiu!");
    }
    public static GameEvent status(Actor src, Actor tgt, String statusName, int dur) {
        return new GameEvent(GameEvent.Type.STATUS_APPLIED, src, tgt, dur, statusName);
    }
    public static GameEvent tStart(Actor a) { return new GameEvent(GameEvent.Type.TURN_START, a, a, 0, ""); }
    public static GameEvent tEnd(Actor a) { return new GameEvent(GameEvent.Type.TURN_END, a, a, 0, ""); }
}
