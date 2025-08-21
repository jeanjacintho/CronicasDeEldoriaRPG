package rpgx.commands;

import rpgx.battle.Battle;
import rpgx.core.Actor;
import rpgx.items.Consumable;

public class UseItemAction implements CombatAction {
    private final Actor src;
    private final Consumable item;

    public UseItemAction(Actor src, Consumable item) { this.src = src; this.item = item; }

    @Override
    public void execute(Battle b) {
        src.getStats().heal(item.getHeal());
        src.getStats().gainMP(item.getMana());
        src.getStats().gainSP(item.getStamina());
        b.getRenderer().println(src.getName() + " usa " + item + " e se recupera.");
        src.consume(item); // remove da mochila
    }

    @Override public String describe() { return src.getName() + " consome " + item.getName(); }
}
