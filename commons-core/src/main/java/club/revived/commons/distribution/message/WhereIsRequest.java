package club.revived.commons.distribution.message;

import java.util.UUID;

import club.revived.commons.distribution.kvbus.model.Request;

public record WhereIsRequest(UUID uuid) implements Request {
}
