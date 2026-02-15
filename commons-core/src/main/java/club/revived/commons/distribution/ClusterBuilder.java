package club.revived.commons.distribution;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.kvbus.providers.broker.RedisBroker;
import club.revived.commons.distribution.kvbus.providers.cache.RedisCacheStore;
import club.revived.commons.distribution.service.ServiceType;

public final class ClusterBuilder {

  private boolean statusService = true;
  private ServiceType serviceType = ServiceType.MICROSERVICE;
  private String id = "server";
  private String ip = "localhost";
  private String kvbusHost = "localhost";
  private String kvbusPass = "";
  private int kvbusPort = 30000;

  public static ClusterBuilder newCluster() {
    return new ClusterBuilder();
  }

  @NotNull
  public ClusterBuilder statusService(final boolean statusService) {
    this.statusService = statusService;
    return this;
  }

  @NotNull
  public ClusterBuilder serviceType(final ServiceType serviceType) {
    this.serviceType = serviceType;
    return this;
  }

  @NotNull
  public ClusterBuilder id(final String id) {
    this.id = id;
    return this;
  }

  @NotNull
  public ClusterBuilder ip(final String ip) {
    this.ip = ip;
    return this;
  }

  @NotNull
  public ClusterBuilder kvbusHost(final String kvbusHost) {
    this.kvbusHost = kvbusHost;
    return this;
  }

  @NotNull
  public ClusterBuilder kvbusPort(final int kvbusPort) {
    this.kvbusPort = kvbusPort;
    return this;
  }

  @NotNull
  public ClusterBuilder kvbusPass(final String pass) {
    this.kvbusPass = pass;
    return this;
  }

  @NotNull
  public Cluster build() {
    final var broker = new RedisBroker(this.kvbusHost, this.kvbusPort, this.kvbusPass);
    final var cache = new RedisCacheStore(this.kvbusHost, this.kvbusPort, this.kvbusPass);

    return new Cluster(
        broker,
        cache,
        this.serviceType,
        this.ip,
        this.id,
        statusService);
  }

  @NotNull
  public static Cluster fromEnv(final ServiceType serviceType, final String ip, final boolean statusService) {
    final String hostName = System.getenv("HOSTNAME");
    final String host = System.getenv("REDIS_HOST");
    final int port = Integer.parseInt(System.getenv("REDIS_PORT"));

    final var broker = new RedisBroker(host, port, "");
    final var cache = new RedisCacheStore(host, port, "");

    return new Cluster(broker, cache, serviceType, ip, hostName, statusService);
  }
}
