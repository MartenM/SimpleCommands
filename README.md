# SimpleCommands
![Maven](https://github.com/MartenM/SimpleCommands/actions/workflows/maven.yml/badge.svg)

Provides an simple, easy to work with command framework for minecraft spigot servers.

### What this frameworks offers:
* Full developer control, just implement the onCommand() method just like you normally would!
* Simple nesting of commands
* Simple permission handling
* Tab completion for subcommands
* Automatic (customizable) help generator that respects permissions!


## Start using SimpleCommands
In order to use SimpleCommands you need to import the project using Maven or simply copy the files into your project.
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.MartenM</groupId>
    <artifactId>SimpleCommands</artifactId>
    <version>1.0.0</version>
</dependency>

```

### Step 1: Create a base class
```java
public class DebugCommand extends SimpleCommand {

    public DebugCommand() {
        super("debug", "someplugin.commands.debug", false);

        addCommand(new SimpleUnloadPlayer());
        addCommand(new SimpleLoadPlayer());
        addCommand(new SimpleShow());
    }
}
```

### Step 2: Create the subcommands:
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
This step is basically the same as if you had no command framework in place!
```java
SimpleCommand    = new DebugCommand();
PluginCommand pc = getCommand(name);
if(pc == null) {
    getLogger().warning("Failed to load the command: " + name);
    return;
}

pc.setExecutor(command);
pc.setTabCompleter(command);
```

### Step 4: Enjoy the command ingame
You are done! 
