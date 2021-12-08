package nl.martenm.simplecommands.implementations;

import nl.martenm.simplecommands.RootCommand;
import nl.martenm.simplecommands.SimpleCommand;

public class RootTestCommand extends RootCommand {

    public RootTestCommand() {
        super("test",  false);

        addCommand(new SubCommandPlayerOnly());
        addCommand(new SubCommandPermissionTest());
        addCommand(new SubAlways());

        addCommand(new SubNested("nestedAlways", new SubNested("1", new SubAlways())));

        SimpleCommand nestedWithAlias = new SubNested("nestedAlias", new SubAliases(true));
        nestedWithAlias.addAlias("na");

        addCommand(nestedWithAlias);
        addCommand(new SubNested("nestedPermission", new SubNested("1", new SubCommandPermissionTest())));
        addCommand(new SubNested("nestedPlayerOnly", new SubNested("1", new SubCommandPlayerOnly())));
        addCommand(new SimpleArgumentTestCommand());

        addCommand(new SubAliases(true));

        addAlias("t");
    }

}
