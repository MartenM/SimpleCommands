package nl.martenm.simplecommands.implementations;

import nl.martenm.simplecommands.SimpleCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SubAliases extends SimpleCommand {
    public SubAliases(boolean add) {
        super("aliases", false);
        if(add) addDefaultAliases();
    }

    public void addDefaultAliases() {
        addAlias("aa");
        addAlias("bb");
        addAlias("cc");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage("DONE");
        return true;
    }
}
