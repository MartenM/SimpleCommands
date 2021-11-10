package nl.martenm.simplecommands.implementations;

import nl.martenm.simplecommands.RootCommand;

public class RootTestCommand extends RootCommand {

    public RootTestCommand() {
        super("test",  false);

        addCommand(new SubCommandPlayerOnly());
        addCommand(new SubCommandPermissionTest());
        addCommand(new SubAlways());

        addCommand(new SubNested("nestedAlways", new SubNested("1", new SubAlways())));
        addCommand(new SubNested("nestedPermission", new SubNested("1", new SubCommandPermissionTest())));
        addCommand(new SubNested("nestedPlayerOnly", new SubNested("1", new SubCommandPlayerOnly())));
        addCommand(new SimpleArgumentTestCommand());
    }

}