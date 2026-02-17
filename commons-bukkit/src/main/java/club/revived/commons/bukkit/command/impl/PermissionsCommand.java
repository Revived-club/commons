package club.revived.commons.bukkit.command.impl;

import club.revived.commons.bukkit.permissions.BukkitPermissionManager;
import club.revived.commons.game.player.ProfileManager;
import club.revived.commons.permissions.model.Group;
import club.revived.commons.permissions.model.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

import java.util.ArrayList;
import java.util.List;

@Command("permissions")
public final class PermissionsCommand {

    @Command("permissions group create <id>")
    @org.incendo.cloud.annotations.Permission("club.revived.permissions.admin")
    public void createGroup(
            final CommandSender sender,
            @Argument("id") final String id) {
        BukkitPermissionManager.getInstance().getGroup(id).thenAccept(groupOpt -> {
            if (groupOpt.isPresent()) {
                sender.sendRichMessage("<yellow>Group " + id + " already exists.");
                return;
            }

            final var group = new Group(id, "", 0, new ArrayList<>());
            BukkitPermissionManager.getInstance().saveGroup(group).thenRun(() -> {
                sender.sendRichMessage("<green>Group " + id + " has been created.");
            });
        });
    }

    @Command("permissions group delete <id>")
    @org.incendo.cloud.annotations.Permission("club.revived.permissions.admin")
    public void deleteGroup(
            final CommandSender sender,
            @Argument("id") final String id) {
        BukkitPermissionManager.getInstance().getGroup(id).thenAccept(groupOpt -> {
            if (groupOpt.isEmpty()) {
                sender.sendRichMessage("<red>Group " + id + " does not exist.");
                return;
            }

            BukkitPermissionManager.getInstance().deleteGroup(id).thenRun(() -> {
                sender.sendRichMessage("<green>Group " + id + " has been deleted.");
            });
        });
    }

    @Command("permissions group <id> addpermission <permission> [value]")
    @org.incendo.cloud.annotations.Permission("club.revived.permissions.admin")
    public void addPermission(
            final CommandSender sender,
            @Argument("id") final String id,
            @Argument("permission") final String permKey,
            @Argument("value") Boolean value) {
        if (value == null) {
            value = true;
        }

        final boolean finalValue = value;

        BukkitPermissionManager.getInstance().getGroup(id).thenAccept(groupOpt -> {
            if (groupOpt.isEmpty()) {
                sender.sendRichMessage("<red>Group " + id + " does not exist.");
                return;
            }

            final var group = groupOpt.get();
            final List<Permission> permissions = new ArrayList<>(group.permissions());
            permissions.removeIf(p -> p.key().equalsIgnoreCase(permKey));
            permissions.add(new Permission(permKey, finalValue));

            final var newGroup = new Group(group.id(), group.prefix(), group.weight(), permissions);
                BukkitPermissionManager.getInstance().saveGroup(newGroup).thenRun(() -> {
                    sender.sendRichMessage("<green>Added permission " + permKey + " (" + finalValue + ") to group " + id + ".");
                    for (final var player : Bukkit.getOnlinePlayers()) {
                        BukkitPermissionManager.getInstance().getUserGroups(player.getUniqueId()).thenAccept(groups -> {
                            if (groups.stream().anyMatch(g -> g.id().equalsIgnoreCase(id))) {
                                BukkitPermissionManager.getInstance().refreshPermissions(player);
                            }
                        });
                    }
                });
        });
    }

    @Command("permissions group <id> removepermission <permission>")
    @org.incendo.cloud.annotations.Permission("club.revived.permissions.admin")
    public void removePermission(
            final CommandSender sender,
            @Argument("id") final String id,
            @Argument("permission") final String permKey) {
        BukkitPermissionManager.getInstance().getGroup(id).thenAccept(groupOpt -> {
            if (groupOpt.isEmpty()) {
                sender.sendRichMessage("<red>Group " + id + " does not exist.");
                return;
            }

            final var group = groupOpt.get();
            final List<Permission> permissions = new ArrayList<>(group.permissions());
            final boolean removed = permissions.removeIf(p -> p.key().equalsIgnoreCase(permKey));

            if (!removed) {
                sender.sendRichMessage("<red>Permission " + permKey + " not found in group " + id + ".");
                return;
            }

            final var newGroup = new Group(group.id(), group.prefix(), group.weight(), permissions);
            BukkitPermissionManager.getInstance().saveGroup(newGroup).thenRun(() -> {
                sender.sendRichMessage("<green>Removed permission " + permKey + " from group " + id + ".");
                for (final var player : Bukkit.getOnlinePlayers()) {
                    BukkitPermissionManager.getInstance().getUserGroups(player.getUniqueId()).thenAccept(groups -> {
                        if (groups.stream().anyMatch(g -> g.id().equalsIgnoreCase(id))) {
                            BukkitPermissionManager.getInstance().refreshPermissions(player);
                        }
                    });
                }
            });
        });
    }

    @Command("permissions user <player> addgroup <group>")
    @org.incendo.cloud.annotations.Permission("club.revived.permissions.admin")
    public void addGroupToUser(
            final CommandSender sender,
            @Argument("player") final String playerName,
            @Argument("group") final String groupId) {
        BukkitPermissionManager.getInstance().getGroup(groupId).thenAccept(groupOpt -> {
            if (groupOpt.isEmpty()) {
                sender.sendRichMessage("<red>Group " + groupId + " does not exist.");
                return;
            }

            ProfileManager.getInstance().getProfile(playerName).thenAccept(profileOpt -> {
                if (profileOpt.isEmpty()) {
                    sender.sendRichMessage("<red>Player " + playerName + " not found in database.");
                    return;
                }

                final var profile = profileOpt.get();
                final List<String> groups = new ArrayList<>(profile.permissionGroups());
                if (groups.contains(groupId)) {
                    sender.sendRichMessage("<red>Player " + playerName + " is already in group " + groupId + ".");
                    return;
                }

                groups.add(groupId);
                final var newProfile = new club.revived.commons.game.player.PlayerProfile(
                        profile.uuid(), profile.name(), profile.lastLogin(), groups
                );

                ProfileManager.getInstance().saveProfile(newProfile).thenRun(() -> {
                    sender.sendRichMessage("<green>Added player " + playerName + " to group " + groupId + ".");
                    final var onlinePlayer = Bukkit.getPlayer(profile.uuid());
                    if (onlinePlayer != null) {
                        BukkitPermissionManager.getInstance().refreshPermissions(onlinePlayer);
                    }
                });
            });
        });
    }

    @Command("permissions user <player> removegroup <group>")
    @org.incendo.cloud.annotations.Permission("club.revived.permissions.admin")
    public void removeGroupFromUser(
            final CommandSender sender,
            @Argument("player") final String playerName,
            @Argument("group") final String groupId) {
        ProfileManager.getInstance().getProfile(playerName).thenAccept(profileOpt -> {
            if (profileOpt.isEmpty()) {
                sender.sendRichMessage("<red>Player " + playerName + " not found in database.");
                return;
            }

            final var profile = profileOpt.get();
            final List<String> groups = new ArrayList<>(profile.permissionGroups());
            if (!groups.remove(groupId)) {
                sender.sendRichMessage("<red>Player " + playerName + " is not in group " + groupId + ".");
                return;
            }

            final var newProfile = new club.revived.commons.game.player.PlayerProfile(
                    profile.uuid(), profile.name(), profile.lastLogin(), groups
            );

            ProfileManager.getInstance().saveProfile(newProfile).thenRun(() -> {
                sender.sendRichMessage("<green>Removed player " + playerName + " from group " + groupId + ".");
                final var onlinePlayer = Bukkit.getPlayer(profile.uuid());
                if (onlinePlayer != null) {
                    BukkitPermissionManager.getInstance().refreshPermissions(onlinePlayer);
                }
            });
        });
    }

    @Command("permissions group list")
    @org.incendo.cloud.annotations.Permission("club.revived.permissions.admin")
    public void listGroups(final CommandSender sender) {
        BukkitPermissionManager.getInstance().getAllGroups().thenAccept(groups -> {
            if (groups.isEmpty()) {
                sender.sendRichMessage("<yellow>No groups found.");
                return;
            }

            sender.sendRichMessage("<gold>Groups:");
            for (final var group : groups) {
                sender.sendRichMessage("<gray>- <yellow>" + group.id() + " <gray>(Weight: " + group.weight() + ")");
            }
        });
    }

    @Command("permissions group <id> info")
    @org.incendo.cloud.annotations.Permission("club.revived.permissions.admin")
    public void groupInfo(
            final CommandSender sender,
            @Argument("id") final String id) {
        BukkitPermissionManager.getInstance().getGroup(id).thenAccept(groupOpt -> {
            if (groupOpt.isEmpty()) {
                sender.sendRichMessage("<red>Group " + id + " does not exist.");
                return;
            }

            final var group = groupOpt.get();
            sender.sendRichMessage("<gold>Group Info: <yellow>" + group.id());
            sender.sendRichMessage("<gray>Prefix: <white>" + (group.prefix().isEmpty() ? "None" : group.prefix()));
            sender.sendRichMessage("<gray>Weight: <white>" + group.weight());
            sender.sendRichMessage("<gray>Permissions:");
            if (group.permissions().isEmpty()) {
                sender.sendRichMessage("<gray>  None");
            } else {
                for (final var perm : group.permissions()) {
                    sender.sendRichMessage("<gray>  - <white>" + perm.key() + " (" + (perm.value() ? "<green>true" : "<red>false") + "<white>)");
                }
            }
        });
    }

    @Command("permissions user <player> info")
    @org.incendo.cloud.annotations.Permission("club.revived.permissions.admin")
    public void userInfo(
            final CommandSender sender,
            @Argument("player") final String playerName) {
        ProfileManager.getInstance().getProfile(playerName).thenAccept(profileOpt -> {
            if (profileOpt.isEmpty()) {
                sender.sendRichMessage("<red>Player " + playerName + " not found in database.");
                return;
            }

            final var profile = profileOpt.get();
            sender.sendRichMessage("<gold>User Info: <yellow>" + profile.name());
            sender.sendRichMessage("<gray>UUID: <white>" + profile.uuid());
            sender.sendRichMessage("<gray>Groups: <white>" + (profile.permissionGroups().isEmpty() ? "None" : String.join(", ", profile.permissionGroups())));

            BukkitPermissionManager.getInstance().getUserGroups(profile.uuid()).thenAccept(groups -> {
                sender.sendRichMessage("<gray>Resolved Permissions:");
                final var permissions = BukkitPermissionManager.getInstance().resolvePermissions(groups);
                if (permissions.isEmpty()) {
                    sender.sendRichMessage("<gray>  None");
                } else {
                    for (final var entry : permissions.entrySet()) {
                        sender.sendRichMessage("<gray>  - <white>" + entry.getKey() + " (" + (entry.getValue() ? "<green>true" : "<red>false") + "<white>)");
                    }
                }
            });
        });
    }
}
