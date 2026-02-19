package club.revived.commons.bukkit.scoreboard;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.bukkit.item.ColorUtils;
import club.revived.commons.scoreboard.ScoreboardManager;
import fr.mrmicky.fastboard.adventure.FastBoard;

public final class ScoreboardUpdateTask {

  private final ScheduledExecutorService subServer = Executors.newSingleThreadScheduledExecutor();
  private final ScoreboardManager scoreboardManager;

  public ScoreboardUpdateTask(final ScoreboardManager scoreboardManager) {
    this.scoreboardManager = scoreboardManager;
  }

  @NotNull
  public ScheduledFuture<?> start() {
    return this.subServer.scheduleAtFixedRate(() -> {
      this.scoreboardManager.getScoreboards().forEach((scoreboard, uuids) -> {
        for (final var uuid : uuids) {
          final var player = Bukkit.getPlayer(uuid);

          if (player == null) {
            continue;
          }

          ScoreboardManager.getInstance().getFastboards().forEach((ids, board) -> {

          });

          final var fastboard = ScoreboardManager.getInstance().getFastboards()
              .computeIfAbsent(uuid, id -> {
                final var board = new FastBoard(player);
                final var lines = Arrays.stream(scoreboard.lines())
                    .map(s -> ColorUtils.parse(s))
                    .toList();

                board.updateTitle(ColorUtils.parse(scoreboard.title()));
                board.updateLines(lines);

                return board;
              });
        }

      });
    }, 0, 1, TimeUnit.SECONDS);
  }

}
