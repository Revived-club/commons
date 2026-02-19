package club.revived.commons.game;

import java.util.List;
import java.util.UUID;

import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

@Repository("reports")
public record GameReport(
    @Identifier String reportId,
    String matchId,
    UUID uuid,
    List<UUID> uuids,
    List<UUID> losers,
    long duration,
    KitType kitType) {
}
