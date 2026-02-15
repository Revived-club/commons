package club.revived.commons.game.stats;

import org.jetbrains.annotations.NotNull;

public final class StatsConverter {

  @NotNull
  public static Stats convert(final MatchHistory matchHistory) {
    final var entries = matchHistory.entries();
    final int totalMatches = entries.size();

    int wins = 0;
    int losses = 0;

    for (final var match : entries) {
      if (match.invalidated()) {
        continue;
      }

      if (match.winners().contains(matchHistory.uuid())) {
        wins++;
      } else {
        losses++;
      }
    }

    return new Stats(
        matchHistory.uuid(),
        wins,
        losses,
        totalMatches > 0 ? (double) wins / totalMatches : 0.0,
        totalMatches);
  }
}
