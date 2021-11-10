package nl.martenm.simplecommands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ISimpleHelpFormatter {

    void sendHelp(CommandSender sender, List<BaseCommand> subCommands);
}
