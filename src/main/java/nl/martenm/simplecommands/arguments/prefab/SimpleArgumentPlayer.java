package nl.martenm.simplecommands.arguments.prefab;

import nl.martenm.simplecommands.arguments.SimpleCommandArgument;
import nl.martenm.simplecommands.arguments.ParseFailedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleArgumentPlayer extends SimpleCommandArgument<Player> {

    public SimpleArgumentPlayer(String name) {
        super(name, "&cError for: &7%name%&c. The player &7%input%&c could not be found.");
    }

    public SimpleArgumentPlayer(String name, String errorMessage) {
        super(name, errorMessage);
    }

    @Override
    protected Player parseArgument(String argument) throws ParseFailedException {
        Player player = Bukkit.getPlayer(argument);

        if(player == null) {
            throw new ParseFailedException("Player could not be found!");
        }

        if(!player.isOnline()) {
            throw new ParseFailedException("Player could not be found!");
        }

        return player;
    }

    @Override
    public List<String> onTabCompletion(String input) {
        return Bukkit.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.startsWith(input))
                .collect(Collectors.toList());
    }
}
