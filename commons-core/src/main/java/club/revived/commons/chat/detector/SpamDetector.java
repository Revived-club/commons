package club.revived.commons.chat.detector;

import club.revived.commons.NumberUtils;
import club.revived.commons.chat.model.RecentMessageWindow;
import club.revived.commons.chat.model.Violation;
import club.revived.commons.chat.model.ViolationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class SpamDetector {

  private final double levenshteinSpamThreshold = 0.15;
  private final double jaroWinklerSpamThreshold = 0.85;
  private final double weight1 = 0.5;
  private final double weight2 = 0.5;
  private final double spamConfidenceThreshold = 0.75;

  public @NotNull CompletableFuture<@Nullable Violation> detect(
      final @NotNull String message,
      final @NotNull RecentMessageWindow window) {

    if (window.messages().isEmpty()) {
      window.addMessage(message);
      return CompletableFuture.completedFuture(null);
    }

    double maxSpamScore = 0.0;
    String mostSimilarMessage = null;

    for (final String previous : window.messages()) {
      final double score = this.calculateSpamScore(message, previous);
      if (score > maxSpamScore) {
        maxSpamScore = score;
        mostSimilarMessage = previous;
      }
    }

    window.addMessage(message);

    if (maxSpamScore >= this.spamConfidenceThreshold && mostSimilarMessage != null) {
      return CompletableFuture.completedFuture(
          new Violation(
              ViolationType.SPAM,
              mostSimilarMessage,
              System.currentTimeMillis()));
    }

    return CompletableFuture.completedFuture(null);
  }

  private double calculateSpamScore(
      final @NotNull String message1,
      final @NotNull String message2) {

    if (message1.equals(message2)) {
      return 1.0;
    }

    final double normalizedLev = NumberUtils.normalizedLevenshtein(message1, message2);
    final double jaroWinkler = NumberUtils.jaroWinklerSimilarity(message1, message2);

    double levComponent = 1.0 - normalizedLev;

    if (levComponent <= this.levenshteinSpamThreshold) {
      levComponent = 1.0;
    } else {
      levComponent = 1.0 - levComponent;
    }

    final double jwComponent = jaroWinkler >= this.jaroWinklerSpamThreshold ? jaroWinkler : 0.0;

    return (this.weight1 * levComponent) + (this.weight2 * jwComponent);
  }
}
