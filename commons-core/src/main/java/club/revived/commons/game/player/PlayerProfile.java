package club.revived.commons.game.player;

import java.util.List;
import java.util.UUID;

import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;
import club.revived.commons.punishment.model.Punishment;

@Repository("profile")
public record PlayerProfile(
    @Identifier UUID uuid,
    String name,
    long lastLogin,
    List<Punishment> activePunishments) implements Entity {
}
