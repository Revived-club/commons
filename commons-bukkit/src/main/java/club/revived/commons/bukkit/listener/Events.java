package club.revived.commons.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.influxdb.Cancellable;

import java.util.function.Consumer;

public class Events {

  private static Plugin plugin;

  public static void init(final @NotNull JavaPlugin plugin) {
    Events.plugin = plugin;
  }

  @NotNull
  public static <T extends Event> EventBuilder<T> subscribe(
      final @NotNull Class<T> clazz,
      final @NotNull EventPriority priority) {
    return new EventBuilder<>(clazz, priority);
  }

  public static class EventBuilder<T extends Event> {
    private final Class<T> eventClass;
    private final EventPriority priority;
    private boolean ignoreCancelled = false;

    public EventBuilder(final @NotNull Class<T> eventClass, final @NotNull EventPriority priority) {
      this.eventClass = eventClass;
      this.priority = priority;
    }

    public EventBuilder(final @NotNull Class<T> eventClass) {
      this(eventClass, EventPriority.NORMAL);
    }

    public EventBuilder<T> ignoreCancelled(final boolean value) {
      this.ignoreCancelled = value;
      return this;
    }

    public void handler(final @NotNull Consumer<T> handler) {
      Bukkit.getPluginManager().registerEvent(eventClass, new Listener() {
      }, priority, (l, event) -> {
        if (!eventClass.isInstance(event)) {
          return;
        }

        final T castEvent = eventClass.cast(event);

        if (ignoreCancelled && castEvent instanceof final Cancellable cancellable
            && cancellable.isCancelled()) {
          return;
        }

        handler.accept(castEvent);
      }, plugin);
    }
  }
}
