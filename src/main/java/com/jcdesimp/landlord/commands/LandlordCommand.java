package com.jcdesimp.landlord.commands;

import org.bukkit.command.CommandSender;

/**
 * Created by jcdesimp on 2/17/15.
 * Interface for a player executable Landlord command
 */
public interface LandlordCommand {
    /**
     * Execute the command
     *
     * @param sender who sent the command
     * @param args   array of arguments given with the command
     * @param label  the actual command/alias that was entered.
     * @return boolean on whether or not success
     */
    boolean execute(CommandSender sender, String[] args, String label);

    /**
     * Get this command help test
     *
     * @param sender of the command.
     * @return String that describes the command
     */
    String getHelpText(CommandSender sender);

    /**
     * Get the list of strings (non-case sensitive) that can trigger
     * this command. (command names, aliases, etc.)
     *
     * @return the list of valid command trigger words
     */
    String[] getTriggers();


}
