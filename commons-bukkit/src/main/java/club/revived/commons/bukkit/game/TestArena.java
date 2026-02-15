package club.revived.commons.bukkit.game;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.game.arena.ArenaType;

public final class TestArena extends BukkitArena {

  public TestArena(Location corner1, Location corner2, Location spawn1, Location spawn2) {
    super("ueajueh", "test", ArenaType.INTERACTIVE, corner1, corner2, spawn1, spawn2);
  }

  @Override
  public void generate(@NotNull Location location) {
  }
}
