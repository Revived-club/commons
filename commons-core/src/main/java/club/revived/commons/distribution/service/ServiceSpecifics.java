package club.revived.commons.distribution.service;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import club.revived.commons.distribution.game.OnlinePlayer;
import club.revived.commons.game.GameType;

public final class ServiceSpecifics {

  @Nullable
  private List<GameType> allowedGames;

  @Nullable
  private List<OnlinePlayer> onlinePlayers;

  private ServiceSpecifics() {
  }

  public List<GameType> getAllowedGames() {
    return allowedGames;
  }

  public List<OnlinePlayer> getOnlinePlayers() {
    return onlinePlayers;
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public Builder toBuilder() {
    return new Builder()
        .allowedGames(this.allowedGames)
        .onlinePlayers(this.onlinePlayers);
  }

  public static final class Builder {
    private List<GameType> allowedGames = null;
    private List<OnlinePlayer> onlinePlayers = null;

    private Builder() {
    }

    @NotNull
    public Builder onlinePlayers(final @NotNull List<OnlinePlayer> onlinePlayers) {
      this.onlinePlayers = onlinePlayers;
      return this;
    }

    @NotNull
    public Builder allowedGames(final @NotNull List<GameType> allowedGames) {
      this.allowedGames = allowedGames;
      return this;
    }

    @NotNull
    public ServiceSpecifics build() {
      final ServiceSpecifics specifics = new ServiceSpecifics();
      specifics.allowedGames = this.allowedGames;
      specifics.onlinePlayers = this.onlinePlayers;

      return specifics;
    }
  }
}
