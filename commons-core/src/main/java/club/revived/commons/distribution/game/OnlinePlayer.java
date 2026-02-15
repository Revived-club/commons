package club.revived.commons.distribution.game;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.data.DataRepository;
import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.distribution.Cluster;

public record OnlinePlayer(UUID uuid, String username, String server, String skin, String signature, int ping) {

  public <T> void cacheValue(
      final Class<T> clazz,
      final T obj) {
    Cluster.getInstance()
        .getGlobalCache()
        .set(this.uuid + ":" + clazz.getSimpleName().toLowerCase(), obj);
  }

  public <T> void cacheExValue(
      final Class<T> clazz,
      final T obj,
      final long seconds) {
    Cluster.getInstance()
        .getGlobalCache()
        .setEx(
            this.uuid + ":" + clazz.getSimpleName().toLowerCase(),
            obj,
            seconds);
  }

  @NotNull
  public <T> CompletableFuture<Optional<T>> getCachedValue(final Class<T> clazz) {
    return Cluster.getInstance()
        .getGlobalCache()
        .get(clazz, this.uuid + ":" + clazz.getSimpleName().toLowerCase())
        .thenApply(value -> {
          return Optional.ofNullable(value);
        });
  }

  @NotNull
  public <T extends Entity> CompletableFuture<Optional<T>> getCachedOrLoad(final Class<T> clazz) {
    return this.getCachedValue(clazz).thenCompose(t -> {
      if (t.isPresent()) {
        return CompletableFuture.completedFuture(t);
      }

      return DataRepository.getInstance()
          .get(clazz, this.uuid.toString())
          .thenApply(opt -> {
            opt.ifPresent(val -> this.cacheValue(clazz, val));
            return opt;
          });
    });
  }
}
