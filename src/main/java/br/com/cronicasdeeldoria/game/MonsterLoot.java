package br.com.cronicasdeeldoria.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonsterLoot {
  String itemName;
  double chance; // entre 0.0 e 1.0

  public MonsterLoot(String itemName, double chance) {
    this.itemName = itemName;
    this.chance = chance;
  }
}

class LootTable {
  private List<MonsterLoot> loot = new ArrayList<>();
  private Random random = new Random();

  public void addLoot(String itemName, double chance) {
    loot.add(new MonsterLoot(itemName, chance));
  }

  // Pode tropar 0 a vários itens
  public List<String> getDrops() {
    List<String> drops = new ArrayList<>();
    for (MonsterLoot entry : loot) {
      if (random.nextDouble() <= entry.chance) {
        drops.add(entry.itemName);
      }
    }
    return drops;
  }
}

class WolfLootTable extends LootTable {
  public WolfLootTable() {
    addLoot("health_potion", 0.6);
    addLoot("mana_potion", 0.4);
    addLoot("shield_common", 0.05);
    addLoot("sword_common", 0.05);
    addLoot("hammer_common", 0.05);
    addLoot("bow_common", 0.1);
    addLoot("axe_common", 0.05);
    addLoot("armor_common", 0.05);
    addLoot("ring_common", 0.05);
    addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.01);

    addLoot("sword_rare", 0.01);
    addLoot("hammer_rare", 0.01);
    addLoot("bow_rare", 0.02);
    addLoot("axe_rare", 0.01);
    addLoot("armor_rare", 0.01);
    addLoot("ring_rare", 0.01);
  }
}

class SkeletonLootTable extends LootTable {
  public SkeletonLootTable() {
    addLoot("health_potion", 0.6);
    addLoot("mana_potion", 0.4);
    addLoot("shield_common", 0.05);
    addLoot("sword_common", 0.05);
    addLoot("hammer_common", 0.05);
    addLoot("bow_common", 0.1);
    addLoot("axe_common", 0.05);
    addLoot("armor_common", 0.05);
    addLoot("ring_common", 0.05);
    addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.01);

    addLoot("sword_rare", 0.01);
    addLoot("hammer_rare", 0.01);
    addLoot("bow_rare", 0.02);
    addLoot("axe_rare", 0.01);
    addLoot("armor_rare", 0.01);
    addLoot("ring_rare", 0.01);
  }
}

class FrostbornLootTable extends LootTable {
  public FrostbornLootTable() {
    addLoot("health_potion", 0.6);
    addLoot("mana_potion", 0.4);
    addLoot("shield_common", 0.05);
    addLoot("sword_common", 0.05);
    addLoot("hammer_common", 0.05);
    addLoot("bow_common", 0.1);
    addLoot("axe_common", 0.05);
    addLoot("armor_common", 0.05);
    addLoot("ring_common", 0.05);
    addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.01);

    addLoot("sword_rare", 0.01);
    addLoot("hammer_rare", 0.01);
    addLoot("bow_rare", 0.02);
    addLoot("axe_rare", 0.01);
    addLoot("armor_rare", 0.01);
    addLoot("ring_rare", 0.01);
  }
}

class OrcLootTable extends LootTable {
  public OrcLootTable() {
    addLoot("health_potion", 0.6);
    addLoot("mana_potion", 0.4);
    addLoot("shield_common", 0.05);
    addLoot("sword_common", 0.05);
    addLoot("hammer_common", 0.05);
    addLoot("bow_common", 0.1);
    addLoot("axe_common", 0.05);
    addLoot("armor_common", 0.05);
    addLoot("ring_common", 0.05);
    addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.01);

    addLoot("sword_rare", 0.01);
    addLoot("hammer_rare", 0.01);
    addLoot("bow_rare", 0.02);
    addLoot("axe_rare", 0.01);
    addLoot("armor_rare", 0.01);
    addLoot("ring_rare", 0.01);
  }
}

class OrcBossLootTable extends LootTable {
  public OrcBossLootTable(String charClass) {
    addLoot("orb_earth", 1);
    addLoot("armor_rare", 1);
    addLoot("health_potion", 0.8);
    addLoot("mana_potion", 0.6);
    addLoot("shield_common", 0.1);
    addLoot("sword_rare", 0.05);
    addLoot("hammer_rare", 0.05);
    addLoot("bow_rare", 0.5);
    addLoot("axe_rare", 0.05);
    addLoot("ring_rare", 0.05);
    //addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.05);

    addLoot("shield_legendary", 0.01);
    addLoot("sword_legendary", 0.01);
    addLoot("hammer_legendary", 0.01);
    addLoot("bow_legendary", 0.02);
    addLoot("axe_legendary", 0.01);
    addLoot("armor_legendary", 0.01);
  }
}

class WolfBossLootTable extends LootTable {
  public WolfBossLootTable(String charClass) {
    switch (charClass) {
      case "Mage": addLoot("ring_rare", 1); break;
      case "Ranger": addLoot("bow_rare", 1); break;
      case "Paladin", "Barbarian": addLoot("axe_rare", 1); break;
    }
    addLoot("orb_fire", 1);
    addLoot("health_potion", 0.8);
    addLoot("mana_potion", 0.6);
    addLoot("shield_common", 0.1);
    addLoot("sword_rare", 0.05);
    addLoot("hammer_rare", 0.05);
    addLoot("bow_rare", 0.5);
    addLoot("axe_rare", 0.05);
    addLoot("armor_rare", 0.05);
    //addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.05);

    addLoot("shield_legendary", 0.01);
    addLoot("sword_legendary", 0.01);
    addLoot("hammer_legendary", 0.01);
    addLoot("bow_legendary", 0.02);
    addLoot("axe_legendary", 0.01);
    addLoot("armor_legendary", 0.01);
  }
}

class SkeletonBossLootTable extends LootTable {
  public SkeletonBossLootTable(String charClass) {
    addLoot("orb_air", 1);
    addLoot("health_potion", 0.8);
    addLoot("mana_potion", 0.6);
    addLoot("shield_common", 0.1);
    addLoot("sword_rare", 0.05);
    addLoot("hammer_rare", 0.05);
    addLoot("bow_rare", 0.05);
    addLoot("axe_rare", 0.05);
    addLoot("armor_rare", 0.05);
    //addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.05);

    addLoot("shield_legendary", 0.01);
    addLoot("sword_legendary", 0.01);
    addLoot("hammer_legendary", 0.01);
    addLoot("bow_legendary", 0.02);
    addLoot("axe_legendary", 0.01);
    addLoot("armor_legendary", 0.01);
  }
}

class FrostbornBossLootTable extends LootTable {
  public FrostbornBossLootTable(String charClass) {
    addLoot("orb_water", 1);
    addLoot("health_potion", 0.8);
    addLoot("mana_potion", 0.6);
    addLoot("shield_common", 0.2);
    addLoot("sword_rare", 0.05);
    addLoot("hammer_rare", 0.05);
    addLoot("bow_rare", 0.05);
    addLoot("axe_rare", 0.05);
    addLoot("armor_rare", 0.05);
    //addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.05);

    addLoot("shield_legendary", 0.01);
    addLoot("sword_legendary", 0.01);
    addLoot("hammer_legendary", 0.01);
    addLoot("bow_legendary", 0.02);
    addLoot("axe_legendary", 0.01);
    addLoot("armor_legendary", 0.01);
  }
}
