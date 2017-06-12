package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Created by jcdesimp on 2/19/15.
 * Administrative command to reload various resources/configuration files
 */
public class Reload implements LandlordCommand {

    private Landlord plugin;

    public Reload(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * Reload landlord configuration file
     *
     * @param sender who executed the command
     * @param args   given with command
     * @param label  exact command (or alias) run
     * @return boolean of success
     */
    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String configReloaded = messages.getString("commands.reload.alerts.configReloaded");
        final String noPerms = messages.getString("info.warnings.noPerms");

        if (sender.hasPermission("landlord.admin.reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + configReloaded);
            return true;
        }
        sender.sendMessage(ChatColor.RED + noPerms);
        return true;
    }

    public String getHelpText(CommandSender sender) {

        if (!sender.hasPermission("landlord.admin.list")) {
            return null;
        }

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.reload.usage"); // get the base usage string
        final String desc = messages.getString("commands.reload.description");   // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());
    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.reload.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
