package club.revived.commons.bukkit.draining;

import java.awt.desktop.UserSessionEvent.Reason;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import club.revived.commons.bukkit.listener.Events;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.service.ServiceType;
import club.revived.commons.draining.ServiceDrainManager;
import club.revived.commons.TimedQueue;
import club.revived.commons.bukkit.item.ColorUtils;

public final class BukkitDrainManager extends ServiceDrainManager {

  public BukkitDrainManager() {
    this.initListeners();
  }

  @Override
  protected void onDrainStart() {
    if (Cluster.getInstance().getServiceType() == ServiceType.LOBBY) {

      for (final var player : Bukkit.getOnlinePlayers()) {
        player.sendRichMessage("<red>The server you are on is shutting down!");
      }

      final var uuids = Bukkit.getOnlinePlayers().stream()
          .map(Player::getUniqueId)
          .toList();

      final var queue = new BukkitDisconnectionQueue(uuids);
      queue.start();

    }
  }

  private void initListeners() {
    Events.subscribe(AsyncPlayerPreLoginEvent.class, EventPriority.HIGHEST)
        .ignoreCancelled(false)
        .handler(event -> {
          if (!super.isDraining) {
            return;
          }

          event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ColorUtils.parse("<red>Server is shutting down"));
        });
  }
}
