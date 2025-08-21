package rpgx.items;

public class Consumable extends Item {
    private final int heal, mana, stamina;

    public Consumable(String name, Rarity rarity, int heal, int mana, int stamina) {
        super(name, rarity);
        this.heal = heal; this.mana = mana; this.stamina = stamina;
    }

    public int getHeal() { return heal; }
    public int getMana() { return mana; }
    public int getStamina() { return stamina; }

    @Override public String toString() {
        return super.toString() + " (+HP:" + heal + " +MP:" + mana + " +SP:" + stamina + ")";
    }
}
