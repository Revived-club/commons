package club.revived.commons.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.data.model.DatabaseCredentials;
import club.revived.commons.data.model.LogMetric;
import club.revived.commons.data.provider.InfluxHandler;
import club.revived.commons.data.provider.MongoHandler;
import club.revived.commons.orm.annotations.Entity;

public final class DataRepository {

  private final Map<DatabaseType, Class<? extends DatabaseProvider>> providers = new HashMap<>();
  private final Map<DatabaseType, Class<? extends LogDatabaseProvider>> logProviders = new HashMap<>();

  private final DatabaseType databaseType;

  private DatabaseProvider databaseProvider;
  private LogDatabaseProvider logDatabaseProvider;

  private static DataRepository instance;

  public DataRepository(final @NotNull DatabaseType databaseType) {
    this.providers.put(DatabaseType.MONGODB, MongoHandler.class);
    this.logProviders.put(DatabaseType.INFLUXDB, InfluxHandler.class);

    this.databaseType = databaseType;
  }

  public <T extends Entity> CompletableFuture<Void> save(final @NotNull Class<T> clazz, final @NotNull T entity) {
    System.out.println("Saving entity of type " + clazz.getSimpleName());
    return this.databaseProvider.save(clazz, entity);
  }

  public <T extends Entity> CompletableFuture<Void> save(final @NotNull T entity) {
    @SuppressWarnings("unchecked")
    final Class<T> clazz = (Class<T>) entity.getClass();
    return save(clazz, entity);
  }

  @NotNull
  public <T extends Entity> CompletableFuture<Void> saveAll(
      final @NotNull Class<T> clazz,
      final @NotNull List<T> entities) {
    return this.databaseProvider.saveAll(clazz, entities);
  }

  @NotNull
  public <T extends Entity> CompletableFuture<List<T>> getAllByKeys(
      final @NotNull Class<T> clazz,
      final @NotNull List<?> keys) {
    return this.databaseProvider.getAllByKeys(clazz, keys);
  }

  @NotNull
  public <T extends Entity> CompletableFuture<Optional<T>> get(
      final @NotNull Class<T> clazz,
      final @NotNull Object key) {
    return this.databaseProvider.get(clazz, key);
  }

  public <T extends Entity> void ifEntityExists(
      final @NotNull Class<T> clazz,
      final @NotNull String key,
      final @NotNull Consumer<T> action) {
    this.databaseProvider.get(clazz, key).thenAccept(opt -> opt.ifPresent(action));
  }

  @NotNull
  public <T extends Entity> CompletableFuture<List<T>> getAll(final @NotNull Class<T> clazz) {
    return this.databaseProvider.getAll(clazz);
  }

  @NotNull
  public <T extends Entity> CompletableFuture<List<T>> getByField(
      final @NotNull Class<T> clazz,
      final @NotNull String fieldName,
      final @NotNull Object value) {
    return this.databaseProvider.getByField(clazz, fieldName, value);
  }

  @NotNull
  public <T extends Entity> CompletableFuture<Void> delete(
      final @NotNull Class<T> clazz,
      final @NotNull Object key) {
    return this.databaseProvider.delete(clazz, key);
  }

  public <T extends Entity> void forEachEntity(
      final @NotNull Class<T> clazz,
      final @NotNull Consumer<T> action) {
    this.getAll(clazz).thenAccept(list -> list.forEach(action));
  }

  @NotNull
  public <T extends LogMetric> CompletableFuture<Void> writeLog(
      final @NotNull T metric) {

    return this.logDatabaseProvider.write(metric);
  }

  @NotNull
  public <T extends LogMetric> CompletableFuture<Void> writeLogs(
      final @NotNull List<T> metrics) {

    return this.logDatabaseProvider.writeBatch(metrics);
  }

  @NotNull
  public <T extends LogMetric> CompletableFuture<List<T>> getAllLogs(
      final @NotNull String measurement,
      final @NotNull Class<T> type) {

    return this.logDatabaseProvider.getAll(measurement, type);
  }

  @NotNull
  public <T extends LogMetric> CompletableFuture<List<T>> getAllLogs(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull String tagValue,
      final @NotNull Class<T> type) {

    return this.logDatabaseProvider.getAll(measurement, tagKey, tagValue, type);
  }

  @NotNull
  public <T extends LogMetric> CompletableFuture<List<T>> getLogs(
      final @NotNull String measurement,
      final int hours,
      final @NotNull Class<T> type) {

    return this.logDatabaseProvider.get(measurement, hours, type);
  }

  @NotNull
  public <T extends LogMetric> CompletableFuture<List<T>> getLogs(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull String tagValue,
      final int hours,
      final @NotNull Class<T> type) {

    return this.logDatabaseProvider.get(
        measurement,
        tagKey,
        tagValue,
        hours,
        type);
  }

  @NotNull
  public <T extends LogMetric> CompletableFuture<List<T>> getAllLogs(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull List<String> tagValues,
      final @NotNull Class<T> type) {

    if (tagValues.isEmpty()) {
      return CompletableFuture.completedFuture(List.of());
    }

    return this.logDatabaseProvider.getAll(measurement, tagKey, tagValues, type);
  }

  @NotNull
  public <T extends LogMetric> CompletableFuture<List<T>> getLogs(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull List<String> tagValues,
      final int hours,
      final @NotNull Class<T> type) {

    if (tagValues.isEmpty()) {
      return CompletableFuture.completedFuture(List.of());
    }

    return this.logDatabaseProvider.get(measurement, tagKey, tagValues, hours, type);
  }

  public void initLogging(final @NotNull DatabaseCredentials credentials, final @NotNull DatabaseType databaseType) {
    try {
      final var clazz = this.logProviders.get(databaseType);

      if (clazz == null) {
        throw new UnsupportedOperationException();
      }

      final var provider = clazz.getDeclaredConstructor(DatabaseCredentials.class)
          .newInstance(credentials);

      this.logDatabaseProvider = provider;
      this.logDatabaseProvider.connect();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

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
