package club.revived.commons.distribution.message;

import java.util.UUID;

import club.revived.commons.distribution.kvbus.model.Message;

public record SendActionbar(UUID uuid, String content) implements Message {

}
