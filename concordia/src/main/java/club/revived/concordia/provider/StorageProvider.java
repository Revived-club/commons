package club.revived.concordia.provider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface StorageProvider {

  @NotNull
  void connect(final @NotNull String url);

  @NotNull
  CompletableFuture<byte[]> get(final String key);

  @NotNull
  CompletableFuture<Void> set(final String key, final byte[] value);

  @NotNull
  CompletableFuture<Void> delete(final String key);
}
