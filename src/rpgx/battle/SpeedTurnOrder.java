package rpgx.battle;

import rpgx.core.Actor;

import java.util.*;

public class SpeedTurnOrder implements TurnOrderStrategy {
    private final Random rng = new Random();
    @Override
    public List<Actor> order(List<Actor> actors) {
        List<Actor> copy = new ArrayList<>(actors);
        copy.sort((a,b)->{
            int c = Integer.compare(b.getStats().speed, a.getStats().speed);
            if (c==0) return rng.nextBoolean()?1:-1;
            return c;
        });
        return copy;
    }
}
