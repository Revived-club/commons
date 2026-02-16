package club.revived.commons.distribution.microservice;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.service.Service;
import club.revived.commons.distribution.service.ServiceSpecifics;
import club.revived.commons.distribution.service.ServiceType;

public final class MicroService extends Service {

  public MicroService(final @NotNull String id) {
    super(
        id,
        "localhost",
        ServiceType.MICROSERVICE,
        ServiceSpecifics.builder().build());
  }
}
