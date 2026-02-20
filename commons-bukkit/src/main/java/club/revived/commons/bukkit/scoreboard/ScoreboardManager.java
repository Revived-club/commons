package club.revived.commons.bukkit.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.bukkit.item.ColorUtils;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.game.PlayerManager;
import club.revived.commons.scoreboard.Scoreboard;
import fr.mrmicky.fastboard.adventure.FastBoard;

public final class ScoreboardManager {

  private static ScoreboardManager instance;
  private final Map<Scoreboard, List<UUID>> scoreboards = new ConcurrentHashMap<>();
  private final Map<UUID, FastBoard> fastboards = new ConcurrentHashMap<>();

  private final ScoreboardUpdateTask scoreboardUpdateTask;

  private ScoreboardManager() {
    instance = this;

    this.scoreboardUpdateTask = new ScoreboardUpdateTask(this);
  }

  @NotNull
  public static ScoreboardManager getInstance() {
    if (instance == null) {
      new ScoreboardManager();
      return instance;
    }
    return instance;
  }

  public void apply(final Player player) {
    final UUID uuid = player.getUniqueId();
    final var networkPlayer = PlayerManager.getInstance().fromBukkitPlayer(player);

    if (!this.uuids.containsKey(uuid)) {
      this.uuids.put(uuid, new FastBoard(player));
    }

    final var board = this.uuids.get(uuid);

    board.updateTitle(ColorUtils.parse("<shadow:black><#3B82F6><bold>Revived.club</bold></shadow:black>"));
    board.updateLines(List.of(
        ColorUtils.parse("<shadow:black><dark_gray>" + Cluster.getInstance().getServiceId()),
        ColorUtils.empty(),
        ColorUtils.parse("<shadow:black><white>Wins: <green>349834"),
        ColorUtils.parse("<shadow:black><white>Losses: <red>56"),
        ColorUtils.parse("<shadow:black><white>Queued: <green>true"),
        ColorUtils.empty(),
        ColorUtils.parse("<shadow:black><dark_gray>revived.club")));

  }

  public Map<Scoreboard, List<UUID>> getScoreboards() {
    return scoreboards;
  }

  public Map<UUID, FastBoard> getFastboards() {
    return fastboards;
  }

  public ScoreboardUpdateTask getScoreboardUpdateTask() {
    return scoreboardUpdateTask;
  }
}
