package nl.martenm.simplecommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SimpleCommand implements CommandExecutor, TabCompleter {

    private final String name;
    private final String description;
    private final String permission;
    private final boolean playerOnly;

    // Cache the value of the full permission node.
    private String fullPermission = null;

    // Help formatter
    private SimpleHelpFormatter helpFormatter = new SimpleHelpFormatter();

    // Keep track of the parent in case we need to attach commands.
    private SimpleCommand parent;
    private Map<String, SimpleCommand> subCommands = new HashMap<>();

    public SimpleCommand(String name, boolean playerOnly) {
        this.name = name;
        this.playerOnly = playerOnly;
        this.permission = null;
        this.description = null;
    }

    public SimpleCommand(String name, String permission, boolean playerOnly) {
        this.name = name;
        this.playerOnly = playerOnly;
        this.permission = permission;
        this.description = null;
    }

    public SimpleCommand(String name, String description, String permission, boolean playerOnly) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.playerOnly = playerOnly;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // For roots
        if(this.permission != null && !sender.hasPermission(this.permission)) {
            sender.sendMessage(SimpleCommandMessages.NO_PERMISSION.m());
            return false;
        }

        // Respect the PlayerOnly command.
        if(playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(SimpleCommandMessages.PLAYER_ONLY.m());
            return true;
        }

        // Check if arguments are provided. If not we give a list of possible arguments.
        // If there are no arguments we have reached a dead end (at least for this sender).
        if(args.length == 0) {
            List<SimpleCommand> subCommands = getSubCommands(sender, "");
            if(subCommands.size() == 0) sender.sendMessage(SimpleCommandMessages.UNKNOWN_COMMAND.m());
            else {
                sendHelp(sender);
            }
            return true;
        }

        SimpleCommand sc = subCommands.get(args[0]);
        if(sc == null) {
            sender.sendMessage(String.format(SimpleCommandMessages.UNKNOWN_ARGUMENT.m(), args[0]));
            return true;
        }

        /*
         * A subcommand has been found. Check if the permissions apply and if the command
         * is allowed. We do this so that if onCommand is overridden the developer does not have to
         * do this him/her self.
         */

        if(sc.playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(SimpleCommandMessages.PLAYER_ONLY.m());
            return true;
        }

        // For arguments
        if(sc.permission != null && !sender.hasPermission(sc.permission)) {
            sender.sendMessage(SimpleCommandMessages.NO_PERMISSION.m());
            return true;
        }

        // Pass on the command to the next handler. Remove the first argument.
        return sc.onCommand(sender, command, s, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        // If the length is 0 we return null. We cannot do any suggestions
        if(args.length == 0) return null;

        // Argument length is one. We can do suggestions now
        if(args.length == 1) {
            List<String> completions = new ArrayList<>();
            getSubCommands(sender, args[0]).forEach(cmd -> completions.add(cmd.name));

            return completions;
        }

        SimpleCommand next = subCommands.get(args[0]);
        if(next == null) return null;

        return next.onTabComplete(sender, command, s, Arrays.copyOfRange(args, 1, args.length));
    }

    /**
     * Command used to add sub-commands to a SimpleCommand.
     * @param command The command
     */
    public void addCommand(SimpleCommand command) {
        this.subCommands.put(command.name, command);
        command.setParent(this);
    }

    private void setParent(SimpleCommand parent) {
        this.parent = parent;
    }

    /**
     * Gets a list of all possible subcommands while respecting:
     *  - The senders permission
     *  - The string the sender already typed
     *  - If there sub-commands in the sub-command the player has permission for.
     * @param sender The sender
     * @param prefix The string the user already has typed. Used for command completion.
     * @return A list of all possible subcommands given the restrictions.
     */
    public List<SimpleCommand> getSubCommands(CommandSender sender, String prefix) {
        List<SimpleCommand> commands = new ArrayList<>();
        for(SimpleCommand cmd : subCommands.values()) {
            if(!cmd.name.startsWith(prefix)) continue;
            if(cmd.playerOnly && !(sender instanceof Player)) continue;

            // Check permissions. If the command has no permission it will be checked if the arguments do.
            if(!cmd.depthPermissionSearch(sender)) continue;

            commands.add(cmd);
        }
        return commands;
    }

    /**
     * Returns true if this command has commands that can be exectued by the player
     * @param sender
     * @return
     */
    boolean depthPermissionSearch(CommandSender sender) {
        if(!sender.hasPermission(getFullPermission())) return false;
        if(subCommands.size() == 0) return true;
        return subCommands.values().stream().anyMatch(cmd -> cmd.depthPermissionSearch(sender));
    }

    protected void sendHelp(CommandSender sender) {
        this.helpFormatter.sendHelp(sender, this);
    }

    /**
     * Returns the command name.
     * @return
     */
    public String getName() {
        return this.name;
    }

    public String getDescription() {
        if(description == null) return "";
        return description;
    }

    protected SimpleCommand getParent() {
        return parent;
    }

    public Collection<SimpleCommand> getSubCommands() {
        return this.subCommands.values();
    }

    /**
     * Gets the full command name using its parents.
     * @return The full command.
     */
    public String getFullName() {
        if(parent == null) return getName();

        StringBuilder builder = new StringBuilder();
        SimpleCommand parent = this.parent;
        while(parent != null) {
            builder.insert(0, parent.getName() + " ");
            parent = parent.getParent();
        }
        builder.append(getName());


        return builder.toString();
    }

    public String getPermission() {
        return permission;
    }

    /**
     * Used to create the full permission node for a command.
     * Commands can have their own specific permission but in order to make nesting
     * easier the + operator can be used to concat to the permission of the parent node.
     * @return
     */
    public String getFullPermission() {
        if(fullPermission != null) return fullPermission;

        // If it does not start with the + operator we return this root permission.
        if(this.permission == null) return null;
        if(!this.permission.startsWith("+")) return getPermission();

        // Check if we have a parent. If not, thrown an exception
        if(parent == null) throw new RuntimeException(String.format("Cannot concat the permission %s to it's parent because it has none!", this.permission));

        // Recursion step:
        String parentPermission = parent.getFullPermission();

        // Check if the parent permission is null. If so we cannot attach to it.
        // The other option would be to skip it but this can lead to confusing permission nodes.
        if(parentPermission == null) throw new RuntimeException(String.format("Cannot concat the permission %s to it's parent because it parent (%s) has no permission!", permission, parent.getFullName()));

        this.fullPermission = parentPermission + "." + this.permission.substring(1);
        return this.fullPermission;
    }
}
