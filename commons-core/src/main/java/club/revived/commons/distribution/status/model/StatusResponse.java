package club.revived.commons.distribution.status.model;

import club.revived.commons.distribution.kvbus.model.Response;
import club.revived.commons.distribution.service.ServiceStatus;

public record StatusResponse(ServiceStatus status) implements Response {
}
