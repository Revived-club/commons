package club.revived.commons.bukkit.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// TODO: Implement into commons
public final class SkinUtils {

  @NotNull
  public static String getSkinTexture(final Player player) {
    return player.getPlayerProfile()
        .getProperties()
        .stream()
        .filter(property -> property.getName().equalsIgnoreCase("textures"))
        .toList()
        .getFirst()
        .getValue();
  }

  @NotNull
  public static String getSkinSignature(final Player player) {
    return player.getPlayerProfile()
        .getProperties()
        .stream()
        .filter(property -> property.getName().equalsIgnoreCase("textures"))
        .toList()
        .getFirst()
        .getSignature();
  }
}
