package club.revived.commons.scoreboard;

public record Scoreboard(
    String id,
    String title,
    String... lines) {
}
