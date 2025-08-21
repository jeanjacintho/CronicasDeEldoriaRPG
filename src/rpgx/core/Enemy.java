package rpgx.core;

import rpgx.world.AIBehavior;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Actor {
    private AIBehavior ai;
    private List<Actor> allRefs = new ArrayList<>();

    public Enemy(String clazz, String name, Stats stats, AIBehavior ai) {
        super(clazz, name, Team.ENEMY, stats);
        this.ai = ai;
    }

    public void attachRefs(List<Actor> everyone) { this.allRefs = everyone; }

    @Override protected List<Actor> teamMatesAll() { return allRefs; }
    @Override public boolean isPlayerControlled() { return false; }
    public AIBehavior getAi() { return ai; }
}
