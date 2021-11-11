package biz.princeps.lib.command;

import org.bukkit.command.CommandSender;

import java.util.Set;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 2/4/18
 * <p>
 * A subclass of a MainCommand Class may be extend this class in order to support subcommanding
 */
public abstract class SubCommand implements Command {

    private final Set<String> permissions;

    private final String name;
    private final String usage;
    private final Set<String> aliases;

    public SubCommand(String name, String usage, Set<String> permissions, Set<String> aliases) {
        this.name = name;
        this.usage = usage;
        this.aliases = aliases;
        this.permissions = permissions;
    }

    /**
     * Auto generated to string method for debugging
     *
     * @return a string, which fully describes this subcommand
     */
    @Override
    public String toString() {
        return "SubCommand{" +
                "permissions=" + permissions +
                ", name='" + name + '\'' +
                ", usage='" + usage + '\'' +
                ", aliases=" + aliases +
                '}';
    }

    /**
     * Getter method
     *
     * @return the name string
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method
     *
     * @return the usage string
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Getter Method
     *
     * @return the Aliases set
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * Checks if a CommandSender has the permission to do something
     *
     * @param cs the CommandSender which should be checked
     * @return whether the cs is allowed to execute the cmd or not
     */
    public boolean hasPermission(CommandSender cs) {
        if (permissions.size() == 0) {
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
     * Checks if a given string matches with the subcommands name or one of the aliases
     *
     * @param string the string which should be checked
     * @return whether the string matches or not
     */
    public boolean matches(String string) {
        if (this.name.equals(string)) {
            return true;
        }

        for (String alias : this.aliases) {
            if (alias.equals(string)) {
                return true;
            }
        }

        return false;
    }

}
