package rpgx.commands;

import rpgx.battle.Battle;
import rpgx.core.Actor;
import rpgx.effects.ActiveStatus;
import rpgx.events.Events;
import rpgx.skills.Skill;

import java.util.List;

public class SkillAction implements CombatAction {
    private final Actor src;
    private final Skill skill;
    private final List<Actor> targets;

    public SkillAction(Actor src, Skill skill, List<Actor> targets) {
        this.src = src; this.skill = skill; this.targets = targets;
    }

    @Override
    public void execute(Battle b) {
        if (src.getStats().mp < skill.getMpCost() || src.getStats().sp < skill.getSpCost()) {
            b.getRenderer().println("Custos insuficientes para " + skill.getName());
            return;
        }
        src.getStats().spendMP(skill.getMpCost());
        src.getStats().spendSP(skill.getSpCost());

        for (Actor tgt : targets) {
            if (!tgt.isAlive()) continue;
            if (!src.canReach(tgt, skill.isIgnoreLine())) continue;

            int raw = b.getDamageCalc().computeRawDamage(src, skill);
            boolean crit = b.rng().nextDouble() < b.getDamageCalc().critChance(src);
            if (crit) raw = (int)Math.round(raw * 1.5);

            int dmg = b.getDamageCalc().applyMitigation(tgt, skill.getElement(), raw, skill.isPierceArmor(), tgt.hasGuard());
            tgt.takeDamage(dmg);
            b.getBus().publish(Events.dmg(src, tgt, dmg, (crit?"CRIT ":"") + skill.getName()));
            if (skill.getStatus() != null && tgt.isAlive()) {
                tgt.applyStatus(new ActiveStatus(skill.getStatus(), skill.getStatusDuration()), b.getBus());
                b.getBus().publish(Events.status(src, tgt, skill.getStatus().name(), skill.getStatusDuration()));
            }
            if (!tgt.isAlive()) b.getBus().publish(Events.death(tgt));
        }
    }

    @Override public String describe() { return src.getName() + " -> habilidade " + skill.getName(); }
}
