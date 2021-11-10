package nl.martenm.simplecommands.implementations;

import nl.martenm.simplecommands.arguments.SimpleParsedCommand;
import nl.martenm.simplecommands.arguments.prefab.SimpleArgumentDouble;
import nl.martenm.simplecommands.arguments.prefab.SimpleArgumentFloat;
import nl.martenm.simplecommands.arguments.prefab.SimpleArgumentInteger;
import nl.martenm.simplecommands.arguments.prefab.SimpleArgumentString;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SimpleArgumentTestCommand extends SimpleParsedCommand {


    public SimpleArgumentTestCommand() {
        super("parsed", false);

        addArgument(new SimpleArgumentInteger("someInteger"));
        addArgument(new SimpleArgumentDouble("someDouble"));
        addArgument(new SimpleArgumentFloat("someFloat"));
        addArgument(new SimpleArgumentString("someString"));
    }

    @Override
    protected boolean onArgumentCommand(CommandSender sender, Command command, String s, String[] args, List<Object> parsedArgs) {
        for(Object parsed : parsedArgs) {
            sender.sendMessage(parsed.getClass().getName());
        }

        return true;
    }
}
