package club.revived.commons.distribution.service;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import club.revived.commons.distribution.game.GameService;
import club.revived.commons.distribution.game.LobbyService;
import club.revived.commons.distribution.game.OnlinePlayer;
import club.revived.commons.distribution.game.ProxyService;
import club.revived.commons.distribution.heartbeat.Heartbeat;
import club.revived.commons.distribution.microservice.MicroService;
import club.revived.commons.game.GameType;

public final class ServiceFactory {

  private ServiceFactory() {
  }

  @NotNull
  public static Service createService(
      final @NotNull String id,
      final @NotNull String ip,
      final @NotNull ServiceType type,
      final @Nullable List<OnlinePlayer> onlinePlayers,
      final @Nullable List<GameType> games) {
    return switch (type) {
      case PROXY -> new ProxyService(id, ip);
      case GAME -> new GameService(id, ip, type, games, onlinePlayers);
      case MICROSERVICE -> new MicroService(id);
      case LOBBY, LIMBO -> new LobbyService(id, ip, type, onlinePlayers);

      default -> throw new IllegalArgumentException("Unsupported service type: " + type);
    };
  }

  @NotNull
  public static Service createService(final @NotNull Heartbeat heartbeat) {
    return createService(heartbeat.id(), heartbeat.serverIp(), heartbeat.serviceType(), heartbeat.specifics());

  }

  @NotNull
  public static Service createService(
      final @NotNull String id,
      final @NotNull String ip,
      final @NotNull ServiceType type,
      final @NotNull ServiceSpecifics specifics) {
    return createService(id, ip, type, specifics.getOnlinePlayers(), specifics.getAllowedGames());
  }
}
