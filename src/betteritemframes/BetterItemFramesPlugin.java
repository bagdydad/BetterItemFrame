package betteritemframes;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Lidded;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterItemFramesPlugin extends JavaPlugin implements Listener {

	HashMap<UUID,Location> enderChest = new HashMap<UUID,Location>();

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, 15720);
	}

	@Override
	public void onDisable() {
	}

	@EventHandler(ignoreCancelled = true)
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		if (event.isCancelled() || event.getRightClicked() == null || !event.getRightClicked().getType().toString().contains("ITEM_FRAME") || !event.getHand().equals(EquipmentSlot.HAND) || event.getPlayer().isSneaking()) return;

		event.setCancelled(true);

		Block block = event.getRightClicked().getLocation().getBlock().getRelative(event.getRightClicked().getFacing().getOppositeFace());
		Player player = event.getPlayer();

		openInventory(block, player);

	}

	@EventHandler(ignoreCancelled = true)
	private void onPlayerInteract(PlayerInteractEvent event) {

		if (event.getClickedBlock() == null || !event.getClickedBlock().getType().toString().contains("SIGN") || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getPlayer().isSneaking()) return;

		event.setCancelled(true);

		Block block = event.getClickedBlock().getLocation().getBlock().getRelative(event.getBlockFace().getOppositeFace());
		Player player = event.getPlayer();

		openInventory(block, player);

	}

	@EventHandler(ignoreCancelled = true)
	private void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
			Player player = (Player) event.getPlayer();
			Block block = null;
			if (enderChest.get(player.getUniqueId()) != null) block = enderChest.get(player.getUniqueId()).getBlock();
			if (block != null && block.getType().equals(Material.ENDER_CHEST)) {
				enderChest.remove(player.getUniqueId());
				Lidded enderchest = (Lidded) block.getState();
				enderchest.close();
			}
		}
	}

	private void openInventory(Block block, Player player) {

		if (block.getRelative(BlockFace.UP).getType().isOccluding()) return;

		if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) {
			Chest chest = (Chest) block.getState();
			player.openInventory(chest.getInventory());
		} else if (block.getType().equals(Material.ENDER_CHEST)) {
			Lidded enderchest = (Lidded) block.getState();
			enderchest.open();
			enderChest.put(player.getUniqueId(), block.getLocation());
			player.openInventory(player.getEnderChest());
		} else if (block.getType().equals(Material.BARREL)) {
			Barrel barrel = (Barrel) block.getState();
			player.openInventory(barrel.getInventory());
		} else if (block.getType().toString().contains("SHULKER_BOX")) {
			ShulkerBox shulkerBox = (ShulkerBox) block.getState();
			player.openInventory(shulkerBox.getInventory());
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

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
