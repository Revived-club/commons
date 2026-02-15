package club.revived.commons.distribution.kvbus.providers.cache;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DistributedCacheStore {

  <T> CompletableFuture<T> get(Class<T> clazz, String key);

  <T> void set(
      final String key,
      final T t);

  <T> void setEx(
      final String key,
      final T t,
      final long seconds);

  <T> void push(
      final String listKey,
      final String id,
      final T t);

  <T> void pushEx(
      final String listKey,
      final String id,
      final T t,
      final long ttlSeconds);

  <T> CompletableFuture<List<T>> getAll(
      final String listKey,
      final Class<T> clazz);

  <P> P connect(
      final String host,
      final int port,
      final String password);

  CompletableFuture<Boolean> remove(
      final String key);

  void removeFromList(
      final String listKey,
      final String id,
      final long count);

  <T> CompletableFuture<T> getById(
      final Class<T> clazz,
      final String id);

  <T> void update(
      final String id,
      final T t);

  void invalidateAll(final String param);
}
