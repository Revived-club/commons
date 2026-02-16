package club.revived.commons.distribution.message;

import club.revived.commons.distribution.kvbus.model.Message;

public record BroadcastMessage(String content) implements Message {

}
