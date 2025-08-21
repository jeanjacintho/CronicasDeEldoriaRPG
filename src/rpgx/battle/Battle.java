package rpgx.battle;

import rpgx.commands.*;
import rpgx.core.*;
import rpgx.damage.DamageCalculator;
import rpgx.damage.StandardDamageCalculator;
import rpgx.effects.ActiveStatus;
import rpgx.effects.Burn;
import rpgx.effects.Guard;
import rpgx.events.EventBus;
import rpgx.events.Events;
import rpgx.items.Consumable;
import rpgx.skills.Element;
import rpgx.skills.Skill;
import rpgx.skills.SkillBuilder;
import rpgx.ui.ConsoleRenderer;
import rpgx.ui.Input;
import rpgx.world.AIBehavior;

import java.util.*;
import java.util.stream.Collectors;

public class Battle {
    private final List<Actor> heroes;
    private final List<Actor> enemies;
    private final List<Actor> everyone;
    private final TurnOrderStrategy turnOrder = new SpeedTurnOrder();
    private final Queue<CombatAction> queue = new ArrayDeque<>();
    private final ConsoleRenderer renderer = new ConsoleRenderer();
    private final Input input = new Input();
    private final EventBus bus = new EventBus();
    private final DamageCalculator damageCalc = new StandardDamageCalculator();
    private final Random random = new Random();

    // ataque básico como Skill virtual
    private final Skill basic = SkillBuilder.of("Ataque").element(Element.PHYSICAL).power(0).scale(1.0,0.0).build();

    public Battle(List<Actor> heroes, List<Actor> enemies) {
        this.heroes = heroes;
        this.enemies = enemies;
        this.everyone = new ArrayList<>();
        everyone.addAll(heroes); everyone.addAll(enemies);
        bus.subscribe(renderer);
        // referenciar times para alcance/linhas
        for (Actor a : heroes) if (a instanceof PlayerActor p) p.attachRefs(heroes, enemies);
        for (Actor e : enemies) if (e instanceof Enemy en) en.attachRefs(everyone);
    }

    public void run() {
        renderer.println("\n==============================");
        renderer.println("⚔️  BATALHA INICIADA");
        renderer.println("==============================");
        renderer.printParty("Heróis:", heroes);
        renderer.printParty("Inimigos:", enemies);

        int round = 1;
        while (alive(heroes) && alive(enemies)) {
            renderer.println("\n--- RODADA " + round + " ---");
            List<Actor> order = turnOrder.order(everyone.stream().filter(Actor::isAlive).toList());
            renderer.println("Ordem: " + renderer.orderList(order));

            for (Actor current : order) {
                if (!current.isAlive()) continue;

                bus.publish(Events.tStart(current));
                current.tickStart(bus);

                if (!current.isAlive()) { bus.publish(Events.death(current)); continue; }

                if (current.blocksAction()) {
                    renderer.println(current.getName() + " está incapaz de agir (stun/freeze)!");
                } else if (current.isPlayerControlled()) {
                    playerTurn((PlayerActor) current);
                } else {
                    enemyTurn((Enemy) current);
                }

                // executa primeira ação enfileirada (decisão do turno)
                CombatAction action = queue.poll();
                if (action != null) {
                    renderer.println("Ação: " + action.describe());
                    action.execute(this);
                }

                current.tickEnd(bus);
                cleanupDead();
                bus.publish(Events.tEnd(current));
                if (!alive(heroes) || !alive(enemies)) break;
            }
            round++;
        }

        renderer.println(alive(heroes) ? "\n🏆 Vitória!" : "\n💀 Derrota...");
    }

    private void playerTurn(PlayerActor p) {
        while (true) {
            renderer.println("\nTurno de " + p.getName());
            renderer.println("[1] Atacar  [2] Habilidade  [3] Defender  [4] Mover  [5] Consumível  [6] Fugir");
            int op = input.readInt(1,6);
            switch (op) {
                case 1 -> { // Attack
                    Actor target = chooseTarget(opponentsOf(p));
                    if (target != null) enqueue(new AttackAction(p, target));
                    return;
                }
                case 2 -> { // Skill
                    if (p.getSkills().isEmpty()) { renderer.println("Sem habilidades."); break; }
                    Skill s = chooseSkill(p);
                    if (s == null) break;
                    if (s.getTargetType() == Skill.TargetType.SINGLE) {
                        Actor target = chooseTarget(opponentsOf(p));
                        if (target == null) break;
                        enqueue(new SkillAction(p, s, List.of(target)));
                    } else if (s.getTargetType() == Skill.TargetType.ALL_ENEMIES) {
                        enqueue(new SkillAction(p, s, opponentsOf(p)));
                    } else {
                        enqueue(new SkillAction(p, s, alliesOf(p)));
                    }
                    return;
                }
                case 3 -> { enqueue(new DefendAction(p)); return; }
                case 4 -> {
                    renderer.println("Mover: [1] Frente  [2] Trás");
                    int m = input.readInt(1,2);
                    enqueue(new MoveAction(p, m==1?Position.FRONT:Position.BACK));
                    return;
                }
                case 5 -> {
                    var cons = p.chooseConsumable(input);
                    if (cons == null) { renderer.println("Sem consumíveis."); break; }
                    enqueue(new UseItemAction(p, cons));
                    return;
                }
                case 6 -> { enqueue(new FleeAction(p)); return; }
            }
        }
    }

    private void enemyTurn(Enemy e) {
        AIBehavior ai = e.getAi();
        ai.decideAndEnqueue(e, this);
    }

    private Skill chooseSkill(Actor a) {
        List<Skill> list = a.getSkills();
        for (int i=0;i<list.size();i++) System.out.println("["+(i+1)+"] "+ list.get(i));
        int idx = input.readInt(1, list.size())-1;
        return list.get(idx);
    }

    private Actor chooseTarget(List<Actor> list) {
        List<Actor> alive = list.stream().filter(Actor::isAlive).collect(Collectors.toList());
        if (alive.isEmpty()) return null;
        for (int i=0;i<alive.size();i++) System.out.println("["+(i+1)+"] "+ alive.get(i).getName()+" ("+alive.get(i).getClazz()+") Pos:"+alive.get(i).getPosition());
        int idx = input.readInt(1, alive.size())-1;
        return alive.get(idx);
    }

    private void cleanupDead() {
        // no-op beyond printing, logic uses isAlive checks
    }

    public boolean alive(List<Actor> side) { return side.stream().anyMatch(Actor::isAlive); }

    // Utilities and accessors for actions/AI
    public void enqueue(CombatAction a) { queue.offer(a); }
    public List<Actor> opponentsOf(Actor a) { return a.getTeam()==Team.HERO ? enemies : heroes; }
    public List<Actor> alliesOf(Actor a) { return a.getTeam()==Team.HERO ? heroes : enemies; }
    public List<Actor> getHeroes() { return heroes; }
    public List<Actor> getEnemies() { return enemies; }
    public ConsoleRenderer getRenderer() { return renderer; }
    public EventBus getBus() { return bus; }
    public DamageCalculator getDamageCalc() { return damageCalc; }
    public Skill getBasicAttack() { return basic; }
    public Random rng() { return random; }
    public double avgSpeed(List<Actor> side) { return side.stream().mapToInt(x->x.getStats().speed).average().orElse(1); }
}
