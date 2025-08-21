package rpgx.battle;

import rpgx.core.Actor;

import java.util.List;

public interface TurnOrderStrategy {
    List<Actor> order(List<Actor> actors);
}
