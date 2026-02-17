package club.revived.commons.velocity.permissions;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;

public final class VelocityPermissionProvider
    implements PermissionProvider, PermissionFunction {

  private final Player player;
  private final Map<String, Boolean> permissions;

  public VelocityPermissionProvider(
      final @NotNull Player player,
      final @NotNull Map<String, Boolean> permissions) {

    this.player = player;
    this.permissions = permissions;
  }

  @Override
  public @NotNull PermissionFunction createFunction(final @NotNull PermissionSubject subject) {
    if (subject != this.player) {
      throw new IllegalArgumentException(
          "PermissionSubject does not match bound player");
    }
    return this;
  }

  @Override
  public @NotNull Tristate getPermissionValue(final @NotNull String permission) {
    final Boolean exact = permissions.get(permission);

    if (exact != null) {
      return exact ? Tristate.TRUE : Tristate.FALSE;
    }

    int index = permission.length();

    while ((index = permission.lastIndexOf('.', index - 1)) != -1) {
      final String wildcard = permission.substring(0, index) + ".*";

      final Boolean value = permissions.get(wildcard);
      if (value != null) {
        return value ? Tristate.TRUE : Tristate.FALSE;
      }
    }

    final Boolean global = permissions.get("*");

    if (global != null) {
      return global ? Tristate.TRUE : Tristate.FALSE;
    }

    return Tristate.UNDEFINED;
  }
}
