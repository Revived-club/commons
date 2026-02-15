package club.revived.commons.bukkit.game;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.game.arena.Arena;
import club.revived.commons.game.arena.ArenaType;

public abstract class BukkitArena extends Arena {

  protected final Location corner1;
  protected final Location corner2;

  public BukkitArena(
      final @NotNull String id,
      final @NotNull String parentId,
      final @NotNull ArenaType arenaType,
      final @NotNull Location corner1,
      final @NotNull Location corner2) {
    this.corner1 = corner1;
    this.corner2 = corner2;

    super(id, parentId, arenaType);
  }

  @NotNull
  public Location getCorner1() {
    return this.corner1;
  }

  @NotNull
  public Location getCorner2() {
    return this.corner2;
  }

  public abstract void generate(final @NotNull Location location);

}
