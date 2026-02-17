package club.revived.commons.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.data.DataRepository;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.game.OnlinePlayer;
import club.revived.commons.game.player.PlayerProfile;
import club.revived.commons.permissions.model.Group;

public abstract class PermissionManager<T> {

  public PermissionManager() {
    this.init();
  }

  public abstract CompletableFuture<Map<String, Boolean>> loadPermissions(final @NotNull UUID uuid);

  public abstract CompletableFuture<Map<String, Boolean>> loadPermissions(final @NotNull T obj);

  @NotNull
  public CompletableFuture<Void> saveGroup(final Group group) {
    return DataRepository.getInstance().save(Group.class, group)
        .thenRun(() -> Cluster.getInstance().getGlobalCache().set("group:" + group.id(), group));
  }

  @NotNull
  public CompletableFuture<Void> deleteGroup(final String id) {
    return DataRepository.getInstance().delete(Group.class, id)
        .thenCompose(v -> Cluster.getInstance().getGlobalCache().remove("group:" + id))
        .thenApply(v -> null);
  }

  @NotNull
  public CompletableFuture<Optional<Group>> getGroup(final String id) {
    return Cluster.getInstance().getGlobalCache().get(Group.class, "group:" + id)
        .thenCompose(cachedGroup -> {
          if (cachedGroup != null) {
            return CompletableFuture.completedFuture(Optional.of(cachedGroup));
          }

          return DataRepository.getInstance().get(Group.class, id)
              .thenApply(groupOpt -> {
                groupOpt.ifPresent(group -> Cluster.getInstance().getGlobalCache().set("group:" + group.id(), group));
                return groupOpt;
              });
        });
  }

  @NotNull
  public CompletableFuture<List<Group>> getAllGroups() {
    return DataRepository.getInstance().getAll(Group.class);
  }

  @NotNull
  public CompletableFuture<Void> init() {
    return DataRepository.getInstance()
        .getAll(Group.class)
        .thenAccept(groups -> groups.forEach(group -> Cluster.getInstance()
            .getGlobalCache()
            .set("group:" + group.id(), group)));
  }

  @NotNull
  public Map<String, Boolean> resolvePermissions(final List<Group> groups) {
    final Map<String, Boolean> permissions = new HashMap<>();

    for (final Group group : groups) {
      group.permissions().forEach(permission -> {
        permissions.putIfAbsent(permission.key(), permission.value());
      });
    }

    return permissions;
  }

  @NotNull
  public CompletableFuture<List<Group>> getGroups(final List<String> keys) {
    if (keys == null || keys.isEmpty()) {
      return CompletableFuture.completedFuture(List.of());
    }

    final List<String> redisKeys = keys.stream()
        .map(key -> "group:" + key)
        .toList();

    return Cluster.getInstance().getGlobalCache().getAll(redisKeys, Group.class)
        .thenCompose(cachedGroups -> {

          final Set<String> cachedKeys = cachedGroups.stream()
              .map(Group::id)
              .collect(Collectors.toSet());

          final List<String> missing = keys.stream()
              .filter(k -> !cachedKeys.contains(k))
              .toList();

          if (missing.isEmpty()) {
            return CompletableFuture.completedFuture(cachedGroups);
          }

          return DataRepository.getInstance()
              .getAllByKeys(Group.class, missing)
              .thenApply(loadedGroups -> {

                loadedGroups.forEach(group -> Cluster.getInstance().getGlobalCache()
                    .set("group:" + group.id(), group));

                final List<Group> combined = new ArrayList<>(cachedGroups);
                combined.addAll(loadedGroups);

                return combined;
              });
        });
  }

  @NotNull
  public CompletableFuture<List<Group>> getUserGroups(final UUID uuid) {
    return DataRepository.getInstance().get(PlayerProfile.class, uuid)
        .thenCompose(profileOpt -> {
          if (profileOpt.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
          }

          final var profile = profileOpt.get();

          return this.getGroups(profile.permissionGroups());
        });
  }

  @NotNull
  public CompletableFuture<List<Group>> getUserGroups(final OnlinePlayer onlinePlayer) {
    return onlinePlayer.getCachedOrLoad(PlayerProfile.class)
        .thenCompose(profileOpt -> {
          if (profileOpt.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
          }

          final var profile = profileOpt.get();

          return this.getGroups(profile.permissionGroups());
        });
  }

  @NotNull
  public CompletableFuture<List<Group>> loadUserGroups(final UUID uuid) {
    return DataRepository.getInstance()
        .get(PlayerProfile.class, uuid)
        .thenCompose(profileOpt -> {
          if (profileOpt.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
          }

          final var profile = profileOpt.get();

          return DataRepository.getInstance().getAllByKeys(Group.class, profile.permissionGroups());
        });
  }
}
