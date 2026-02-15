package club.revived.commons.distribution.heartbeat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.kvbus.pubsub.MessageHandler;
import club.revived.commons.distribution.service.ServiceFactory;

public abstract class HeartbeatService implements MessageHandler<Heartbeat> {

  public final static long INTERVAL = 1000;
  private final static long TIMEOUT = 5000;

  private final Map<String, Long> lastSeen = new ConcurrentHashMap<>();
  public final ScheduledExecutorService subServer = Executors.newScheduledThreadPool(1);

  public HeartbeatService() {
    this.startTask();
  }

  @NotNull
  public abstract ScheduledFuture<?> startTask();

  @NotNull
  public ScheduledFuture<?> startTimeoutTask() {
    return this.subServer.scheduleAtFixedRate(() -> {
      final var now = System.currentTimeMillis();

      this.lastSeen.entrySet().removeIf(entry -> {
        if (now - entry.getValue() > TIMEOUT) {
          Cluster.getInstance().getServices().remove(entry.getKey());
          return true;
        }
        return false;
      });
    }, 0, TIMEOUT, TimeUnit.MILLISECONDS);
  }

  @Override
  public void handle(final Heartbeat message) {
    final var service = ServiceFactory.createService(
        message.id(),
        message.serverIp(),
        message.serviceType());

    final var now = System.currentTimeMillis();

    this.lastSeen.put(message.id(), now);
    Cluster.getInstance().getServices().put(message.id(), service);
  }
}
