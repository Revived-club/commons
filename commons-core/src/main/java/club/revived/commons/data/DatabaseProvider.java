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
      @NotNull final String key);

  @NotNull
  <T extends Entity> CompletableFuture<List<T>> getAll(
      @NotNull final Class<T> clazz);

  @NotNull
  <T extends Entity> CompletableFuture<Void> save(
      @NotNull final Class<T> clazz,
      @NotNull final T entity);

  void connect();
}
