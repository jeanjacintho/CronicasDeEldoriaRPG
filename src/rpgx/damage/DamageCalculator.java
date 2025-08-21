package rpgx.damage;

import rpgx.core.Actor;
import rpgx.skills.Element;
import rpgx.skills.Skill;

public interface DamageCalculator {
    int computeRawDamage(Actor src, Skill skill);
    int applyMitigation(Actor target, Element element, int raw, boolean pierceArmor, boolean guardActive);
    double critChance(Actor src);
}
