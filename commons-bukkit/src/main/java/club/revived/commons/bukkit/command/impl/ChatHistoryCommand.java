package club.revived.commons.bukkit.command.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.chat.ChatHistory;
import club.revived.commons.chat.ChatHistory.ChatMessage;
import club.revived.commons.data.DataRepository;
import club.revived.commons.distribution.game.OnlinePlayer;
import club.revived.commons.distribution.game.PlayerManager;
import me.xdrop.fuzzywuzzy.FuzzySearch;

@Command("chat-history")
public final class ChatHistoryCommand {
  private static final int FUZZY_MATCH_THRESHOLD = 60;
  private static final int MESSAGES_PER_PAGE = 5;
  private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
      .withZone(ZoneId.systemDefault());

  @Command("<target> [page]")
  public void viewHistory(
      final @NotNull Player player,
      final @Argument(value = "target") Player target,
      final @Default("1") @Argument(value = "page") int page) {

    final OnlinePlayer onlinePlayer = PlayerManager.getInstance().get(player.getUniqueId());
    if (onlinePlayer == null) {
      player.sendRichMessage("<red>Target player not found!");
      return;
    }

    this.getHistory(onlinePlayer).thenAccept(history -> {
      player.sendRichMessage(this.buildHistoryMessage(history, page, target.getName(), player.getName(), null));
    });
  }

  @Command("<target> search <query> [page]")
  public void searchHistory(
      final @NotNull Player player,
      final @Argument(value = "target") Player target,
      final @Argument(value = "query") String query,
      final @Default("1") @Argument(value = "page") int page) {

    final OnlinePlayer onlinePlayer = PlayerManager.getInstance().get(player.getUniqueId());
    if (onlinePlayer == null) {
      player.sendRichMessage("<red>Target player not found!");
      return;
    }

    this.getHistory(onlinePlayer).thenAccept(history -> {
      player.sendRichMessage(this.buildSearchMessage(history, query, page, target.getName(), player.getName()));
    });
  }

  @NotNull
  private CompletableFuture<ChatHistory> getHistory(final OnlinePlayer onlinePlayer) {
    return onlinePlayer.getCachedOrLoad(ChatHistory.class).thenApply(opt -> {
      return opt.orElse(new ChatHistory(onlinePlayer.uuid(), new ArrayList<>()));
    });
  }

  @NotNull
  private CompletableFuture<ChatHistory> loadHistory(final UUID uuid) {
    return DataRepository.getInstance().get(ChatHistory.class, uuid).thenApply(opt -> {
      return opt.orElse(new ChatHistory(uuid, new ArrayList<>()));
    });
  }

  @NotNull
  private String buildHistoryMessage(final ChatHistory history, final int page,
      final String targetName, final String viewerName, final String query) {

    final List<ChatMessage> allMessages = history.chatMessages()
        .stream()
        .sorted((a, b) -> Long.compare(b.sentAt(), a.sentAt()))
        .toList();

    if (allMessages.isEmpty()) {
      return "<red>That player has no chat history!";
    }

    final int totalPages = (int) Math.ceil((double) allMessages.size() / MESSAGES_PER_PAGE);

    if (page > totalPages || page < 1) {
      return "<red>Invalid page number! Valid pages: 1-" + totalPages;
    }

    final int startIndex = (page - 1) * MESSAGES_PER_PAGE;
    final int endIndex = Math.min(startIndex + MESSAGES_PER_PAGE, allMessages.size());
    final List<ChatMessage> messages = allMessages.subList(startIndex, endIndex);

    final StringBuilder result = new StringBuilder();

    result.append("<gray>━━━━━ <white>Chat History (Page: ").append(page).append(")</white> ━━━━━</gray>\n");

    for (ChatMessage message : messages) {
      result.append(this.formatMessage(message, viewerName)).append("\n");
    }

    result.append("<gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gray>\n");
    result.append(this.buildPagination(targetName, page, totalPages, query));

    return result.toString();
  }

  @NotNull
  private String buildSearchMessage(final ChatHistory history, final String query,
      final int page, final String targetName, final String viewerName) {

    final List<ChatMessage> allMatchingMessages = history.chatMessages()
        .stream()
        .filter(message -> FuzzySearch.ratio(message.content().toLowerCase(),
            query.toLowerCase()) >= FUZZY_MATCH_THRESHOLD)
        .sorted((a, b) -> {
          final int scoreA = FuzzySearch.ratio(a.content().toLowerCase(), query.toLowerCase());
          final int scoreB = FuzzySearch.ratio(b.content().toLowerCase(), query.toLowerCase());
          final int scoreComparison = Integer.compare(scoreB, scoreA);
          return scoreComparison != 0 ? scoreComparison : Long.compare(b.sentAt(), a.sentAt());
        })
        .toList();

    if (allMatchingMessages.isEmpty()) {
      return "<red>No messages found matching: \"" + query + "\"";
    }

    final int totalPages = (int) Math.ceil((double) allMatchingMessages.size() / MESSAGES_PER_PAGE);

    if (page > totalPages || page < 1) {
      return "<red>Invalid page number! Valid pages: 1-" + totalPages;
    }

    final int startIndex = (page - 1) * MESSAGES_PER_PAGE;
    final int endIndex = Math.min(startIndex + MESSAGES_PER_PAGE, allMatchingMessages.size());
    final List<ChatMessage> messages = allMatchingMessages.subList(startIndex, endIndex);

    final StringBuilder result = new StringBuilder();

    result.append("<gray>━━━━━ <white>Search: \"").append(query).append("\" (Page: ").append(page)
        .append(")</white> ━━━━━</gray>\n");

    for (ChatMessage message : messages) {
      result.append(this.formatMessage(message, viewerName)).append("\n");
    }

    result.append("<gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gray>\n");
    result.append(this.buildPagination(targetName, page, totalPages, query));

    return result.toString();
  }

  @NotNull
  private String formatMessage(final ChatMessage message, final String viewerName) {
    final String formattedTime = TIME_FORMAT.format(Instant.ofEpochMilli(message.sentAt()));

    final StringBuilder result = new StringBuilder();
    result.append("<gray>[").append(formattedTime).append("]</gray> ");
    result.append("<aqua>Unknown</aqua>");
    result.append("<white>: ").append(message.content()).append("</white>");

    if (message.blocked()) {
      result.append(" <red>❌ Blocked</red>");
    }

    return result.toString();
  }

  @NotNull
  private String buildPagination(final String targetName, final int currentPage,
      final int totalPages, final String query) {

    if (totalPages <= 1) {
      return "";
    }

    final StringBuilder pagination = new StringBuilder();

    if (currentPage > 1) {
      final String prevCommand = query == null
          ? "/chat-history " + targetName + " " + (currentPage - 1)
          : "/chat-history " + targetName + " search " + query + " " + (currentPage - 1);

      pagination.append("<gray><click:run_command:'").append(prevCommand).append("'><hover:show_text:'Go to page ")
          .append(currentPage - 1).append("'>< Prev</hover></click></gray>");
    } else {
      pagination.append("<dark_gray>< Prev</dark_gray>");
    }

    pagination.append("<gray>   |   </gray>");

    final int startPage = Math.max(1, currentPage - 2);
    final int endPage = Math.min(totalPages, currentPage + 2);

    for (int i = startPage; i <= endPage; i++) {
      if (i > startPage) {
        pagination.append("<gray>  </gray>");
      }

      if (i == currentPage) {
        pagination.append("<bold><gold>[").append(i).append("]</gold></bold>");
      } else {
        final String pageCommand = query == null
            ? "/chat-history " + targetName + " " + i
            : "/chat-history " + targetName + " search " + query + " " + i;

        pagination.append("<white><click:run_command:'").append(pageCommand).append("'><hover:show_text:'Go to page ")
            .append(i).append("'>").append(i).append("</hover></click></white>");
      }
    }

    pagination.append("<gray>   |   </gray>");

    if (currentPage < totalPages) {
      final String nextCommand = query == null
          ? "/chat-history " + targetName + " " + (currentPage + 1)
          : "/chat-history " + targetName + " search " + query + " " + (currentPage + 1);

      pagination.append("<gray><click:run_command:'").append(nextCommand).append("'><hover:show_text:'Go to page ")
          .append(currentPage + 1).append("'>Next ></hover></click></gray>");
    } else {
      pagination.append("<dark_gray>Next ></dark_gray>");
    }

    return pagination.toString();
  }
}
