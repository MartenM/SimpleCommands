package nl.martenm.simplecommands;

import nl.martenm.simplecommands.bukkit.ConsoleSender;
import nl.martenm.simplecommands.bukkit.PlayerSender;
import nl.martenm.simplecommands.implementations.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Simple unit test. Especially for permissions to make sure these don't break.
 */
public class SimpleCommandTest {

    private ConsoleSender consoleSender;
    private PlayerSender playerSender;

    private Command cmd;
    private RootTestCommand testCommand;

    @BeforeEach
    public void setup() {
        this.consoleSender = new ConsoleSender("Console sender");
        this.playerSender = new PlayerSender("Player sender");
        this.testCommand = new RootTestCommand();
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
    public void testHelpAliases() {
        RootCommand testRoot = new SimpleRoot("test", false);
        SubAliases aliases = new SubAliases(false);
        aliases.addAlias("THIS");

        testRoot.addCommand(aliases);

        testRoot.onCommand(consoleSender, null, "test", new String[] {});
        assert consoleSender.hasReceived("/test aliases", true);
    }

    @Test
    public void testNestedHelp() {
        testCommand.onCommand(consoleSender, cmd, "test nestedAlways", new String[] {"nestedAlways"});
        assert consoleSender.getMessages().size() >= 3;
        consoleSender.reset();

        testCommand.onCommand(consoleSender, cmd, "test nestedAlias", new String[] {"nestedAlias"});
        assert consoleSender.getMessages().size() >= 2;
        consoleSender.reset();

        testCommand.onCommand(consoleSender, cmd, "test na", new String[] {"na"});
        assert consoleSender.getMessages().size() >= 2;
        consoleSender.reset();
    }

    @Test
    public void testAttachedPermission() {
        RootCommand root = new RootCommand("permission", "permission", false) {

        };
        SimpleCommand attached = new SubAttached();
        root.addCommand(attached);

        assert attached.getFullPermission().equalsIgnoreCase("permission.attached");

        root.onCommand(playerSender, null, "permission attachedPermission", new String[] {"attachedPermission"});
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
        RootCommand root = new SubAlways();
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
    public void testParsedValid() {
        testCommand.onCommand(consoleSender, cmd, "test parsed 1 1.00 2.00 hello", new String[] {"parsed", "1", "1.00", "2.00", "hello"});
        assert consoleSender.hasReceived("java.lang.Integer");
        assert consoleSender.hasReceived("java.lang.Double");
        assert consoleSender.hasReceived("java.lang.Float");
        assert consoleSender.hasReceived("java.lang.String");
    }

    @Test
    public void testParsedInvalid() {
        testCommand.onCommand(consoleSender, cmd, "test parsed 1 a 2.00 hello", new String[] {"parsed", "1", "a", "2.00", "hello"});
        assert consoleSender.hasReceived("The argument someDouble could not be parsed. Reason: java.lang.NumberFormatException: For input string: \"a\". Value: a", true);
    }

    @Test
    public void testParsedMissing() {
        testCommand.onCommand(consoleSender, cmd, "test parsed 1 1.00", new String[] {"parsed", "1", "1.00"});
        assert consoleSender.hasReceived("Missing arguments: someFloat, someString", true);
    }

    @Test
    public void testParsedTabCompletion() {
        consoleSender.testCommandCompletion(testCommand, cmd, "test parsed ", new String[]{"parsed", ""});
        assert consoleSender.hasTabCompletion("someInteger");
        consoleSender.reset();

        consoleSender.testCommandCompletion(testCommand, cmd, "test parsed 1 ", new String[]{"parsed", "1", ""});
        assert consoleSender.hasTabCompletion("someDouble");
        consoleSender.reset();
    }

    @Test
    public void testParsedTabCompletionOutOfIndex() {
        List<String> tabCompletions = testCommand.onTabComplete(consoleSender, cmd, "test parsed 1 1.00 2.00 hello aaa", new String[] {"parsed", "1", "1.00", "2.00", "hello", "aaaa"});
        assert tabCompletions.isEmpty();
    }

    @Test
    public void testAliasCommand() {
        SimpleCommand command = new SubAliases(false);
        assert !command.hasAlias();

        command.addAlias("aaa");
        assert command.hasAlias();
        assert command.getAlias().equalsIgnoreCase("aaa");

        command.addAlias("bbb");
        assert command.hasAlias();
        assert command.getAliases().size() == 2;
    }

    @Test
    public void testAliasExecution() {
        testCommand.onCommand(consoleSender, null, "/t aliases", new String[] {"aliases"});
        assert consoleSender.hasReceived("DONE");
        consoleSender.reset();

        testCommand.onCommand(consoleSender, null, "/t aa", new String[] {"aa"});
        assert consoleSender.hasReceived("DONE");
        consoleSender.reset();

        testCommand.onCommand(consoleSender, null, "/t qq", new String[] {"qq"});
        assert !consoleSender.hasReceived("DONE");
        consoleSender.reset();
    }

    @Test
    public void testHiddenCommand() {
        SimpleRoot root = new SimpleRoot("test", false);
        SimpleCommand sub = new SubAlways();
        root.addCommand(sub);
        sub.setHidden(true);

        root.onCommand(consoleSender, null, "test", new String[] {});
        assert consoleSender.hasReceived("Commands are present but hidden by the developer.", true);
    }
}
