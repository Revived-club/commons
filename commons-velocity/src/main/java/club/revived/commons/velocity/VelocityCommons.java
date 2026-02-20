package club.revived.commons.velocity;

import java.net.InetAddress;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.proxy.ProxyServer;

import club.revived.commons.Commons;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.message.ConnectMessage;
import club.revived.commons.distribution.service.ServiceSpecifics;
import club.revived.commons.distribution.service.ServiceType;
import club.revived.commons.velocity.heartbeat.VelocityHeartbeat;

public final class VelocityCommons extends Commons {

  private final ProxyServer proxyServer;
  private final ServiceSpecifics serviceSpecifics;

  public VelocityCommons(
      final @NotNull ProxyServer proxyServer,
      final @NotNull ServiceSpecifics serviceSpecifics) {
    this.proxyServer = proxyServer;
    this.serviceSpecifics = serviceSpecifics;
  }

  public void init() {
    super.init(ServiceType.PROXY);
  }

  @Override
  protected void initHeartbeats() {
    new VelocityHeartbeat(super.cluster, this.proxyServer, this.serviceSpecifics).startTask();
  }

  @Override
  protected void initMessageHandlers() {
    Cluster.getInstance().getMessagingService()
        .registerMessageHandler(ConnectMessage.class, message -> {
          final var uuid = message.uuid();

          this.proxyServer.getPlayer(uuid).ifPresent(player -> {
            this.proxyServer.getServer(message.id()).ifPresentOrElse(server -> {
              player.createConnectionRequest(server).fireAndForget();
            }, () -> {
              // TODO: Implement error handling
            });
          });
        });
  }

  @Override
  protected String getServiceIP() {
    try {
      final var ip = InetAddress.getLocalHost().getHostAddress();
      final var port = 19132;

      return ip + ":" + port;
    } catch (final Exception e) {
      throw new IllegalStateException("Service failed to get IP");
    }
  }
}
