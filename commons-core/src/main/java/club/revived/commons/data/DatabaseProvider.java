package club.revived.commons.data;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.orm.annotations.Entity;

public interface DatabaseProvider {

  @NotNull
  public <T> CompletableFuture<Optional<T>> get(final Class<? extends Entity> clazz, final String key);

  @NotNull
  public <T> CompletableFuture<List<T>> getAll(final Class<? extends Entity> clazz);

  @NotNull
  public <T> CompletableFuture<Void> save(final Class<? extends Entity> clazz, final T t);

}
