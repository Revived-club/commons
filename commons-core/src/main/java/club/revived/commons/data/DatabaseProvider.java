package club.revived.commons.data;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.orm.annotations.Entity;

public interface DatabaseProvider {

  @NotNull
  <T extends Entity> CompletableFuture<Optional<T>> get(
      @NotNull final Class<T> clazz,
      @NotNull final Object key);

  @NotNull
  <T extends Entity> CompletableFuture<List<T>> getAll(
      @NotNull final Class<T> clazz);

  @NotNull
  <T extends Entity> CompletableFuture<Void> save(
      @NotNull final Class<T> clazz,
      @NotNull final T entity);

  @NotNull
  <T extends Entity> CompletableFuture<List<T>> getAllByKeys(
      @NotNull final Class<T> clazz,
      @NotNull final List<?> keys);

  @NotNull
  <T extends Entity> CompletableFuture<Void> saveAll(
      @NotNull final Class<T> clazz,
      @NotNull final List<T> entities);

  @NotNull
  <T extends Entity> CompletableFuture<Void> delete(
      @NotNull final Class<T> clazz,
      @NotNull final Object key);

  @NotNull
  <T extends Entity> CompletableFuture<List<T>> getByField(
      @NotNull final Class<T> clazz,
      @NotNull final String fieldName,
      @NotNull final Object value);

  void connect();
}
