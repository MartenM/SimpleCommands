package nl.martenm.simplecommands.arguments.prefab;

import nl.martenm.simplecommands.arguments.SimpleCommandArgument;
import nl.martenm.simplecommands.arguments.ParseFailedException;

public class SimpleArgumentString extends SimpleCommandArgument<String> {

    public SimpleArgumentString(String name) {
        super(name);
    }

    public SimpleArgumentString(String name, String errorMessage) {
        super(name, errorMessage);
    }

    @Override
    protected String parseArgument(String argument) throws ParseFailedException {
        return argument;
    }
}
