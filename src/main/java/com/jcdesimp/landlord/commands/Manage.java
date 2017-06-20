package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.LandManager;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by jcdesimp on 2/18/15.
 * Command to bring up the land management interface.
 */
public class Manage implements LandlordCommand {

    private Landlord plugin;

    public Manage(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * Command for managing player land perms
     *
     * @param sender who executed the command
     * @param args   given with command
     * @return boolean
     */
    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String noPerms = messages.getString("info.warnings.noPerms");
        final String noLand = messages.getString("commands.manage.alerts.noLand");
        final String notOwner = messages.getString("info.warnings.notOwner");
        final String otherLand = messages.getString("commands.manage.alerts.otherLand");


        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }
            if (plugin.getFlagManager().getRegisteredFlags().size() <= 0) {
                player.sendMessage(ChatColor.RED + noLand);
                return true;
            }
            Chunk currChunk = player.getLocation().getChunk();
            OwnedLand land = plugin.getLandManager().getApplicableLand(currChunk);
            if (land == null || (!land.getOwner().equals(player.getUniqueId()) && !player.hasPermission("landlord.admin.manage"))) {
                player.sendMessage(ChatColor.RED + notOwner);
                return true;
            }
            if (!land.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + otherLand);
            }
            plugin.getManageViewManager().activateView(player, land, plugin);


        }
        return true;
    }

    public String getHelpText(CommandSender sender) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.manage.usage");       // get the base usage string
        final String desc = messages.getString("commands.manage.description");   // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.manage.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
