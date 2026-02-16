package club.revived.commons.punishment.model;

import java.time.Instant;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import club.revived.commons.data.model.LogMetric;

@Measurement(name = "punishment")
public class PunishmentLog implements LogMetric {

  @NotNull
  @Column(timestamp = true)
  private final Instant time;

  @NotNull
  @Column(tag = true)
  private final String playerId;

  @NotNull
  @Column(tag = true)
  private final String type;

  @NotNull
  @Column
  private final String reason;

  @Column
  private final Instant expiresAt;

  @NotNull
  @Column(tag = true)
  private final String issuedBy;

  @NotNull
  @Column
  private final boolean active;

  public PunishmentLog(
      final @NotNull UUID playerId,
      final @NotNull String type,
      final @NotNull String reason,
      final Instant expiresAt,
      final @NotNull String issuedBy,
      final boolean active) {
    this.time = Instant.now();
    this.playerId = playerId.toString();
    this.type = type;
    this.reason = reason;
    this.expiresAt = expiresAt;
    this.issuedBy = issuedBy;
    this.active = active;
  }

  public @NotNull Instant getTime() {
    return time;
  }

  public @NotNull UUID getPlayerId() {
    return UUID.fromString(playerId);
  }

  public @NotNull String getType() {
    return type;
  }

  public @NotNull String getReason() {
    return reason;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public @NotNull String getIssuedBy() {
    return issuedBy;
  }

  public boolean isActive() {
    return active;
  }
}
