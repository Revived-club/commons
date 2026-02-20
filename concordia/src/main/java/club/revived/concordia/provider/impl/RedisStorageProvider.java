package club.revived.concordia.provider.impl;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.concordia.provider.StorageProvider;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;

public final class RedisStorageProvider implements StorageProvider {

  private StatefulRedisConnection<byte[], byte[]> connection;
  private RedisAsyncCommands<byte[], byte[]> asyncCommands;

  @Override
  public void connect(final @NotNull String url) {
    RedisClient client = RedisClient.create(url);
    this.connection = client.connect(ByteArrayCodec.INSTANCE);
    this.asyncCommands = connection.async();
  }

  @Override
  public @NotNull CompletableFuture<Void> delete(final @NotNull String key) {
    return asyncCommands.del(key.getBytes())
        .toCompletableFuture()
        .thenApply(deleted -> null);
  }

  @Override
  public @NotNull CompletableFuture<byte[]> get(final @NotNull String key) {
    return asyncCommands.get(key.getBytes())
        .toCompletableFuture();
  }

  @Override
  public @NotNull CompletableFuture<Void> set(
      final @NotNull String key,
      final byte[] value) {
    return asyncCommands.set(key.getBytes(), value)
        .toCompletableFuture()
        .thenApply(status -> null);
  }
}
