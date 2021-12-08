package nl.martenm.simplecommands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * A Root command is not supposed to execute any custom command logic.
 * It it's used to create a hierarchy of commands. For example:
 *  /debug start
 *  /debug end
 *  /debug show
 * In this example "debug" is a root command.
 */
public abstract class RootCommand extends SimpleCommand {

    // List of all subCommands
    private final List<SimpleCommand> subCommands = new ArrayList<>();
    // Command map, this includes aliases
    private final Map<String, SimpleCommand> commandMap = new HashMap<>();

    public RootCommand(String name, boolean playerOnly) {
        super(name, playerOnly);
    }

    public RootCommand(String name, String permission, boolean playerOnly) {
        super(name, permission, playerOnly);
    }

    public RootCommand(String name, String description, String permission, boolean playerOnly) {
        super(name, description, permission, playerOnly);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // If there are no subcommands the onCommand method should have been overwritten.
        // Throw an exception if this is not the case.
        if(commandMap.size() == 0) {
            throw new RuntimeException("No sub-commands for the command: " + getFullName());
        }

        // Respect the PlayerOnly command.
        if(!isAllowedSender(sender)) {
            sender.sendMessage(SimpleCommandMessages.PLAYER_ONLY.m());
            return true;
        }

        // If no arguments are provided check if there are any possible sub-commands.
        // Send a help about these. If the available subCommands.size() == 0 that means the sender cannot execute any due to missing
        // permissions or them being playerOnly commands.
        if(args.length == 0) {
            List<SimpleCommand> subCommands = getSubCommands(sender);

            // Check if the subCommands are possible
            if(subCommands.size() == 0) {
                sender.sendMessage(SimpleCommandMessages.NO_PERMISSION.m());
                return true;
            }

            // Remove hidden commands and resend the help.
            subCommands.removeIf(SimpleCommand::isHidden);
            sendHelp(sender, subCommands);
            return true;
        }

        SimpleCommand sc = commandMap.get(args[0]);

        if(sc == null) {
            sender.sendMessage(String.format(SimpleCommandMessages.UNKNOWN_ARGUMENT.m(), args[0]));
            return true;
        }

        /*
         * A subcommand has been found. Check if the permissions apply and if the command
         * is allowed. We do this so that if onCommand is overridden the developer does not have to
         * do this him/her self.
         */

        if(!sc.isAllowedSender(sender)) {
            sender.sendMessage(SimpleCommandMessages.PLAYER_ONLY.m());
            return true;
        }

        // Do the permission check for the child.
        if(!sc.checkPermission(sender)) {
            sender.sendMessage(SimpleCommandMessages.NO_PERMISSION.m());
            return true;
        }

        // Pass on the command to the next handler. Remove the first argument.
        return sc.onCommand(sender, command, s, Arrays.copyOfRange(args, 1, args.length));
    }

    /**
     * Adds the wildcard that nodes with subcommands should not be checked.
     * @param sender The command sender
     * @return True if allowed
     */
    @Override
    public boolean checkPermission(CommandSender sender) {
        if(this.commandMap.size() != 0) return true;
        return super.checkPermission(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        // If the length is 0 we return null. We cannot do any suggestions
        if(args.length == 0) return null;

        // Argument length is one. We can do suggestions now
        if(args.length == 1) {
            List<String> completions = new ArrayList<>();
            getSubCommands(sender).forEach(cmd -> {
                if(cmd.isHidden()) return;
                completions.addAll(cmd.getTabCompletions(args[0]));
            });

            return completions;
        }

        SimpleCommand next = commandMap.get(args[0]);
        if(next == null) return null;

        return next.onTabComplete(sender, command, s, Arrays.copyOfRange(args, 1, args.length));
    }

    /**
     * Command used to add sub-commands to a SimpleCommand.
     * @param command The command
     */
    public void addCommand(SimpleCommand command) {
        if(this.subCommands.contains(command)) {
            throw new RuntimeException(String.format("Attempted to add the command %s but it was already added! (Root: %s)", command.getName(), this.name));
        }
        this.subCommands.add(command);

        // Add the command to the map
        this.commandMap.put(command.name, command);

        // Add aliases to the same hashmap
        command.getAliases().forEach(alias -> this.commandMap.put(alias, command));

        command.setParent(this);
    }

    /**
     * Gets a list of all possible subcommands while respecting:
     *  - The senders permission
     *  - The string the sender already typed
     *  - If there sub-commands in the sub-command the player has permission for.
     * @param sender The sender
     * @return A list of all possible subcommands given the restrictions.
     */
    public List<SimpleCommand> getSubCommands(CommandSender sender) {
        List<SimpleCommand> commands = new ArrayList<>();
        for(SimpleCommand cmd : subCommands) {
            if(cmd.playerOnly && !(sender instanceof Player)) continue;

            // Check permissions. If the command has no permission it will be checked if the arguments do.
            if(!cmd.isAllowed(sender)) continue;

            commands.add(cmd);
        }
        return commands;
    }

    /**
     * Returns true if this command has commands that can be executed by the player.
     * If a node has no permission that means that will return true.
     *
     * @param sender The command sender
     * @return True if this node can be executed.
     */
    @Override
    public boolean isAllowed(CommandSender sender) {
        // TODO: Strict node. If the node has a permission and this is enabled players NEED to have the permission of the current command. No exceptions.
        //if(getFullPermission() != null && !sender.hasPermission(getFullPermission())) return false;

        if(subCommands.size() == 0) {
            return checkPermission(sender);
        }
        return subCommands.stream().anyMatch(cmd -> cmd.isAllowed(sender));
    }

    /**
     * Sends the help for this command node to the command sender specified.
     * @param sender The help receiver
     * @param subCommands The list of subcommands
     */
    protected void sendHelp(CommandSender sender, List<SimpleCommand> subCommands) {
        getHelpFormatter().sendHelp(sender, subCommands);
    }

    /**
     * Gets and returns a collection of all subcommands.
     * @return The subcommands
     */
    public Collection<SimpleCommand> getSubCommands() {
        return this.subCommands;
    }
}
