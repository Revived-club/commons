package club.revived.commons.bukkit.permissions;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.permissions.PermissionManager;

public final class BukkitPermissionManager extends PermissionManager<Player> {

  public BukkitPermissionManager() {
    this.initListeners();
  }

  @Override
  public @NotNull CompletableFuture<Map<String, Boolean>> loadPermissions(final @NotNull UUID uuid) {
    return super.getUserGroups(uuid)
        .thenApply(groups -> {

          return super.resolvePermissions(groups);
        });
  }

  @Override
  public @NotNull CompletableFuture<Map<String, Boolean>> loadPermissions(final @NotNull Player player) {
    return super.getUserGroups(player.getUniqueId())
        .thenApply(groups -> {

          return super.resolvePermissions(groups);
        });
  }

  public void initListeners() {
  }
}
