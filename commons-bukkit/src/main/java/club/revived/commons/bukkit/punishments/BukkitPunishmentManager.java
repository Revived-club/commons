package club.revived.commons.bukkit.punishments;

import org.bukkit.Bukkit;

import club.revived.commons.bukkit.item.ColorUtils;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.message.KickMessage;
import club.revived.commons.punishment.PunishmentManager;

public final class BukkitPunishmentManager extends PunishmentManager {

  private static BukkitPunishmentManager instance;

  public BukkitPunishmentManager() {
    instance = this;
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

  public static BukkitPunishmentManager getInstance() {
    if (instance == null) {
      return new BukkitPunishmentManager();
    }

    return instance;
  }
}
