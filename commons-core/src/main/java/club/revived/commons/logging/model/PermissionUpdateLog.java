
package club.revived.commons.logging.model;

import java.time.Instant;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import club.revived.commons.data.model.LogMetric;

@Measurement(name = "command")
public class PermissionUpdateLog implements LogMetric {

  @Column(tag = true)
  private String uuid;

  @Column(timestamp = true)
  private Instant sentAt;

  @Column
  private String affectedGroup;

  @Column
  private String permission;

  @Column
  private boolean value;

  public PermissionUpdateLog() {
  }

  public PermissionUpdateLog(
      final String uuid,
      final Instant sentAt) {
    this.uuid = uuid;
    this.sentAt = sentAt;
  }

  public String getUuid() {
    return uuid;
  }

  public Instant getSentAt() {
    return sentAt;
  }

  public String getAffectedGroup() {
    return affectedGroup;
  }

  public String getPermission() {
    return permission;
  }

  public boolean isValue() {
    return value;
  }
}
