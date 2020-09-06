package awayFromKeyboard.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import awayFromKeyboard.AwayFromKeyboard;
import awayFromKeyboard.SubCommand;

public class ListCommand extends SubCommand {

	public ListCommand(AwayFromKeyboard afk) {
		super(afk, "list");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (afk.getAfkPlayers().size() == 0) {
			sender.sendMessage("There are no AFK players at the moment.");
			return;
		}
		if (afk.getAfkPlayers().size() == 1) {
			sender.sendMessage("One player is currently AFK: ");
		} else {
			sender.sendMessage("AFK Players: ");
		}
		for (Player p : afk.getAfkPlayers()) {
			sender.sendMessage("- " + ChatColor.GRAY + p.getName() + ChatColor.RESET + ": " + afk.getTimeAFK(p));
		}
	}

	@Override
	public String description() {
		return "List all afk players.";
	}

	@Override
	public String usage() {
		return "";
	}

	@Override
	public String permission() {
		return "afk.list";
	}

}
