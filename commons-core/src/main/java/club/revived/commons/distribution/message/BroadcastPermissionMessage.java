package club.revived.commons.distribution.message;

import club.revived.commons.distribution.kvbus.model.Message;

public record BroadcastPermissionMessage(String content, String permission) implements Message {
}
