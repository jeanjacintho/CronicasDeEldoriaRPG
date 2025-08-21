package rpgx.commands;

import rpgx.battle.Battle;
import rpgx.core.Actor;

public class FleeAction implements CombatAction {
    private final Actor src;
    public FleeAction(Actor src) { this.src = src; }

    @Override
    public void execute(Battle b) {
        double vHero = b.avgSpeed(b.getHeroes());
        double vEnemy = b.avgSpeed(b.getEnemies());
        double chance = Math.min(0.9, Math.max(0.1, (vHero/(vEnemy+0.1))*0.5));
        boolean ok = b.rng().nextDouble() < chance;
        b.getRenderer().println(src.getName() + " tenta fugir... " + (int)(chance*100) + "% -> " + (ok?"SUCESSO":"FALHA"));
        if (ok) b.getEnemies().clear();
    }

    @Override public String describe() { return src.getName() + " tenta fugir"; }
}
