package club.revived.commons.game.stats;

import java.util.UUID;

import org.bson.UuidRepresentation;

import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

@Repository("stats")
public record Stats(
    @Identifier UUID uuid,
    int wins,
    int losses,
    double winRate,
    int totalMatches) implements Entity {

  public static void main(String[] args) {
    final var eiawuhawe = new Stats(UUID.randomUUID(), 1, 2, 2.0, 11);
    eiawuhawe.save();
  }
}
