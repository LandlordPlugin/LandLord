package com.jcdesimp.landlord.commands;

import org.bukkit.ChatColor;

/**
 * Created by jcdesimp on 2/19/15.
 * Static utility functions for landlord commands
 */
public abstract class Utils {

    /**
     * Create a formatted and colorized help string for a command
     *
     * @param usage   The usage string with command field and params
     * @param desc    a brief, one line description of the command
     * @param command the string to use for as the command
     * @return The fully formatted command help string
     */
    public static String helpString(String usage, String desc, String command) {
        usage = usage.replace("#{cmd}", command); // replace cmd with the first (main) alias

        // return the constructed and colorized help string
        return ChatColor.DARK_AQUA + usage + ChatColor.RESET + " - " + desc;
    }

}
