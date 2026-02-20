package club.revived.concordia.api;

import club.revived.concordia.builder.MessageBuilder;
import club.revived.concordia.internal.CachingService;
import club.revived.concordia.internal.HeartbeatService;
import club.revived.concordia.internal.MessagingService;
import club.revived.commons.proto.Envelope;
import club.revived.commons.proto.Heartbeat;
import club.revived.concordia.provider.PubSubProvider;
import club.revived.concordia.provider.StorageProvider;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class Concordia {

  private static Concordia instance;

  private final String serviceId;
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  private final MessagingService messagingService;
  private final HeartbeatService heartbeatService;
  private final CachingService cachingService;

  private Concordia(
          final @NotNull PubSubProvider pubSubProvider,
          final @NotNull StorageProvider storageProvider,
          final @NotNull String serviceId) {
    this.serviceId = serviceId;

    this.messagingService = new MessagingService(pubSubProvider, serviceId);
    this.heartbeatService = new HeartbeatService(this, serviceId, scheduler);
    this.cachingService = new CachingService(this, storageProvider);
  }

  @NotNull
  public MessageBuilder message(final @NotNull MessageLite message) {
    return messagingService.message(message);
  }

  @NotNull
  public MessageBuilder reply(
          final @NotNull Envelope original,
          final @NotNull MessageLite response) {
    return messagingService.reply(original, response);
  }

  public void transmit(final @NotNull Envelope envelope) {
    messagingService.transmit(envelope);
  }

  public <T extends MessageLite> void subscribe(
          final @NotNull Class<T> type,
          final @NotNull Consumer<T> handler) {
    messagingService.subscribe(type, handler);
  }

  public <T extends MessageLite> void subscribe(
          final @NotNull Class<T> type,
          final @NotNull Parser<T> parser,
          final @NotNull Consumer<T> handler) {
    messagingService.subscribe(type, parser, handler);
  }

  public <T extends MessageLite> void subscribe(
          final @NotNull Class<T> type,
          final @NotNull BiConsumer<T, Envelope> handler) {
    messagingService.subscribe(type, handler);
  }

  public <T extends MessageLite> void subscribe(
          final @NotNull Class<T> type,
          final @NotNull Parser<T> parser,
          final @NotNull BiConsumer<T, Envelope> handler) {
    messagingService.subscribe(type, parser, handler);
  }

  public void registerAck(
          final @NotNull UUID id,
          final @NotNull CompletableFuture<Envelope> future) {
    messagingService.registerAck(id, future);
  }

  public void registerRequest(
          final @NotNull UUID id,
          final @NotNull CompletableFuture<Envelope> future) {
    messagingService.registerRequest(id, future);
  }

  public void removePending(final @NotNull UUID id) {
    messagingService.removePending(id);
  }

  @NotNull
  public List<String> getActiveServices() {
    return heartbeatService.getActiveServices();
  }

  public void onServiceInvalidated(final @NotNull Consumer<String> callback) {
    heartbeatService.setOnServiceInvalidated(callback);
  }

  public void watch(
          final @NotNull String key,
          final @NotNull BiConsumer<String, byte[]> callback) {
    cachingService.watch(key, callback);
  }

  public <T extends MessageLite> void watch(
          final @NotNull String key,
          final @NotNull Class<T> type,
          final @NotNull Consumer<T> handler) {
    cachingService.watch(key, type, handler);
  }

  public void updateSyncable(
          final @NotNull String key,
          final byte[] value) {
    cachingService.updateSyncable(key, value);
  }

  @NotNull
  public String getServiceId() {
    return serviceId;
  }

  public void shutdown() {
    scheduler.shutdownNow();
    instance = null;
  }

  @NotNull
  public static Concordia instance() {
    if (instance == null) {
      throw new IllegalStateException("Concordia has not been initialized");
    }
    return instance;
  }

  @NotNull
  public static Concordia init(
          final @NotNull PubSubProvider pubSubProvider,
          final @NotNull StorageProvider storageProvider,
          final @NotNull String serviceId) {
    if (instance != null) {
      throw new IllegalStateException("already initialized");
    }
    instance = new Concordia(pubSubProvider, storageProvider, serviceId);
    return instance;
  }

  @Deprecated
  @NotNull
  public static Concordia init(
          final @NotNull PubSubProvider pubSubProvider,
          final @NotNull StorageProvider storageProvider,
          final @NotNull String serviceId,
          final @NotNull Supplier<Heartbeat> heartbeatSupplier) {
    return init(pubSubProvider, storageProvider, serviceId);
  }
}