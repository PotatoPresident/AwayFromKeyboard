package awayFromKeyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import awayFromKeyboard.commands.KickAllCommand;
import awayFromKeyboard.commands.ListCommand;
import awayFromKeyboard.commands.ReloadCommand;
import awayFromKeyboard.commands.SetTimeCommand;
import net.md_5.bungee.api.ChatColor;

public class AwayFromKeyboard extends JavaPlugin implements Listener, CommandExecutor {
	private String version = "1.0";
	private List<SubCommand> commands = new ArrayList<>();
	public ConcurrentMap<UUID, Boolean> afkMap = new ConcurrentHashMap<UUID, Boolean>();
	public ConcurrentMap<UUID, Long> timeWentAFK = new ConcurrentHashMap<UUID, Long>();
	public ConcurrentMap<UUID, Integer> runnableMap = new ConcurrentHashMap<UUID, Integer>();
	public ConcurrentMap<UUID, Boolean> inBufferPeriod = new ConcurrentHashMap<UUID, Boolean>();
	public String pluginTag = ChatColor.RED + "[AFK] " + ChatColor.RESET;
	public Logger logger = Bukkit.getLogger();

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		this.logger.info("Enabling AwayFromKeyboard " + version + "...");
		new Listeners(this);
		commands.add(new ListCommand(this));
		commands.add(new SetTimeCommand(this));
		commands.add(new ReloadCommand(this));
		commands.add(new KickAllCommand(this));
		addDefaultMessage("markedYourselfAfk", "You marked yourself as AFK.");
		addDefaultMessage("isNowAfk", "%playername% is now AFK.");
		addDefaultMessage("noLongerAfk", "%playername% is no longer AFK.");
		addDefaultMessage("announcementToServer",
				"&c[Notice] &7All AFK players have been kicked due to poor server performance.");
		addDefaultMessage("messageToKickedPlayers", "All AFK players have been kicked due to poor server performance.");
		addDefaultMessage("tabListTag", "&8AFK");
		addDefaultMessage("noPermission", "&cError: &rYou cannot do that.");
		this.getConfig().addDefault("afkTime", 5);
		this.getConfig().addDefault("consoleNotifications", true);
		this.getConfig().addDefault("announceWhenKickingPlayers", true);
		this.getConfig().addDefault("displayTabListTag", true);
		this.saveDefaultConfig();
	}

	private void addDefaultMessage(String path, String message) {
		this.getConfig().addDefault("messages." + path,
				"'" + ChatColor.translateAlternateColorCodes('&', message) + "'");
	}

	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this); /** Cancel all tasks */
		this.logger.info("Disabled AwayFromKeyboard " + version + ".");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		/** Handle the base /afk command */
		if (args.length < 1) {

			if (!(sender instanceof Player)) {
				displayMenu(sender);
				return true;
			}

			Player player = (Player) sender;
			if (!player.hasPermission("afk.goafk")) {
				
				// message player if they don't see notifications to prevent lack of command feedback
				if (!player.hasPermission("afk.seeNotifications")) {
					notification(player, "markedYourselfAfk");
				}
				noPermission(sender);
				return true;
			}

			setAFK(player);
			applyBuffer(player); // prevent removal of afk status for 3 seconds
			return true;
		}

		String[] restOfArgs = Arrays.copyOfRange(args, 1, args.length);

		for (SubCommand subCmd : commands) {
			if (subCmd.getName().equals(args[0])) {
				if (sender.hasPermission(subCmd.permission())) {
					subCmd.execute(sender, restOfArgs);
				} else {
					noPermission(sender);
				}
				return true;
			}
		}

		displayMenu(sender);
		return true;
	}

	public void displayMenu(CommandSender s) {
		String arrow = ChatColor.GRAY + "> " + ChatColor.DARK_GRAY;
		s.sendMessage(arrow + ChatColor.GREEN + "AwayFromKeyboard Usage");

		if (s.hasPermission("afk.goafk"))
			s.sendMessage(arrow + "/afk" + ChatColor.RESET + " - " + "Mark yourself as AFK.");

		for (SubCommand cmd : commands) {
			if (s.hasPermission(cmd.permission())) {
				s.sendMessage(arrow + "/afk " + ChatColor.GRAY + cmd.getName() + cmd.usage() + ChatColor.WHITE + " - "
						+ cmd.description());
			}
		}
	}

	public void setAFK(Player player) {
		afkMap.put(player.getUniqueId(), true);
		notification(player, "isNowAfk");
	}

	public void removeAFK(Player player) {
		afkMap.put(player.getUniqueId(), false);
		if (player.isOnline()) {
			notification(player, "noLongerAfk");
		}
		if (getConfig().getBoolean("displayTabListTag")) {
			player.setPlayerListName(player.getName());
		}
	}

	public void noPermission(CommandSender sender) {
		sender.sendMessage(stringFromConfig(sender, "noPermission"));
	}

	public void error(CommandSender sender, String error) {
		sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + error);
	}

	public String stringFromConfig(CommandSender sender, String path) {
		String fromConfig = getConfig().getString("messages." + path).replaceAll("'", "");
		String message = ChatColor.translateAlternateColorCodes('&', fromConfig);

		if (message != null && path != null)
			message = message.replace("%playername%", sender.getName());

		return message;
	}

	public void applyBuffer(Player player) {
		inBufferPeriod.put(player.getUniqueId(), true);
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {

			@Override
			public void run() {
				inBufferPeriod.put(player.getUniqueId(), false);
			}

		}, 60);
	}

	public void notification(CommandSender sender, String message) {
		message = stringFromConfig(sender, message);

		if (getConfig().getBoolean("consoleNotifications"))
			logger.info(message);

		Bukkit.broadcast(message, "afk.seeNotifications");
	}

	public String getTimeAFK(Player p) {
		long now = System.currentTimeMillis();
		long wentAFK = timeWentAFK.get(p.getUniqueId());
		long timeAFK = now - wentAFK;
		long secondsAFK = timeAFK / 1000;
		String result = String.format("%02dh %02dm", TimeUnit.SECONDS.toHours(secondsAFK),
				TimeUnit.SECONDS.toMinutes(secondsAFK) % TimeUnit.HOURS.toMinutes(1));
		if (secondsAFK < 72000) {
			return String.format("%02d", TimeUnit.SECONDS.toMinutes(secondsAFK) % TimeUnit.HOURS.toMinutes(1)) + "m";
		}
		return result;
	}

	public List<Player> getAfkPlayers() {
		List<Player> afkList = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (playerIsAFK(player)) {
				afkList.add(player);
			}
		}
		return afkList;
	}

	public boolean playerIsAFK(Player p) {
		if (afkMap.get(p.getUniqueId()) == null) {
			return false;
		} else {
			return afkMap.get(p.getUniqueId());
		}
	}
}
