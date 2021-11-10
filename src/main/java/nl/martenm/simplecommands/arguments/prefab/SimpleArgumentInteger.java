package nl.martenm.simplecommands.arguments.prefab;

import nl.martenm.simplecommands.arguments.SimpleCommandArgument;
import nl.martenm.simplecommands.arguments.ParseFailedException;

public class SimpleArgumentInteger extends SimpleCommandArgument<Integer> {

    public SimpleArgumentInteger(String name) {
        super(name);
    }

    public SimpleArgumentInteger(String name, String errorMessage) {
        super(name, errorMessage);
    }

    @Override
    protected Integer parseArgument(String argument) throws ParseFailedException {
        Integer integer = null;

        try {
            integer = Integer.parseInt(argument);
        } catch (NumberFormatException ex) {
            throw new ParseFailedException(ex);
        }

        return integer;
    }
}
