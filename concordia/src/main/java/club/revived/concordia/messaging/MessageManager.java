package club.revived.concordia.messaging;

import club.revived.commons.proto.Envelope;
import club.revived.concordia.Concordia;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class MessageManager {

    private final Map<String, AckHandler> pendingAcks = new ConcurrentHashMap<>();
    private final Map<Class<? extends Message>, List<Handler<?>>> handlers = new ConcurrentHashMap<>();

    public <T extends Message> void registerHandler(
            final @NotNull Class<T> type,
            final @NotNull Parser<T> parser,
            final @NotNull Consumer<T> consumer
    ) {
        this.handlers.computeIfAbsent(type, _ -> new ArrayList<>()).add(new Handler<>(parser, consumer));
    }

    public void registerAck(
            final @NotNull String correlationId,
            final @NotNull Duration timeout,
            final @NotNull Runnable onTimeout,
            final @NotNull Consumer<Envelope> onAck
    ) {
        final ScheduledFuture<?> timeoutTask = Concordia.instance().scheduler().schedule(() -> {
            final AckHandler handler = this.pendingAcks.remove(correlationId);
            if (handler != null) {
                onTimeout.run();
            }
        }, timeout.toMillis(), TimeUnit.MILLISECONDS);

        this.pendingAcks.put(correlationId, new AckHandler(onAck, timeoutTask));
    }

    public void handleIncoming(
            final @NotNull String channel,
            final byte[] data
    ) {
        try {
            final Envelope envelope = Envelope.parseFrom(data);

            if (!envelope.getTarget().equals("*") && !envelope.getTarget().equals(Concordia.instance().nodeId())) {
                return;
            }

            if (envelope.getIsAck()) {
                final String correlationId = envelope.getCorrelationId().getValue();
                final AckHandler handler = this.pendingAcks.remove(correlationId);

                if (handler != null) {
                    handler.timeoutTask().cancel(false);
                    handler.consumer().accept(envelope);
                }
                return;
            }

            for (final Map.Entry<Class<? extends Message>, List<Handler<?>>> entry : this.handlers.entrySet()) {
                for (final Handler<?> handler : entry.getValue()) {
                    try {
                        final Message message = handler.parser().parseFrom(envelope.getPayload());
                        if (message.getClass().equals(entry.getKey())) {
                            //noinspection unchecked
                            ((Consumer<Message>) handler.consumer()).accept(message);
                        }
                    } catch (final Exception ignored) {
                    }
                }
            }

            if (envelope.getIsRequest()) {
                final Envelope ack = Envelope.newBuilder()
                        .setCorrelationId(envelope.getCorrelationId())
                        .setSender(Concordia.instance().nodeId())
                        .setTarget(envelope.getSender())
                        .setIsAck(true)
                        .build();

                Concordia.instance().pubSubProvider().publish(channel, ack.toByteArray());
            }

        } catch (final Exception ignored) {
        }
    }

    private record AckHandler(
            @NotNull Consumer<Envelope> consumer,
            @NotNull ScheduledFuture<?> timeoutTask
    ) {
    }

    private record Handler<T extends Message>(
            @NotNull Parser<T> parser,
            @NotNull Consumer<T> consumer
    ) {
    }
}
