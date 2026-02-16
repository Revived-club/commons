package club.revived.commons.distribution.heartbeat;

import club.revived.commons.distribution.service.ServiceSpecifics;
import club.revived.commons.distribution.service.ServiceType;

public record Heartbeat(
    long timestamp,
    ServiceType serviceType,
    String id,
    String serverIp,
    ServiceSpecifics specifics) {
}
