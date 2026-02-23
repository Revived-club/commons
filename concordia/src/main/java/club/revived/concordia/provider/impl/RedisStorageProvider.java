package club.revived.concordia.provider.impl;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.concordia.provider.StorageProvider;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;

public final class RedisStorageProvider implements StorageProvider {

    private RedisAsyncCommands<byte[], byte[]> asyncCommands;

  @Override
  public void connect(final @NotNull String url) {
    final RedisClient client = RedisClient.create(url);
      final StatefulRedisConnection<byte[], byte[]> connection = client.connect(ByteArrayCodec.INSTANCE);
    this.asyncCommands = connection.async();
  }

  @Override
  @NotNull
  public CompletableFuture<Void> delete(final @NotNull String key) {
    return asyncCommands.del(key.getBytes())
        .toCompletableFuture()
        .thenApply(deleted -> null);
  }

  @Override
  @NotNull
  public CompletableFuture<byte[]> get(final @NotNull String key) {
    return asyncCommands.get(key.getBytes())
        .toCompletableFuture();
  }

  @Override
  @NotNull
  public CompletableFuture<Void> set(
      final @NotNull String key,
      final byte[] value) {
    return asyncCommands.set(key.getBytes(), value)
        .toCompletableFuture()
        .thenApply(status -> null);
  }
}
