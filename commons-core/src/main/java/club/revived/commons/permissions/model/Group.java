package club.revived.commons.permissions.model;

import java.util.List;

import club.revived.commons.data.model.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

@Repository("groups")
public record Group(
    @Identifier String id,
    String prefix,
    int weight,
    List<Permission> permissions) implements Entity {
}
