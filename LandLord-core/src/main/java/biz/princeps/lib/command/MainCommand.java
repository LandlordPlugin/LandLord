package biz.princeps.lib.command;

import biz.princeps.lib.PrincepsLib;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 2/4/18
 * <p>
 * The MainCommand class which must be inherited by any custom command.
 * Also supports subcommands, see @see test.TestCommand for further information
 */
public abstract class MainCommand extends BukkitCommand implements Command {

    protected final Map<String, SubCommand> subCommandMap;
    protected final Set<String> permissions;

    private final String noPermissionsCmd = PrincepsLib.getTranslateableStrings().get("Confirmation.accept");

    /**
     * Creates a basic MainCommand
     *
     * @param name         the name of the command
     * @param description  the description, which should be displayed in the bukkit menu
     * @param usageMessage the message, which should be displayed, in case the user doesnt use the correct syntax
     * @param perms        the perms required to execute this command
     * @param aliases      the aliases which are also viable to trigger this command
     */
    public MainCommand(String name, String description, String usageMessage, Set<String> perms, String... aliases) {
        super(name, description, usageMessage, Arrays.asList(aliases));
        this.subCommandMap = new HashMap<>();
        this.permissions = perms;

        for (Class<?> aClass : this.getClass().getDeclaredClasses()) {
            if (SubCommand.class.isAssignableFrom(aClass)) {
                try {
                    Constructor<?> constructor = aClass.getConstructors()[0];
                    constructor.setAccessible(true);
                    SubCommand subCommand = (SubCommand) constructor.newInstance(this);

                    this.addSubcommand(subCommand);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Internal method, which must be called by BukkitCommand. It is delegating stuff down to Command#onCommand
     *
     * @param commandSender the sender
     * @param s             the command executed
     * @param strings       the additional arguments
     * @return if the execution was successful (not used)
     */
    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {

        if (!hasPermission(commandSender)) {
            commandSender.sendMessage(noPermissionsCmd.replace("%cmd%", this.getName()));
            return true;
        }

        if (strings.length > 0) {
            for (Map.Entry<String, SubCommand> entry : subCommandMap.entrySet()) {
                if (entry.getKey().equals(strings[0]) || entry.getValue().getAliases().contains(strings[0])) {

                    if (!entry.getValue().hasPermission(commandSender)) {
                        commandSender.sendMessage(noPermissionsCmd.replace("%cmd%", this.getName() + " " + strings[0]));
                        return true;
                    }

                    entry.getValue().onCommand(new Properties(commandSender, entry.getValue()), new Arguments(Arrays.copyOfRange(strings, 1, strings.length)));
                    return true;
                }
            }
        }

        onCommand(new Properties(commandSender, this), new Arguments(strings));
        return true;
    }

    /**
     * Checks if a CommandSender has the permission to do something
     *
     * @param cs the CommandSender which should be checked
     * @return whether the cs is allowed to execute the cmd or not
     */
    public boolean hasPermission(CommandSender cs) {
        if (permissions.isEmpty()) {
            return true;
        }

        for (String permission : permissions) {
            if (cs.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Can be called to add a new subcommand. Useful to structure code a bit better
     *
     * @param subCommand the subcmd which should be added
     */
    public void addSubcommand(SubCommand subCommand) {
        if (subCommandMap.containsKey(subCommand.getName())) {
            throw new RuntimeException("Subcommand with the name " + subCommand.getName() + " has been already added to " + this.getName());
        }
        subCommandMap.put(subCommand.getName(), subCommand);
    }

    public void clearSubcommands() {
        subCommandMap.clear();
    }

    /**
     * tries to find a specific SubCommand (defined via the class) in the subcommand map and returns it if found.
     *
     * @param classy the subcommand to look for
     * @return the subcommand
     */
    public String getCommandString(Class<? extends SubCommand> classy) {
        for (SubCommand value : this.subCommandMap.values()) {
            if (value.getClass() == classy) {
                return "/" + getName() + " " + value.getName();
            }
        }
        return null;
    }
}

