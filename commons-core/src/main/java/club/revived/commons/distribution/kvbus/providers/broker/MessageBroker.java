package club.revived.commons.distribution.kvbus.providers.broker;

import club.revived.commons.distribution.kvbus.pubsub.MessageHandler;

public interface MessageBroker {

  <T> void publish(String topic, T message);

  <T> void subscribe(String topic, Class<T> type, MessageHandler<T> handler);

  <P> P connect(
      final String host,
      final int port,
      final String password);
}
