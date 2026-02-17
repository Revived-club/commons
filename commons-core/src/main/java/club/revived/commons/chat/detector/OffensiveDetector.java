package club.revived.commons.chat.detector;

import club.revived.commons.chat.model.ChatViolationDetector;
import club.revived.commons.chat.model.Violation;
import club.revived.commons.chat.model.ViolationType;
import club.revived.commons.shared.SharedData;
import club.revived.commons.shared.SharedDataManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class OffensiveDetector implements ChatViolationDetector {

  @Override
  public @NotNull CompletableFuture<@Nullable Violation> detect(final @NotNull String message) {
    final String normalized = message.toLowerCase(Locale.ROOT);
    final String[] tokens = this.tokenize(normalized);

    return SharedDataManager.getInstance()
        .getSharedData()
        .thenApply(sharedData -> this.process(sharedData, normalized, tokens));
  }

  @Nullable
  private Violation process(
      final @Nullable SharedData sharedData,
      final @NotNull String normalized,
      final @NotNull String[] tokens) {

    if (sharedData == null) {
      return null;
    }

    final List<String> filteredWords = sharedData.filteredWords();
    if (filteredWords == null || filteredWords.isEmpty()) {
      return null;
    }

    for (final String token : tokens) {
      for (final String filtered : filteredWords) {
        final String banned = filtered.toLowerCase(Locale.ROOT);
        if (token.equals(banned) || normalized.contains(banned)) {
          return new Violation(
              ViolationType.OFFENSIVE_LANGUAGE,
              banned,
              System.currentTimeMillis());
        }
      }
    }

    return null;
  }

  private @NotNull String[] tokenize(final @NotNull String text) {
    return text.split("[\\s\\p{Punct}]+");
  }
}
