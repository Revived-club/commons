package club.revived.commons.velocity.heartbeat;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import club.revived.commons.distribution.game.OnlinePlayer;
import com.velocitypowered.api.proxy.ProxyServer;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.heartbeat.Heartbeat;
import club.revived.commons.distribution.heartbeat.HeartbeatService;
import club.revived.commons.distribution.kvbus.providers.broker.MessageBroker;

public final class VelocityHeartbeat extends HeartbeatService {

  private final MessageBroker broker;
  private final Cluster cluster;
  private final ProxyServer proxyServer;

  public VelocityHeartbeat(final Cluster cluster, final ProxyServer proxyServer) {
    this.cluster = cluster;
    this.broker = cluster.getBroker();
    this.proxyServer = proxyServer;
  }

  @Override
  public ScheduledFuture<?> startTask() {
    return super.subServer.scheduleAtFixedRate(() -> {
      this.broker.publish("service:heartbeat", new Heartbeat(
          System.currentTimeMillis(),
          cluster.getServiceType(),
          cluster.getServiceId(),
          proxyServer.getAllPlayers().size(),
          proxyServer.getAllPlayers().stream()
              .map(player -> new OnlinePlayer(
                  player.getUniqueId(),
                  player.getUsername(),
                  this.cluster.getServiceId(),
                  "",
                  "",
                  (int) player.getPing()))
              .toList(),
          cluster.getIp()));
    }, 0, HeartbeatService.INTERVAL, TimeUnit.MILLISECONDS);
  }
}
