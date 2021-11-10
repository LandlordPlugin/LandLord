package biz.princeps.lib.command;

import biz.princeps.lib.PrincepsLib;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 2/4/18
 * <p>
 * This class is responsible for registering all the commands.
 * You must write in your onEnable Method something like this:
 * commandManager.registerCommand(new TestCommand());
 * TestCommand extends MainCommand and has subclasses extended SubCommand
 * Example: @see test.TestCommand
 */
public class CommandManager {

    // A set of all registered commands. Might need it later
    private final Set<MainCommand> commandSet;
    // The internal command map of bukkit
    private CommandMap cmdMap;
    // Instance of the javaplugin, which is using PrincepsLib
    private final JavaPlugin plugin;

    /**
     * Creates a new CommandManager and tries to initialize the commandMap from Bukkit
     */
    public CommandManager() {
        this.commandSet = new HashSet<>();
        this.plugin = PrincepsLib.getPluginInstance();

        initBukkitCommandMap();
    }

    private void initBukkitCommandMap() {
        try {
            Field bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            this.cmdMap = (CommandMap) bukkitCommandMap.get(plugin.getServer());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers a command directly in bukkit, so no additional code inside the plugin.yml
     * is needed.
     *
     * @param cmd the command, which should be registered
     */
    public void registerCommand(MainCommand cmd) {
        if (cmd == null) {
            throw new NullPointerException("CMD IS NULL!!");
        }

        commandSet.add(cmd);

        if (cmdMap == null) initBukkitCommandMap();

        cmdMap.register(cmd.getName(), cmd);

        plugin.getLogger().info(cmd.getLabel() + " was registered successfully!");
    }

    public MainCommand getCommand(Class<? extends MainCommand> classy) {
        for (MainCommand mainCommand : commandSet) {
            if (mainCommand.getClass() == classy) {
                return mainCommand;
            }
        }
        return null;
    }
}
