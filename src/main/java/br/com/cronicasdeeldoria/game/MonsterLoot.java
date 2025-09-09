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

  public String getRandomDrop() {
    double roll = random.nextDouble(); // 0.0 até 1.0
    double cumulative = 0.0;

    for (MonsterLoot entry : loot) {
      cumulative += entry.chance;
      if (roll <= cumulative) {
        return entry.itemName;
      }
    }
    return null; // sem loot
  }
}

class WolfLootTable extends LootTable {
  public WolfLootTable() {
    addLoot("health_potion", 0.5);
    addLoot("mana_potion", 0.4);
    addLoot("shield_common", 0.2);
    addLoot("sword_common", 0.2);
    addLoot("hammer_common", 0.2);
    addLoot("bow_common", 0.2);
    addLoot("axe_common", 0.2);
    addLoot("armor_common", 0.25);
    addLoot("key_common", 0.1);
    addLoot("boot_speed", 0.05);
  }
}

class SkeletonLootTable extends LootTable {
  public SkeletonLootTable() {
    addLoot("health_potion", 0.5);
    addLoot("mana_potion", 0.4);
    addLoot("shield_common", 0.2);
    addLoot("sword_common", 0.2);
    addLoot("hammer_common", 0.2);
    addLoot("bow_common", 0.2);
    addLoot("axe_common", 0.2);
    addLoot("armor_common", 0.25);
    addLoot("key_common", 0.1);
    addLoot("boot_speed", 0.05);
  }
}
