package nl.martenm.simplecommands.arguments;

import nl.martenm.simplecommands.SimpleCommandMessages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Argument for a SimpleParsedCommand.
 * This class provides a parser and tab completer.
 * @param <T> The class of object this parser will return.
 */
public abstract class SimpleCommandArgument<T> {

    private final String name;
    private final String errorMessage;

    /**
     * Simple constructor with the default error message.
     * @param name The name of this argument. Eg: target, id
     */
    public SimpleCommandArgument(String name) {
        this.name = name;
        this.errorMessage = SimpleCommandMessages.DEFAULT_ARGUMENT_ERROR.m();
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

    /**
     * Called when a string needs to be converted into it's object.
     * @param argument The argument to be converted
     * @return A object
     * @throws ParseFailedException Thrown when parsing is not possible
     */
    protected abstract T parseArgument(String argument) throws ParseFailedException;

    /**
     * Called when a tab-completion is being called for.
     * @param input The input already there
     * @return A list of tab completions
     */
    public List<String> onTabCompletion(String input) {
        return Collections.singletonList(ChatColor.AQUA + this.getName() + ChatColor.RESET);
    }

    /**
     * The error to be send to the user when the parsing is unsuccessful.
     * @param sender The command executor
     * @param input The input string
     * @param ex The parse exception
     */
    public void sendError(CommandSender sender, String input, Exception ex) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getErrorMessage(input, ex)));
    }

    /**
     * Gets the error message. Replacing the parameters: %name%, %input%, %reason%.
     * @param input The input argument
     * @param ex The parse exception
     * @return A formatted error message
     */
    private String getErrorMessage(String input, Exception ex) {
        return this.errorMessage
                .replace("%name%", this.name)
                .replace("%input%", input)
                .replace("%reason%", ex.getMessage());
    }

    /**
     * The name of this argument.
     * Used to help guide the user enter the right one.
     * @return The argument name
     */
    public String getName() {
        return name;
    }
}
