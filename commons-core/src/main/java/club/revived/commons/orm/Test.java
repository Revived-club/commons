package club.revived.commons.orm;

import club.revived.commons.orm.annotations.Collection;
import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.annotations.Identifier;

@Collection("test")
public record Test(@Identifier String id, String name) implements Entity {
}
