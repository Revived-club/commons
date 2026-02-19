package club.revived.concordia.internal;

import club.revived.concordia.api.Concordia;
import club.revived.commons.proto.SyncUpdate;
import club.revived.concordia.provider.StorageProvider;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class CachingService {

    private final Concordia concordia;
    private final StorageProvider storageProvider;
    private final Map<String, List<BiConsumer<String, byte[]>>> watchers = new ConcurrentHashMap<>();

    public CachingService(
            final @NotNull Concordia concordia,
            final @NotNull StorageProvider storageProvider) {
        this.concordia = concordia;
        this.storageProvider = storageProvider;

        this.concordia.subscribe(SyncUpdate.class, this::handleSyncUpdate);
    }

    private void handleSyncUpdate(final @NotNull SyncUpdate update) {
        final String key = update.getKey();
        final byte[] value = update.getValue().toByteArray();

        final List<BiConsumer<String, byte[]>> keyWatchers = watchers.get(key);

        if (keyWatchers != null) {
            for (var watcher : keyWatchers) {
                watcher.accept(key, value);
            }
        }
    }

    public void watch(
            final @NotNull String key,
            final @NotNull BiConsumer<String, byte[]> callback) {
        watchers.computeIfAbsent(key, _ -> new ArrayList<>()).add(callback);

        storageProvider.get(key).thenAccept(value -> {
            if (value != null) {
                callback.accept(key, value);
            }
        });
    }

    public <T extends MessageLite> void watch(
            final @NotNull String key,
            final @NotNull Class<T> type,
            final @NotNull Consumer<T> handler) {
        try {
            final var parserField = type.getDeclaredField("PARSER");
            parserField.setAccessible(true);

            @SuppressWarnings("unchecked")
            final Parser<T> parser = (Parser<T>) parserField.get(null);

            this.watch(key, (_, v) -> {
                try {
                    handler.accept(parser.parseFrom(v));
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (final Exception e) {
            throw new RuntimeException("Could not find PARSER for " + type.getName(), e);
        }
    }

    public void updateSyncable(
            final @NotNull String key,
            final byte[] value) {
        storageProvider.set(key, value);
        final SyncUpdate update = SyncUpdate.newBuilder()
                .setKey(key)
                .setValue(ByteString.copyFrom(value))
                .build();

        concordia.message(update).now();
    }
}
