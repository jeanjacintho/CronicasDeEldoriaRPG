package rpgx.commands;

import rpgx.battle.Battle;
import rpgx.core.Actor;
import rpgx.skills.Element;
import rpgx.events.Events;

public class AttackAction implements CombatAction {
    private final Actor src;
    private final Actor tgt;

    public AttackAction(Actor src, Actor tgt) {
        this.src = src; this.tgt = tgt;
    }

    @Override
    public void execute(Battle b) {
        if (!src.canReach(tgt, false)) {
            b.getRenderer().println("Alvo fora de alcance (linha de trás protegida).");
            return;
        }
        int raw = b.getDamageCalc().computeRawDamage(src, b.getBasicAttack());
        boolean crit = b.rng().nextDouble() < b.getDamageCalc().critChance(src);
        if (crit) raw = (int)Math.round(raw * 1.5);
        int dmg = b.getDamageCalc().applyMitigation(tgt, Element.PHYSICAL, raw, false, tgt.hasGuard());
        tgt.takeDamage(dmg);
        b.getBus().publish(Events.dmg(src, tgt, dmg, (crit?"CRIT ":"") + "Ataque Básico"));
        if (!tgt.isAlive()) b.getBus().publish(Events.death(tgt));
    }

    @Override
    public String describe() { return src.getName() + " -> ataque básico em " + tgt.getName(); }
}
