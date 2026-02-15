package club.revived.commons.distribution.game;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.service.Service;
import club.revived.commons.distribution.service.ServiceType;

public final class GameService extends Service {

  private final List<OnlinePlayer> onlinePlayers = new ArrayList<>();

  public GameService(@NotNull String id, @NotNull String ip, @NotNull ServiceType type,
      final List<OnlinePlayer> onlinePlayers) {
    super(id, ip, type);

    this.onlinePlayers.addAll(onlinePlayers);
  }

  public List<OnlinePlayer> getOnlinePlayers() {
    return onlinePlayers;
  }
}
