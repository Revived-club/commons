package club.revived.commons.game;

import java.util.List;
import java.util.UUID;

import club.revived.commons.orm.annotations.Identifier;

public record GameData(
    @Identifier String id,
    GameType gameType,
    GameState gameState,
    List<UUID> participants,
    List<UUID> spectators) {
}
