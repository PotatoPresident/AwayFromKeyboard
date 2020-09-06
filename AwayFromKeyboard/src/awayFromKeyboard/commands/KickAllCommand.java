package awayFromKeyboard.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import awayFromKeyboard.AwayFromKeyboard;
import awayFromKeyboard.SubCommand;

public class KickAllCommand extends SubCommand {

	public KickAllCommand(AwayFromKeyboard afk) {
		super(afk, "kickall");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		int players = afk.getAfkPlayers().size();
		if (players == 0) {
			afk.error(sender, "No players are away at the moment.");
			return;
		}

		String playerOrPlayers = "";

		if (args.length == 0) {
			if (players == 1) {
				playerOrPlayers = "player";
			} else {
				playerOrPlayers = "players";
			}

			sender.sendMessage("You're about to kick " + ChatColor.RED + players + " " + ChatColor.RESET
					+ playerOrPlayers + ". Are you sure?");
			sender.sendMessage("To confirm, type \"/afk kickall confirm\".");
			return;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
			if (!(sender instanceof ConsoleCommandSender)) {
				afk.logger.info(sender.getName() + " kicked all AFK players.");
			}

			for (Player player : afk.getAfkPlayers()) {
				player.kickPlayer(afk.stringFromConfig(sender, "messageToKickedPlayers"));
				afk.removeAFK(player);
			}
			
			// TODO: Delay this message
			if (afk.getConfig().getBoolean("announceWhenKickingPlayers")) {
				Bukkit.broadcastMessage(afk.stringFromConfig(sender, "announcementToServer"));
			}
		}
	}

	@Override
	public String description() {
		return "Kick all AFK players from the server.";
	}

	@Override
	public String usage() {
		return "";
	}

	@Override
	public String permission() {
		return "afk.kickall";
	}

}
