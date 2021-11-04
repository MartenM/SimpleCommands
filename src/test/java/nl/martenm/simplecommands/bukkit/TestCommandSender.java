package nl.martenm.simplecommands.bukkit;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestCommandSender {

    private String senderName;

    private List<String> messages = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();

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

    public boolean isExecuted() {
        return messages.stream().anyMatch(message -> message.equalsIgnoreCase("done"));
    }

    public List<String> getMessages() {
        return messages;
    }
}
