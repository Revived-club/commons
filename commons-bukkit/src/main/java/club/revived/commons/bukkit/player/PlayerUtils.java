package club.revived.commons.bukkit.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlayerUtils {

  @NotNull
  public static List<Player> withPermissions(final @NotNull String permission) {
    final List<Player> ret = new ArrayList<>();

    for (final Player gl : Bukkit.getOnlinePlayers()) {
      if (gl.hasPermission(permission))
        ret.add(gl);
    }

    return ret;
  }
}
