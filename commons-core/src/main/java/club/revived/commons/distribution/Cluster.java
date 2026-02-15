package club.revived.commons.distribution;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.game.GameService;
import club.revived.commons.distribution.game.ProxyService;
import club.revived.commons.distribution.kvbus.providers.broker.MessageBroker;
import club.revived.commons.distribution.kvbus.providers.cache.DistributedCacheStore;
import club.revived.commons.distribution.kvbus.pubsub.ServiceMessageBus;
import club.revived.commons.distribution.service.Service;
import club.revived.commons.distribution.service.ServiceStatus;
import club.revived.commons.distribution.service.ServiceType;
import club.revived.commons.distribution.status.StatusService;

public final class Cluster {

  private static Cluster instance;

  @NotNull
  private final String serviceId;

  @NotNull
  private final ServiceType serviceType;

  @NotNull
  private final MessageBroker broker;
  @NotNull
  private final DistributedCacheStore globalCache;

  @NotNull
  private final ServiceMessageBus messagingService;

  @NotNull
  private final Map<String, Service> services = new ConcurrentHashMap<>();

  @NotNull
  private final String ip;

  private final boolean statusService;

  private ServiceStatus status = ServiceStatus.STARTING;

  public Cluster(
      final @NotNull MessageBroker broker,
      final @NotNull DistributedCacheStore cache,
      final @NotNull ServiceType serviceType,
      final @NotNull String serviceIp,
      final @NotNull String id) {
    this(broker, cache, serviceType, serviceIp, id, true);
  }

  public Cluster(
      final @NotNull MessageBroker broker,
      final @NotNull DistributedCacheStore cache,
      final @NotNull ServiceType serviceType,
      final @NotNull String serviceIp,
      final @NotNull String id,
      final boolean statusService) {
    this.broker = broker;
    this.serviceType = serviceType;
    this.serviceId = id;
    this.ip = serviceIp;
    this.messagingService = new ServiceMessageBus(broker, id);
    this.globalCache = cache;
    this.statusService = statusService;

    instance = this;
  }

  public void init() {
    if (this.statusService) {
      new StatusService(this.messagingService);
    }

    // TODO: implement rest of initialization logic
  }

  public <T> void send(
      final String id,
      final T message) {
    this.broker.publish(id, message);
  }

  @NotNull
  public GameService getLeastLoadedProxy(final ServiceType serviceType) {
    final var services = this.services.values()
        .stream()
        .filter(clusterService -> clusterService.getType() == serviceType)
        .filter(clusterService -> clusterService instanceof ProxyService)
        .map(clusterService -> (GameService) clusterService)
        .sorted(Comparator.comparingInt(service -> service.getOnlinePlayers().size()))
        .toList();

    return services.getFirst();
  }

  @NotNull
  public GameService getLeastLoadedGameServer(final ServiceType serviceType) {
    final var services = this.services.values()
        .stream()
        .filter(clusterService -> clusterService.getType() == serviceType)
        .filter(clusterService -> clusterService instanceof GameService)
        .map(clusterService -> (GameService) clusterService)
        .sorted(Comparator.comparingInt(service -> service.getOnlinePlayers().size()))
        .toList();

    return services.getFirst();
  }

  public @NotNull String getIp() {
    return ip;
  }

  public @NotNull MessageBroker getBroker() {
    return broker;
  }

  public @NotNull DistributedCacheStore getGlobalCache() {
    return globalCache;
  }

  public @NotNull ServiceMessageBus getMessagingService() {
    return messagingService;
  }

  public @NotNull Map<String, Service> getServices() {
    return services;
  }

  public @NotNull ServiceType getServiceType() {
    return serviceType;
  }

  public @NotNull String getServiceId() {
    return serviceId;
  }

  public static Cluster getInstance() {
    if (instance == null) {
      throw new UnsupportedOperationException("There is no cluster registered!");
    }

    return instance;
  }

  public ServiceStatus getStatus() {
    return status;
  }

  public void setStatus(final @NotNull ServiceStatus status) {
    this.status = status;
  }
}
