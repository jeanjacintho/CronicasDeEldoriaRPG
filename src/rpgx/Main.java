package rpgx;

import rpgx.world.GameFactory;
import rpgx.battle.Battle;
import rpgx.core.Actor;
import rpgx.core.Team;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        GameFactory factory = new GameFactory();
        List<Actor> heroes = factory.createDefaultHeroes();
        List<Actor> enemies = factory.createEncounter1();

        Battle battle = new Battle(heroes, enemies);
        battle.run();

        // Encontro 2 (opcional para testar continuidade)
        if (heroes.stream().anyMatch(Actor::isAlive)) {
            System.out.println("\n=== NOVO ENCONTRO SURGE! ===");
            enemies = factory.createEncounter2();
            battle = new Battle(heroes, enemies);
            battle.run();
        }

        System.out.println("\n=== FIM ===");
    }
}
