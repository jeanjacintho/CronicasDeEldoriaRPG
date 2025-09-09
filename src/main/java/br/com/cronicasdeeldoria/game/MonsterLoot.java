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

  // Pode tropas 0 a vários itens
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
    addLoot("bow_common", 0.05);
    addLoot("axe_common", 0.05);
    addLoot("armor_common", 0.05);
    addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.01);
  }
}

class SkeletonLootTable extends LootTable {
  public SkeletonLootTable() {
    addLoot("health_potion", 0.4);
    addLoot("mana_potion", 0.35);
    addLoot("shield_common", 0.05);
    addLoot("sword_common", 0.05);
    addLoot("hammer_common", 0.05);
    addLoot("bow_common", 0.05);
    addLoot("axe_common", 0.05);
    addLoot("armor_common", 0.05);
    addLoot("key_common", 0.01);
    addLoot("boot_speed", 0.01);
  }
}
