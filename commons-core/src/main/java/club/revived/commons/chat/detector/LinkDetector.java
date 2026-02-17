package club.revived.commons.chat.detector;

import club.revived.commons.chat.model.ChatViolationDetector;
import club.revived.commons.chat.model.Violation;
import club.revived.commons.chat.model.ViolationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LinkDetector implements ChatViolationDetector {

  private final Pattern urlPattern = Pattern.compile(
      "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+))",
      Pattern.CASE_INSENSITIVE);
  private final Pattern ipPattern = Pattern.compile(
      "\\b(?:\\d{1,3}\\.){3}\\d{1,3}(?::\\d+)?\\b");
  private final Pattern obfuscatedPattern = Pattern.compile(
      "(?i)\\b[a-z0-9]+\\s*(?:\\(|\\[)?\\s*(?:dot|d0t|\\.)\\s*(?:\\)|\\])?\\s*(?:com|net|org|gg|io|xyz|me|co)\\b",
      Pattern.CASE_INSENSITIVE);

  @Override
  public @NotNull CompletableFuture<@Nullable Violation> detect(final @NotNull String message) {
    final Set<String> detected = new HashSet<>();

    final Matcher urlMatcher = this.urlPattern.matcher(message);
    while (urlMatcher.find()) {
      detected.add(urlMatcher.group());
    }

    final Matcher ipMatcher = this.ipPattern.matcher(message);
    while (ipMatcher.find()) {
      detected.add(ipMatcher.group());
    }

    final Matcher obfMatcher = this.obfuscatedPattern.matcher(message);
    while (obfMatcher.find()) {
      detected.add(obfMatcher.group());
    }

    if (!detected.isEmpty()) {
      final String match = detected.iterator().next();
      return CompletableFuture.completedFuture(
          new Violation(
              ViolationType.LINK,
              match,
              System.currentTimeMillis()));
    }

    return CompletableFuture.completedFuture(null);
  }
}
