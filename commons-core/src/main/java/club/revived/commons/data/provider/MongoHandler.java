package club.revived.commons.data.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.WriteModel;

import club.revived.commons.data.DatabaseProvider;
import club.revived.commons.data.model.DatabaseCredentials;
import club.revived.commons.orm.annotations.Repository;
import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.codec.EntityCodecProvider;

public final class MongoHandler implements DatabaseProvider {

  private final DatabaseCredentials credentials;

  private MongoClient mongoClient;
  private MongoDatabase database;
  private boolean connected = false;

  private CodecRegistry codecRegistry;

  public MongoHandler(final DatabaseCredentials credentials) {
    this.credentials = credentials;
  }

  @Override
  public void connect() {
    try {

      final String connectionString;

      if (credentials.password() != null && !credentials.password().isEmpty()) {
        connectionString = String.format(
            "mongodb+srv://%s:%s@%s/%s",
            credentials.user(),
            credentials.password(),
            credentials.host(),
            credentials.database());
      } else {
        connectionString = String.format(
            "mongodb+srv://%s/%s",
            credentials.host(),
            credentials.database());
      }

      this.codecRegistry = CodecRegistries.fromRegistries(
          MongoClientSettings.getDefaultCodecRegistry(),
          CodecRegistries.fromProviders(new EntityCodecProvider()));

      final var settings = MongoClientSettings.builder()
          .applyConnectionString(new ConnectionString(connectionString))
          .codecRegistry(this.codecRegistry)
          .uuidRepresentation(UuidRepresentation.STANDARD)
          .build();

      this.mongoClient = MongoClients.create(settings);
      this.database = mongoClient.getDatabase(credentials.database());
      this.connected = true;

    } catch (Exception e) {
      this.connected = false;
      throw new RuntimeException("Failed to connect to MongoDB", e);
    }
  }

  @Override
  public <T extends Entity> @NotNull CompletableFuture<List<T>> getAll(final Class<T> clazz) {
    if (!this.connected) {
      return CompletableFuture.failedFuture(new IllegalStateException("The database is not connected"));
    }

    return CompletableFuture.supplyAsync(() -> {
      final var name = this.getCollection(clazz);

      final MongoCollection<T> collection = this.database.getCollection(name)
          .withDocumentClass(clazz)
          .withCodecRegistry(this.codecRegistry);

      final List<T> result = new ArrayList<>();

      for (final T entity : collection.find()) {
        result.add(entity);
      }

      return result;
    });
  }

  @Override
  public <T extends Entity> @NotNull CompletableFuture<Optional<T>> get(
      final Class<T> clazz,
      final Object key) {
    if (!this.connected) {
      return CompletableFuture.failedFuture(new IllegalStateException("The database is not connected"));
    }

    return CompletableFuture.supplyAsync(() -> {
      final var name = this.getCollection(clazz);
      final MongoCollection<T> collection = this.database.getCollection(name)
          .withDocumentClass(clazz)
          .withCodecRegistry(this.codecRegistry);

      final T value = collection.find(Filters.eq("_id", key)).first();
      return Optional.ofNullable(value);
    });
  }

  @Override
  public <T extends Entity> @NotNull CompletableFuture<Void> save(final Class<T> clazz, final T entity) {
    if (!this.connected) {
      return CompletableFuture.failedFuture(new IllegalStateException("The database is not connected"));
    }

    return CompletableFuture.runAsync(() -> {
      final var name = this.getCollection(clazz);

      final MongoCollection<T> collection = this.database.getCollection(name)
          .withDocumentClass(clazz)
          .withCodecRegistry(this.codecRegistry);

      final Object id = getId(entity);

      collection.replaceOne(
          Filters.eq("_id", id),
          entity,
          new ReplaceOptions().upsert(true));
    });
  }

  @Override
  public <T extends Entity> @NotNull CompletableFuture<List<T>> getAllByKeys(
      final @NotNull Class<T> clazz,
      final @NotNull List<?> keys) {

    if (!this.connected) {
      return CompletableFuture.failedFuture(
          new IllegalStateException("The database is not connected"));
    }

    if (keys == null || keys.isEmpty()) {
      return CompletableFuture.completedFuture(List.of());
    }

    return CompletableFuture.supplyAsync(() -> {
      final String name = this.getCollection(clazz);

      final MongoCollection<T> collection = this.database.getCollection(name)
          .withDocumentClass(clazz)
          .withCodecRegistry(this.codecRegistry);

      final List<T> result = new ArrayList<>();

      for (final T entity : collection.find(Filters.in("_id", keys))) {
        result.add(entity);
      }

      return result;
    });
  }

  @Override
  public <T extends Entity> @NotNull CompletableFuture<Void> delete(final @NotNull Class<T> clazz,
      final @NotNull Object key) {
    if (!this.connected) {
      return CompletableFuture.failedFuture(new IllegalStateException("The database is not connected"));
    }

    return CompletableFuture.runAsync(() -> {
      final var name = this.getCollection(clazz);

      final MongoCollection<T> collection = this.database.getCollection(name)
          .withDocumentClass(clazz)
          .withCodecRegistry(this.codecRegistry);

      collection.deleteOne(Filters.eq("_id", key));
    });
  }

  @Override
  public <T extends Entity> @NotNull CompletableFuture<List<T>> getByField(
      final @NotNull Class<T> clazz,
      final @NotNull String fieldName,
      final @NotNull Object value) {
    if (!this.connected) {
      return CompletableFuture.failedFuture(new IllegalStateException("The database is not connected"));
    }

    return CompletableFuture.supplyAsync(() -> {
      final String name = this.getCollection(clazz);

      final MongoCollection<T> collection = this.database.getCollection(name)
          .withDocumentClass(clazz)
          .withCodecRegistry(this.codecRegistry);

      final List<T> result = new ArrayList<>();

      for (final T entity : collection.find(Filters.eq(fieldName, value))) {
        result.add(entity);
      }

      return result;
    });
  }

  @Override
  public <T extends Entity> @NotNull CompletableFuture<Void> saveAll(
      final Class<T> clazz,
      final List<T> entities) {
    if (!this.connected) {
      return CompletableFuture.failedFuture(new IllegalStateException("The database is not connected"));
    }

    if (entities == null || entities.isEmpty()) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.runAsync(() -> {
      final var name = this.getCollection(clazz);

      final MongoCollection<T> collection = this.database.getCollection(name)
          .withDocumentClass(clazz)
          .withCodecRegistry(this.codecRegistry);

      final List<WriteModel<T>> bulkOperations = new ArrayList<>();

      for (final T entity : entities) {
        final Object id = getId(entity);
        bulkOperations.add(
            new ReplaceOneModel<>(
                Filters.eq("_id", id),
                entity,
                new ReplaceOptions().upsert(true)));
      }

      collection.bulkWrite(bulkOperations);
    });
  }

  @NotNull
  private <T extends Entity> String getCollection(final Class<T> clazz) {
    final var simpleName = clazz.getSimpleName().toLowerCase();

    try {
      if (clazz.isAnnotationPresent(Repository.class)) {
        final String name = clazz.getAnnotation(Repository.class).value();

        return name.isEmpty() ? simpleName : name;
      }
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve collection name from entity", e);
    }

    return simpleName;
  }

  @NotNull
  private <T extends Entity> Object getId(final Entity entity) {
    try {
      for (final var field : entity.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Identifier.class)) {
          field.setAccessible(true);
          return field.get(entity);
        }
      }
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve @Identifier from entity", e);
    }

    throw new IllegalStateException("Entity has no @Identifier field");
  }
}
