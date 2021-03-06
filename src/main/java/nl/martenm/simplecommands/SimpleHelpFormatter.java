package nl.martenm.simplecommands;

import nl.martenm.simplecommands.misc.NameFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SimpleHelpFormatter implements ISimpleHelpFormatter {

    private String header;

    public SimpleHelpFormatter() {
        this.header = "&6&lCommand Help";
    }

    /**
     * Sends the help to the player.
     * @param sender The sender that should receive the help page.
     * @param subCommands The commands that are available for this sender.
     */
    @Override
    public void sendHelp(CommandSender sender, List<SimpleCommand> subCommands) {
        send(sender, String.format("&7===============[ %s &7]===============", header));
        send(sender, " ");

        if(subCommands.size() == 0) {
            send(sender, SimpleCommandMessages.NO_VISIBLE_COMMANDS.m());
        }

        for(SimpleCommand cmd : subCommands) {
            if(cmd.hasDescription()) {
                send(sender, String.format("&2/&a%s &7- &e%s", cmd.getFullName(NameFormat.ROOT_ALIAS), cmd.getDescription()));
            } else {
                send(sender, String.format("&2/&a%s", cmd.getFullName(NameFormat.ROOT_ALIAS)));
            }

        }

        send(sender, " ");
    }

    /**
     * Sets the header name.
     * @param header The new header name.
     */
    public void setHeader(String header) {
        this.header = header;
    }

    private void send(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
