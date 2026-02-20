package club.revived.concordia.provider.impl;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import club.revived.concordia.provider.PubSubProvider;

public final class NATSPubSubProvider implements PubSubProvider {

  @Override
  public void connect(final @NotNull String url) {

  }

  @Override
  public void publish(
      final @NotNull String channel,
      final @NotNull byte[] message) {

  }

  @Override
  public void subscribe(String channel, Consumer<byte[]> handler) {
    // TODO Auto-generated method stub

  }
}
