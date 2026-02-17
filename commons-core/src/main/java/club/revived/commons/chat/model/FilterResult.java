package club.revived.commons.chat.model;

import java.util.UUID;

public record FilterResult(
    UUID uuid,
    String message,
    String filteredMessage,
    boolean blocked) {
}
