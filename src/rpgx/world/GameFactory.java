package rpgx.world;

import rpgx.core.*;
import rpgx.effects.*;
import rpgx.items.*;
import rpgx.skills.*;

import java.util.ArrayList;
import java.util.List;

public class GameFactory {

    // Skills
    private Skill spinSlash() {
        return SkillBuilder.of("Corte Giratório")
                .element(Element.PHYSICAL).power(12).scale(1.1, 0.0).cost(0,6)
                .target(Skill.TargetType.ALL_ENEMIES).status(new Bleed(), 2).build();
    }
    private Skill charge() {
        return SkillBuilder.of("Investida")
                .element(Element.PHYSICAL).power(16).scale(1.2,0.0).cost(0,5)
                .target(Skill.TargetType.SINGLE).pierceArmor().ignoreLine().status(new Stun(),1).build();
    }
    private Skill fireball() {
        return SkillBuilder.of("Bola de Fogo")
                .element(Element.FIRE).power(20).scale(0.2,1.0).cost(10,3)
                .target(Skill.TargetType.SINGLE).ignoreLine().status(new Burn(),2).build();
    }
    private Skill iceStorm() {
        return SkillBuilder.of("Tempestade Gélida")
                .element(Element.ICE).power(14).scale(0.1,0.9).cost(16,4)
                .target(Skill.TargetType.ALL_ENEMIES).ignoreLine().status(new Freeze(),1).build();
    }
    private Skill lightning() {
        return SkillBuilder.of("Raio")
                .element(Element.ELECTRIC).power(18).scale(0.1,1.1).cost(12,3)
                .target(Skill.TargetType.SINGLE).ignoreLine().pierceArmor().status(new Stun(),1).build();
    }
    private Skill rainOfArrows() {
        return SkillBuilder.of("Chuva de Flechas")
                .element(Element.PHYSICAL).power(10).scale(0.9,0.0).cost(0,6)
                .target(Skill.TargetType.ALL_ENEMIES).ignoreLine().build();
    }
    private Skill preciseShot() {
        return SkillBuilder.of("Tiro Preciso")
                .element(Element.PHYSICAL).power(12).scale(1.4,0.0).cost(0,4)
                .target(Skill.TargetType.SINGLE).ignoreLine().pierceArmor().build();
    }
    private Skill rootArrow() {
        return SkillBuilder.of("Flecha Enredante")
                .element(Element.PHYSICAL).power(6).scale(0.6,0.0).cost(0,4)
                .target(Skill.TargetType.SINGLE).ignoreLine().status(new Root(),2).build();
    }
    private Skill ogreQuake() {
        return SkillBuilder.of("Soco Terremoto")
                .element(Element.PHYSICAL).power(18).scale(1.0,0.0).cost(0,8)
                .target(Skill.TargetType.ALL_ENEMIES).status(new Stun(),1).build();
    }
    private Skill shamanFlame() {
        return SkillBuilder.of("Chama Bruta")
                .element(Element.FIRE).power(14).scale(0.2,0.8).cost(10,3)
                .target(Skill.TargetType.SINGLE).ignoreLine().status(new Burn(),2).build();
    }

    // Equipments (with extra skill for Uniques)
    private Equipment uniqueZeusBolt() {
        Equipment e = new Equipment("Raio de Zeus ✦✦✦", Rarity.UNIQUE, Slot.WEAPON, 20, 12, 8, 6, 6, 6, 8, 3, 0.04);
        e.addExtraSkill(SkillBuilder.of("Raio de Zeus")
                .element(Element.ELECTRIC).power(24).scale(0.2,1.1).cost(12,4)
                .target(Skill.TargetType.ALL_ENEMIES).ignoreLine().pierceArmor().status(new Stun(),1).build());
        return e;
    }

    public List<Actor> createDefaultHeroes() {
        List<Actor> heroes = new ArrayList<>();

        PlayerActor warrior = new PlayerActor("Guerreiro", "Ragnar",
                new Stats(120, 20, 40, 14, 6, 9, 8, 8, 0.08));
        warrior.addSkill(spinSlash());
        warrior.addSkill(charge());
        warrior.getResistances().add(Element.PHYSICAL);
        warrior.getWeaknesses().add(Element.DARK);
        warrior.setPosition(Position.FRONT);
        warrior.addItem(new Consumable("Poção Menor", Rarity.COMMON, 25, 10, 8));

        PlayerActor mage = new PlayerActor("Mago", "Selene",
                new Stats(90, 70, 30, 8, 14, 6, 4, 10, 0.07));
        mage.addSkill(fireball());
        mage.addSkill(iceStorm());
        mage.addSkill(lightning());
        mage.getResistances().add(Element.HOLY);
        mage.getWeaknesses().add(Element.PHYSICAL);
        mage.setPosition(Position.BACK);
        mage.addItem(new Consumable("Poção Menor", Rarity.COMMON, 25, 10, 8));

        PlayerActor archer = new PlayerActor("Arqueiro", "Ilyan",
                new Stats(100, 20, 40, 12, 6, 7, 5, 12, 0.10));
        archer.addSkill(preciseShot());
        archer.addSkill(rainOfArrows());
        archer.addSkill(rootArrow());
        archer.getResistances().add(Element.ELECTRIC);
        archer.setPosition(Position.BACK);
        archer.addItem(new Consumable("Elixir Âmbar", Rarity.RARE, 40, 20, 15));

        // unique gear to show off
        archer.addItem(uniqueZeusBolt());

        heroes.add(warrior); heroes.add(mage); heroes.add(archer);
        return heroes;
    }

    public List<Actor> createEncounter1() {
        List<Actor> enemies = new ArrayList<>();

        Enemy gobLancer = new Enemy("Monstro", "Goblin Lanceiro",
                new Stats(85, 0, 40, 12, 2, 5, 6, 9, 0.05), new AggressiveAI());
        gobLancer.getResistances().add(Element.PHYSICAL);
        gobLancer.setPosition(Position.FRONT);

        Enemy gobShaman = new Enemy("Monstro", "Goblin Xamã",
                new Stats(70, 30, 30, 8, 12, 3, 3, 8, 0.06), new TacticalAI());
        gobShaman.addSkill(shamanFlame());
        gobShaman.setPosition(Position.BACK);

        Enemy ogre = new Enemy("Chefe", "Ogro da Ponte",
                new Stats(160, 0, 50, 18, 4, 8, 10, 6, 0.05), new TacticalAI());
        ogre.addSkill(ogreQuake());
        ogre.setPosition(Position.FRONT);

        enemies.add(gobLancer); enemies.add(gobShaman); enemies.add(ogre);
        return enemies;
    }

    public List<Actor> createEncounter2() {
        List<Actor> enemies = new ArrayList<>();
        Enemy rogue = new Enemy("Assassino", "Lâmina Sombria",
                new Stats(95, 10, 50, 16, 6, 8, 4, 14, 0.15), new AggressiveAI());
        rogue.getResistances().add(Element.DARK);
        rogue.getWeaknesses().add(Element.HOLY);
        rogue.setPosition(Position.BACK);
        enemies.add(rogue);

        Enemy cultist = new Enemy("Ocultista", "Vulto do Véu",
                new Stats(110, 40, 30, 10, 14, 6, 6, 10, 0.10), new TacticalAI());
        cultist.addSkill(SkillBuilder.of("Sussurros Sombrio")
                .element(Element.DARK).power(16).scale(0.2,1.0).cost(12,3)
                .target(Skill.TargetType.SINGLE).ignoreLine().build());
        cultist.setPosition(Position.FRONT);
        enemies.add(cultist);
        return enemies;
    }
}
