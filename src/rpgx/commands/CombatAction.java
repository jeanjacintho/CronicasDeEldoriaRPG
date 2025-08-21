package rpgx.commands;

import rpgx.battle.Battle;

public interface CombatAction {
    void execute(Battle ctx);
    String describe();
}
