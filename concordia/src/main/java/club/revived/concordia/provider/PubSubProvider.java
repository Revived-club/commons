package club.revived.concordia.provider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

public interface PubSubProvider {

  void connect(final @NotNull String url);

  @NotNull
  CompletableFuture<Void> publish(final @NotNull String channel, final byte[] message);

  @NotNull
  CompletableFuture<Void> subscribe(final @NotNull String channel, final @NotNull Consumer<byte[]> handler);
}
