package club.revived.commons.game.arena;

import org.jetbrains.annotations.NotNull;

public abstract class Arena {

  protected final String id;
  protected final ArenaType type;
  protected final String parentId;

  public Arena(final @NotNull String id, final @NotNull String parentId, final @NotNull ArenaType arenaType) {
    this.id = id;
    this.parentId = parentId;
    this.type = arenaType;
  }

  public String getId() {
    return id;
  }

  public ArenaType getType() {
    return type;
  }

  public String getParentId() {
    return parentId;
  }
}
