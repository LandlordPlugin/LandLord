package biz.princeps.lib.command;

import org.bukkit.command.CommandSender;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 2/4/18
 * <p>
 * This interface is implemented by SubCommand and MainCommand
 */
public interface Command {

    /**
     * Checks if a CommandSender has the permission to do something
     *
     * @param cs the CommandSender which should be checked
     * @return whether the cs is allowed to execute the cmd or not
     */
    boolean hasPermission(CommandSender cs);


    /**
     * This method is actually called onCommand. It is not implemented in one of the abstract classes Main/SubCommand
     * You will need to implement it in a actual command
     *
     * @param properties the commandProperties. Stuff around the CommandSender.
     * @param arguments  the Arguments
     */
    void onCommand(Properties properties, Arguments arguments);


    /**
     * Gets the String of the correct usage of this command
     *
     * @return the string
     */
    String getUsage();
}
