package club.revived.concordia.storage;

import club.revived.commons.proto.Options;
import club.revived.concordia.Concordia;
import club.revived.concordia.annotation.Syncable;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SyncableObject<T> {

    @NotNull
    private final String key;

    @Nullable
    private final Parser<T> parser;

    @NotNull
    private final Class<T> type;

    @NotNull
    private final T defaultInstance;

    @Nullable
    private T current;

    @Nullable
    private Consumer<T> onUpdate;

    public SyncableObject(
            final @NotNull String key,
            final @Nullable Parser<T> parser,
            final @NotNull Class<T> type,
            final @NotNull T defaultInstance
    ) {
        this.key = key;
        this.parser = parser;
        this.type = type;
        this.defaultInstance = defaultInstance;
        this.initialize();
    }

    public SyncableObject(
            final @NotNull Class<T> type,
            final @Nullable Parser<T> parser
    ) {
        this.type = type;
        this.parser = parser;
        this.defaultInstance = this.resolveDefaultInstance(type);
        this.key = this.resolveKey(type);
        this.initialize();
    }

    public SyncableObject(
            final @NotNull Class<T> type
    ) {
        this(type, null);
    }

    private void initialize() {
        Concordia.instance().watcher().watch(this.key, this.type, this.parser, value -> {
            this.current = value;
            if (this.onUpdate != null) {
                this.onUpdate.accept(value);
            }
        });
        this.load();
    }

    @NotNull
    private T resolveDefaultInstance(final @NotNull Class<T> type) {
        if (Message.class.isAssignableFrom(type)) {
            try {
                //noinspection unchecked
                return (T) type.getMethod("getDefaultInstance").invoke(null);
            } catch (final Exception e) {
                throw new RuntimeException("Could not resolve default instance for " + type.getName(), e);
            }
        }
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            throw new RuntimeException("Could not create default instance for " + type.getName() + ". Ensure it has a no-args constructor.", e);
        }
    }

    private String resolveKey(final @NotNull Class<T> type) {
        if (type.isAnnotationPresent(Syncable.class)) {
            final String value = type.getAnnotation(Syncable.class).value();
            if (!value.isEmpty()) {
                return value;
            }
        }

        if (this.defaultInstance instanceof Message message) {
            try {
                final String kvKey = message.getDescriptorForType().getOptions().getExtension(Options.kvStoreKey);
                if (!kvKey.isEmpty()) {
                    return kvKey;
                }
            } catch (final Exception ignored) {
            }
        }

        throw new IllegalArgumentException("Could not resolve key for " + type.getName());
    }

    public void load() {
        Concordia.instance().cacheManager().read(this.key, this.type, this.parser).thenAccept(value -> {
            if (value != null) {
                this.current = value;
                if (this.onUpdate != null) {
                    this.onUpdate.accept(value);
                }
            } else {
                this.update(this.defaultInstance);
            }
        });
    }

    @NotNull
    public final String key() {
        return this.key;
    }

    @NotNull
    public T cached() {
        if (this.current == null) {
            return this.defaultInstance;
        }
        return this.current;
    }

    @Nullable
    public T get() {
        return this.current;
    }

    public void update(final @NotNull T value) {
        this.current = value;
        Concordia.instance().cacheManager().write(this.key, value);
    }

    public void editAndSave(final @NotNull Consumer<T> editor) {
        final T model = this.cached();
        if (model instanceof Message message) {
            final Message.Builder builder = message.toBuilder();
            //noinspection unchecked
            editor.accept((T) builder);
            //noinspection unchecked
            this.update((T) builder.build());
        } else {
            editor.accept(model);
            this.update(model);
        }
    }

    public void onUpdate(final @NotNull Consumer<T> onUpdate) {
        this.onUpdate = onUpdate;
    }
}
