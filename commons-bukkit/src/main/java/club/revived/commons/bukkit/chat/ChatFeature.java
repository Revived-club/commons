package club.revived.commons.bukkit.chat;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.antlr.v4.parse.ANTLRParser.notSet_return;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

import com.fasterxml.jackson.databind.ext.NioPathDeserializer;
import com.mongodb.lang.Nullable;

import club.revived.commons.NumberUtils;
import club.revived.commons.bukkit.item.ColorUtils;
import club.revived.commons.bukkit.listener.Events;
import club.revived.commons.bukkit.player.PlayerUtils;
import club.revived.commons.bukkit.punishments.BukkitPunishmentManager;
import club.revived.commons.chat.ChatFilter;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.game.PlayerManager;
import club.revived.commons.distribution.message.BroadcastMessage;
import club.revived.commons.distribution.message.BroadcastPermissionMessage;
import club.revived.commons.distribution.message.SendActionbar;
import club.revived.commons.distribution.message.SendMessage;
import club.revived.commons.distribution.message.SendTitle;
import club.revived.commons.feature.Feature;
import club.revived.commons.logging.model.ChatMessage;
import club.revived.commons.logging.task.ChatHistorySaveTask;
import club.revived.commons.punishment.model.PunishmentType;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public final class ChatFeature extends Feature {

  private ChatHistorySaveTask saveTask;
  private final ChatFilter chatFilter;

  public ChatFeature(final ChatFilter chatFilter) {
    super("chat");

    this.chatFilter = chatFilter;
  }

  @Override
  public void onEnable() {
    this.saveTask = new ChatHistorySaveTask();
    this.initMessageHandlers();
  }

  public void init() {
    Events.subscribe(AsyncChatEvent.class, EventPriority.MONITOR)
        .ignoreCancelled(true)
        .handler(event -> {
          final var player = event.getPlayer();
          final var uuid = player.getUniqueId();
          final var onlinePlayer = PlayerManager.getInstance().get(uuid);

          if (onlinePlayer == null) {
            player.sendRichMessage("<red>De-sync error! Try chatting again in a second!");
            return;
          }

          final var message = MiniMessage.miniMessage().serialize(event.message());

          BukkitPunishmentManager.getInstance().isPlayerPunished(uuid, PunishmentType.MUTE)
              .thenCombine(this.chatFilter.filterMessage(uuid, message), (muted, filter) -> {
                if (muted) {
                  onlinePlayer.sendMessage("<red>You are muted!");
                  this.logMessage(uuid, message, true);
                  return null;
                }

                if (filter != null) {
                  Cluster.getInstance().getMessagingService().sendGlobalMessage(new BroadcastPermissionMessage(
                      String.format("%s flagged the filter! Type: %s Flagged: %s",
                          player.getName(),
                          filter.type().toString(),
                          filter.matchedContent()),
                      "club.revived.filter.alert"));
                  player.sendRichMessage(
                      "<red>Your message was blocked by the filter! Please avoid using inappropriate language!");
                  return null;
                }

                Cluster.getInstance().getMessagingService().sendGlobalMessage(new BroadcastMessage(message));
                return null;
              });

        });
  }

  public void logMessage(final UUID uuid, final String message, final boolean blocked) {
    final var chatMessage = new ChatMessage(
        uuid.toString(),
        message,
        Instant.now(),
        false,
        Cluster.getInstance().getServiceId());

    this.saveTask.queryMessage(chatMessage);
  }

  private void initMessageHandlers() {
    Cluster.getInstance().getMessagingService().registerMessageHandler(BroadcastPermissionMessage.class, message -> {
      for (final var player : PlayerUtils.withPermissions(message.permission())) {
        player.sendRichMessage(message.content());
      }
    });

    Cluster.getInstance().getMessagingService().registerMessageHandler(BroadcastMessage.class, message -> {
      for (final var player : Bukkit.getOnlinePlayers()) {
        player.sendRichMessage(message.content());
      }
    });

    Cluster.getInstance().getMessagingService().registerMessageHandler(SendMessage.class, message -> {
      final var player = Bukkit.getPlayer(message.uuid());

      if (player == null) {
        return;
      }

      player.sendRichMessage(message.content());
    });

    Cluster.getInstance().getMessagingService().registerMessageHandler(SendActionbar.class, message -> {
      final var player = Bukkit.getPlayer(message.uuid());

      if (player == null) {
        return;
      }

      player.sendActionBar(ColorUtils.parse(message.content()));
    });

    Cluster.getInstance().getMessagingService().registerMessageHandler(SendTitle.class, message -> {
      final var player = Bukkit.getPlayer(message.uuid());

      if (player == null) {
        return;
      }

      final var title = Title.title(
          ColorUtils.parse(message.title()),
          ColorUtils.parse(message.subtitle()),
          Times.times(
              Duration.ofMillis(message.fadeIn()),
              Duration.ofMillis(message.stay()),
              Duration.ofMillis(message.fadeOut())));

      player.showTitle(title);
    });
  }
}
