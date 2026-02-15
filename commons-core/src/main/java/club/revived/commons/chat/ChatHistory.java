package club.revived.commons.chat;

import java.util.List;
import java.util.UUID;

import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

// TODO: Add Filter
@Repository("chat_history")
public record ChatHistory(@Identifier UUID uuid, List<ChatMessage> chatMessages) implements Entity {

  public record ChatMessage(UUID uuid, String content, long sentAt, boolean blocked) {
  }
}
