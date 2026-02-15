package club.revived.commons.distribution.service;

import org.jetbrains.annotations.NotNull;

public abstract class Service {

  private final String id;
  private final String ip;
  private final ServiceType type;

  public Service(final @NotNull String id, final @NotNull String ip, final @NotNull ServiceType type) {
    this.id = id;
    this.ip = ip;
    this.type = type;
  }

  @NotNull
  public String getId() {
    return id;
  }

  @NotNull
  public String getIp() {
    return ip;
  }

  @NotNull
  public ServiceType getType() {
    return type;
  }
}
