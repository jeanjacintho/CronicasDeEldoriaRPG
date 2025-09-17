package br.com.cronicasdeeldoria.entity.item;

import br.com.cronicasdeeldoria.entity.Entity;
import br.com.cronicasdeeldoria.entity.object.ObjectSpriteLoader;
import java.awt.Rectangle;

import java.util.*;

/**
 * Classe que representa um item do jogo, herdando de Entity.
 * Itens podem ser coletados, armazenados no inventário e equipados.
 */
public class Item extends Entity {
  private String itemId;
  private String description;
  private ItemType itemType;
  private ItemRarity rarity;
  private int value;
  private boolean stackable;
  private int stackSize;
  private int maxStackSize;
  private ObjectSpriteLoader.ObjectDefinition objectDefinition;
  private List<String> allowedClass;
  private int strengthFromEquip;
  private int armorFromEquip;
  private int healthFromEquip;
  private int manaFromEquip;

  /**
   * Construtor simples para criar um item de inventário (sem posição no mundo).
   *
   * @param itemId      ID único do item.
   * @param name        Nome do item.
   * @param itemType    Tipo do item.
   * @param rarity      Raridade do item.
   * @param description Descrição do item.
   * @param value       Valor em moedas.
   * @param stackable   Se o item pode ser empilhado.
   */
  public Item(String itemId, String name, ItemType itemType, ItemRarity rarity,
              String description, int value, boolean stackable) {
    super(0, 0, 0, "none", name);
    this.itemId = itemId;
    this.itemType = itemType;
    this.rarity = rarity;
    this.description = description;
    this.value = value;
    this.stackable = stackable;
    this.maxStackSize = stackable ? 99 : 1;
    this.stackSize = 1;
    this.objectDefinition = null;
  }

  /**
   * Construtor para criar um item.
   *
   * @param itemId           ID único do item.
   * @param name             Nome do item.
   * @param worldX           Posição X no mundo.
   * @param worldY           Posição Y no mundo.
   * @param itemType         Tipo do item.
   * @param rarity           Raridade do item.
   * @param description      Descrição do item.
   * @param value            Valor em moedas.
   * @param stackable        Se o item pode ser empilhado.
   * @param maxStackSize     Tamanho máximo da pilha.
   * @param objectDefinition Definição visual do objeto.
   * @param tileSize         Tamanho do tile.
   */
  public Item(String itemId, String name, int worldX, int worldY, ItemType itemType,
              ItemRarity rarity, String description, int value, boolean stackable, int maxStackSize,
              ObjectSpriteLoader.ObjectDefinition objectDefinition, int tileSize, List<String> allowedClass,
              int strengthFromEquip, int armorFromEquip, int healthFromEquip, int manaFromEquip ) {
    super(worldX, worldY, 0, "none", name);
    this.itemId = itemId;
    this.itemType = itemType;
    this.rarity = rarity;
    this.description = description;
    this.value = value;
    this.stackable = stackable;
    this.maxStackSize = maxStackSize;
    this.stackSize = 1;
    this.objectDefinition = objectDefinition;
    this.allowedClass = allowedClass != null ? new ArrayList<>(allowedClass) : null;
    this.strengthFromEquip = strengthFromEquip;
    this.armorFromEquip = armorFromEquip;
    this.healthFromEquip = healthFromEquip;
    this.manaFromEquip = manaFromEquip;

    // Configurar hitbox para itens (1x1 tile)
    this.setHitbox(new Rectangle(0, 0, tileSize, tileSize));
    this.setCollisionOn(false); // Itens não têm colisão por padrão
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
  }

  public ItemRarity getRarity() {
    return rarity;
  }

  public void setRarity(ItemRarity rarity) {
    this.rarity = rarity;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public boolean isStackable() {
    return stackable;
  }

  public void setStackable(boolean stackable) {
    this.stackable = stackable;
  }

  public int getStackSize() {
    return stackSize;
  }

  public void setStackSize(int stackSize) {
    this.stackSize = Math.max(0, Math.min(stackSize, maxStackSize));
  }

  public int getMaxStackSize() {
    return maxStackSize;
  }

  public void setMaxStackSize(int maxStackSize) {
    this.maxStackSize = maxStackSize;
  }

  public ObjectSpriteLoader.ObjectDefinition getObjectDefinition() {
    return objectDefinition;
  }

  public void setObjectDefinition(ObjectSpriteLoader.ObjectDefinition objectDefinition) {
    this.objectDefinition = objectDefinition;
  }

  public boolean canBeEquippedBy(String playerClass) {
    // se o campo estiver vazio ou null, qualquer classe pode usar
    return allowedClass == null || allowedClass.isEmpty() || allowedClass.contains(playerClass.toLowerCase());
  }

  public List<String> getAllowedClass() {
    return allowedClass;
  }

  public void setAllowedClass(List<String> allowedClass) {
    this.allowedClass = allowedClass;
  }

  public int getManaFromEquip() {
    return manaFromEquip;
  }

  public void setManaFromEquip(int manaFromEquip) {
    this.manaFromEquip = manaFromEquip;
  }

  public int getHealthFromEquip() {
    return healthFromEquip;
  }

  public void setHealthFromEquip(int healthFromEquip) {
    this.healthFromEquip = healthFromEquip;
  }

  public int getArmorFromEquip() {
    return armorFromEquip;
  }

  public void setArmorFromEquip(int armorFromEquip) {
    this.armorFromEquip = armorFromEquip;
  }

  public int getStrengthFromEquip() {
    return strengthFromEquip;
  }

  public void setStrengthFromEquip(int strengthFromEquip) {
    this.strengthFromEquip = strengthFromEquip;
  }

  /**
   * Verifica se o item pode ser equipado.
   *
   * @return true se o item é equipável.
   */
  public boolean isEquipable() {
    return itemType != null && itemType.isEquipable();
  }

  /**
   * Verifica se o item pode ser empilhado com outro item.
   *
   * @param otherItem Outro item para comparação.
   * @return true se os itens podem ser empilhados.
   */
  public boolean canStackWith(Item otherItem) {
    if (otherItem == null || !stackable || !otherItem.isStackable()) {
      return false;
    }

    // Verificar se são o mesmo tipo de item (mesmo ID)
    if (!itemId.equals(otherItem.getItemId())) {
      return false;
    }

    // Verificar se há espaço para empilhar
    return stackSize < maxStackSize;
  }

  /**
   * Tenta empilhar outro item neste item.
   *
   * @param otherItem Item a ser empilhado.
   * @return Quantidade que foi empilhada.
   */
  public int stackItem(Item otherItem) {
    if (!canStackWith(otherItem)) {
      return 0;
    }

    int availableSpace = maxStackSize - stackSize;
    int toStack = Math.min(availableSpace, otherItem.getStackSize());

    stackSize += toStack;
    otherItem.setStackSize(otherItem.getStackSize() - toStack);

    return toStack;
  }

  /**
   * Cria uma cópia do item.
   *
   * @return Nova instância do item.
   */
  public Item copy() {
    Item copy = new Item(itemId, getName(), getWorldX(), getWorldY(), itemType, rarity, description, value,
      stackable, maxStackSize, objectDefinition, 16, allowedClass, strengthFromEquip, armorFromEquip,
      healthFromEquip, manaFromEquip  );
    copy.setStackSize(stackSize);
    return copy;
  }
}
