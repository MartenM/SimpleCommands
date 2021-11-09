package nl.martenm.simplecommands.arguments;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Argument for a SimpleParsedCommand.
 * This class provides a parser and tab completer.
 * @param <T> The class of object this parser will return.
 */
public abstract class SimpleCommandArgument<T extends Object> {

    private String name;
    private String errorMessage;

    /**
     * Simple constructor with the default error message.
     * @param name The name of this argument. Eg: target, id
     */
    public SimpleCommandArgument(String name) {
        this.name = name;
        this.errorMessage = "The argument %name% could not be parsed. Value %input%";
    }

    /**
     * Advanced constructor which allows to change the error message.
     * The errorMessage supports the following placeholders: %name% - %input%
     * @param name The name of this argument. Eg: target, id
     * @param errorMessage Error message
     */
    public SimpleCommandArgument(String name, String errorMessage) {
        this.name = name;
        this.errorMessage = errorMessage;
    }

    protected abstract T parseArgument(String argument) throws ParseFailedException;

    public List<String> onTabCompletion(String input) {
        return Collections.singletonList(ChatColor.AQUA + this.getName() + ChatColor.RESET);
    }

    public void sendError(CommandSender sender, String input, Exception ex) {
        sender.sendMessage(getErrorMessage(input));
    }

    private String getErrorMessage(String input) {
        return this.errorMessage.replace("%name%", this.name).replace("%input%", input);
    }

    public String getName() {
        return name;
    }
}
