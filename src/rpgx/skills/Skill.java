package rpgx.skills;

import rpgx.effects.StatusEffect;

public class Skill {
    public enum TargetType { SINGLE, ALL_ENEMIES, ALL_ALLIES }

    private final String name;
    private final Element element;
    private final int basePower;
    private final double scaleStr;
    private final double scaleInt;
    private final int mpCost, spCost;
    private final TargetType targetType;
    private final boolean ignoreLine;
    private final boolean pierceArmor;
    private final StatusEffect status;
    private final int statusDuration;

    public Skill(String name, Element element, int basePower, double scaleStr, double scaleInt, int mpCost, int spCost,
                 TargetType targetType, boolean ignoreLine, boolean pierceArmor, StatusEffect status, int statusDuration) {
        this.name = name; this.element = element; this.basePower = basePower;
        this.scaleStr = scaleStr; this.scaleInt = scaleInt;
        this.mpCost = mpCost; this.spCost = spCost;
        this.targetType = targetType; this.ignoreLine = ignoreLine; this.pierceArmor = pierceArmor;
        this.status = status; this.statusDuration = statusDuration;
    }

    public String getName() { return name; }
    public Element getElement() { return element; }
    public int getBasePower() { return basePower; }
    public double getScaleStr() { return scaleStr; }
    public double getScaleInt() { return scaleInt; }
    public int getMpCost() { return mpCost; }
    public int getSpCost() { return spCost; }
    public TargetType getTargetType() { return targetType; }
    public boolean isIgnoreLine() { return ignoreLine; }
    public boolean isPierceArmor() { return pierceArmor; }
    public StatusEffect getStatus() { return status; }
    public int getStatusDuration() { return statusDuration; }

    @Override public String toString() {
        return name + " [" + element + " base=" + basePower + " MP=" + mpCost + " SP=" + spCost + " " + targetType + "]";
    }
}
