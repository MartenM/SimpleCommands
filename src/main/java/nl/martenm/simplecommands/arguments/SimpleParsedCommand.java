package nl.martenm.simplecommands.arguments;

import nl.martenm.simplecommands.SimpleCommand;
import nl.martenm.simplecommands.SimpleCommandMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SimpleParsedCommand extends SimpleCommand {

    private final List<SimpleCommandArgument> arguments = new ArrayList<>();

    public SimpleParsedCommand(String name, boolean playerOnly) {
        super(name, playerOnly);
    }

    public SimpleParsedCommand(String name, String permission, boolean playerOnly) {
        super(name, permission, playerOnly);
    }

    public SimpleParsedCommand(String name, String description, String permission, boolean playerOnly) {
        super(name, description, permission, playerOnly);
    }

    /**
     * Adds an argument. These should be called in order.
     */
    protected void addArgument(SimpleCommandArgument argument) {
        this.arguments.add(argument);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Pre checks;
        if(arguments.size() > args.length) {
            String missing = arguments.stream().skip(args.length).map(SimpleCommandArgument::getName).collect(Collectors.joining(", "));
            sender.sendMessage(String.format(SimpleCommandMessages.MISSING_ARGUMENTS.m(), missing));
            return true;
        }

        // Parse the arguments
        List<Object> parsedArguments = new ArrayList<>();

        for(int i = 0; i < arguments.size(); i++) {
            SimpleCommandArgument parser = arguments.get(i);
            Object parsed = null;
            try {
                parsed = parser.parseArgument(args[i]);
            } catch (ParseFailedException e) {
                parser.sendError(sender, args[i], e);
                return true;
            }

            if (parsed == null) throw new RuntimeException("Parsed value returned null");

            parsedArguments.add(parsed);
        }

        // Call onArgumentCommand.
        return onArgumentCommand(sender, command, s, args, parsedArguments);
    }

    /**
     * Called when parsing was successful.
     * @param sender
     * @param command
     * @param s
     * @param args
     * @param parsedArgs
     * @return
     */
    protected abstract boolean onArgumentCommand(CommandSender sender, Command command, String s, String[] args, List<Object> parsedArgs);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        // Get the correct parser
        String current = args[args.length - 1];
        SimpleCommandArgument parser = this.arguments.get(args.length - 1);
        if(parser == null) return new ArrayList<>();
        return parser.onTabCompletion(current);
    }
}
