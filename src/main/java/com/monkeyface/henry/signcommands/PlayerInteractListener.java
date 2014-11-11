package com.monkeyface.henry.signcommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

	private SignCommands plugin;

	public PlayerInteractListener(SignCommands p) {
		this.plugin = p;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// Set to proper command sign if
			// Permission && Sneaking
			if (event.getClickedBlock().hasMetadata("command")) {
				if (event.getPlayer().isSneaking()
						&& event.getPlayer().hasPermission(
								"signcommands.signsetting")) {
					Block targetblock = event.getClickedBlock();

					// Turn the targetblock into a proper CommandSign
					// targetblock.setType(Material.SIGN_POST);
					if (targetblock.getState().getType()
							.equals(Material.SIGN_POST)
							|| targetblock.getState().getType().equals(Material.WALL_SIGN)) {
						Sign sign = ((Sign) targetblock.getState());

						String line1 = plugin.getConfig().getString(
								"command_sign_format.line_one");
						String line2 = plugin.getConfig().getString(
								"command_sign_format.line_two");
						String line3 = plugin.getConfig().getString(
								"command_sign_format.line_three");
						String line4 = plugin.getConfig().getString(
								"command_sign_format.line_four");

						// Logger
						plugin.getLogger().info(line1);
						plugin.getLogger().info(line2);
						plugin.getLogger().info(line3);
						plugin.getLogger().info(line4);

						// Line1
						if (line1.equalsIgnoreCase("default")) {
							(sign).setLine(0,
									(ChatColor.GREEN + "[SignCommand]"));
							// sign.setData(arg0);

						} else {
							(sign).setLine(0, line1);
						}
						// Line2
						if (line2.equalsIgnoreCase("default")) {
							(sign).setLine(1, "");
						} else {
							(sign).setLine(1, line2);
						}
						// Line3
						if (line3.equalsIgnoreCase("default")) {
							(sign).setLine(2, (ChatColor.ITALIC + targetblock
									.getMetadata("command").get(0).asString()));
						} else {
							(sign).setLine(2, line3);
						}

						// Line4
						if (line4.equalsIgnoreCase("default")) {
							(sign).setLine(3, event.getPlayer().getDisplayName());
						} else {
							(sign).setLine(3, line4);
						}

						// Logger
						plugin.getLogger().info(sign.getLine(0));
						plugin.getLogger().info(sign.getLine(1));
						plugin.getLogger().info(sign.getLine(2));
						plugin.getLogger().info(sign.getLine(3));

						// Update the sign
						sign.update();
					} else {
						event.getPlayer()
								.sendMessage(
										ChatColor.GREEN
												+ "[SignCommands] "
												+ ChatColor.DARK_RED
												+ " The target must be a sign for this action");
					}
				}

				// Permission
				if (event.getPlayer().hasPermission(
						"signcommands.activatesigns")
						&& !event.getPlayer().isSneaking()) {

					Block block = event.getClickedBlock();
					Player player = event.getPlayer();

					// See what the metadata is
					plugin.getLogger().info(
							"@PlayerInteractEvent: \"command\" meatadate: "
									+ block.getMetadata("command").get(0)
											.asString());
					player.sendMessage(ChatColor.GREEN + "[SignCommands] "
							+ ChatColor.GOLD
							+ block.getMetadata("command").get(0).asString());

					// Run the attached command
					player.getServer().dispatchCommand((CommandSender) player,
							block.getMetadata("command").get(0).asString());

				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().hasMetadata("command")) {
			String cmd = event.getBlock().getMetadata("command").get(0)
					.asString();
			event.getBlock().removeMetadata("command", plugin);
			plugin.getLogger().info(
					"Removed metadata from destroyed block, containing command: "
							+ cmd);
		}
	}

}
