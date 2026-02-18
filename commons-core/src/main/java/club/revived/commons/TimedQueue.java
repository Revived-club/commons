package club.revived.commons;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.NotNull;

public abstract class TimedQueue<T> {

  private final Queue<T> queue;
  private final ScheduledExecutorService scheduler;
  private final long period;
  private final TimeUnit unit;
  private ScheduledFuture<?> future;
  private final AtomicBoolean running = new AtomicBoolean(false);

  public TimedQueue(
      final @NotNull Collection<? extends T> elements,
      final @NotNull long period,
      final @NotNull TimeUnit unit) {
    this.queue = new ConcurrentLinkedQueue<>(elements);
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
    this.period = period;
    this.unit = unit;
  }

  public void start() {
    if (!running.compareAndSet(false, true)) {
      return;
    }

    future = scheduler.scheduleAtFixedRate(() -> {
      final T element = queue.poll();

      if (element == null) {
        this.stop();
        return;
      }

      try {
        this.handle(element);
      } catch (final Exception ex) {
        ex.printStackTrace();
      }

    }, 0, period, unit);
  }

  public void stop() {
    if (!running.compareAndSet(true, false)) {
      return;
    }

    if (future != null) {
      future.cancel(false);
    }

    scheduler.shutdown();
    this.onComplete();
  }

  public boolean isRunning() {
    return running.get();
  }

  protected abstract void handle(final T element);

  protected abstract void onComplete();
}
