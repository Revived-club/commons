package club.revived.commons.distribution.message;

import java.util.UUID;

import club.revived.commons.distribution.kvbus.model.Message;

public record ConnectMessage(UUID uuid, String id) implements Message {
}
