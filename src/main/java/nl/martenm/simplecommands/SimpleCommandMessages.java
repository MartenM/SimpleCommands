package nl.martenm.simplecommands;

import org.bukkit.ChatColor;

import java.util.Locale;

public enum SimpleCommandMessages {

    UNKNOWN_COMMAND("&cUnknown command"),
    UNKNOWN_ARGUMENT("&cUnknown command argument:&7 %s"),
    PLAYER_ONLY("&cThis is a player only command."),
    NO_PERMISSION("&cYou do not have permission to execute this command!");

    private String key;
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
