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

  public DataRepository(@NotNull final DatabaseType databaseType) {
    this.providers.put(DatabaseType.MONGODB, MongoHandler.class);
    this.databaseType = databaseType;
  }

  public <T extends Entity> void save(@NotNull final Class<T> clazz, @NotNull final T entity) {
    System.out.println("Saving entity of type " + clazz.getSimpleName());
    this.databaseProvider.save(clazz, entity);
  }

  public <T extends Entity> void save(@NotNull final T entity) {
    @SuppressWarnings("unchecked")
    final Class<T> clazz = (Class<T>) entity.getClass();
    save(clazz, entity);
  }

  @NotNull
  public <T extends Entity> CompletableFuture<Optional<T>> get(
      @NotNull final Class<T> clazz,
      @NotNull final String key) {
    return this.databaseProvider.get(clazz, key);
  }

  public <T extends Entity> void ifEntityExists(
      @NotNull final Class<T> clazz,
      @NotNull final String key,
      @NotNull final Consumer<T> action) {
    this.databaseProvider.get(clazz, key).thenAccept(opt -> opt.ifPresent(action));
  }

  @NotNull
  public <T extends Entity> CompletableFuture<List<T>> getAll(@NotNull final Class<T> clazz) {
    return this.databaseProvider.getAll(clazz);
  }

  public <T extends Entity> void forEachEntity(
      @NotNull final Class<T> clazz,
      @NotNull final Consumer<T> action) {
    this.getAll(clazz).thenAccept(list -> list.forEach(action));
  }

  @NotNull
  public void init(@NotNull final DatabaseCredentials credentials) {
    try {
      final Class<? extends DatabaseProvider> clazz = providers.get(this.databaseType);
      if (clazz == null) {
        throw new UnsupportedOperationException("No provider registered for " + this.databaseType);
      }

      final DatabaseProvider provider = clazz.getDeclaredConstructor(DatabaseCredentials.class)
          .newInstance(credentials);

      this.databaseProvider = provider;
      this.databaseProvider.connect();

      instance = this;

    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  public static DataRepository getInstance() {
    if (instance == null) {
      throw new UnsupportedOperationException("DataRepository not initialized");
    }
    return instance;
  }
}
