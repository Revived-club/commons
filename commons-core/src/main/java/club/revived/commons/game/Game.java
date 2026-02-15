package club.revived.commons.game;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public abstract class Game {

  protected final String id;
  protected final GameType type;
  protected GameState state;
  protected final List<UUID> participants;
  protected final List<UUID> spectators;

  public Game(
      final @NotNull String id,
      final @NotNull GameType type,
      final @NotNull GameState state,
      final @NotNull List<UUID> participants,
      final @NotNull List<UUID> spectators) {
    this.id = id;
    this.type = type;
    this.state = state;
    this.participants = participants;
    this.spectators = spectators;
  }

  public abstract void prepare();

  public abstract void start();

  public abstract void end();

  public abstract void discard();

  public boolean addParticipant(final @NotNull UUID uuid) {
    return this.participants.add(uuid);
  }

  public boolean removeParticipant(final @NotNull UUID uuid) {
    return this.participants.remove(uuid);
  }

  public boolean containsParticipant(final @NotNull UUID uuid) {
    return this.participants.contains(uuid);
  }

  public void addSpectator(final @NotNull UUID uuid) {
    this.spectators.add(uuid);
  }

  public void removeSpectator(final @NotNull UUID uuid) {
    this.spectators.remove(uuid);
  }

  public String getId() {
    return id;
  }

  public GameType getType() {
    return type;
  }

  public GameState getState() {
    return state;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  public List<UUID> getParticipants() {
    return participants;
  }

  public List<UUID> getSpectators() {
    return spectators;
  }

}
