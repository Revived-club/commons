package club.revived.commons.velocity.punishments;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.event.player.configuration.PlayerFinishedConfigurationEvent;
import com.velocitypowered.api.proxy.ProxyServer;

import club.revived.commons.punishment.PunishmentManager;
import club.revived.commons.punishment.model.PunishmentType;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class VelocityPunishmentManager extends PunishmentManager {

  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  private final ProxyServer proxyServer;

  public VelocityPunishmentManager(final @NotNull ProxyServer proxyServer) {
    this.proxyServer = proxyServer;

    this.initListeners();
  }

  private void initListeners() {
    this.proxyServer.getEventManager().register(proxyServer, PlayerFinishedConfigurationEvent.class, event -> {
      final var player = event.player();
      final var uuid = player.getUniqueId();

      super.getPunishments(uuid).thenAccept(punishments -> {
        if (punishments.isEmpty()) {
          return;
        }

        final var bans = punishments.stream()
            .filter(punishment -> punishment.type() == PunishmentType.BAN && punishment.isActive())
            .toList();

        if (bans.isEmpty()) {
          return;
        }

        final var ban = bans.getFirst();

        player.disconnect(
            this.miniMessage.deserialize(String.format("""
                <#9b282a><bold>You are banned from this server</bold></#9b282a>

                <white>Reason: %s</white>
                <white>Duration: %s</white>

                <gray>If you believe this is a mistake,</gray>
                <gray>appeal at:</gray>
                <gray>https://revived.club/appeal

                <dark_gray>%s</dark_gray>

                """, ban.reason(), ban.expiry(), ban.id())));

      });

    });
  }

}
