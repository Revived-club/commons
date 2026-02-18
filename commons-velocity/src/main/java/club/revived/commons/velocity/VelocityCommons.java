package club.revived.commons.velocity;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.proxy.ProxyServer;

import club.revived.commons.Commons;
import club.revived.commons.distribution.service.ServiceSpecifics;
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

  @Override
  protected void initHeartbeats() {
    new VelocityHeartbeat(super.cluster, this.proxyServer, this.serviceSpecifics).startTask();
  }

  @Override
  protected void initMessageHandlers() {

  }

  @Override
  protected String getServiceIP() {
    return null;
  }
}
