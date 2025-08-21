package rpgx.core;

import rpgx.effects.*;
import rpgx.events.EventBus;
import rpgx.items.Consumable;
import rpgx.items.Equipment;
import rpgx.items.Item;
import rpgx.items.Slot;
import rpgx.skills.Element;
import rpgx.skills.Skill;

import java.util.*;

public abstract class Actor {
    protected final String clazz;
    protected final String name;
    protected final Team team;
    protected final Stats stats;
    protected Position position = Position.FRONT;

    protected final List<Skill> skills = new ArrayList<>();
    protected final List<Item> inventory = new ArrayList<>();
    protected final EnumMap<Slot, Equipment> equipped = new EnumMap<>(Slot.class);

    protected final List<ActiveStatus> statuses = new ArrayList<>();

    protected final EnumSet<Element> weaknesses = EnumSet.noneOf(Element.class);
    protected final EnumSet<Element> resistances = EnumSet.noneOf(Element.class);
    protected final EnumSet<Element> immunities = EnumSet.noneOf(Element.class);

    protected final Random rng = new Random();

    protected Actor(String clazz, String name, Team team, Stats stats) {
        this.clazz = clazz; this.name = name; this.team = team; this.stats = stats;
    }

    public String getClazz() { return clazz; }
    public String getName() { return name; }
    public Team getTeam() { return team; }
    public Stats getStats() { return stats; }
    public Position getPosition() { return position; }
    public void setPosition(Position p) { position = p; }

    public boolean isAlive() { return stats.hp > 0; }

    public List<Skill> getSkills() { return skills; }
    public List<Item> getInventory() { return inventory; }

    public EnumSet<Element> getWeaknesses() { return weaknesses; }
    public EnumSet<Element> getResistances() { return resistances; }
    public EnumSet<Element> getImmunities() { return immunities; }

    public void addSkill(Skill s) { if (!skills.contains(s)) skills.add(s); }
    public void addItem(Item i) { inventory.add(i); if (i instanceof Equipment eq) equip(eq); }
    public void consume(Consumable c) { inventory.remove(c); }

    public void equip(Equipment eq) {
        Equipment old = equipped.put(eq.getSlot(), eq);
        if (old != null) old.remove(stats);
        eq.apply(stats);
        for (var ex : eq.getExtraSkills()) addSkill(ex);
    }

    public boolean canReach(Actor target, boolean ignoreLine) {
        if (ignoreLine) return true;
        if (target.position == Position.FRONT) return true;
        // se alvo está atrás, só alcança se a frente inimiga estiver vazia
        return !frontAlive(target.getTeam());
    }

    protected boolean frontAlive(Team t) { return teamMatesAll().stream().anyMatch(a -> a.getTeam()==t && a.isAlive() && a.getPosition()==Position.FRONT); }
    protected abstract List<Actor> teamMatesAll();

    public void applyStatus(ActiveStatus st, EventBus bus) {
        statuses.add(st);
        st.effect.onApply(this, bus);
    }

    public boolean hasGuard() { return statuses.stream().anyMatch(s -> s.effect instanceof Guard && s.remaining>0); }
    public boolean hasRoot() { return statuses.stream().anyMatch(s -> s.effect instanceof Root && s.remaining>0); }

    public void tickStart(EventBus bus) {
        for (ActiveStatus s : new ArrayList<>(statuses)) {
            s.effect.onTurnStart(this, bus);
        }
    }

    public boolean blocksAction() {
        for (ActiveStatus s : statuses) if (s.remaining>0 && s.effect.blocksAction()) return true;
        return false;
    }

    public void tickEnd(EventBus bus) {
        for (ActiveStatus s : new ArrayList<>(statuses)) {
            s.effect.onTurnEnd(this, bus);
            s.remaining--;
        }
        statuses.removeIf(s -> s.remaining <= 0);
        stats.gainSP(2);
    }

    public void takeDamage(int amount) {
        stats.hp -= Math.max(0, amount);
        if (stats.hp < 0) stats.hp = 0;
    }
    public void trueDamage(int amount) {
        stats.hp -= Math.max(0, amount);
        if (stats.hp < 0) stats.hp = 0;
    }

    public abstract boolean isPlayerControlled();
}
