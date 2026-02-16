package club.revived.commons.distribution.message;

import club.revived.commons.distribution.kvbus.model.Response;

public record WhereIsResponse(String server) implements Response {
}
