package club.revived.commons.distribution.message;

import java.util.UUID;

public record PlayerErrorMessage(UUID uuid, String error) {
}
