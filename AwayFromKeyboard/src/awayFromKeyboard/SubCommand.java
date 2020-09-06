package awayFromKeyboard;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
public abstract class SubCommand {
    protected AwayFromKeyboard afk;
    private String name;

    public SubCommand(AwayFromKeyboard afk, String name) {
        this.afk = afk;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    public abstract void execute(CommandSender sender, String[] args);
    
    public abstract String permission();

    public abstract String description();

    public abstract String usage();
}