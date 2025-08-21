package rpgx.world;

import rpgx.battle.Battle;
import rpgx.core.Actor;

public interface AIBehavior {
    void decideAndEnqueue(Actor self, Battle ctx);
}
