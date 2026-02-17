package club.revived.commons.chat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.ListUtils;
import club.revived.commons.chat.detector.LinkDetector;
import club.revived.commons.chat.detector.OffensiveDetector;
import club.revived.commons.chat.detector.SpamDetector;
import club.revived.commons.chat.model.FilterResult;
import club.revived.commons.chat.model.RecentMessageWindow;
import club.revived.commons.chat.model.Violation;

public final class ChatFilter {

  private final ConcurrentHashMap<UUID, RecentMessageWindow> messageWindows = new ConcurrentHashMap<>();
  private final TextNormalizer normalizer = new TextNormalizer();

  private final SpamDetector spamDetector = new SpamDetector();
  private final OffensiveDetector offensiveDetector = new OffensiveDetector();
  private final LinkDetector linkDetector = new LinkDetector();

  public ChatFilter() {

  }

  @NotNull
  public CompletableFuture<Violation> filterMessage(
      final @NotNull UUID uuid,
      final @NotNull String message) {

    final String normalizedMessage = this.normalizer.normalize(message);

    final RecentMessageWindow messages = this.messageWindows.computeIfAbsent(uuid,
        k -> new RecentMessageWindow(new LinkedList<>(), 3));

    final CompletableFuture<Violation> spamFuture = this.spamDetector.detect(normalizedMessage, messages);

    final CompletableFuture<Violation> offensiveFuture = this.offensiveDetector.detect(normalizedMessage);

    final CompletableFuture<Violation> linkFuture = this.linkDetector.detect(normalizedMessage);

    return spamFuture.thenCombine(offensiveFuture, (spam, offensive) -> {
      if (spam != null) {
        return spam;
      }

      return offensive;
    }).thenCombine(linkFuture, (first, link) -> {
      if (first != null) {
        return first;
      }

      return link;
    });
  }

}
