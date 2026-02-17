package club.revived.commons.game.stats;

import java.util.UUID;

import club.revived.commons.data.model.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

@Repository("stats")
public record Stats(
    @Identifier UUID uuid,
    int wins,
    int losses,
    double winRate,
    int totalMatches) implements Entity {

}
