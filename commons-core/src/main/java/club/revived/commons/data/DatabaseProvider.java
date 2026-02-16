package club.revived.commons.data;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.orm.annotations.Entity;

public interface DatabaseProvider {

  @NotNull
  <T extends Entity> CompletableFuture<Optional<T>> get(
      final @NotNull Class<T> clazz,
      final @NotNull Object key);

  @NotNull
  <T extends Entity> CompletableFuture<List<T>> getAll(
      final @NotNull Class<T> clazz);

  @NotNull
  <T extends Entity> CompletableFuture<Void> save(
      final @NotNull Class<T> clazz,
      final @NotNull T entity);

  @NotNull
  <T extends Entity> CompletableFuture<List<T>> getAllByKeys(
      final @NotNull Class<T> clazz,
      final @NotNull List<?> keys);

  @NotNull
  <T extends Entity> CompletableFuture<Void> saveAll(
      final @NotNull Class<T> clazz,
      final @NotNull List<T> entities);

  @NotNull
  <T extends Entity> CompletableFuture<Void> delete(
      final @NotNull Class<T> clazz,
      final @NotNull Object key);

  @NotNull
  <T extends Entity> CompletableFuture<List<T>> getByField(
      final @NotNull Class<T> clazz,
      final @NotNull String fieldName,
      final @NotNull Object value);

  void connect();
}
