package club.revived.commons.distribution.kvbus.pubsub;

@FunctionalInterface
public interface MessageHandler<T> {

  void handle(final T message);
}
