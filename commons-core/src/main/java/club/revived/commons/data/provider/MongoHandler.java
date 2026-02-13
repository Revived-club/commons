package club.revived.commons.data.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import club.revived.commons.data.DatabaseProvider;
import club.revived.commons.data.model.DatabaseCredentials;
import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.mongodb.MongoObjectMapper;

public final class MongoHandler implements DatabaseProvider {

  private final DatabaseCredentials credentials;
  private final MongoObjectMapper mongoObjectMapper = new MongoObjectMapper();

  private MongoClient mongoClient;
  private MongoDatabase database;
  private boolean connected = false;

  public MongoHandler(final DatabaseCredentials credentials) {
    this.credentials = credentials;
  }

  public void connect() {
    try {
      final String connectionString;

      if (this.credentials.password() != null && !this.credentials.password().isEmpty()) {
        connectionString = String.format(
            "mongodb://%s:%s@%s:%d",
            this.credentials.user(),
            this.credentials.password(),
            this.credentials.host(),
            this.credentials.port());
      } else {
        connectionString = String.format(
            "mongodb://%s:%d",
            this.credentials.host(),
            this.credentials.port());
      }

      final var settings = MongoClientSettings.builder()
          .applyConnectionString(new ConnectionString(connectionString))
          .build();

      this.mongoClient = MongoClients.create(settings);
      this.database = mongoClient.getDatabase(credentials.database());
      this.connected = true;

    } catch (Exception e) {
      this.connected = false;
      throw new RuntimeException("Failed to connect to MongoDB", e);
    }
  }

  @NotNull
  private MongoCollection<Document> getCollection(final Class<?> clazz) {
    final String name = clazz.getSimpleName().toLowerCase();

    return database.getCollection(name);
  }

  @Override
  public <T> @NotNull CompletableFuture<Optional<T>> get(
      final Class<? extends Entity> clazz,
      final String key) {
    return CompletableFuture.supplyAsync(() -> {
      final MongoCollection<Document> collection = getCollection(clazz);
      final Document document = collection.find(Filters.eq("_id", key)).first();

      if (document == null) {
        return Optional.empty();
      }

      @SuppressWarnings("unchecked")
      final T value = (T) mongoObjectMapper.read(document, clazz);

      return Optional.of(value);
    });
  }

  @Override
  public <T> @NotNull CompletableFuture<List<T>> getAll(final Class<? extends Entity> clazz) {
    return CompletableFuture.supplyAsync(() -> {
      final MongoCollection<Document> collection = getCollection(clazz);
      final List<T> result = new ArrayList<>();

      for (final var document : collection.find()) {

        @SuppressWarnings("unchecked")
        final T value = (T) mongoObjectMapper.read(document, clazz);

        result.add(value);
      }

      return result;
    });
  }

  @Override
  public <T> @NotNull CompletableFuture<Void> save(final Class<? extends Entity> clazz, final T t) {
    return CompletableFuture.runAsync(() -> {

      final Document document = mongoObjectMapper.write(t);
      final MongoCollection<Document> collection = getCollection(clazz);

      collection.replaceOne(
          Filters.eq("_id", document.get("_id")),
          document,
          new ReplaceOptions().upsert(true));
    });
  }
}
