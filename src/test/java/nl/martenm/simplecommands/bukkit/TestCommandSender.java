package nl.martenm.simplecommands.bukkit;

import nl.martenm.simplecommands.SimpleCommandMessages;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TestCommandSender {

    private String senderName;

    private boolean executed = false;
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
        return this.permissions.contains(permission);
    }

    private void log(String type, String message) {
        System.out.println(String.format("[%s] %s: %s", senderName, type, ChatColor.stripColor(message)));
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public void reset() {
        executed = false;
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
