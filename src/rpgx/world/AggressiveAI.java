package rpgx.world;

import rpgx.battle.*;
import rpgx.core.*;

import java.util.List;
import java.util.Random;

public class AggressiveAI implements AIBehavior {
    private final Random rng = new Random();

    @Override
    public void decideAndEnqueue(Enemy self, Battle b) {
        // Inimigo agressivo sempre tenta atacar alguém do time oposto
        List<Actor> enemies = b.opponentsOf(self);
        if (enemies.isEmpty()) return;

        Actor target = enemies.get(rng.nextInt(enemies.size()));
        b.enqueue(new AttackAction(self, target));
    }
}
