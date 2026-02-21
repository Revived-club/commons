package club.revived.commons.shared;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import club.revived.concordia.api.Concordia;
import club.revived.concordia.sync.SyncableObject;

public final class ShardDataSyncService extends SyncableObject<SharedData> {

  public ShardDataSyncService() {
    super(Concordia.instance().getCachingService());
  }

  @Override
  protected @Nullable SharedData deserialize(final byte[] data) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public @NotNull String key() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void load() {
    // TODO Auto-generated method stub
    super.load();
  }

  @Override
  public @NotNull Class<SharedData> type() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected @NotNull SharedData defaultInstance() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected byte[] serialize(@NotNull SharedData model) {
    // TODO Auto-generated method stub
    return null;
  }

}
