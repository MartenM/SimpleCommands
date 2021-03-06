# SimpleCommands
![Maven](https://github.com/MartenM/SimpleCommands/actions/workflows/maven.yml/badge.svg) [![](https://jitpack.io/v/MartenM/SimpleCommands.svg)](https://jitpack.io/#MartenM/SimpleCommands)

Provides a simple, easy to work with command framework for minecraft spigot servers.

### What this framework offers:
* Full developer control, just implement the onCommand() method just like you normally would!
* Simple nesting of commands
* Simple permission handling
* Stack permission nodes
* Tab completion for subcommands
* Automatic (customizable) help generator that respects permissions!


## Start using SimpleCommands
In order to use SimpleCommands you need to import the project using Maven or simply copy the files into your project.
The latest release tag can be seen in the shield above.

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependencies>
    <dependency>
        <groupId>com.github.MartenM</groupId>
        <artifactId>SimpleCommands</artifactId>
        <version>[FROM JitPack badge]</version>
    </dependency>
</dependencies>
```

### Step 1: Create a RootCommand
```java
public class DebugCommand extends RootCommand {

    public DebugCommand() {
        super("debug", "someplugin.commands.debug", false);

        addCommand(new SimpleUnloadPlayer());
        addCommand(new SimpleLoadPlayer());
        addCommand(new SimpleShow());
    }
}
```

### Step 2: Create the subcommands (SimpleCommand):
```java
public class SimpleLoadPlayer extends SimpleCommand {

    public SimpleLoadPlayer() {
        super("load", "+loadplayer", true);
        
        // The + symbol means that this permission will be attached to that of the parent.
        // This allows for easy permission stacking but keeps options open to do whatever you want!
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Command logic here
        return true;
     }
}
```

### Step 3: Register the command to the Server
The SimpleCommands class provides an easy way to register commands.
Simply follow the syntax below. Please note that commands with parents CANNOT be registered.

```java
@Override
public class TestPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        
        SimpleCommand testCommand = new SimpleTestCommand();
        testCommand.registerCommand(this);
        
    }
}
```

### Step 4: Enjoy the command in game
You are done! 

## Checkout the wiki for more options.
The wiki offers a guide for almost everything this libary covers.
Check it out here: https://github.com/MartenM/SimpleCommands/wiki

## Feature additions
These are features planned for upcoming releases.

* Hide commands from the help formatter
* Add documentation on isAllowed() overwrites
