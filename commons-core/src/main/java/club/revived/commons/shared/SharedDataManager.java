package club.revived.commons.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.data.DataRepository;
import club.revived.commons.distribution.Cluster;
import club.revived.concordia.api.Concordia;

// This class is lwk abt crammed n shit. Not thinking of doing it otherwise really... It's just for saving shitty global data
public final class SharedDataManager {

  private final Concordia concordia = Concordia.instance();

  private static SharedDataManager instance;
  private static final String ID = "shared";

  private void init() {

  }

  @NotNull
  public CompletableFuture<SharedData> loadSharedData() {
    return DataRepository.getInstance()
        .get(SharedData.class, ID)
        .thenApply(opt -> opt.orElseGet(() -> new SharedData(
            ID,
            List.of(),
            "<red>An Error occurred while loading the MOTD!</red>",
            false,
            List.of())));
  }

  public void initSharedData() {
    this.loadSharedData().thenAccept(data -> {
      Cluster.getInstance().getGlobalCache().set(ID, data);
    });
  }

  @NotNull
  public CompletableFuture<SharedData> getSharedData() {
    return Cluster.getInstance().getGlobalCache()
        .get(SharedData.class, ID)
        .thenCompose(data -> {
          if (data == null) {
            return loadSharedData();
          }
          return CompletableFuture.completedFuture(data);
        });
  }

  public void saveSharedData(final @NotNull SharedData data) {
    data.save();
    Cluster.getInstance().getGlobalCache().set(ID, data);
  }

  public void setMotd(final @NotNull String motd) {
    this.getSharedData().thenAccept(data -> saveSharedData(new SharedData(
        data.id(),
        data.filteredWords(),
        motd,
        data.whitelist(),
        data.whitelistedPlayers())));
  }

  public void setWhitelist(final boolean enabled) {
    this.getSharedData().thenAccept(data -> saveSharedData(new SharedData(
        data.id(),
        data.filteredWords(),
        data.motd(),
        enabled,
        data.whitelistedPlayers())));
  }

  public void addWhitelistedPlayer(final @NotNull UUID player) {
    this.getSharedData().thenAccept(data -> {
      if (data.whitelistedPlayers().contains(player)) {
        return;
      }

      final List<UUID> updated = new ArrayList<>(data.whitelistedPlayers());
      updated.add(player);

      this.saveSharedData(new SharedData(
          data.id(),
          data.filteredWords(),
          data.motd(),
          data.whitelist(),
          updated));
    });
  }

  public void removeWhitelistedPlayer(final @NotNull UUID player) {
    this.getSharedData().thenAccept(data -> {
      List<UUID> updated = new ArrayList<>(data.whitelistedPlayers());
      if (!updated.remove(player)) {
        return;
      }

      this.saveSharedData(new SharedData(
          data.id(),
          data.filteredWords(),
          data.motd(),
          data.whitelist(),
          updated));
    });
  }

  public void addFilteredWord(final @NotNull String word) {
    this.getSharedData().thenAccept(data -> {
      if (data.filteredWords().contains(word)) {
        return;
      }

      final List<String> updated = new ArrayList<>(data.filteredWords());
      updated.add(word);

      this.saveSharedData(new SharedData(
          data.id(),
          updated,
          data.motd(),
          data.whitelist(),
          data.whitelistedPlayers()));
    });
  }

  public void removeFilteredWord(final @NotNull String word) {
    this.getSharedData().thenAccept(data -> {
      final List<String> updated = new ArrayList<>(data.filteredWords());

      if (!updated.remove(word)) {
        return;
      }

      this.saveSharedData(new SharedData(
          data.id(),
          updated,
          data.motd(),
          data.whitelist(),
          data.whitelistedPlayers()));
    });
  }

  public static SharedDataManager getInstance() {
    if (instance == null) {
      instance = new SharedDataManager();
    }
    return instance;
  }
}
