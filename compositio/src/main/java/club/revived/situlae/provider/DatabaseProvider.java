package club.revived.situlae.provider;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.situlae.model.DatabaseCredentials;
import club.revived.situlae.model.QueryFilter;

public interface DatabaseProvider<T> {

  void connect(final @NotNull DatabaseCredentials credentials);

  @NotNull
  CompletableFuture<Void> write(final @NotNull T t);

  @NotNull
  CompletableFuture<Void> writeBatch(final @NotNull List<T> list);

  @NotNull
  CompletableFuture<Optional<T>> find(final @NotNull QueryFilter filter);

  @NotNull
  CompletableFuture<List<T>> findBatch(final @NotNull Collection<? extends QueryFilter> filters);

}
