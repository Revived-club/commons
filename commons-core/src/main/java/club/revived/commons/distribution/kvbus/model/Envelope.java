package club.revived.commons.distribution.kvbus.model;

import java.util.UUID;

public record Envelope(
    UUID correlationId,
    String senderId,
    String targetId,
    String payloadType,
    String payloadJson) {
}
