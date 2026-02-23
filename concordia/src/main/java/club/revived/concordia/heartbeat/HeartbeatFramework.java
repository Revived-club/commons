package club.revived.concordia.heartbeat;

import club.revived.commons.proto.ServiceState;
import club.revived.concordia.Concordia;
import club.revived.concordia.messaging.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class HeartbeatFramework {

    @NotNull
    private final String channel;

    @NotNull
    private final Supplier<ServiceState> stateSupplier;

    @NotNull
    private final Duration interval;

    public HeartbeatFramework(
            final @NotNull String channel,
            final @NotNull Supplier<ServiceState> stateSupplier,
            final @NotNull Duration interval
    ) {
        this.channel = channel;
        this.stateSupplier = stateSupplier;
        this.interval = interval;
    }

    public void start() {
        Concordia.instance().scheduler().scheduleAtFixedRate(
                this::sendHeartbeat,
                0,
                this.interval.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    private void sendHeartbeat() {
        final ServiceState state = this.stateSupplier.get();
        MessageBuilder.of(state)
                .channel(this.channel)
                .send();
    }
}
