package club.revived.concordia.provider;

import java.util.function.Consumer;

public interface PubSubProvider {

    void publish(final String channel, final byte[] message);
    void subscribe(final String channel, final Consumer<byte[]> handler);
}
