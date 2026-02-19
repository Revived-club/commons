package club.revived.concordia.builder;

import club.revived.commons.proto.UUID;
import club.revived.concordia.api.Concordia;
import club.revived.concordia.internal.MessagingService;
import club.revived.commons.proto.Envelope;
import com.google.protobuf.MessageLite;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class MessageBuilder {
  private final MessagingService api;
  private final MessageLite message;
  private String targetService;
  private java.util.UUID correlationId;
  private long timeout = 30;
  private TimeUnit unit = TimeUnit.SECONDS;
  private Consumer<Throwable> onTimeout;

  public MessageBuilder(final @NotNull MessagingService api, final @NotNull MessageLite message) {
    this.api = api;
    this.message = message;
    this.correlationId = java.util.UUID.randomUUID();
  }

  @NotNull
  public MessageBuilder toService(final @NotNull String service) {
    this.targetService = service;
    return this;
  }

  @NotNull
  public MessageBuilder withCorrelationId(final @NotNull java.util.UUID correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  @NotNull
  public MessageBuilder setCorrelationId(final @NotNull java.util.UUID correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  @NotNull
  public MessageBuilder timeout(final long timeout, final @NotNull TimeUnit unit) {
    this.timeout = timeout;
    this.unit = unit;
    return this;
  }

  @NotNull
  public MessageBuilder onTimeout(final @NotNull Consumer<Throwable> onTimeout) {
    this.onTimeout = onTimeout;
    return this;
  }

  @NotNull
  public CompletableFuture<Envelope> now() {
    final var env = buildEnvelope(false);
    final var id = fromProto(env.getId());
    final var future = new CompletableFuture<Envelope>();
    api.registerAck(id, future);

    future.orTimeout(timeout, unit).whenComplete((v, t) -> {
      if (t != null) {
        api.removePending(id);
        if (onTimeout != null) {
          onTimeout.accept(t);
        }
      }
    });

    api.transmit(env);
    return future;
  }

  @NotNull
  public CompletableFuture<Envelope> request() {
    final var env = buildEnvelope(true);
    final var id = fromProto(env.getId());
    final var future = new CompletableFuture<Envelope>();

    api.registerRequest(id, future);

    future.orTimeout(timeout, unit).whenComplete((v, t) -> {
      if (t != null) {
        api.removePending(id);
        if (onTimeout != null) {
          onTimeout.accept(t);
        }
      }
    });

    api.transmit(env);
    return future;
  }

  @NotNull
  private Envelope buildEnvelope(final boolean isRequest) {
    final var id = java.util.UUID.randomUUID();
    return Envelope.newBuilder()
        .setId(toProto(id))
        .setCorrelationId(toProto(this.correlationId))
        .setTarget(this.targetService == null ? "" : this.targetService)
        .setPayload(this.message.toByteString())
        .setPayloadType(this.message.getClass().getName())
        .setSender(Concordia.instance().getServiceId())
        .setIsRequest(isRequest)
        .setIsAck(false)
        .build();
  }

  private UUID toProto(java.util.UUID uuid) {
    return UUID.newBuilder()
            .setValue(uuid.toString())
        .build();
  }

  private java.util.UUID fromProto(UUID proto) {
    return java.util.UUID.fromString(proto.getValue());
  }
}
