package club.revived.commons.bukkit.scoreboard;

import java.util.concurrent.ScheduledFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.scoreboard.ScoreboardManager;

public final class BukkitScoreboardManager extends ScoreboardManager {

  @Override
  public @NotNull ScheduledFuture<?> startTask() {
    return new ScoreboardUpdateTask(this).start();
  }
}
