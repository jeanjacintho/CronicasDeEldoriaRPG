package rpgx.items;

public abstract class Item {
    protected final String name;
    protected final Rarity rarity;

    protected Item(String name, Rarity rarity) {
        this.name = name;
        this.rarity = rarity;
    }

    public String getName() { return name; }
    public Rarity getRarity() { return rarity; }

    @Override public String toString() { return name + " {" + rarity + "}"; }
}
