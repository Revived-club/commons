package club.revived.commons.distribution.message;

import java.util.UUID;

import club.revived.commons.distribution.kvbus.model.Message;

public record SendTitle(
    UUID uuid,
    String title,
    String subtitle,
    int fadeIn,
    int stay,
    int fadeOut) implements Message {
}
