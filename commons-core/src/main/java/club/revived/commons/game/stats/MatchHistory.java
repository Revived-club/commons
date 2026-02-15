package club.revived.commons.game.stats;

import java.util.List;
import java.util.UUID;

import club.revived.commons.game.GameType;
import club.revived.commons.game.KitType;
import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

@Repository("match_history")
public record MatchHistory(@Identifier UUID uuid, List<MatchHistoryEntry> entries) implements Entity {

  public record MatchHistoryEntry(
      GameType gameType,
      KitType kitType,
      List<UUID> participants,
      List<UUID> winners,
      String id,
      boolean invalidated) {
  }
}
