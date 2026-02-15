package club.revived.commons.distribution.heartbeat;

import java.util.List;

import club.revived.commons.distribution.game.OnlinePlayer;
import club.revived.commons.distribution.service.ServiceType;

public record Heartbeat(
    long timestamp,
    ServiceType serviceType,
    String id,
    int playerCount,
    List<OnlinePlayer> onlinePlayers,
    String serverIp) {
}
