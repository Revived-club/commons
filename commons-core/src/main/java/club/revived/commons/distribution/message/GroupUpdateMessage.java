package club.revived.commons.distribution.message;

import club.revived.commons.distribution.kvbus.model.Message;

public record GroupUpdateMessage(String groupId) implements Message {
}
