package club.revived.concordia.provider;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class InMemoryPubSubProvider implements PubSubProvider {

    private final Map<String, List<Consumer<byte[]>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void connect(@NotNull String url) {
        // No connection needed for in-memory
    }

    @NotNull
    @Override
    public CompletableFuture<Void> publish(@NotNull String channel, byte[] message) {
        List<Consumer<byte[]>> channelSubscribers = subscribers.get(channel);
        if (channelSubscribers != null) {
            for (Consumer<byte[]> subscriber : channelSubscribers) {
                subscriber.accept(message);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    @Override
    public CompletableFuture<Void> subscribe(@NotNull String channel, @NotNull Consumer<byte[]> handler) {
        subscribers.computeIfAbsent(channel, _ -> new ArrayList<>()).add(handler);
        return CompletableFuture.completedFuture(null);
    }

    public void clear() {
        subscribers.clear();
    }
}
