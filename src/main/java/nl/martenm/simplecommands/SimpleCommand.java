package nl.martenm.simplecommands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * The basis for a commands. This abstract class contains all basic information required to structure
 * the commands in a tree like structure. The {@link RootCommand} can be used to create a collection of commands.
 */
public abstract class SimpleCommand implements CommandExecutor, TabCompleter {

    protected final String name;
    protected final String description;
    protected final String permission;
    protected final boolean playerOnly;

    // Cache the value of the full permission node.
    protected String fullPermission = null;

    // Parent of this node
    protected SimpleCommand parent;

    // Help formatter - Not sure if this one should be here but it's anyway.
    protected ISimpleHelpFormatter helpFormatter = null;

    /**
     * Create a SimpleCommand with no permission and no description.
     * @param name The command name
     * @param playerOnly If the command is player only or not.
     */
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
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        // Default no tab completion.
        return new ArrayList<>();
    }

    /**
     * Permission check for this node.
     * @param sender The command sender
     * @return True if allowed
     */
    public boolean checkPermission(CommandSender sender) {
        if(this.getFullPermission() == null) return true;
        return sender.hasPermission(this.getFullPermission());
    }

    /**
     * Checks if the sender is allowed to execute this command.
     * Mainly used for playerOnly checks.
     * @param sender The command sender
     * @return True if allowed.
     */
    public boolean isAllowedSender(CommandSender sender) {
        if(this.playerOnly && !(sender instanceof Player)) return false;
        else return true;
    }

    /**
     * Gets the original permission of this node.
     * This can include wildcards like the '+' symbol.
     * @return
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Check if a command is allowed to be executed, keeping in mind playerOnly and the
     * permissions. This method can be overridden by sub-classes.
     * @param sender The command sender
     * @return True if this command can be executed.
     */
    public boolean isAllowed(CommandSender sender) {
        return checkPermission(sender);
    }

    /**
     * Used to create the full permission node for a command.
     * Commands can have their own specific permission but in order to make nesting
     * easier the + operator can be used to concat to the permission of the parent node.
     * @return A command node like "commands.debug.test.xxx"
     */
    public String getFullPermission() {
        if(fullPermission != null) return fullPermission;

        // If it does not start with the + operator we return this root permission.
        // For cases were an argument has no permission (null) we get the permission of it's parent.
        if(this.permission == null) {
            if(parent == null) return null;
            return parent.getFullPermission();
        }
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


    /**
     * Register the plugin to the server using the plugin specified.
     * @param plugin The plugin the command should be registered too.
     */
    public void registerCommand(JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand(this.name);
        if(command == null) throw new RuntimeException(String.format("Plugin tried to register the SimpleCommand /%s but it was not specified in the plugin.yml", name));
        if(parent != null) throw new RuntimeException(String.format("Plugin tried to register the SimpleCommand /%s but has a parent node!", getFullName()));

        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    /**
     * Sets the parent of this command
     * @param parent The parent
     */
    protected void setParent(RootCommand parent) {
        this.parent = parent;
    }

    /**
     * Gets the parent of this command.
     * @return
     */
    protected SimpleCommand getParent() {
        return parent;
    }

    /**
     * Returns the command name.
     * @return The command name
     */
    public String getName() {
        return this.name;
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

    /**
     * Get the help formatter for this command.
     * At the moment this is mostly used for nested commands that have a {@link RootCommand} as parent.
     * @return The formatter
     */
    protected ISimpleHelpFormatter getHelpFormatter() {
        if(this.helpFormatter != null) return helpFormatter;
        if(this.parent != null) return parent.getHelpFormatter();

        this.helpFormatter = new SimpleHelpFormatter();
        return this.helpFormatter;
    }

    /**
     * Sets the formatter for this command.
     * Please note that each root command can have it's own look and feel help.
     * @param formatter The new formatter
     */
    protected void setHelpFormatter(SimpleHelpFormatter formatter) {
        this.helpFormatter = formatter;
    }

    /**
     * Checks if the command has a description set.
     * @return True if a description is available
     */
    public boolean hasDescription() {
        return this.description != null;
    }

    /**
     * Gets the description
     * @return The description
     */
    public String getDescription() {
        return description;
    }

}
