package club.revived.commons.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.chat.ChatHistory.ChatMessage;
import club.revived.commons.data.DataRepository;

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

      DataRepository.getInstance().getAllByKeys(ChatHistory.class, uuids)
          .thenAccept(histories -> {
            for (final var history : histories) {
              final var messages = queried.stream()
                  .filter(message -> message.uuid().equals(history.uuid()))
                  .toList();

              history.chatMessages().addAll(messages);

              history.save();
            }
          });

    }, 0, 5, TimeUnit.SECONDS);
  }

  public void queueMessage(final @NotNull ChatMessage chatMessage) {
    this.messages.add(chatMessage);
  }
}
