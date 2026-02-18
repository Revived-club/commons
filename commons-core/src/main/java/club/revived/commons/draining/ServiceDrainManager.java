package club.revived.commons.draining;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.message.ShutdownMessage;

public abstract class ServiceDrainManager {

  protected boolean isDraining = false;

  public ServiceDrainManager() {
    this.initMessageHandlers();
  }

  private void initMessageHandlers() {
    Cluster.getInstance().getMessagingService().registerMessageHandler(ShutdownMessage.class, message -> {
      if (isDraining) {
        return;
      }

      isDraining = true;
      onDrainStart();
    });

  }

  protected abstract void onDrainStart();
}
