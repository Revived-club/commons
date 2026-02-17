package club.revived.commons.distribution.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import club.revived.commons.distribution.exception.PlayerNotFoundException;

public final class PlayerManager {

  private final Map<UUID, OnlinePlayer> onlinePlayers = new HashMap<>();

  private static PlayerManager instance;

  public PlayerManager() {
  }

  public void registerPlayer(final OnlinePlayer onlinePlayer) {
    this.onlinePlayers.put(onlinePlayer.uuid(), onlinePlayer);
  }

  @Nullable
  public Optional<OnlinePlayer> getOptional(final UUID uuid) {
    return Optional.ofNullable(this.onlinePlayers.get(uuid));
  }

  @Nullable
  public OnlinePlayer get(final UUID uuid) {
    final var onlinePlayer = this.onlinePlayers.get(uuid);

    return onlinePlayer;
  }

  @NotNull
  public OnlinePlayer getExceptionally(final UUID uuid) {
    final var onlinePlayer = this.onlinePlayers.get(uuid);

    if (onlinePlayer == null) {
      throw new PlayerNotFoundException();
    }

    return onlinePlayer;
  }

  @Nullable
  public OnlinePlayer withName(final String name) {
    final var players = this.onlinePlayers.values()
        .stream()
        .filter(networkPlayer -> networkPlayer.username().equalsIgnoreCase(name))
        .toList();

    if (players.isEmpty()) {
      return null;
    }

    return players.getFirst();
  }

  public static PlayerManager getInstance() {
    if (instance == null) {
      instance = new PlayerManager();
      return instance;
    }

    return instance;
  }

  public Map<UUID, OnlinePlayer> getOnlinePlayers() {
    return onlinePlayers;
  }
}
