package awayFromKeyboard.commands;

import org.bukkit.command.CommandSender;

import awayFromKeyboard.AwayFromKeyboard;
import awayFromKeyboard.SubCommand;

public class MenuCommand extends SubCommand {

	public MenuCommand(AwayFromKeyboard afk) {
		super(afk, "menu");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		afk.displayMenu(sender);
	}

	@Override
	public String description() {
		return "Display this menu.";
	}

	@Override
	public String usage() {
		return "";
	}

	@Override
	public String permission() {
		return "afk.menu";
	}

}
