package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by jcdesimp on 2/18/15.
 * Display the landlord map
 */
public class ShowMap implements LandlordCommand {

    private Landlord plugin;

    public ShowMap(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * Toggles the land map display
     *
     * @param sender who executed the command
     * @param args   given with command
     * @return boolean
     */
    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String mapDisabled = messages.getString("commands.showMap.alerts.mapDisabled");
        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String noPerms = messages.getString("info.warnings.noPerms");
        final String noMap = messages.getString("commands.showMap.alerts.noMap");

        if (!plugin.getConfig().getBoolean("options.enableMap", true)) {      //conf
            sender.sendMessage(ChatColor.YELLOW + mapDisabled);
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.map")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }
            try {
                plugin.getMapManager().toggleMap(player);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + noMap);
            }
        }
        return true;
    }

    public String getHelpText(CommandSender sender) {

        // only bother showing them this command if they have permission to do it.
        if (!sender.hasPermission("landlord.player.map") || !plugin.getConfig().getBoolean("options.enableMap", true)) {
            return null;
        }

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.showMap.usage"); // get the base usage string
        final String desc = messages.getString("commands.showMap.description");   // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.showMap.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
