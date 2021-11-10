package nl.martenm.simplecommands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected final String name;
    protected final String description;
    protected final String permission;
    protected final boolean playerOnly;

    // Cache the value of the full permission node.
    protected String fullPermission = null;

    // Parent of this node
    protected BaseCommand parent;

    // Help formatter - Not sure if this one should be here but it's anyway.
    protected ISimpleHelpFormatter helpFormatter = null;
    
    public BaseCommand(String name, boolean playerOnly) {
        this.name = name;
        this.playerOnly = playerOnly;
        this.permission = null;
        this.description = null;
    }

    public BaseCommand(String name, String permission, boolean playerOnly) {
        this.name = name;
        this.playerOnly = playerOnly;
        this.permission = permission;
        this.description = null;
    }

    public BaseCommand(String name, String description, String permission, boolean playerOnly) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.playerOnly = playerOnly;
    }

    /**
     * Permission check for this node.
     * Only executed when it's a leaf (in other worlds no subCommands).
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

    protected void setParent(SimpleCommand parent) {
        this.parent = parent;
    }

    protected BaseCommand getParent() {
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
        BaseCommand parent = this.parent;
        while(parent != null) {
            builder.insert(0, parent.getName() + " ");
            parent = parent.getParent();
        }
        builder.append(getName());


        return builder.toString();
    }

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

    public boolean hasDescription() {
        return this.description != null;
    }

    public String getDescription() {
        return description;
    }

}
