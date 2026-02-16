package club.revived.commons.bukkit.heartbeat;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import club.revived.commons.bukkit.player.SkinUtils;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.game.OnlinePlayer;
import club.revived.commons.distribution.heartbeat.Heartbeat;
import club.revived.commons.distribution.heartbeat.HeartbeatService;
import club.revived.commons.distribution.kvbus.providers.broker.MessageBroker;
import club.revived.commons.distribution.service.ServiceSpecifics;

import org.bukkit.Bukkit;

public final class BukkitHeartbeat extends HeartbeatService {

  private final MessageBroker broker;
  private final Cluster cluster;

  private final ServiceSpecifics specifics;

  public BukkitHeartbeat(final Cluster cluster, final ServiceSpecifics specifics) {
    this.cluster = cluster;
    this.broker = cluster.getBroker();
    this.specifics = specifics;
  }

  @Override
  public ScheduledFuture<?> startTask() {
    return super.subServer.scheduleAtFixedRate(() -> {
      final var specificsBuilder = this.specifics.toBuilder();

      final var onlinePlayers = Bukkit.getOnlinePlayers()
          .stream()
          .map(player -> {
            return new OnlinePlayer(
                player.getUniqueId(),
                player.getName(),
                this.cluster.getServiceId(),
                SkinUtils.getSkinTexture(player),
                SkinUtils.getSkinSignature(player),
                player.getPing());
          }).toList();

      specificsBuilder.onlinePlayers(onlinePlayers);

      this.broker.publish("service:heartbeat", new Heartbeat(
          System.currentTimeMillis(),
          cluster.getServiceType(),
          cluster.getServiceId(),
          cluster.getIp(),
          specificsBuilder.build()));
    }, 0, HeartbeatService.INTERVAL, TimeUnit.MILLISECONDS);
  }
}
