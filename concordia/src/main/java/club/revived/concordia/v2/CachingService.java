package club.revived.concordia.v2;

import org.jetbrains.annotations.NotNull;

import club.revived.concordia.provider.PubSubProvider;
import club.revived.concordia.provider.StorageProvider;

public final class CachingService {

  private final PubSubProvider pubSubProvider;
  private final StorageProvider storageProvider;

  public CachingService(
      final @NotNull PubSubProvider pubSubProvider,
      final @NotNull StorageProvider storageProvider) {
    this.pubSubProvider = pubSubProvider;
    this.storageProvider = storageProvider;
  }

  public void cache(final @NotNull String key, final @NotNull Object value, final long ttl) {
    final var entry = CacheEntry.newBuilder()    
  }

}
