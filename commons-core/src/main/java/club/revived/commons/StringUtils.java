package club.revived.commons;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public final class StringUtils {

  @NotNull
  public static String generateId(final String prefix) {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final StringBuilder builder = new StringBuilder(8);
    builder.append(prefix);

    for (int i = 0; i < 8; ++i) {
      final var chars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
      final int randomIndex = random.nextInt(chars.length());
      final char randomChar = chars.charAt(randomIndex);

      builder.append(randomChar);
    }

    return builder.toString();
  }
}
