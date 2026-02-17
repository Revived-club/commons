package club.revived.commons.shared;

import java.util.concurrent.CompletableFuture;

import club.revived.commons.distribution.Cluster;

// TODO: Implement modifying shared data
public final class SharedDataManager {

  private static SharedDataManager instance;
  private static final String ID = "shared";

  public CompletableFuture<SharedData> getSharedData() {
    return Cluster.getInstance().getGlobalCache()
        .get(SharedData.class, ID);
  }

  public static SharedDataManager getInstance() {
    if (instance == null) {
      instance = new SharedDataManager();
    }
    return instance;
  }
}
