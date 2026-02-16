package club.revived.commons.distribution.game;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.service.Service;
import club.revived.commons.distribution.service.ServiceSpecifics;
import club.revived.commons.distribution.service.ServiceType;

public final class ProxyService extends Service {

  public ProxyService(@NotNull String id, @NotNull String ip) {
    super(id, ip, ServiceType.PROXY, ServiceSpecifics.builder().build());
  }
}
