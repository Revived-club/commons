
package club.revived.commons.logging.model;

import java.time.Instant;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import club.revived.commons.data.model.LogMetric;

@Measurement(name = "command")
public class CommandLog implements LogMetric {

  @Column(tag = true)
  private String uuid;

  @Column
  private String content;

  @Column(timestamp = true)
  private Instant sentAt;

  @Column
  private String server;

  public CommandLog() {
  }

  public CommandLog(
      final String uuid,
      final String content,
      final Instant sentAt,
      final String server) {
    this.uuid = uuid;
    this.content = content;
    this.sentAt = sentAt;
    this.server = server;
  }

  public String getUuid() {
    return uuid;
  }

  public String getContent() {
    return content;
  }

  public Instant getSentAt() {
    return sentAt;
  }
}
