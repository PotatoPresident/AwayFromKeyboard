package awayFromKeyboard.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import awayFromKeyboard.AwayFromKeyboard;
import awayFromKeyboard.SubCommand;
import net.md_5.bungee.api.ChatColor;

public class SetTimeCommand extends SubCommand {

	public SetTimeCommand(AwayFromKeyboard afk) {
		super(afk, "settime");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("Usage: /afk settime <minutes>");
			return;
		}

		if (!StringUtils.isNumeric(args[0])) {
			sender.sendMessage(ChatColor.RED + "You must enter a numeric whole number.");
			return;
		}

		int input = Integer.parseInt(args[0]);
		if (input > 1000 || input < 1) {
			sender.sendMessage(ChatColor.RED + "You can only set the time between 2 and 1000 minutes.");
			return;
		}

		if (input == afk.getConfig().getInt("afkTime")) {
			sender.sendMessage(ChatColor.RED + "The AFK time is already set to " + input + ".");
			return;
		}

		afk.getConfig().set("afkTime", input);
		afk.saveConfig();
		Bukkit.broadcast(afk.pluginTag + sender.getName() + " set the AFK time to " + input + " minutes.",
				"afk.changetime");
	}

	@Override
	public String description() {
		return "Time before a player is marked AFK.";
	}

	@Override
	public String usage() {
		return " <minutes>";
	}

	@Override
	public String permission() {
		return "afk.changetime";
	}

}
