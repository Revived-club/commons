package club.revived.commons.distribution.game;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.service.Service;
import club.revived.commons.distribution.service.ServiceSpecifics;
import club.revived.commons.distribution.service.ServiceStatus;
import club.revived.commons.distribution.service.ServiceType;
import club.revived.commons.game.GameType;

public final class GameService extends Service {

  private final List<OnlinePlayer> onlinePlayers;
  private final List<GameType> acceptedGames;

  public GameService(
      final @NotNull String id,
      final @NotNull String ip,
      final @NotNull ServiceType type,
      final @NotNull ServiceStatus status,
      final @NotNull List<GameType> games,
      final List<OnlinePlayer> onlinePlayers) {
    super(
        id,
        ip,
        type,
        ServiceStatus.STARTING,
        ServiceSpecifics.builder()
            .allowedGames(games)
            .build());

    this.acceptedGames = games;
    this.onlinePlayers = onlinePlayers;
  }

  public List<OnlinePlayer> getOnlinePlayers() {
    return onlinePlayers;
  }

  public List<GameType> getAcceptedGames() {
    return acceptedGames;
  }
}
