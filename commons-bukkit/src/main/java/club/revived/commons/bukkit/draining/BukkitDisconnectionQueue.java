package club.revived.commons.bukkit.draining;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.TimedQueue;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.game.PlayerManager;
import club.revived.commons.distribution.service.ServiceType;

public final class BukkitDisconnectionQueue extends TimedQueue<UUID> {

  public BukkitDisconnectionQueue(final @NotNull Collection<? extends UUID> elements) {
    super(elements, 1, TimeUnit.SECONDS);
  }

  @Override
  protected void handle(final UUID uuid) {
    final var player = Bukkit.getPlayer(uuid);

    if (player == null || !player.isOnline()) {
      return;
    }

    final var limbo = Cluster.getInstance().getLeastLoadedLobby(ServiceType.LIMBO);
    PlayerManager.getInstance().getOptional(uuid)
        .ifPresentOrElse(onlinePlayer -> {
          
        }, () -> {

        });

  }

  @Override
  protected void onComplete() {

  }

}
