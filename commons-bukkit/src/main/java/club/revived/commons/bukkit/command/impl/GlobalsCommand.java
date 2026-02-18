package club.revived.commons.bukkit.command.impl;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.shared.SharedDataManager;

import java.util.UUID;

@Command("globals")
@Permission("club.revived.globals")
public final class GlobalsCommand {

  @Command("globals info")
  @CommandDescription("View the current shared data")
  public void info(final @NotNull CommandSender sender) {
    SharedDataManager.getInstance().getSharedData().thenAccept(data -> {
      sender.sendRichMessage("<gold><bold>Shared Data</bold></gold>");
      sender.sendRichMessage("<gray>MOTD: <white>" + data.motd());
      sender.sendRichMessage("<gray>Whitelist: " + (data.whitelist() ? "<green>Enabled" : "<red>Disabled"));
      sender.sendRichMessage("<gray>Whitelisted Players: <white>" + data.whitelistedPlayers().size());
      sender.sendRichMessage("<gray>Filtered Words: <white>" + data.filteredWords().size());
    });
  }

  @Command("globals motd <motd>")
  @CommandDescription("Set the server MOTD")
  public void setMotd(
      final @NotNull CommandSender sender,
      final @NotNull @Argument("motd") String motd) {
    SharedDataManager.getInstance().setMotd(motd);
    sender.sendRichMessage("<green>MOTD updated to: <white>" + motd);
  }

  @Command("globals whitelist <enabled>")
  @CommandDescription("Enable or disable the whitelist")
  public void setWhitelist(
      final @NotNull CommandSender sender,
      final @NotNull @Argument("enabled") Boolean enabled) {
    SharedDataManager.getInstance().setWhitelist(enabled);
    sender.sendRichMessage("<green>Whitelist " + (enabled ? "<green>enabled" : "<red>disabled") + "<green>.");
  }

  @Command("globals whitelist add <player>")
  @CommandDescription("Add a player to the whitelist")
  public void addWhitelistedPlayer(
      final @NotNull CommandSender sender,
      final @NotNull @Argument("player") UUID player) {
    SharedDataManager.getInstance().addWhitelistedPlayer(player);
    sender.sendRichMessage("<green>Added <white>" + player + " <green>to the whitelist.");
  }

  @Command("globals whitelist remove <player>")
  @CommandDescription("Remove a player from the whitelist")
  public void removeWhitelistedPlayer(
      final @NotNull CommandSender sender,
      final @NotNull @Argument("player") UUID player) {
    SharedDataManager.getInstance().removeWhitelistedPlayer(player);
    sender.sendRichMessage("<green>Removed <white>" + player + " <green>from the whitelist.");
  }

  @Command("globals whitelist list")
  @CommandDescription("List all whitelisted players")
  public void listWhitelistedPlayers(final @NotNull CommandSender sender) {
    SharedDataManager.getInstance().getSharedData().thenAccept(data -> {
      if (data.whitelistedPlayers().isEmpty()) {
        sender.sendRichMessage("<red>No whitelisted players.");
        return;
      }
      sender.sendRichMessage("<gold><bold>Whitelisted Players</bold></gold>");
      data.whitelistedPlayers().forEach(uuid -> sender.sendRichMessage("<gray>- <white>" + uuid));
    });
  }

  @Command("globals filter add <word>")
  @CommandDescription("Add a word to the filter list")
  public void addFilteredWord(
      final @NotNull CommandSender sender,
      final @NotNull @Argument("word") String word) {
    SharedDataManager.getInstance().addFilteredWord(word);
    sender.sendRichMessage("<green>Added <white>" + word + " <green>to the filter list.");
  }

  @Command("globals filter remove <word>")
  @CommandDescription("Remove a word from the filter list")
  public void removeFilteredWord(
      final @NotNull CommandSender sender,
      final @NotNull @Argument("word") String word) {
    SharedDataManager.getInstance().removeFilteredWord(word);
    sender.sendRichMessage("<green>Removed <white>" + word + " <green>from the filter list.");
  }

  @Command("globals filter list")
  @CommandDescription("List all filtered words")
  public void listFilteredWords(final @NotNull CommandSender sender) {
    SharedDataManager.getInstance().getSharedData().thenAccept(data -> {
      if (data.filteredWords().isEmpty()) {
        sender.sendRichMessage("<red>No filtered words.");
        return;
      }
      sender.sendRichMessage("<gold><bold>Filtered Words</bold></gold>");
      data.filteredWords().forEach(word -> sender.sendRichMessage("<gray>- <white>" + word));
    });
  }
}
