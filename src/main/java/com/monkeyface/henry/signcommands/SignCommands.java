package com.monkeyface.henry.signcommands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SignCommands extends JavaPlugin {

	public static final String[] commands = { "create <command>", "delete",
			"show" };

	@Override
	public void onEnable() {
		getLogger().info("SignCommands has been enabled");

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerInteractListener(this), this);

		updateConfig();
	}

	// Setup config.yml
	public void updateConfig() {
		if (!new File(getDataFolder(), "config.yml").exists()) {
			this.saveDefaultConfig();
		}
		this.reloadConfig();
	}

	@Override
	public void onDisable() {
		getLogger().info("SignCommands has been disabled");
		updateConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (sender instanceof Player) {
			// Is a player
			Player player = (Player) sender;

			// Block to play with (directly in front of player)
			Block targetblock = null;
			int x = 0, y = 0, z = 0;
			// Logger direction
			getLogger().info(getCardinalDirection(player));
			if (getCardinalDirection(player).equals("N")) {
				x = -1;
			} else if (getCardinalDirection(player).equals("E")) {
				z = -1;
			} else if (getCardinalDirection(player).equals("S")) {
				x = 1;
			} else if (getCardinalDirection(player).equals("W")) {
				z = 1;
			} else {
				player.sendMessage(ChatColor.GREEN + "[SignCommands]"
						+ ChatColor.DARK_RED
						+ " You must be facing a cardinal direction");
				return false;
			}
			targetblock = player.getLocation().add(x, y, z).getBlock();
			// player.get
			getLogger()
					.info("Got targetblock, with \"command\" metadata on it: "
							+ String.valueOf(targetblock.hasMetadata("command")));

			// Is SignCommands command
			if (cmd.getName().equalsIgnoreCase("signcommands")
					|| cmd.getName().equalsIgnoreCase("sc")) {
				// Permissions
				if (!player.hasPermission("signcommands.commands")) {
					player.sendMessage("You do not have permission!");
					return false;
				}

				// General Info Command
				if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
					player.sendMessage(ChatColor.GREEN + "[SignCommands] "
							+ ChatColor.DARK_GREEN + "SignCommands Commands:");
					for (String i : commands) {
						player.sendMessage(ChatColor.DARK_GREEN + "     /cb "
								+ i);
					}
					player.sendMessage(ChatColor.LIGHT_PURPLE
							+ "(Always targets block immediately in front you)");
				}

				// /sc DELETE or SHOW
				if (args.length == 1) {

					if (args[0].equals("create")) {
						// Incorrect usage of CREATE
						player.sendMessage(ChatColor.GREEN
								+ "[SignCommands]"
								+ ChatColor.DARK_RED
								+ " incorrect usage: you need to supply a command after 'create'");
					} else if (targetblock.hasMetadata("command")) {
						// DELETE
						if (args[0].equals("delete")) {
							String storedcommand = targetblock
									.getMetadata("command").get(0).asString();
							targetblock.removeMetadata("command", this);
							player.sendMessage(ChatColor.GREEN
									+ "[SignCommands]" + ChatColor.DARK_GREEN
									+ "Removed '" + ChatColor.BLUE
									+ ChatColor.DARK_GREEN + storedcommand
									+ "' command from block");

							// SHOW
						} else if (args[0].equals("show")) {
							player.sendMessage(ChatColor.GREEN
									+ "[SignCommands]"
									+ ChatColor.DARK_GREEN + " " 
									+ targetblock.getMetadata("command").get(0)
											.asString());
						}

						// HELP
					} else if (args[0].equals("help")) {
						// Already done

						// Block Has no metadata
					} else {
						player.sendMessage(ChatColor.GREEN + "[SignCommands]"
								+ ChatColor.DARK_RED
								+ " This block has no SignCommands metadata");
					}

					// CREATE
				} else if (args.length >= 2) {
					getLogger().info("Got args.length >= 2");

					// Which SignCommands command
					if (args[0].equals("create")) {
						getLogger().info("Got CREATE Command");

						// Passed <command> argument
						String command = "";
						for (int i = 1; i < args.length; i++) {
							command += args[i] + " ";
						}
						getLogger().info("Got arg1: " + command);

						// Block to set the command of
						getLogger().info(
								"Got targetblock at "
										+ targetblock.getLocation().toString());

						// Set metadata "command" to the passed command
						targetblock.setMetadata("command",
								new FixedMetadataValue(this, command));
						getLogger().info(
								"Set \"command\" metadata of targetblock to"
										+ targetblock.getMetadata("command"));
						getLogger().info(
								"targetblock has \"command\" metadata on it? "
										+ String.valueOf(targetblock
												.hasMetadata("command")));

						// Player feedback
						player.sendMessage(ChatColor.GREEN
								+ "[SignCommands] "
								+ ChatColor.DARK_GREEN
								+ "Command Set: "
								+ ChatColor.GREEN
								+ targetblock.getMetadata("command").get(0)
										.asString());
					}
					return true;
				}
			}
			return true;
		}

		// Not a player
		sender.sendMessage(ChatColor.GREEN + "[SignCommands] " + ChatColor.RED
				+ "You must be a player to use this command");
		return false;
	}

	public static String getCardinalDirection(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
			return "N";
		} else if (22.5 <= rotation && rotation < 67.5) {
			return "NE";
		} else if (67.5 <= rotation && rotation < 112.5) {
			return "E";
		} else if (112.5 <= rotation && rotation < 157.5) {
			return "SE";
		} else if (157.5 <= rotation && rotation < 202.5) {
			return "S";
		} else if (202.5 <= rotation && rotation < 247.5) {
			return "SW";
		} else if (247.5 <= rotation && rotation < 292.5) {
			return "W";
		} else if (292.5 <= rotation && rotation < 337.5) {
			return "NW";
		} else if (337.5 <= rotation && rotation < 360.0) {
			return "N";
		} else {
			return null;
		}
	}
}
