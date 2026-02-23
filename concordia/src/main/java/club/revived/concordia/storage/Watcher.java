package club.revived.concordia.storage;

import club.revived.concordia.Concordia;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public final class Watcher {

    public <T> void watch(
            final @NotNull String key,
            final @NotNull Class<T> clazz,
            final @Nullable Parser<T> parser,
            final @NotNull Consumer<T> callback
    ) {
        if (clazz.isAssignableFrom(MessageLite.class)) {
            Objects.requireNonNull(parser);

            this.watch(key, parser, callback);
            return;
        }

        this.watch(key, clazz, callback);
    }


    public <T> void watch(
            final @NotNull String key,
            final @NotNull Parser<T> parser,
            final @NotNull Consumer<T> callback
    ) {
        Concordia.instance().pubSubProvider().subscribe(key + ":updates", data -> {
            try {
                final T message = parser.parseFrom(data);
                callback.accept(message);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> void watch(
            final @NotNull String key,
            final @NotNull Class<T> type,
            final @NotNull Consumer<T> callback
    ) {
        Concordia.instance().pubSubProvider().subscribe(key + ":updates", data -> {
            try {
                final String json = new String(data);
                final T object = Concordia.instance().gson().fromJson(json, type);
                callback.accept(object);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
