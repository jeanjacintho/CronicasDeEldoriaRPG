package rpgx.skills;

import rpgx.effects.StatusEffect;

public class SkillBuilder {
    private String name;
    private Element element = Element.PHYSICAL;
    private int basePower = 0;
    private double scStr = 1.0, scInt = 0.0;
    private int mp = 0, sp = 0;
    private Skill.TargetType targetType = Skill.TargetType.SINGLE;
    private boolean ignoreLine = false, pierceArmor = false;
    private StatusEffect status = null;
    private int statusDur = 0;

    public static SkillBuilder of(String name) { SkillBuilder b = new SkillBuilder(); b.name = name; return b; }
    public SkillBuilder element(Element e) { this.element = e; return this; }
    public SkillBuilder power(int p) { this.basePower = p; return this; }
    public SkillBuilder scale(double sStr, double sInt) { this.scStr = sStr; this.scInt = sInt; return this; }
    public SkillBuilder cost(int mp, int sp) { this.mp = mp; this.sp = sp; return this; }
    public SkillBuilder target(Skill.TargetType t) { this.targetType = t; return this; }
    public SkillBuilder ignoreLine() { this.ignoreLine = true; return this; }
    public SkillBuilder pierceArmor() { this.pierceArmor = true; return this; }
    public SkillBuilder status(StatusEffect st, int dur) { this.status = st; this.statusDur = dur; return this; }

    public Skill build() {
        return new Skill(name, element, basePower, scStr, scInt, mp, sp, targetType, ignoreLine, pierceArmor, status, statusDur);
    }
}
