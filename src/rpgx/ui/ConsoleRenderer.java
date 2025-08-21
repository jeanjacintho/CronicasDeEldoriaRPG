package rpgx.ui;

import rpgx.core.Actor;
import rpgx.core.Position;
import rpgx.events.EventBus;
import rpgx.events.EventListener;
import rpgx.events.GameEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ConsoleRenderer implements EventListener {
    public void println(String s) { System.out.println(s); }

    public void printParty(String title, List<Actor> actors) {
        System.out.println(title);
        for (Actor a : actors) {
            var st = a.getStats();
            String line = String.format("  [%s] %s HP:%d/%d MP:%d/%d SP:%d/%d Str:%d Int:%d Def%%:%d Arm:%d Spd:%d Crit:%d%% Pos:%s",
                    a.getClazz(), a.getName(), st.hp, st.maxHP, st.mp, st.maxMP, st.sp, st.maxSP,
                    st.strength, st.intellect, st.defensePercent, st.armor, st.speed, (int)(st.critChance*100),
                    a.getPosition()== Position.FRONT?"FRENTE":"TRÁS");
            System.out.println(line);
        }
    }

    @Override
    public void onEvent(GameEvent e) {
        switch (e.type) {
            case DAMAGE -> System.out.println("💥 " + e.target.getName() + " sofre " + e.amount + " (" + e.detail + ")");
            case HEAL -> System.out.println("✨ " + e.target.getName() + " cura " + e.amount + " (" + e.detail + ")");
            case DEATH -> System.out.println("☠️  " + e.target.getName() + " foi derrotado!");
            case STATUS_APPLIED -> System.out.println("🌀 " + e.target.getName() + " recebe " + e.detail + " por " + e.amount + " turno(s).");
            case TURN_START -> System.out.println("\n— Turno de " + e.actor.getName() + " —");
            case TURN_END -> { /* opcional */ }
        }
    }

    public String orderList(List<Actor> list) {
        return list.stream().map(Actor::getName).collect(Collectors.joining(" -> "));
    }
}
