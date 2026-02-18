package club.revived.commons.bukkit.permissions;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.bukkit.listener.Events;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.message.GroupUpdateMessage;
import club.revived.commons.game.player.ProfileManager;
import club.revived.commons.permissions.PermissionManager;

public final class BukkitPermissionManager extends PermissionManager<Player> {

  private final JavaPlugin plugin;
  private static BukkitPermissionManager instance;

  public BukkitPermissionManager(final JavaPlugin plugin) {
    this.plugin = plugin;

    this.initListeners();
    this.initMessageHandlers();
    instance = this;
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
    Events.subscribe(PlayerJoinEvent.class, EventPriority.HIGHEST)
        .ignoreCancelled(false)
        .handler(event -> {
          this.refreshPermissions(event.getPlayer());
        });
  }

  private void initMessageHandlers() {
    Cluster.getInstance().getMessagingService().registerMessageHandler(GroupUpdateMessage.class, message -> {
      for (final var player : Bukkit.getOnlinePlayers()) {
        ProfileManager.getInstance().getProfile(player.getUniqueId()).thenAccept(profile -> {
          if (profile.isEmpty()) {
            return;
          }

          if (profile.get().permissionGroups().contains(message.groupId())) {
            this.refreshPermissions(player);
          }
        });
      }
    });
  }

  public void refreshPermissions(final Player player) {
    final var permission = player.addAttachment(plugin);

    this.loadPermissions(player).thenAccept(map -> {
      for (final var key : map.keySet()) {
        final var value = map.get(key);

        permission.setPermission(key, value);
      }
    });
  }

  public static BukkitPermissionManager getInstance() {
    if (instance == null) {
      throw new IllegalStateException("BukkitPermissionManager is not registered");
    }

    return instance;
  }
}
