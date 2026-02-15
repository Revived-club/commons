package club.revived.commons.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.chat.ChatHistory.ChatMessage;

public final class ChatHistorySaveTask {

  private final List<ChatMessage> messages = new ArrayList<>();
  public final ScheduledExecutorService subServer = Executors.newScheduledThreadPool(1);

  public void start() {
    this.subServer.scheduleAtFixedRate(() -> {
      final List<ChatMessage> queried = new ArrayList<>(this.messages);
      this.messages.clear();

      final List<UUID> uuids = queried.stream()
          .map(message -> message.uuid())
          .toList();

      // TODO: Implement InfluxDB lookup

    }, 0, 5, TimeUnit.SECONDS);
  }

  public void queueMessage(final @NotNull ChatMessage chatMessage) {
    this.messages.add(chatMessage);
  }
}
