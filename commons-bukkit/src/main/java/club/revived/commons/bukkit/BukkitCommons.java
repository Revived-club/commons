package club.revived.commons.bukkit;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.Commons;
import club.revived.commons.bukkit.heartbeat.BukkitHeartbeat;
import club.revived.commons.distribution.service.ServiceSpecifics;
import club.revived.commons.distribution.service.ServiceType;

import java.net.InetAddress;

public final class BukkitCommons extends Commons {

  private final ServiceSpecifics serviceSpecifics;

  public BukkitCommons(final @NotNull ServiceSpecifics serviceSpecifics) {
    this.serviceSpecifics = serviceSpecifics;
  }

  public void init(final @NotNull ServiceType serviceType) {
    super.init(serviceType);
  }

  @Override
  protected void initHeartbeats() {
    new BukkitHeartbeat(super.cluster, this.serviceSpecifics).startTask();
  }

  @Override
  protected void initMessageHandlers() {

  }

  @Override
  protected String getServiceIP() {
    try {
      final var ip = InetAddress.getLocalHost().getHostAddress();
      final var port = Bukkit.getPort();

      return ip + ":" + port;
    } catch (final Exception e) {
      throw new IllegalStateException("Service failed to get IP");
    }
  }
}
