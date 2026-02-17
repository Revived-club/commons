package club.revived.commons.velocity.motd;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import club.revived.commons.shared.SharedDataManager;

public final class MotdFetchTask {

  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public void start() {
    scheduler.scheduleAtFixedRate(() -> {
      SharedDataManager.getInstance().getSharedData().thenAccept(data -> {
        if (data == null) {
          return;
        }

        MotdManager.getInstance().setMotd(data.motd());
      });

    }, 0, 5, TimeUnit.SECONDS);
  }

  public void stop() {
    scheduler.shutdown();
  }
}
