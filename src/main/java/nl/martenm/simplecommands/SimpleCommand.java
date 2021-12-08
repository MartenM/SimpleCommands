package nl.martenm.simplecommands;

import nl.martenm.simplecommands.misc.NameFormat;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The basis for a commands. This abstract class contains all basic information required to structure
 * the commands in a tree like structure. The {@link RootCommand} can be used to create a collection of commands.
 */
public abstract class SimpleCommand implements CommandExecutor, TabCompleter {

    protected final String name;
    protected final String description;
    protected final String permission;
    protected final boolean playerOnly;
    protected List<String> aliases = new ArrayList<>();

    private boolean hidden = false;

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
     * Executes a test to see if the input matches the command name or any of the aliases.
     * Check ignores upper/lower case
     * @param input The test input
     * @return If the command matches
     */
    public boolean testNameMatch(String input) {
        if(this.name.equalsIgnoreCase(input)) return true;
        return this.aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(input));
    }

    /**
     * Returns all possible tab completions for this command including aliases
     * for this command.
     * @param prefix The already typed string
     * @return All possible tab completions
     */
    public List<String> getTabCompletions(String prefix) {
        List<String> completions = new ArrayList<>();
        if(this.name.startsWith(prefix)) completions.add(this.name);
        for(String alias : this.aliases) {
            if(alias.startsWith(prefix)) completions.add(this.name);
        }
        return completions;
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

        // Set the alias for this command.
        if(this.aliases != null) {
            this.aliases.addAll(command.getAliases());
        }

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
     * Returns true if the command has a parent.
     * @return
     */
    public boolean hasParent() {
        return this.parent != null;
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
        return getFullName(NameFormat.NO_ALIAS);
    }

    /**
     * Gets the full command name using its parents.
     * @return The full command.
     */
    public String getFullName(NameFormat format) {
        StringBuilder builder = new StringBuilder();
        SimpleCommand parent = this.parent;
        while(parent != null) {
            switch (format) {
                case NO_ALIAS:
                    builder.insert(0, parent.getName() + " ");
                    break;
                case ALL_ALIAS:
                    builder.insert(0, parent.getAlias() + " ");
                    break;
                case ROOT_ALIAS:
                    // ROOT has no parent
                    if(!parent.hasParent()) builder.insert(0, parent.getAlias() + " ");
                    else builder.insert(0, parent.getName() + " ");
                    break;
            }

            parent = parent.getParent();
        }

        if(format == NameFormat.ALL_ALIAS) builder.append(getAlias());
        else builder.append(getName());

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

    /**
     * Checks if a alias is present;
     * @return True if this command has an alias.
     */
    public boolean hasAlias() {
        return this.aliases.size() > 0;
    }

    /**
     * Gets the alias of this command. It takes the first entry
     * in the list of aliases.
     *
     * If no alias is present it will return the default name of the command.
     * @return The command alias
     */
    public String getAlias() {
        if(hasAlias()) return aliases.get(0);
        return getName();
    }

    /**
     * Get the list of aliases for this command.
     * @return All aliases
     */
    public List<String> getAliases() {
        return this.aliases;
    }

    /**
     * Sets the alias of this command.
     * @param alias The new alias
     */
    public void addAlias(String alias) {
        if(this.aliases.contains(alias)) return;
        this.aliases.add(alias);
    }

    /**
     * Sets if a plugin should be hidden from recommendations.
     * @param hidden
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * True if a plugin is hidden
     * @return True if hidden
     */
    public boolean isHidden() {
        return hidden;
    }
}
