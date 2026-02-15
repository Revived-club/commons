package club.revived.commons.game;

import club.revived.commons.game.arena.ArenaType;

public enum KitType {

  UHC(
      "UHC",
      false,
      ArenaType.INTERACTIVE),

  SWORD(
      "Sword",
      false,
      ArenaType.RESTRICTED),

  MACE(
      "Mace",
      false,
      ArenaType.RESTRICTED),

  CART(
      "Cart",
      false,
      ArenaType.INTERACTIVE),

  SMP(
      "SMP",
      false,
      ArenaType.RESTRICTED),

  NETHERITE_POTION(
      "Nethpot",
      false,
      ArenaType.RESTRICTED),

  DIAMOND_POTION(
      "Diapot",
      false,
      ArenaType.RESTRICTED),

  TNT(
      "TNT",
      false,
      ArenaType.INTERACTIVE),

  SPLEEF(
      "Spleef",
      false,
      ArenaType.INTERACTIVE),

  AXE(
      "Axe",
      false,
      ArenaType.RESTRICTED),

  CRYSTAL(
      "Custom Kit",
      false,
      ArenaType.INTERACTIVE

  );

  private final String bName;
  private final boolean isRanked;
  private final ArenaType arenaType;

  KitType(
      final String beautifiedName,
      final boolean ranked,
      final ArenaType arenaType) {
    this.bName = beautifiedName;
    this.isRanked = ranked;
    this.arenaType = arenaType;
  }

  public String getBeautifiedName() {
    return this.bName;
  }

  public boolean isRanked() {
    return this.isRanked;
  }

  public ArenaType getArenaType() {
    return arenaType;
  }
}
