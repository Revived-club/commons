package club.revived.concordia.storage;

import club.revived.commons.proto.Cachable;
import club.revived.concordia.Concordia;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CacheManager {


    @NotNull
    public <T> CompletableFuture<Void> write(
            final @NotNull String key,
            final @NotNull T value
    ) {
        final byte[] data;
        final byte[] updatePayload;

        if (value instanceof Message message) {
            final Cachable cachable = Cachable.newBuilder()
                    .setKey(key)
                    .setValue(Base64.getEncoder().encodeToString(message.toByteArray()))
                    .build();
            data = cachable.toByteArray();
            updatePayload = message.toByteArray();
        } else {
            final String json = Concordia.instance().gson().toJson(value);
            final Cachable cachable = Cachable.newBuilder()
                    .setKey(key)
                    .setValue(json)
                    .build();
            data = cachable.toByteArray();
            updatePayload = json.getBytes();
        }

        return Concordia.instance().storageProvider().set(key, data)
                .thenRun(() -> Concordia.instance().pubSubProvider().publish(key + ":updates", updatePayload));
    }

    @NotNull
    public <T> CompletableFuture<@Nullable T> read(
            final @NotNull String key,
            final @NotNull Class<T> type,
            final @Nullable Parser<T> parser
    ) {
        return Concordia.instance().storageProvider().get(key).thenApply(data -> {
            if (data == null) {
                return null;
            }
            try {
                final Cachable cachable = Cachable.parseFrom(data);
                if (Message.class.isAssignableFrom(type)) {
                    if (parser == null) {
                        throw new IllegalArgumentException("Parser is required for Protobuf messages");
                    }
                    final byte[] payload = Base64.getDecoder().decode(cachable.getValue());
                    return parser.parseFrom(payload);
                } else {
                    return Concordia.instance().gson().fromJson(cachable.getValue(), type);
                }
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @NotNull
    public CompletableFuture<Void> batchWrite(
            final @NotNull List<Entry<?>> entries
    ) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (final Entry<?> entry : entries) {
            futures.add(this.write(entry.key(), entry.value()));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public record Entry<T>(@NotNull String key, @NotNull T value) {}
}
