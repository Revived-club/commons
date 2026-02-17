package club.revived.commons.punishment.model;

import java.time.Instant;
import java.util.UUID;

import club.revived.commons.data.model.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

@Repository("punishments")
public record Punishment(
    @Identifier String id,
    UUID uuid,
    String reason,
    PunishmentType type,
    Instant expiry) implements Entity {

  public boolean isActive() {
    return expiry == null || expiry.isBefore(Instant.now());
  }
}
