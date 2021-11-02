package nl.martenm.simplecommands.implementations;

import nl.martenm.simplecommands.SimpleCommand;

public class SimpleTestCommand extends SimpleCommand {

    public SimpleTestCommand() {
        super("test", false);

        addCommand(new SubCommandPlayerOnly());
        addCommand(new SubCommandPermissionTest());
        addCommand(new SubAlways());

        addCommand(new SubNested("nestedAlways", new SubNested("1", new SubAlways())));
        addCommand(new SubNested("nestedPermission", new SubNested("1", new SubCommandPermissionTest())));
        addCommand(new SubNested("nestedPlayerOnly", new SubNested("1", new SubCommandPlayerOnly())));

    }

}
