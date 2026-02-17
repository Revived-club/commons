package club.revived.commons.chat;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public final class TextNormalizer {

  private static Map<Character, Character> replacements = new HashMap<>();

  static {
    replacements.put('4', 'a');
    replacements.put('3', 'e');
    replacements.put('1', 'i');
    replacements.put('!', 'i');
    replacements.put('|', 'i');
    replacements.put('0', 'o');
    replacements.put('5', 's');
    replacements.put('7', 't');
    replacements.put('@', 'a');
    replacements.put('$', 's');
    replacements.put('8', 'b');
    replacements.put('6', 'g');
    replacements.put('9', 'g');
  }

  @NotNull
  public String normalize(@NotNull final String input) {
    final StringBuilder normalized = new StringBuilder(input.length());

    for (final char c : input.toCharArray()) {
      if (replacements.containsKey(c)) {
        normalized.append(replacements.get(c));
      } else {
        normalized.append(c);
      }
    }

    return normalized.toString();
  }
}
