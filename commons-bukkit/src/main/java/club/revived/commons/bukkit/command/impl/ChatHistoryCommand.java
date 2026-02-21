package club.revived.commons.bukkit.command.impl;

import club.revived.commons.data.DataRepository;
import club.revived.commons.game.player.ProfileManager;
import club.revived.commons.logging.model.ChatMessage;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Command("chathistory")
public final class ChatHistoryCommand {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      .withZone(ZoneId.systemDefault());
  private static final int PAGE_SIZE = 10;

  @Command("chathistory <player> [hours] [query] [page]")
  @Permission("club.revived.chathistory")
  public void chatHistory(
      final CommandSender sender,
      @Argument("player") final String targetName,
      @Argument("hours") @Default("24") final int hours,
      @Argument("query") @Default("") final String query,
      @Argument("page") @Default("1") final int page) {

    ProfileManager.getInstance().getProfile(targetName).thenAccept(profileOpt -> {
      if (profileOpt.isEmpty()) {
        sender.sendRichMessage("<red>Player " + targetName + " not found.");
        return;
      }

      final var profile = profileOpt.get();
      final String uuid = profile.uuid().toString();

      sender.sendRichMessage("<dark_gray>Loading history...");

      DataRepository.getInstance().getLogs("chat_message", "uuid", uuid, hours, ChatMessage.class)
          .thenAccept(logs -> handleLogs(sender, targetName, hours + " hours", query, page, logs));
    });
  }

  @Command("chathistory <player> all [query] [page]")
  @Permission("club.revived.chathistory")
  public void chatHistoryAll(
      final CommandSender sender,
      @Argument("player") final String targetName,
      @Argument("query") @Default("") final String query,
      @Argument("page") @Default("1") final int page) {

    ProfileManager.getInstance().getProfile(targetName).thenAccept(profileOpt -> {
      if (profileOpt.isEmpty()) {
        sender.sendRichMessage("<red>Player " + targetName + " not found.");
        return;
      }

      final var profile = profileOpt.get();
      final String uuid = profile.uuid().toString();

      sender.sendRichMessage("<dark_gray>Loading history...");

      DataRepository.getInstance().getAllLogs("chat_message", "uuid", uuid, ChatMessage.class)
          .thenAccept(logs -> handleLogs(sender, targetName, "all time", query, page, logs));
    });
  }

  private void handleLogs(CommandSender sender, String targetName, String timeframe, String query, int page,
      List<ChatMessage> logs) {
    if (logs.isEmpty()) {
      sender.sendRichMessage("<yellow>No chat history found for " + targetName + " in " + timeframe + ".");
      return;
    }

    final List<ChatMessage> filteredLogs = logs.stream()
        .sorted(Comparator.comparing(ChatMessage::getSentAt).reversed())
        .collect(Collectors.toList());

    if (!query.isEmpty()) {
      filteredLogs = filteredLogs.stream()
          .filter(log -> FuzzySearch.partialRatio(query.toLowerCase(), log.getContent().toLowerCase()) > 70)
          .toList();
    }

    if (filteredLogs.isEmpty()) {
      sender.sendRichMessage("<yellow>No chat history found for " + targetName + " matching query '" + query + "'.");
      return;
    }

    final int totalPages = (int) Math.ceil((double) filteredLogs.size() / PAGE_SIZE);
    if (page < 1 || page > totalPages) {
      sender.sendRichMessage("<red>Invalid page number. Total pages: " + totalPages);
      return;
    }

    sender.sendRichMessage("<gold>Chat History for <yellow>" + targetName + " <gray>(" + timeframe + ") (Page " + page
        + "/" + totalPages + "):");

    final int start = (page - 1) * PAGE_SIZE;
    final int end = Math.min(start + PAGE_SIZE, filteredLogs.size());

    for (int i = start; i < end; i++) {
      final ChatMessage log = filteredLogs.get(i);
      final String formattedDate = DATE_FORMATTER.format(log.getSentAt());
      sender.sendRichMessage(
          "<gray>[" + formattedDate + "] " + (log.isBlocked() ? "<red>[BLOCKED] " : "") + "<white>" + log.getContent());
    }
  }
}
