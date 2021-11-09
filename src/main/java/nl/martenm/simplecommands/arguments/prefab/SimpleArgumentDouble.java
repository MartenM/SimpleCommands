package nl.martenm.simplecommands.arguments.prefab;

import nl.martenm.simplecommands.arguments.SimpleCommandArgument;
import nl.martenm.simplecommands.arguments.ParseFailedException;

public class SimpleArgumentDouble extends SimpleCommandArgument<Double> {

    public SimpleArgumentDouble(String name) {
        super(name);
    }

    public SimpleArgumentDouble(String name, String errorMessage) {
        super(name, errorMessage);
    }

    @Override
    protected Double parseArgument(String argument) throws ParseFailedException {
        Double d = null;

        try {
            d = Double.parseDouble(argument);
        } catch (NumberFormatException ex) {
            throw new ParseFailedException(ex);
        }

        return d;
    }
}
