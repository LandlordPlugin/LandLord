package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.LandManager;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by jcdesimp on 2/19/15.
 * Command to view info about the current land.
 */
public class Info implements LandlordCommand {

    private Landlord plugin;

    public Info(Landlord plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String noPerms = messages.getString("info,warnings.noPerms");
        final String noOwner = messages.getString("info.alerts.noOwner");
        final String landInfoString = messages.getString("commands.info.alerts.landInfo");
        final String landOwnerString = messages.getString("commands.info.alerts.landOwner");

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.info")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }
            Chunk currChunk = player.getLocation().getChunk();
            OwnedLand land = plugin.getLandManager().getLandFromCache(currChunk.getWorld().getName(), currChunk.getX(), currChunk.getZ());
            String owner = ChatColor.GRAY + "" + ChatColor.ITALIC + noOwner;
            if (land != null) {

                /*
                 * *************************************
                 * mark for possible change    !!!!!!!!!
                 * *************************************
                 */
                owner = ChatColor.GOLD + land.getOwnerUsername();
            }
            if (plugin.getConfig().getBoolean("options.particleEffects")) {
                OwnedLand.highlightLand(player, Effect.LAVADRIP);
            }

            // Build the land info string
            String msg = ChatColor.DARK_GREEN + "--- " + landInfoString
                    .replace("#{chunkCoords}", (ChatColor.GOLD + "(" + currChunk.getX() + ", " + currChunk.getZ() + ")" + ChatColor.DARK_GREEN))
                    .replace("#{worldName}", ChatColor.GOLD + "\"" + currChunk.getWorld().getName() + "\"") +

                    ChatColor.DARK_GREEN + "-----\n" + landOwnerString.replace("#{ownerName}", owner);
            player.sendMessage(msg);

        }
        return true;
    }

    public String getHelpText(CommandSender sender) {

        if (!sender.hasPermission("landlord.player.info")) {   // make sure player has permission to do this command
            return null;
        }

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.info.usage"); // get the base usage string
        final String desc = messages.getString("commands.info.description");   // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());
    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.info.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
