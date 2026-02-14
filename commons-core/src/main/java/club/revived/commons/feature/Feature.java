package club.revived.commons.feature;

import org.jetbrains.annotations.NotNull;

public abstract class Feature {

  private final @NotNull String identifier;

  public Feature(final @NotNull String identifier) {
    this.identifier = identifier;
  }

  public abstract void onEnable();

  public abstract void onDisable();

}
