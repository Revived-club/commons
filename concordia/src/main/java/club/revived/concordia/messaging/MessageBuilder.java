package club.revived.concordia.messaging;

import club.revived.commons.proto.Envelope;
import club.revived.commons.proto.UUID;
import club.revived.concordia.Concordia;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.function.Consumer;

public final class MessageBuilder<T extends Message> {

    @NotNull
    private final T payload;

    private @Nullable String channel;
    private @Nullable String target;
    private @Nullable Duration timeout;
    private @Nullable Runnable onTimeout;
    private @Nullable Consumer<Envelope> onAck;

    private MessageBuilder(final @NotNull T payload) {
        this.payload = payload;
    }

    @NotNull
    public static <T extends Message> MessageBuilder<T> of(final @NotNull T payload) {
        return new MessageBuilder<>(payload);
    }

    @NotNull
    public MessageBuilder<T> channel(final @NotNull String channel) {
        this.channel = channel;
        return this;
    }

    @NotNull
    public MessageBuilder<T> target(final @NotNull String target) {
        this.target = target;
        return this;
    }

    @NotNull
    public MessageBuilder<T> timeout(final @NotNull Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    @NotNull
    public MessageBuilder<T> onTimeout(final @NotNull Runnable onTimeout) {
        this.onTimeout = onTimeout;
        return this;
    }

    @NotNull
    public MessageBuilder<T> onAck(final @NotNull Consumer<Envelope> onAck) {
        this.onAck = onAck;
        return this;
    }

    public void send() {
        if (this.channel == null) {
            throw new IllegalStateException("Channel must be set");
        }

        final var correlationId = java.util.UUID.randomUUID().toString();
        final var envelope = Envelope.newBuilder()
                .setCorrelationId(UUID.newBuilder().setValue(correlationId).build())
                .setSender(Concordia.instance().nodeId())
                .setTarget(this.target != null ? this.target : "*")
                .setPayload(this.payload.toByteString())
                .setIsRequest(this.onAck != null)
                .build();

        if (this.onAck != null) {
            if (this.timeout == null) {
                throw new IllegalStateException("Timeout must be set for Acknowledgements");
            }
            if (this.onTimeout == null) {
                throw new IllegalStateException("onTimeout must be set for Acknowledgements");
            }
            Concordia.instance().messageManager().registerAck(
                    correlationId,
                    this.timeout,
                    this.onTimeout,
                    this.onAck
            );
            Concordia.instance().subscribe(this.channel);
        }

        Concordia.instance().pubSubProvider().publish(this.channel, envelope.toByteArray());
    }
}
