package club.revived.commons.distribution.game;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.data.DataRepository;
import club.revived.commons.data.model.Entity;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.message.KickMessage;
import club.revived.commons.distribution.message.SendActionbar;
import club.revived.commons.distribution.message.SendMessage;
import club.revived.commons.distribution.message.WhereIsRequest;
import club.revived.commons.distribution.message.WhereIsResponse;
import club.revived.commons.distribution.service.Service;

public record OnlinePlayer(UUID uuid, String username, String server, String skin, String signature, int ping) {

  public void connect(final String serviceId) {

  }

  public void sendMessage(
      final String message) {
    final var whereIs = this.whereIs();

    whereIs.thenAccept(service -> {
      if (service == null) {
        return;
      }

      service.sendMessage(new SendMessage(this.uuid, message));
    });
  }

  public void kick(final String reason) {
    this.whereIs().thenAccept(service -> {
      if (service == null) {
        return;
      }

      service.sendMessage(new KickMessage(this.uuid, reason));
    });
  }

  public void sendActionbar(final String message) {
    this.whereIs().thenAccept(service -> {
      if (service == null) {
        return;
      }

      service.sendMessage(new SendActionbar(this.uuid, message));
    });
  }

  @NotNull
  public CompletableFuture<Service> whereIs() {
    return Cluster.getInstance().getMessagingService()
        .sendGlobalRequest(new WhereIsRequest(this.uuid), WhereIsResponse.class)
        .thenApply(whereIsResponse -> {
          final var id = whereIsResponse.getFirst().server();

          return Cluster.getInstance().getServices().get(id);
        });
  }

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
          .get(clazz, this.uuid)
          .thenApply(opt -> {
            opt.ifPresent(val -> this.cacheValue(clazz, val));
            return opt;
          });
    });
  }
}
