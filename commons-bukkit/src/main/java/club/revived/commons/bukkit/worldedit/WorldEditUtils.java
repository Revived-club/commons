package club.revived.commons.bukkit.worldedit;

import java.io.File;
import java.nio.file.Files;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;

public final class WorldEditUtils {

  @NotNull
  public static Block[] getRegionCorners(final Player player) {
    final var pl = BukkitAdapter.adapt(player);
    final var wld = BukkitAdapter.adapt(player.getWorld());

    try {
      if (WorldEdit.getInstance().getSessionManager().get(pl).getSelection(wld) != null) {
        final Region region = WorldEdit.getInstance().getSessionManager().get(pl).getSelection(wld);
        final Location corner1 = BukkitAdapter.adapt(player.getWorld(), region.getMaximumPoint());
        final Location corner2 = BukkitAdapter.adapt(player.getWorld(), region.getMinimumPoint());

        return new Block[] { corner1.getBlock(), corner2.getBlock() };
      }
    } catch (final IncompleteRegionException e) {
      return null;
    }

    return null;
  }

  public static void paste(
      final File file,
      final Location location) {
    final BlockVector3 to = BukkitAdapter.adapt(location).toVector().toBlockPoint();
    final ClipboardFormat format = ClipboardFormats.findByFile(file);

    if (format != null)
      try {
        final var reader = format.getReader(Files.newInputStream(file.toPath()));
        final var clipboard = reader.read();
        final var editSession = WorldEdit.getInstance()
            .newEditSession(BukkitAdapter.adapt(location.getWorld()));
        editSession.setFastMode(true);

        final var operation = (new ClipboardHolder(clipboard)).createPaste(editSession).ignoreAirBlocks(false).to(to)
            .build();
        Operations.complete(operation);

        editSession.close();
        editSession.close();
        reader.close();
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
  }
}
