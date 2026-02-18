package club.revived.commons.bukkit.command.impl;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.service.Service;
import club.revived.commons.distribution.service.ServiceType;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

@Command("service")
@Permission("commons.command.service")
public final class ServiceCommand {

  @Command("service list")
  public void listServices(final CommandSender sender) {
    final var services = Cluster.getInstance().getServices();

    if (services.isEmpty()) {
      sender.sendRichMessage("<red>No services are currently registered.");
      return;
    }

    sender.sendRichMessage(String.format("<gold>--- Services (%d) ---", services.size()));

    for (final Service service : services.values()) {
      final String statusColor = switch (service.getStatus()) {
        case RUNNING -> "green";
        case STARTING -> "yellow";
        case LOCKED -> "gold";
        case SHUTTING_DOWN -> "red";
        default -> "gray";
      };

      sender.sendRichMessage(String.format("""
          <dark_gray>[<aqua>%s</aqua>]</dark_gray> <white>%s</white> <gray>-</gray> <%s>%s</%s>""",
          service.getType().name(),
          service.getId(),
          statusColor, service.getStatus().name(), statusColor));
    }
  }

  @Command("service info <id>")
  public void serviceInfo(final CommandSender sender, final @Argument("id") String id) {
    final Service service = Cluster.getInstance().getServices().get(id);

    if (service == null) {
      sender.sendRichMessage(String.format("<red>No service found with ID: <white>%s", id));
      return;
    }

    final String statusColor = switch (service.getStatus()) {
      case RUNNING -> "green";
      case STARTING -> "yellow";
      case LOCKED -> "gold";
      case SHUTTING_DOWN -> "red";
      default -> "gray";
    };

    sender.sendRichMessage(String.format("""
        <gold>--- Service Info ---
        <gray>ID: <white>%s
        <gray>Type: <aqua>%s
        <gray>Status: <%s>%s</%s>
        <gray>IP: <white>%s
        <gray>Players: <white>%d""",
        service.getId(),
        service.getType().name(),
        statusColor, service.getStatus().name(), statusColor,
        service.getIp(),
        service.getSpecifics().getOnlinePlayers().size()));
  }

  @Command("service list <type>")
  public void listByType(
      final CommandSender sender,
      final @Argument("type") ServiceType type) {
    final var services = Cluster.getInstance().getServices();

    final var filtered = services.values().stream()
        .filter(service -> service.getType() == type)
        .toList();

    if (filtered.isEmpty()) {
      sender.sendRichMessage(String.format(
          "<red>No services found for type: <aqua>%s</aqua>",
          type.name()));
      return;
    }

    sender.sendRichMessage(String.format(
        "<gold>--- Services of type %s (%d) ---",
        type.name(), filtered.size()));

    for (final Service service : filtered) {
      final String statusColor = switch (service.getStatus()) {
        case RUNNING -> "green";
        case STARTING -> "yellow";
        case LOCKED -> "gold";
        case SHUTTING_DOWN -> "red";
        default -> "gray";
      };

      sender.sendRichMessage(String.format("""
          <dark_gray>[<aqua>%s</aqua>]</dark_gray> <white>%s</white> <gray>-</gray> <%s>%s</%s>""",
          service.getType().name(),
          service.getId(),
          statusColor, service.getStatus().name(), statusColor));
    }
  }

  @Command("service current")
  public void currentService(final CommandSender sender) {
    final var cluster = Cluster.getInstance();

    sender.sendRichMessage(String.format("""
        <gold>--- This Service ---
        <gray>ID: <white>%s
        <gray>Type: <aqua>%s
        <gray>Status: <green>%s
        <gray>IP: <white>%s""",
        cluster.getServiceId(),
        cluster.getServiceType().name(),
        cluster.getStatus().name(),
        cluster.getIp()));
  }
}
