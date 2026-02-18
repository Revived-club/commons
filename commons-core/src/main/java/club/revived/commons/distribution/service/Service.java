package club.revived.commons.distribution.service;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NonBlocking;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.kvbus.model.Message;
import club.revived.commons.distribution.kvbus.model.Request;
import club.revived.commons.distribution.kvbus.model.Response;

public abstract class Service {

  private final String id;
  private final String ip;
  private final ServiceType type;
  private final ServiceSpecifics specifics;
  private final ServiceStatus status;

  public Service(
      final @NotNull String id,
      final @NotNull String ip,
      final @NotNull ServiceType type,
      final @NotNull ServiceStatus status,
      final @NotNull ServiceSpecifics specifics) {
    this.specifics = specifics;
    this.id = id;
    this.ip = ip;
    this.type = type;
    this.status = status;
  }

  @NotNull
  public <T extends Response> CompletableFuture<T> sendRequest(
      final Request request,
      final Class<T> responseType) {
    return Cluster.getInstance().getMessagingService().sendRequest(
        this.id,
        request,
        responseType);
  }

  public void sendMessage(final Message message) {
    Cluster.getInstance().getMessagingService().sendMessage(this.id, message);
  }

  @NotNull
  public ServiceSpecifics getSpecifics() {
    return this.specifics;
  }

  @NotNull
  public String getId() {
    return id;
  }

  @NotNull
  public String getIp() {
    return ip;
  }

  @NotNull
  public ServiceType getType() {
    return type;
  }

  @NotNull
  public ServiceStatus getStatus() {
    return status;
  }
}
