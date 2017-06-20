package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.Bukkit.getOfflinePlayer;

/**
 * Created by jcdesimp on 2/18/15.
 * Administration command to clear all land in a specified world.
 */
public class ClearWorld implements LandlordCommand {

    private Landlord plugin;

    public ClearWorld(Landlord plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.clearWorld.usage");

        final String noPerms = messages.getString("info.warnings.noPerms");
        final String unknownPlayer = messages.getString("info.warnings.unknownPlayer");
        final String notConsole = messages.getString("commands.clearWorld.alerts.notConsole");
        final String noLand = messages.getString("commands.clearWorld.alerts.noLand");
        final String confirmation = messages.getString("commands.clearWorld.alerts.success");


        if (!sender.hasPermission("landlord.admin.clearworld")) {
            sender.sendMessage(ChatColor.RED + noPerms);
            return true;
        }
        if (args.length > 1) {
            List<OwnedLand> land;
            if (args.length > 2) {
                /*
                 * *************************************
                 * mark for possible change    !!!!!!!!!
                 * *************************************
                 */
                OfflinePlayer possible = getOfflinePlayer(args[2]);
                if (!possible.hasPlayedBefore() && !possible.isOnline()) {
                    sender.sendMessage(ChatColor.RED + unknownPlayer);
                    return true;
                }
                land = plugin.getDatabase().getLands(possible.getUniqueId(), args[1]);
            } else {
                if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.RED + notConsole);
                    return true;
                }
                land = plugin.getDatabase().getLands(args[1]);
            }
            if (land.isEmpty()) {
                sender.sendMessage(ChatColor.RED + noLand);
                return true;
            }

            for (OwnedLand land1 : land) {
                land1.delete();
            }
            plugin.getMapManager().updateAll();
            sender.sendMessage(ChatColor.GREEN + confirmation);

        } else {
            sender.sendMessage(ChatColor.RED + usage.replace("#{label}", label).replace("#{cmd}", args[0]));
        }
        return true;
    }

    public String getHelpText(CommandSender sender) {

        // only bother showing them this command if they have permission to do it.
        if (!sender.hasPermission("landlord.admin.clearworld")) {
            return null;
        }

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.clearWorld.usage");             // get the base usage string
        final String desc = messages.getString("commands.clearWorld.description");
        final String chunkWarning = messages.getString("commands.clearWorld.alerts.chunkWarning");            // get the "chunks won't regen" warning


        String helpString = ""; // start building the help string


        // add the formatted string to it.
        helpString += Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

        // If chunk regen is on, warn them that bulk deletions will not regen
        if (plugin.getConfig().getBoolean("options.regenOnUnclaim", false)) {  //conf
            helpString += ChatColor.YELLOW + " " + ChatColor.ITALIC + chunkWarning;
        }

        // return the constructed and colorized help string
        return helpString;

    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.clearWorld.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
