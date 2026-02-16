package club.revived.commons.bukkit.command.impl;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.message.BroadcastPermissionMessage;

@Command("staffchat")
public final class StaffChatCommand {

  @Command("staffchat <message>")
  @Permission("club.revived.staffchat")
  public void staffchat(
      final Player player,
      @Argument("message") @Greedy final String message) {
    Cluster.getInstance().getMessagingService()
        .sendGlobalMessage(new BroadcastPermissionMessage(message, "club.revived.staffchat"));
  }
}
