package rpgx.core;

import rpgx.items.Consumable;
import rpgx.items.Item;
import rpgx.ui.Input;

import java.util.ArrayList;
import java.util.List;

public class PlayerActor extends Actor {
    private List<Actor> alliesRef = new ArrayList<>();
    private List<Actor> enemiesRef = new ArrayList<>();

    public PlayerActor(String clazz, String name, Stats stats) {
        super(clazz, name, Team.HERO, stats);
    }

    public void attachRefs(List<Actor> allies, List<Actor> enemies) {
        alliesRef = allies; enemiesRef = enemies;
    }

    @Override protected List<Actor> teamMatesAll() {
        List<Actor> all = new ArrayList<>();
        all.addAll(alliesRef); all.addAll(enemiesRef);
        return all;
    }

    @Override public boolean isPlayerControlled() { return true; }

    public Consumable chooseConsumable(Input in) {
        List<Consumable> cons = new ArrayList<>();
        for (Item it : inventory) if (it instanceof Consumable c) cons.add(c);
        if (cons.isEmpty()) return null;
        for (int i=0;i<cons.size();i++) System.out.println("["+(i+1)+"] "+cons.get(i));
        int idx = in.readInt(1, cons.size())-1;
        return cons.get(idx);
    }
}
