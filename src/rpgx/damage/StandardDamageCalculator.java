package rpgx.damage;

import rpgx.core.Actor;
import rpgx.skills.Element;
import rpgx.skills.Skill;

import java.util.EnumSet;

public class StandardDamageCalculator implements DamageCalculator {

    @Override
    public int computeRawDamage(Actor src, Skill skill) {
        int base = skill.getBasePower();
        int str = src.getStats().strength;
        int intel = src.getStats().intellect;
        double scaled = base + (str * skill.getScaleStr()) + (intel * skill.getScaleInt());
        return Math.max(0, (int)Math.round(scaled));
    }

    @Override
    public double critChance(Actor src) {
        return src.getStats().critChance;
    }

    @Override
    public int applyMitigation(Actor target, Element element, int raw, boolean pierceArmor, boolean guard) {
        // elemental modifiers
        double m = 1.0;
        if (target.getImmunities().contains(element)) return 0;
        if (target.getWeaknesses().contains(element)) m *= 1.4;
        if (target.getResistances().contains(element)) m *= 0.75;
        if (guard) m *= 0.6;

        int v = (int)Math.round(raw * m);

        // defense% first
        v = (int)Math.round(v * (1 - (target.getStats().defensePercent / 100.0)));

        // armor (flat)
        if (!pierceArmor) {
            int mitigated = Math.min(target.getStats().armor, Math.max(0, v - 1));
            v -= mitigated;
            // wear armor
            target.getStats().armor = Math.max(0, target.getStats().armor - Math.max(1, mitigated / 3));
        }

        return Math.max(0, v);
    }
}
