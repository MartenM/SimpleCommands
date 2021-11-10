package nl.martenm.simplecommands.implementations;

import nl.martenm.simplecommands.SimpleCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SubAttached extends SimpleCommand {
    public SubAttached() {
        super("attachedPermission", "+attached", false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        sender.sendMessage("DONE");
        return true;
    }
}
