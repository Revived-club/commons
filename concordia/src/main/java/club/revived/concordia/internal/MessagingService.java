package club.revived.concordia.internal;

import club.revived.concordia.builder.MessageBuilder;
import club.revived.commons.proto.Envelope;
import club.revived.concordia.provider.PubSubProvider;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class MessagingService {

  private final PubSubProvider pubSubProvider;
  private final String serviceId;

  private final Map<UUID, CompletableFuture<Envelope>> pendingAcks = new ConcurrentHashMap<>();
  private final Map<UUID, CompletableFuture<Envelope>> pendingRequests = new ConcurrentHashMap<>();
  private final Map<String, List<Consumer<Envelope>>> handlers = new ConcurrentHashMap<>();

  public MessagingService(
      final @NotNull PubSubProvider pubSubProvider,
      final @NotNull String serviceId) {
    this.pubSubProvider = pubSubProvider;
    this.serviceId = serviceId;

    this.pubSubProvider.subscribe("concordia_global", this::handleIncomingMessage);
    this.pubSubProvider.subscribe("concordia_" + serviceId, this::handleIncomingMessage);
  }

  private void handleIncomingMessage(final byte[] data) {
    try {
      final Envelope envelope = Envelope.parseFrom(data);
      final UUID correlationId = fromProto(envelope.getCorrelationId());

      if (envelope.getIsAck()) {
        final CompletableFuture<Envelope> ackFuture = pendingAcks.remove(correlationId);
        if (ackFuture != null) {
          ackFuture.complete(envelope);
        }
        return;
      }

      final CompletableFuture<Envelope> requestFuture = pendingRequests.remove(correlationId);
      if (requestFuture != null) {
        requestFuture.complete(envelope);
      }

      final boolean isForMe = envelope.getTarget().isEmpty() || envelope.getTarget().equals(serviceId);

      if (isForMe) {
        sendAck(envelope);

        final String type = envelope.getPayloadType();
        if (!type.isEmpty()) {
          final List<Consumer<Envelope>> typeHandlers = handlers.get(type);
          if (typeHandlers != null) {
            for (final Consumer<Envelope> handler : typeHandlers) {
              handler.accept(envelope);
            }
          }
        }
      }

    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void sendAck(final @NotNull Envelope original) {
    final Envelope ack = Envelope.newBuilder()
        .setId(toProto(UUID.randomUUID()))
        .setCorrelationId(original.getId())
        .setTarget("")
        .setSender(serviceId)
        .setIsAck(true)
        .build();
    transmit(ack);
  }

  public void transmit(final @NotNull Envelope envelope) {
    try {
      final String target = envelope.getTarget();
      final String channel = target.isEmpty() ? envelope.getTopic() : envelope.getTarget();
      pubSubProvider.publish(channel, envelope.toByteArray());
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public <T extends MessageLite> void subscribe(
      final @NotNull Class<T> type,
      final @NotNull Consumer<T> handler) {
    try {
      final var parserField = type.getDeclaredField("PARSER");
      parserField.setAccessible(true);
      @SuppressWarnings("unchecked")
      final Parser<T> parser = (Parser<T>) parserField.get(null);
      subscribe(type, parser, handler);
    } catch (final Exception e) {
      throw new RuntimeException("Could not find PARSER for " + type.getName(), e);
    }
  }

  public <T extends MessageLite> void subscribe(
      final @NotNull Class<T> type,
      final @NotNull Parser<T> parser,
      final @NotNull Consumer<T> handler) {
    handlers.computeIfAbsent(type.getName(), _ -> new ArrayList<>()).add(envelope -> {
      try {
        final T message = parser.parseFrom(envelope.getPayload());
        handler.accept(message);
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  public <T extends MessageLite> void subscribe(
      final @NotNull Class<T> type,
      final @NotNull BiConsumer<T, Envelope> handler) {
    try {
      final var parserField = type.getDeclaredField("PARSER");
      parserField.setAccessible(true);
      @SuppressWarnings("unchecked")
      final Parser<T> parser = (Parser<T>) parserField.get(null);
      subscribe(type, parser, handler);
    } catch (final Exception e) {
      throw new RuntimeException("Could not find PARSER for " + type.getName(), e);
    }
  }

  public <T extends MessageLite> void subscribe(
      final @NotNull Class<T> type,
      final @NotNull Parser<T> parser,
      final @NotNull BiConsumer<T, Envelope> handler) {
    handlers.computeIfAbsent(type.getName(), _ -> new ArrayList<>()).add(envelope -> {
      try {
        final T message = parser.parseFrom(envelope.getPayload());
        handler.accept(message, envelope);
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  @NotNull
  public MessageBuilder message(final @NotNull MessageLite message) {
    return new MessageBuilder(this, message);
  }

  @NotNull
  public MessageBuilder reply(
      final @NotNull Envelope original,
      final @NotNull MessageLite response) {
    return this.message(response)
        .toService(original.getSender())
        .setCorrelationId(fromProto(original.getId()));
  }

  public void registerAck(
      final @NotNull UUID id,
      final @NotNull CompletableFuture<Envelope> future) {
    pendingAcks.put(id, future);
  }

  public void registerRequest(
      final @NotNull UUID id,
      final @NotNull CompletableFuture<Envelope> future) {
    pendingRequests.put(id, future);
  }

  public void removePending(final @NotNull UUID id) {
    pendingAcks.remove(id);
    pendingRequests.remove(id);
  }

  @NotNull
  private club.revived.commons.proto.UUID toProto(final @NotNull UUID uuid) {
    return club.revived.commons.proto.UUID.newBuilder()
        .setValue(uuid.toString())
        .build();
  }

  @NotNull
  private UUID fromProto(final @NotNull club.revived.commons.proto.UUID proto) {
    return UUID.fromString(proto.getValue());
  }
}
