package club.revived.commons.game.player;

import java.util.List;
import java.util.UUID;

import club.revived.commons.data.model.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

@Repository("profile")
public record PlayerProfile(
    @Identifier UUID uuid,
    String name,
    long lastLogin,
    List<String> permissionGroups) implements Entity {
}
