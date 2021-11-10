package nl.martenm.simplecommands.arguments.prefab;

import nl.martenm.simplecommands.arguments.ParseFailedException;
import nl.martenm.simplecommands.arguments.SimpleCommandArgument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleBooleanArgument extends SimpleCommandArgument<Boolean> {

    public SimpleBooleanArgument(String name) {
        super(name);
    }

    public SimpleBooleanArgument(String name, String errorMessage) {
        super(name, errorMessage);
    }

    @Override
    protected Boolean parseArgument(String argument) throws ParseFailedException {
        Boolean output = null;

        if(argument.equalsIgnoreCase("true")) return true;
        if(argument.equalsIgnoreCase("false")) return false;
        throw new ParseFailedException("Could not parse the boolean.");
    }

    @Override
    public List<String> onTabCompletion(String input) {
        List<String> potentional = Arrays.asList("true", "false");
        return potentional.stream().filter(s -> s.startsWith(input)).collect(Collectors.toList());
    }
}
