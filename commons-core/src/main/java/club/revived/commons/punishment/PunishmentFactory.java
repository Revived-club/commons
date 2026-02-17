package club.revived.commons.punishment;

import java.time.Instant;
import java.util.UUID;

import club.revived.commons.StringUtils;
import club.revived.commons.logging.model.PunishmentLog;
import club.revived.commons.punishment.model.Punishment;
import club.revived.commons.punishment.model.PunishmentType;

public final class PunishmentFactory {

  public static Punishment create(
      final UUID uuid,
      final String reason,
      final PunishmentType type,
      final Instant expiry) {
    final var id = StringUtils.generateId("");
    return new Punishment(id, uuid, reason, type, expiry);
  }

  public static PunishmentLog create(
      final UUID uuid,
      final Punishment punishment) {
    return new PunishmentLog(
        punishment.uuid(),
        uuid,
        punishment.type().toString(),
        punishment.reason(),
        punishment.expiry());
  }
}
