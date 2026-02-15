package club.revived.commons.distribution.status;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.kvbus.pubsub.ServiceMessageBus;
import club.revived.commons.distribution.status.model.StatusRequest;
import club.revived.commons.distribution.status.model.StatusResponse;

public final class StatusService {

  public StatusService(final ServiceMessageBus messageBus) {
    messageBus.registerHandler(StatusRequest.class, statusRequest -> {
      final var status = Cluster.getInstance().getStatus();

      return new StatusResponse(status);
    });
  }
}
