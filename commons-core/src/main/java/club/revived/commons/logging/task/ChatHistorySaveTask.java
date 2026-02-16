package club.revived.commons.logging.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.data.DataRepository;
import club.revived.commons.logging.model.ChatMessage;

public final class ChatHistorySaveTask {

  private final ConcurrentLinkedQueue<ChatMessage> messages = new ConcurrentLinkedQueue<>();
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public void start() {
    scheduler.scheduleAtFixedRate(() -> {
      final List<ChatMessage> batch = new ArrayList<>();

      ChatMessage msg;

      while ((msg = messages.poll()) != null) {
        batch.add(msg);
      }

      if (batch.isEmpty()) {
        return;
      }

      DataRepository.getInstance().writeLogs(batch);

    }, 0, 5, TimeUnit.SECONDS);
  }

  public void stop() {
    scheduler.shutdown();
  }

  public void queryMessage(final @NotNull ChatMessage chatMessage) {
    messages.add(chatMessage);
  }
}
