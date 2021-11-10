package nl.martenm.simplecommands.bukkit;

import nl.martenm.simplecommands.SimpleCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TestCommandSender implements CommandSender {

    private String senderName;

    private List<String> messages = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();

    public TestCommandSender(String senderName) {
        this.senderName = senderName;
    }

    protected void handleSendMessage(String message) {
        this.messages.add(message);
        log("MESSAGE", message);
    }

    protected boolean handlePermissionCheck(String permission) {
        log("PERMISSION", permission);
        Objects.requireNonNull(permission);
        return this.permissions.contains(permission);
    }

    private void log(String type, String message) {
        System.out.printf("[%s] %s: %s%n", senderName, type, ChatColor.stripColor(message));
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public void reset() {
        messages.clear();
        permissions.clear();
    }

    public boolean hasReceived(String message) {
        return messages.stream().anyMatch(m -> m.equalsIgnoreCase(message));
    }

    public boolean hasReceived(String message, boolean strippedColour) {
        if(!strippedColour) return hasReceived(message);
        return messages.stream().map(ChatColor::stripColor).anyMatch(m -> m.equalsIgnoreCase(message));
    }

    public boolean isExecuted() {
        return messages.stream().anyMatch(message -> message.equalsIgnoreCase("done"));
    }

    public List<String> getMessages() {
        return messages;
    }

    public void testCommandCompletion(SimpleCommand simpleCommand, Command command, String s, String[] args) {
        List<String> completions = simpleCommand.onTabComplete(this, command, s, args);
        completions.stream().map(ChatColor::stripColor).forEach(suggestion -> {
            suggestions.add(suggestion);
            log("TAB COMPLETION", suggestion);
        });
    }

    public boolean hasTabCompletion(String test) {
        return this.suggestions.contains(test);
    }
}
