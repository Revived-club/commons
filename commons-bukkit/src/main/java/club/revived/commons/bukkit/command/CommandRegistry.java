package club.revived.commons.bukkit.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import io.papermc.paper.command.brigadier.CommandSourceStack;

public final class CommandRegistry {

  private final PaperCommandManager<CommandSourceStack> commandManager;
  private final AnnotationParser<CommandSourceStack> annotationParser;

  private final List<Class<?>> commands = new ArrayList<>();

  public CommandRegistry(final JavaPlugin plugin) {
    this.commandManager = PaperCommandManager.builder()
        .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
        .buildOnEnable(plugin);

    this.annotationParser = new AnnotationParser<>(
        this.commandManager,
        CommandSourceStack.class);
  }

  public void register(final Class<?> clazz) {
    this.commands.add(clazz);
  }

  public void init() {
    for (final var clazz : this.commands) {
      this.annotationParser.parse(clazz);
    }
  }

}
