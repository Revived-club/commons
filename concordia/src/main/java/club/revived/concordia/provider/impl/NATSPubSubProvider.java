package club.revived.concordia.provider.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import club.revived.concordia.provider.PubSubProvider;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;

public final class NATSPubSubProvider implements PubSubProvider {

  private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
  private Connection connection;

  @Override
  public void connect(final @NotNull String url) {
    try {
      final var options = new Options.Builder()
          .server(url)
          .connectExecutor(executorService)
          .connectionListener((conn, event) -> {
            System.out.println("NATS connection event: " + event);
          }).build();

      this.connection = Nats.connect(options);

    } catch (final Exception e) {
      throw new RuntimeException("Failed to connect to NATS server at " + url, e);
    }
  }

  @Override
  public void publish(
      final @NotNull String channel,
      final byte[] message) {
    CompletableFuture.runAsync(() -> {
      try {
        this.connection.publish(channel, message);
      } catch (final Exception e) {
        throw new RuntimeException("Failed to publish message to channel " + channel, e);
      }
    }, executorService);
  }

  @Override
  public void subscribe(final @NotNull String channel, final @NotNull Consumer<byte[]> handler) {
    CompletableFuture.runAsync(() -> {
      try {
        final var dispatcher = this.connection.createDispatcher();
        dispatcher.subscribe(channel, msg -> {
          handler.accept(msg.getData());
        });
      } catch (final Exception e) {
        throw new RuntimeException("Failed to subscribe to channel " + channel, e);
      }
    }, executorService);
  }
}
