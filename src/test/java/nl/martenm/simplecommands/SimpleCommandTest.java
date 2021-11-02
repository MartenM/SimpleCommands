package nl.martenm.simplecommands;

import nl.martenm.simplecommands.bukkit.ConsoleSender;
import nl.martenm.simplecommands.bukkit.PlayerSender;
import nl.martenm.simplecommands.bukkit.TestCommandSender;
import nl.martenm.simplecommands.implementations.SimpleTestCommand;
import nl.martenm.simplecommands.implementations.SubAlways;
import nl.martenm.simplecommands.implementations.SubAttached;
import nl.martenm.simplecommands.implementations.SubCommandPermissionTest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Simple unit test. Especially for permissions to make sure these don't break.
 */
public class SimpleCommandTest {

    private ConsoleSender consoleSender;
    private PlayerSender playerSender;

    private Command cmd;
    private SimpleTestCommand testCommand;

    @BeforeEach
    public void setup() {
        this.consoleSender = new ConsoleSender("Console sender.");
        this.playerSender = new PlayerSender("Player sender");
        this.testCommand = new SimpleTestCommand();
        this.cmd = new Command("test") {
            @Override
            public boolean execute(CommandSender commandSender, String s, String[] strings) {
                return testCommand.onCommand(commandSender, this, s, strings);
            }
        };
    }

    @Test
    public void testUnknownArgument() {
        String gibberish = "AAABBCC";
        testCommand.onCommand(consoleSender, cmd, "test " + gibberish, new String[] {gibberish});
        String expected = String.format(SimpleCommandMessages.UNKNOWN_ARGUMENT.m(), gibberish);

        assert consoleSender.hasReceived(expected);
        assert !consoleSender.isExecuted();
    }

    @Test
    public void testAlwaysDone() {
        testCommand.onCommand(consoleSender, cmd, "test always", new String[] {"always"});
        assert consoleSender.isExecuted();
    }

    @Test
    public void testHelp() {
        testCommand.onCommand(consoleSender, cmd, "test", new String[] {});
        assert consoleSender.getMessages().size() >= 5;

        testCommand.onCommand(playerSender, cmd, "test", new String[] {});
        assert playerSender.getMessages().size() < consoleSender.getMessages().size();
    }

    @Test
    public void testNestedHelp() {
        testCommand.onCommand(consoleSender, cmd, "test nestedAlways", new String[] {"nestedAlways"});
        assert consoleSender.getMessages().size() >= 3;
    }

    @Test
    public void testAttachedPermission() {
        SimpleCommand root = new SimpleCommand("permission", "permission", false) {

        };
        SimpleCommand attached = new SubAttached();
        root.addCommand(attached);

        assert attached.getFullPermission().equalsIgnoreCase("permission.attached");

        root.onCommand(playerSender, null, "permission attached", new String[] {"attached"});
        assert !playerSender.isExecuted();
        assert playerSender.hasReceived(SimpleCommandMessages.NO_PERMISSION.m());
    }

    @Test()
    public void testAttachedNoParent() {
        SimpleCommand command = new SubAttached();
        Assertions.assertThrows(RuntimeException.class, command::getFullPermission);
    }

    @Test
    public void testAttachedNoParentPermission() {
        SimpleCommand root = new SubAlways();
        SimpleCommand attached = new SubAttached();
        root.addCommand(attached);

        Assertions.assertThrows(RuntimeException.class, attached::getFullPermission);
    }

    @Test
    public void testPlayerOnly() {
        testCommand.onCommand(consoleSender, cmd, "test playerOnly", new String[] {"playerOnly"});
        assert !consoleSender.isExecuted();
        assert consoleSender.hasReceived(SimpleCommandMessages.PLAYER_ONLY.m());

        testCommand.onCommand(playerSender, cmd, "test playerOnly", new String[] {"playerOnly"});
        assert playerSender.isExecuted();
    }

    @Test
    public void testNestedPlayerOnly() {
        testCommand.onCommand(consoleSender, cmd, "test nestedPlayerOnly 1 playerOnly", new String[] {"nestedPlayerOnly", "1", "playerOnly"});
        assert !consoleSender.isExecuted();
        assert consoleSender.hasReceived(SimpleCommandMessages.PLAYER_ONLY.m());

        testCommand.onCommand(playerSender, cmd, "test nestedPlayerOnly 1 playerOnly", new String[] {"nestedPlayerOnly", "1", "playerOnly"});
        assert playerSender.isExecuted();
    }

    @Test
    public void testPermission() {
        testCommand.onCommand(consoleSender, cmd, "test permission", new String[] {"permission"});
        assert consoleSender.isExecuted();

        testCommand.onCommand(playerSender, cmd, "test permission", new String[] {"permission"});
        assert !playerSender.isExecuted();
        assert playerSender.hasReceived(SimpleCommandMessages.NO_PERMISSION.m());

        playerSender.reset();

        playerSender.addPermission("permission");
        testCommand.onCommand(playerSender, cmd, "test permission", new String[] {"permission"});
        assert playerSender.isExecuted();
    }

    @Test
    public void testNestedPermission() {
        testCommand.onCommand(consoleSender, cmd, "test nestedPermission 1 permission", new String[] {"nestedPermission", "1", "permission"});
        assert consoleSender.isExecuted();

        testCommand.onCommand(playerSender, cmd, "test nestedPermission 1 permission", new String[] {"nestedPermission", "1", "permission"});
        assert !playerSender.isExecuted();
        assert playerSender.hasReceived(SimpleCommandMessages.NO_PERMISSION.m());

        playerSender.reset();

        playerSender.addPermission("permission");
        testCommand.onCommand(playerSender, cmd, "test permission", new String[] {"permission"});
        assert playerSender.isExecuted();
    }

    @Test
    public void test () {
        double test = 5.4;

        test = Math.round(test * 2.0) / 2.0;
        System.out.println(test);

    }
}
