package nl.martenm.simplecommands.implementations;

import nl.martenm.simplecommands.RootCommand;
import nl.martenm.simplecommands.SimpleCommand;

public class SubNested extends RootCommand {

    public SubNested(String name, SimpleCommand command) {
        super(name, false);
        addCommand(command);
    }
}
