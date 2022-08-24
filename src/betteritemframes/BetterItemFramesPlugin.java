package betteritemframes;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterItemFramesPlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, 15720);
	}

	@Override
	public void onDisable() {
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		if (event.isCancelled()) return;
		if (!event.getRightClicked().getType().toString().contains("ITEM_FRAME")) return;

		if (event.getPlayer().isSneaking()) return;

		event.setCancelled(true);

		if (!event.getHand().equals(EquipmentSlot.HAND)) return;

		Block block = event.getRightClicked().getLocation().getBlock().getRelative(event.getRightClicked().getFacing().getOppositeFace());
		Player player = event.getPlayer();

		if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) {
			Chest chest = (Chest) block.getState();
			player.openInventory(chest.getInventory());
		} else if (block.getType().equals(Material.ENDER_CHEST)) {
			player.openInventory(player.getEnderChest());
			player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.7f, 0.7f);
		} else if (block.getType().equals(Material.BARREL)) {
			Barrel barrel = (Barrel) block.getState();
			player.openInventory(barrel.getInventory());
		} else if (block.getType().toString().contains("SHULKER_BOX")) {
			ShulkerBox shulkerBox = (ShulkerBox) block.getState();
			player.openInventory(shulkerBox.getInventory());
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		if (event.isCancelled() || !(event.getDamager() instanceof Player)) return;

		Player player = (Player) event.getDamager();
		Entity itemFrameEntity = event.getEntity();

		if (!itemFrameEntity.getType().toString().contains("ITEM_FRAME")) return;

		ItemFrame itemFrame = (ItemFrame) itemFrameEntity;

		if (!itemFrame.getItem().getType().equals(Material.AIR) && itemFrame.isVisible() && !player.isSneaking()) {
			event.setCancelled(true);
			itemFrame.setVisible(false);
			itemFrameEntity.getWorld().playSound(itemFrameEntity.getLocation(), Sound.ENTITY_SHULKER_CLOSE, 0.7f, 0.7f);
		} else if (!itemFrame.isVisible()) {
			event.setCancelled(true);
			itemFrame.setVisible(true);
			itemFrameEntity.getWorld().playSound(itemFrameEntity.getLocation(), Sound.ENTITY_SHULKER_OPEN, 0.7f, 0.7f);
		}

	}

}
