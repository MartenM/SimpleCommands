package nl.martenm.simplecommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SimpleHelpFormatter {

    private String pluginName;

    public SimpleHelpFormatter() {
        this.pluginName = "&6&lCommand Help";

    }

    public void sendHelp(CommandSender sender, List<SimpleCommand> subCommands) {
        send(sender, String.format("&7===============[ %s &7]===============", pluginName));
        send(sender, " ");

        for(SimpleCommand cmd : subCommands) {
            send(sender, String.format("&2/&a%s &7- &e%s", cmd.getFullName(), cmd.getDescription()));
        }

        send(sender, " ");
    }

    private void send(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }
}
