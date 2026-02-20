package club.revived.concordia.provider;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

public interface PubSubProvider {

  @NotNull
  void connect(final @NotNull String url);

  void publish(final @NotNull String channel, final byte[] message);

  void subscribe(final @NotNull String channel, final @NotNull Consumer<byte[]> handler);
}
