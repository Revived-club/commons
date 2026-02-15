package club.revived.commons.chat;

import java.util.List;
import java.util.UUID;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

public record ChatHistory(@Identifier UUID uuid, List<ChatMessage> chatMessages) {

  @Measurement(name = "chat_message")
  public record ChatMessage(
      @Column(name = "uuid", tag = true) UUID uuid,
      @Column(name = "content") String content,
      @Column(name = "sent_at", timestamp = true) long sentAt,
      @Column(name = "blocked") boolean blocked) {
  }
}
