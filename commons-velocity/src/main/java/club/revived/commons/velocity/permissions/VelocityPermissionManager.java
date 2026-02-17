package club.revived.commons.velocity.permissions;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import club.revived.commons.permissions.PermissionManager;

public final class VelocityPermissionManager extends PermissionManager<Player> {

  private final ProxyServer server;

  public VelocityPermissionManager(final ProxyServer proxyServer) {
    super();

    this.server = proxyServer;
    this.initListeners();
  }

  @Override
  public CompletableFuture<Map<String, Boolean>> loadPermissions(final @NotNull UUID uuid) {
    return super.getUserGroups(uuid)
        .thenApply(groups -> {

          return super.resolvePermissions(groups);
        });
  }

  @Override
  public CompletableFuture<Map<String, Boolean>> loadPermissions(final @NotNull Player player) {
    return super.getUserGroups(player.getUniqueId())
        .thenApply(groups -> {

          return super.resolvePermissions(groups);
        });
  }

  private void initListeners() {
    this.server.getEventManager().register(this.server, PermissionsSetupEvent.class, event -> {
      if (!(event.getSubject() instanceof final Player player)) {
        return;
      }

      this.loadPermissions(player).thenAccept(perms -> {
        final var provider = new VelocityPermissionProvider(player, perms);

        event.setProvider(provider);
      });

    });
  }
}
