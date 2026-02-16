package club.revived.commons.game;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.game.GameService;

public final class GameManager {

  public GameManager() {
  }

  @NotNull
  public Optional<GameService> getAvailableService() {
    return Cluster.getInstance()
        .getServices()
        .values()
        .stream()
        .filter(service -> service instanceof GameService)
        .map(service -> (GameService) service)
        .sorted(Comparator.comparingInt(service -> service.getOnlinePlayers().size()))
        .findFirst();
  }

  @NotNull
  public Optional<GameService> getAvailableService(final GameType gameType) {
    return Cluster.getInstance()
        .getServices()
        .values()
        .stream()
        .filter(
            service -> service instanceof GameService gameService &&
                gameService.getAcceptedGames().contains(gameType))
        .map(service -> (GameService) service)
        .sorted(Comparator.comparingInt(service -> service.getOnlinePlayers().size()))
        .findFirst();
  }

  @NotNull
  public CompletableFuture<Optional<GameData>> getGameData(final String id) {
    return Cluster.getInstance().getGlobalCache()
        .getById(GameData.class, id)
        .thenApply(game -> Optional.ofNullable(game));
  }
}
