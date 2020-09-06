package awayFromKeyboard.commands;

import org.bukkit.command.CommandSender;

import awayFromKeyboard.AwayFromKeyboard;
import awayFromKeyboard.SubCommand;

public class ReloadCommand extends SubCommand {

	public ReloadCommand(AwayFromKeyboard afk) {
		super(afk, "reload");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		afk.reloadConfig();
		afk.getLogger().info(sender.getName() + " reloaded the configuration.");
		sender.sendMessage(afk.pluginTag + "Successfully reloaded the configuration.");
	}

	@Override
	public String description() {
		return "Reload the plugin.";
	}

	@Override
	public String usage() {
		return "";
	}

	@Override
	public String permission() {
		return "afk.reload";
	}
}
