package club.revived.commons.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.data.model.DatabaseCredentials;
import club.revived.commons.data.provider.MongoHandler;
import club.revived.commons.orm.annotations.Entity;

public final class DataRepository {

  private final Map<DatabaseType, Class<? extends DatabaseProvider>> providers = new HashMap<>();
  private final DatabaseType databaseType;
  private DatabaseProvider databaseProvider;

  private static DataRepository instance;

  public DataRepository(final DatabaseType databaseType) {
    this.providers.put(DatabaseType.MONGODB, MongoHandler.class);
    this.databaseType = databaseType;
  }

  public <T> void save(final Class<? extends Entity> clazz, final T t) {
    this.databaseProvider.save(clazz, t);
  }

  @NotNull
  public <T> CompletableFuture<Optional<T>> get(final Class<? extends Entity> clazz, final String key) {
    return this.databaseProvider.get(clazz, key);
  }

  @NotNull
  public <T> void ifEntityExists(final Class<? extends Entity> clazz, final String key, final Consumer<T> action) {
    this.databaseProvider.get(clazz, key).thenAccept(opt -> {

      opt.ifPresent(value -> action.accept((T) value));
    });
  }

  @NotNull
  public <T> CompletableFuture<List<T>> getAll(final Class<? extends Entity> clazz) {
    return this.databaseProvider.getAll(clazz);
  }

  @NotNull
  public <T> void forEachEntity(final Class<? extends Entity> clazz, final Consumer<T> action) {
    this.getAll(clazz).thenAccept(list -> {
      for (final var entry : list) {
        action.accept((T) entry);
      }
    });
  }

  @NotNull
  public void init(final DatabaseCredentials credentials) {
    try {
      final var clazz = providers.get(this.databaseType);

      final var provider = clazz.getDeclaredConstructor(DatabaseCredentials.class).newInstance(credentials);
      this.databaseProvider = provider;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static DataRepository getInstance() {
    if (instance == null) {
      throw new UnsupportedOperationException();
    }

    return instance;
  }
}
