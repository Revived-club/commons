package club.revived.concordia.internal;

import club.revived.concordia.api.Concordia;
import club.revived.commons.proto.Envelope;
import club.revived.commons.proto.Heartbeat;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class HeartbeatService {

    private final Concordia concordia;
    private final String serviceId;
    private final Map<String, Long> activeServices = new ConcurrentHashMap<>();
    private Consumer<String> onServiceInvalidated;

    public HeartbeatService(@NotNull Concordia concordia, @NotNull String serviceId, @NotNull ScheduledExecutorService scheduler) {
        this.concordia = concordia;
        this.serviceId = serviceId;

        this.concordia.subscribe(Heartbeat.class, this::handleHeartbeat);
        scheduler.scheduleAtFixedRate(this::sendHeartbeat, 0, 5, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::cleanupServices, 10, 10, TimeUnit.SECONDS);
    }

    private void handleHeartbeat(final Heartbeat heartbeat, final Envelope envelope) {
        this.activeServices.put(envelope.getSender(), System.currentTimeMillis());
    }

    private void sendHeartbeat() {
        final Heartbeat heartbeat = Heartbeat.newBuilder()
                .setId(serviceId)
                .setTimestamp(System.currentTimeMillis())
                .build();
        this.concordia.message(heartbeat).now();
    }

    private void cleanupServices() {
        final long now = System.currentTimeMillis();
        activeServices.entrySet().removeIf(entry -> {
            if (now - entry.getValue() > 15000) {
                if (onServiceInvalidated != null) {
                    onServiceInvalidated.accept(entry.getKey());
                }
                return true;
            }
            return false;
        });
    }

    @NotNull
    public List<String> getActiveServices() {
        return new ArrayList<>(activeServices.keySet());
    }

    public void setOnServiceInvalidated(final @NotNull Consumer<String> onServiceInvalidated) {
        this.onServiceInvalidated = onServiceInvalidated;
    }
}
