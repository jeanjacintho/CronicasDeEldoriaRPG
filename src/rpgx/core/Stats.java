package rpgx.core;

public class Stats {
    public int maxHP, hp;
    public int maxMP, mp;
    public int maxSP, sp;
    public int strength, intellect;
    public int defensePercent; // % redução
    public int armor;          // plano, desgasta
    public int speed;
    public double critChance;  // 0..1

    public Stats(int hp, int mp, int sp, int strength, int intellect, int defPct, int armor, int speed, double crit) {
        this.maxHP = this.hp = hp;
        this.maxMP = this.mp = mp;
        this.maxSP = this.sp = sp;
        this.strength = strength;
        this.intellect = intellect;
        this.defensePercent = defPct;
        this.armor = armor;
        this.speed = speed;
        this.critChance = crit;
    }

    public Stats copy() {
        Stats s = new Stats(maxHP, maxMP, maxSP, strength, intellect, defensePercent, armor, speed, critChance);
        s.hp = hp; s.mp = mp; s.sp = sp;
        return s;
    }

    public void heal(int v) { hp = Math.min(maxHP, hp + Math.max(0, v)); }
    public void gainMP(int v) { mp = Math.min(maxMP, mp + Math.max(0, v)); }
    public void gainSP(int v) { sp = Math.min(maxSP, sp + Math.max(0, v)); }
    public void spendMP(int v) { mp = Math.max(0, mp - Math.max(0, v)); }
    public void spendSP(int v) { sp = Math.max(0, sp - Math.max(0, v)); }
}
