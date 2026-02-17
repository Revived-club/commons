package club.revived.commons.logging.model;

import java.time.Instant;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import club.revived.commons.data.model.LogMetric;

@Measurement(name = "chat_filter")
public class ChatFilterLog implements LogMetric {

  @Column(tag = true)
  private String uuid;

  @Column
  private String content;

  @Column(timestamp = true)
  private Instant sentAt;

  public ChatFilterLog() {
  }

  public ChatFilterLog(
      final String uuid,
      final String content,
      final Instant sentAt) {
    this.uuid = uuid;
    this.content = content;
    this.sentAt = sentAt;
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
