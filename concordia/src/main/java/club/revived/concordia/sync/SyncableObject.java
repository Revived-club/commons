package club.revived.concordia.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import club.revived.concordia.internal.CachingService;

public abstract class SyncableObject<T> {

  @Nullable
  private volatile T cached = null;
  private final List<Consumer<T>> reloadListeners = new ArrayList<>();
  protected final CachingService cachingService;

  protected SyncableObject(final @NotNull CachingService cachingService) {
    this.cachingService = cachingService;
  }

  public abstract @NotNull String key();

  public abstract @NotNull Class<T> type();

  protected abstract byte[] serialize(final @NotNull T model);

  protected abstract @Nullable T deserialize(final byte[] data);

  protected abstract @NotNull T defaultInstance();

  protected void onReloaded(final @NotNull T model) {
  }

  public AutoCloseable addReloadListener(final @NotNull Consumer<T> listener) {
    reloadListeners.add(listener);
    return () -> reloadListeners.remove(listener);
  }

  public @NotNull T cached() {
    if (cached == null) {
      throw new IllegalStateException("Cached value is null for key: " + key());
    }

    return cached;
  }

  public void editAndSave(final @NotNull Consumer<T> editor) {
    final T model = cached();

    editor.accept(model);
    this.sync(model);
  }

  public void sync(final @NotNull T model) {
    this.cached = model;
    cachingService.updateSyncable(key(), serialize(model));
  }

  public void load() {
    cachingService.watch(key(), (_, data) -> {
      T model = deserialize(data);
      if (model != null) {
        this.reload(model);
      }

    });
    if (cached == null) {
      this.sync(defaultInstance());
    }
  }

  public void onRemoteUpdate(final byte[] data) {
    final T model = deserialize(data);
    if (model != null) {
      this.reload(model);
    }
  }

  private void reload(final @NotNull T model) {
    this.cached = model;

    try {
      for (final Consumer<T> listener : reloadListeners) {
        listener.accept(model);
      }

      this.onReloaded(model);

    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
