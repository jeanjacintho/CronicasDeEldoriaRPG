package rpgx.effects;

public class ActiveStatus {
    public final StatusEffect effect;
    public int remaining;

    public ActiveStatus(StatusEffect effect, int duration) {
        this.effect = effect;
        this.remaining = duration;
    }
}
