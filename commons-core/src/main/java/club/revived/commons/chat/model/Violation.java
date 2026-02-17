package club.revived.commons.chat.model;

public record Violation(
    ViolationType type,
    String matchedContent,
    long timestamp) {
}
