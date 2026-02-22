package club.revived.situlae.model;

import java.time.Instant;
import java.util.Map;

public record QueryFilter(
    Instant start,
    Instant end,
    Boolean timed,
    Map<String, Object> fields) {
}
