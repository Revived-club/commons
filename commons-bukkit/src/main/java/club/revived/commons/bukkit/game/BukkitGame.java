package club.revived.commons.bukkit.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.game.Game;
import club.revived.commons.game.GameState;
import club.revived.commons.game.GameType;

public abstract class BukkitGame extends Game {

  public BukkitGame(
      final @NotNull String id,
      final @NotNull GameType type,
      final @NotNull GameState state,
      final @NotNull List<UUID> participants) {
    super(id, type, state, participants, new ArrayList<>());
  }

  @NotNull
  public List<Player> getPlayers() {
    return this.participants.stream()
        .map(uuid -> Bukkit.getPlayer(uuid))
        .map(Objects::requireNonNull)
        .toList();
  }

  @NotNull
  public List<Player> getSpectatingPlayers() {
    return this.spectators.stream()
        .map(uuid -> Bukkit.getPlayer(uuid))
        .map(Objects::requireNonNull)
        .toList();
  }
}
