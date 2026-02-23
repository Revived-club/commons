package club.revived.concordia.provider;

import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

public class InMemoryStorageProvider implements StorageProvider {

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();

    @Override
    public void connect(@NotNull String url) {
        // No connection needed for in-memory
    }

    @NotNull
    @Override
    public CompletableFuture<byte[]> get(String key) {
        return CompletableFuture.completedFuture(storage.get(key));
    }

    @NotNull
    @Override
    public CompletableFuture<Void> set(String key, byte[] value) {
        storage.put(key, value);
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    @Override
    public CompletableFuture<Void> delete(String key) {
        storage.remove(key);
        return CompletableFuture.completedFuture(null);
    }

    public void clear() {
        storage.clear();
    }
}
