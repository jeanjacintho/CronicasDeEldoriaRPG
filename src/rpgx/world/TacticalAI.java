package rpgx.world;

import rpgx.battle.*;
import rpgx.core.*;

import java.util.List;
import java.util.Random;

public class TacticalAI implements AIBehavior {
    private final Random rng = new Random();

    @Override
    public void decideAndEnqueue(Enemy self, Battle b) {
        // Inimigo tático escolhe aleatoriamente entre atacar ou defender
        List<Actor> enemies = b.opponentsOf(self);
        if (enemies.isEmpty()) return;

        if (rng.nextDouble() < 0.7) { // 70% de chance de atacar
            Actor target = enemies.get(rng.nextInt(enemies.size()));
            b.enqueue(new AttackAction(self, target));
        } else { // 30% de chance de defender
            b.enqueue(new DefendAction(self));
        }
    }
}
