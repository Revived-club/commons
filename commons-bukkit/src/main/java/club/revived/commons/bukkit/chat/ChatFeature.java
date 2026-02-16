package club.revived.commons.bukkit.chat;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

import club.revived.commons.bukkit.item.ColorUtils;
import club.revived.commons.bukkit.listener.Events;
import club.revived.commons.bukkit.player.PlayerUtils;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.game.PlayerManager;
import club.revived.commons.distribution.message.BroadcastMessage;
import club.revived.commons.distribution.message.BroadcastPermissionMessage;
import club.revived.commons.distribution.message.SendActionbar;
import club.revived.commons.distribution.message.SendMessage;
import club.revived.commons.distribution.message.SendTitle;
import club.revived.commons.feature.Feature;
import club.revived.commons.game.player.PlayerProfile;
import club.revived.commons.logging.model.ChatMessage;
import club.revived.commons.logging.task.ChatHistorySaveTask;
import club.revived.commons.punishment.model.PunishmentType;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public final class ChatFeature extends Feature {

  private ChatHistorySaveTask saveTask;

  public ChatFeature() {
    super("chat");
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

          PlayerManager.getInstance().getOptional(uuid)
              .ifPresentOrElse(
                  onlinePlayer -> onlinePlayer.getCachedOrLoad(PlayerProfile.class).thenAccept(profileOpt -> {
                    if (profileOpt.isEmpty())
                      return;

                    final var profile = profileOpt.get();
                    final var punishments = profile.activePunishments();

                    final boolean isMuted = punishments.stream()
                        .anyMatch(p -> p.type() == PunishmentType.MUTE && p.isActive());

                    final var message = MiniMessage.miniMessage().serialize(event.message());

                    if (isMuted) {
                      player.sendRichMessage("<red>You are muted!");
                      this.logMessage(uuid, message, true);
                      return;
                    }

                    Cluster.getInstance().getMessagingService().sendGlobalMessage(new BroadcastMessage(message));
                  }), () -> {
                    player.sendRichMessage("<red>A de-sync error occurred. Try chatting again in a second");
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
