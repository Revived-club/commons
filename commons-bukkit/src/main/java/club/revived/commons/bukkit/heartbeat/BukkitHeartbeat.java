package club.revived.commons.bukkit.heartbeat;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import club.revived.commons.bukkit.player.SkinUtils;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.game.OnlinePlayer;
import club.revived.commons.distribution.heartbeat.Heartbeat;
import club.revived.commons.distribution.heartbeat.HeartbeatService;
import club.revived.commons.distribution.kvbus.providers.broker.MessageBroker;

import org.bukkit.Bukkit;

public final class BukkitHeartbeat extends HeartbeatService {

  private final MessageBroker broker;
  private final Cluster cluster;

  public BukkitHeartbeat(final Cluster cluster) {
    this.cluster = cluster;
    this.broker = cluster.getBroker();
  }

  @Override
  public ScheduledFuture<?> startTask() {
    return super.subServer.scheduleAtFixedRate(() -> {
      this.broker.publish("service:heartbeat", new Heartbeat(
          System.currentTimeMillis(),
          cluster.getServiceType(),
          cluster.getServiceId(),
          Bukkit.getOnlinePlayers().size(),
          Bukkit.getOnlinePlayers().stream()
              .map(player -> new OnlinePlayer(
                  player.getUniqueId(),
                  player.getName(),
                  this.cluster.getServiceId(),
                  SkinUtils.getSkinTexture(player),
                  SkinUtils.getSkinSignature(player),
                  player.getPing()))
              .toList(),
          cluster.getIp()));
    }, 0, HeartbeatService.INTERVAL, TimeUnit.MILLISECONDS);
  }
}
