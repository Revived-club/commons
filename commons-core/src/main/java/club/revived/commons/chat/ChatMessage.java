package club.revived.commons.chat;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import club.revived.commons.data.model.LogMetric;

@Measurement(name = "chat_message")
public class ChatMessage implements LogMetric {

  @Column(tag = true)
  private String uuid;

  @Column
  private String content;

  @Column(timestamp = true)
  private long sentAt;

  @Column
  private boolean blocked;

  public ChatMessage() {
  }

  public ChatMessage(
      final String uuid,
      final String content,
      final long sentAt,
      final boolean blocked) {
    this.uuid = uuid;
    this.content = content;
    this.sentAt = sentAt;
    this.blocked = blocked;
  }

  public String getUuid() {
    return uuid;
  }

  public String getContent() {
    return content;
  }

  public long getSentAt() {
    return sentAt;
  }

  public boolean isBlocked() {
    return blocked;
  }
}
