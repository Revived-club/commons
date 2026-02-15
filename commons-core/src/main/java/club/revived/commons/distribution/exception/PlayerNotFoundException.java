package club.revived.commons.distribution.exception;

public class PlayerNotFoundException extends RuntimeException {

  public PlayerNotFoundException() {
    super("Player not found");
  }
}
