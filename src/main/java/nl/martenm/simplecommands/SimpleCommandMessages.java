package nl.martenm.simplecommands;

import org.bukkit.ChatColor;

import java.util.Locale;

public enum SimpleCommandMessages {

    MISSING_ARGUMENTS("&cMissing arguments:&7 %s"),
    DEFAULT_ARGUMENT_ERROR("&cThe argument &7%name%&c could not be parsed. Reason:&r %reason%&c. Value: &r%input%"),
    UNKNOWN_ARGUMENT("&cUnknown command argument:&7 %s"),
    PLAYER_ONLY("&cThis is a player only command."),
    NO_VISIBLE_COMMANDS("&7Commands are present but hidden by the developer."),
    NO_PERMISSION("&cYou do not have permission to execute this command!");

    private final String key;
    private String message;

    SimpleCommandMessages(String message) {
        this.key = name().toLowerCase(Locale.ROOT).replaceAll("_", " ");
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public String m() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
