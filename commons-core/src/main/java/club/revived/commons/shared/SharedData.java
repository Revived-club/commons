package club.revived.commons.shared;

import java.util.List;
import java.util.UUID;

import club.revived.commons.data.model.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

@Repository("shared")
public record SharedData(
    @Identifier String id,
    List<String> filteredWords,
    String motd,
    boolean whitelist,
    List<UUID> whitelistedPlayers) implements Entity {
}
