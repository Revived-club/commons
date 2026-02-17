package club.revived.commons.bukkit.game.kits;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import club.revived.commons.game.KitType;
import club.revived.commons.data.model.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Repository;

@Repository("kits")
public record KitHolder(@Identifier UUID uuid, List<Kit> kits) implements Entity {

  public record Kit(UUID uuid, KitType kitType, Map<Integer, ItemStack> content) {

  }
}
