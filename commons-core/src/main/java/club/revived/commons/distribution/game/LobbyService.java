package club.revived.commons.distribution.game;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.service.Service;
import club.revived.commons.distribution.service.ServiceSpecifics;
import club.revived.commons.distribution.service.ServiceType;

public final class LobbyService extends Service {

  private final List<OnlinePlayer> onlinePlayers;

  public LobbyService(
      final @NotNull String id,
      final @NotNull String ip,
      final @NotNull ServiceType type,
      final List<OnlinePlayer> onlinePlayers) {
    super(
        id,
        ip,
        type,
        ServiceSpecifics.builder()
            .build());

    this.onlinePlayers = onlinePlayers;
  }

  public List<OnlinePlayer> getOnlinePlayers() {
    return onlinePlayers;
  }
}
