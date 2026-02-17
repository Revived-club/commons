package club.revived.commons.punishment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import club.revived.commons.data.DataRepository;
import club.revived.commons.distribution.game.PlayerManager;
import club.revived.commons.distribution.message.KickMessage;
import club.revived.commons.punishment.model.Punishment;
import club.revived.commons.punishment.model.PunishmentType;

public abstract class PunishmentManager {

  public void punishPlayer(
      final @NotNull UUID playerId,
      final @NotNull UUID punisher,
      final @NotNull PunishmentType type,
      final @NotNull String reason,
      final @Nullable Instant expiry) {
    final var punishment = PunishmentFactory.create(playerId, reason, type, expiry);
    final var log = PunishmentFactory.create(punisher, punishment);

    log.save();
    punishment.save();
  }

  @NotNull
  public CompletableFuture<Void> expirePunishments(final @NotNull UUID uuid) {
    return getPunishments(uuid).thenCompose(punishments -> {
      final var expired = punishments.stream()
          .filter(p -> p.expiry() != null && p.expiry().isBefore(Instant.now()))
          .toList();

      if (expired.isEmpty()) {
        return CompletableFuture.completedFuture(null);
      }

      final var repository = DataRepository.getInstance();

      final var deletions = expired.stream()
          .map(p -> repository.delete(Punishment.class, p.id()))
          .toArray(CompletableFuture[]::new);

      return CompletableFuture.allOf(deletions);
    });
  }

  @NotNull
  public CompletableFuture<Boolean> isPlayerPunished(
      final @NotNull UUID uuid,
      final @NotNull PunishmentType type) {
    return expirePunishments(uuid)
        .thenCompose(ignored -> getPunishments(uuid))
        .thenApply(punishments -> punishments.stream()
            .anyMatch(p -> p.type() == type));
  }

  @NotNull
  public CompletableFuture<List<Punishment>> getPunishments(final UUID uuid) {
    return DataRepository.getInstance().getByField(Punishment.class, "uuid", uuid);
  }

  public void executePunishment(final Punishment punishment) {
    final var uuid = punishment.uuid();

    PlayerManager.getInstance().getOptional(uuid).ifPresent(onlinePlayer -> {
      if (punishment.type() == PunishmentType.MUTE) {
        onlinePlayer.sendMessage(
            String.format("<red>You have been muted for '%s'. You may chat again in %s", punishment.reason(), "test"));
        return;
      }

      onlinePlayer.whereIs().thenAccept(service -> {
        service.sendMessage(new KickMessage(uuid, "<red>You have been kicked! Try rejoining!"));
      });
    });
  }
}
