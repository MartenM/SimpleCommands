package nl.martenm.simplecommands.arguments.prefab;

import nl.martenm.simplecommands.arguments.SimpleCommandArgument;
import nl.martenm.simplecommands.arguments.ParseFailedException;

public class SimpleArgumentFloat extends SimpleCommandArgument<Float> {

    public SimpleArgumentFloat(String name) {
        super(name);
    }

    public SimpleArgumentFloat(String name, String errorMessage) {
        super(name, errorMessage);
    }

    @Override
    protected Float parseArgument(String argument) throws ParseFailedException {
        Float f = null;

        try {
            f = Float.parseFloat(argument);
        } catch (NumberFormatException ex) {
            throw new ParseFailedException(ex);
        }

        return f;
    }
}
