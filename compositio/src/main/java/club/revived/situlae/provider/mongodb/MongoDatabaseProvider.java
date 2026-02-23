package club.revived.situlae.provider.mongodb;

import java.util.Collection;
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
import com.mongodb.client.MongoDatabase;

import club.revived.situlae.codec.EntityCodecProvider;
import club.revived.situlae.model.DatabaseCredentials;
import club.revived.situlae.model.Entity;
import club.revived.situlae.model.QueryFilter;
import club.revived.situlae.model.annotation.Repository;
import club.revived.situlae.provider.DatabaseProvider;

public final class MongoDatabaseProvider implements DatabaseProvider<Entity> {

  private MongoClient mongoClient;
  private MongoDatabase database;
  private boolean connected = false;

  private CodecRegistry codecRegistry;

  @Override
  public void connect(final @NotNull DatabaseCredentials credentials) {
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

    } catch (final Exception e) {
      this.connected = false;
      throw new RuntimeException("Failed to connect to MongoDB", e);
    }

  }

  @Override
  public @NotNull CompletableFuture<Optional<Entity>> find(final @NotNull QueryFilter filter) {
    return null;
  }

  @Override
  public @NotNull CompletableFuture<List<Entity>> findBatch(
      final @NotNull Collection<? extends QueryFilter> filters) {
    return null;
  }

  @Override
  public @NotNull CompletableFuture<Void> write(final @NotNull Entity t) {
    return null;
  }

  @Override
  public @NotNull CompletableFuture<Void> writeBatch(final @NotNull List<Entity> list) {
    return null;
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
}
