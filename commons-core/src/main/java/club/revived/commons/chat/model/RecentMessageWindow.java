package club.revived.commons.chat.model;

import java.util.LinkedList;

import org.jetbrains.annotations.NotNull;

public record RecentMessageWindow(
    LinkedList<String> messages,
    int maxSize) {

  public void addMessage(final @NotNull String message) {
    if (messages.size() >= maxSize) {
      messages.removeFirst();
    }
    messages.addLast(message);
  }
}
