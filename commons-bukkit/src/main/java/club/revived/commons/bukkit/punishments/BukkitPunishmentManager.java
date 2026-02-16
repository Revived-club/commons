package club.revived.commons.bukkit.punishments;

import org.bukkit.Bukkit;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.message.KickMessage;
import club.revived.commons.punishment.PunishmentManager;
import club.revived.commons.bukkit.item.ColorUtils;

public final class BukkitPunishmentManager extends PunishmentManager {

  public BukkitPunishmentManager() {
  }

  public void initMessageHandlers() {
    Cluster.getInstance().getMessagingService().registerMessageHandler(KickMessage.class, message -> {
      final var player = Bukkit.getPlayer(message.uuid());

      if (player == null) {
        return;
      }

      player.kick(ColorUtils.parse(message.reason()));
    });
  }
}
