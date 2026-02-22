package club.revived.concordia.v2;

import org.jetbrains.annotations.NotNull;

public final class CachableBuilder<T> {

  private final CachingService cachingService;
  private String key;
  private T value;
  private Long ttl;
  private Runnable onComplete;

  public CachableBuilder(final @NotNull CachingService cachingService) {
    this.cachingService = cachingService;
  }

  @NotNull
  public CachableBuilder<T> key(final @NotNull String key) {
    this.key = key;
    return this;
  }

  @NotNull
  public CachableBuilder<T> ttl(final long ttl) {
    if (ttl < 0) {
      throw new IllegalArgumentException("TTL must be >= 0");
    }

    this.ttl = ttl;
    return this;
  }

  @NotNull
  public CachableBuilder<T> onComplete(final @NotNull Runnable onComplete) {
    this.onComplete = onComplete;
    return this;
  }

  @NotNull
  private CachableBuilder<T> value(final @NotNull T value) {
    this.value = value;
    return this;
  }

  public void complete() {

  }
}
