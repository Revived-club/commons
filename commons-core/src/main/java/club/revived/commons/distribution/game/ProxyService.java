package club.revived.commons.distribution.game;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.service.Service;
import club.revived.commons.distribution.service.ServiceSpecifics;
import club.revived.commons.distribution.service.ServiceStatus;
import club.revived.commons.distribution.service.ServiceType;

public final class ProxyService extends Service {

  public ProxyService(
      final @NotNull String id,
      final @NotNull String ip,
      final @NotNull ServiceStatus status) {
    super(
        id,
        ip,
        ServiceType.PROXY,
        status,
        ServiceSpecifics.builder().build());
  }
}
