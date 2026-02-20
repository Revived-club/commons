package club.revived.commons.scoreboard;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public abstract class ScoreboardManager {

  private final Map<Scoreboard, List<UUID>> scoreboards = new ConcurrentHashMap<>();

  public ScoreboardManager() {
    this.startTask();
  }

  public abstract ScheduledFuture<?> startTask();

  public void update(final UUID uuid, final Scoreboard scoreboard) {

  }

  public Map<Scoreboard, List<UUID>> getScoreboards() {
    return scoreboards;
  }
}
