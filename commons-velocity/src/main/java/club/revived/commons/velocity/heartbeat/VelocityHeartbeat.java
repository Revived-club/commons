package club.revived.commons.velocity.heartbeat;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import club.revived.commons.distribution.game.OnlinePlayer;
import com.velocitypowered.api.proxy.ProxyServer;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.heartbeat.Heartbeat;
import club.revived.commons.distribution.heartbeat.HeartbeatService;
import club.revived.commons.distribution.kvbus.providers.broker.MessageBroker;
import club.revived.commons.distribution.service.ServiceSpecifics;

public final class VelocityHeartbeat extends HeartbeatService {

  private final MessageBroker broker;
  private final Cluster cluster;
  private final ProxyServer proxyServer;
  private final ServiceSpecifics specifics;

  public VelocityHeartbeat(final Cluster cluster, final ProxyServer proxyServer, final ServiceSpecifics specifics) {
    this.cluster = cluster;
    this.broker = cluster.getBroker();
    this.proxyServer = proxyServer;
    this.specifics = specifics;
  }

  @Override
  public ScheduledFuture<?> startTask() {
    return super.subServer.scheduleAtFixedRate(() -> {
      final var specificsBuilder = this.specifics.toBuilder();

      final var onlinePlayers = this.proxyServer.getAllPlayers()
          .stream()
          .map(player -> {
            return new OnlinePlayer(
                player.getUniqueId(),
                player.getUsername(),
                this.cluster.getServiceId(),
                "",
                "",
                (int) player.getPing());
          }).toList();

      specificsBuilder.onlinePlayers(onlinePlayers);

      this.broker.publish("service:heartbeat", new Heartbeat(
          System.currentTimeMillis(),
          cluster.getServiceType(),
          cluster.getServiceId(),
          cluster.getIp(),
          cluster.getStatus(),
          specificsBuilder.build()));
    }, 0, HeartbeatService.INTERVAL, TimeUnit.MILLISECONDS);
  }
}
