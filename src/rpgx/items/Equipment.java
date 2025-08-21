package rpgx.items;

import rpgx.core.Stats;
import rpgx.skills.Skill;

import java.util.ArrayList;
import java.util.List;

public class Equipment extends Item {
    private final Slot slot;
    private final int bHP, bMP, bSP, bStr, bInt, bDefPct, bArmor, bSpeed;
    private final double bCrit;
    private final List<Skill> extraSkills = new ArrayList<>();

    public Equipment(String name, Rarity rarity, Slot slot, int bHP, int bMP, int bSP, int bStr, int bInt, int bDefPct, int bArmor, int bSpeed, double bCrit) {
        super(name, rarity);
        this.slot = slot;
        this.bHP = bHP; this.bMP = bMP; this.bSP = bSP;
        this.bStr = bStr; this.bInt = bInt; this.bDefPct = bDefPct; this.bArmor = bArmor; this.bSpeed = bSpeed;
        this.bCrit = bCrit;
    }

    public Slot getSlot() { return slot; }
    public void addExtraSkill(Skill s) { extraSkills.add(s); }
    public List<Skill> getExtraSkills() { return extraSkills; }

    public void apply(Stats s) {
        s.maxHP += bHP; s.hp += bHP;
        s.maxMP += bMP; s.mp += bMP;
        s.maxSP += bSP; s.sp += bSP;
        s.strength += bStr;
        s.intellect += bInt;
        s.defensePercent += bDefPct;
        s.armor += bArmor;
        s.speed += bSpeed;
        s.critChance += bCrit;
    }

    public void remove(Stats s) {
        s.maxHP -= bHP; s.hp = Math.min(s.hp, s.maxHP);
        s.maxMP -= bMP; s.mp = Math.min(s.mp, s.maxMP);
        s.maxSP -= bSP; s.sp = Math.min(s.sp, s.maxSP);
        s.strength -= bStr;
        s.intellect -= bInt;
        s.defensePercent -= bDefPct;
        s.armor -= bArmor;
        s.speed -= bSpeed;
        s.critChance -= bCrit;
    }

    @Override
    public String toString() {
        return super.toString() + " [" + slot + " +HP:" + bHP + " +Str:" + bStr + " +Def%:" + bDefPct + " +Arm:" + bArmor + "]";
    }
}
