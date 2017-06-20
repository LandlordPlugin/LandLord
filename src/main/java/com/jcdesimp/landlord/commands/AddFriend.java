package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.LandManager;
import com.jcdesimp.landlord.persistantData.Friend;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.Bukkit.getOfflinePlayer;

/**
 * Created by jcdesimp on 2/18/15.
 * LandlordCommand to add a friend to a plot of land
 */
public class AddFriend implements LandlordCommand {

    private Landlord plugin;

    public AddFriend(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a friend to an owned chunk
     * Called when landlord addfriend command is executed
     * This command must be run by a player
     *
     * @param sender who executed the command
     * @param args   given with command
     * @param label  base command executed
     * @return boolean
     */
    public boolean execute(CommandSender sender, String[] args, String label) {
        //is sender a player
        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.addFriend.usage");
        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String noPerms = messages.getString("info.warnings.noPerms");

        final String notOwner = messages.getString("info.warnings.notOwner");
        final String unknownPlayer = messages.getString("info.warnings.unknownPlayer");
        final String alreadyFriend = messages.getString("commands.addFriend.alerts.alreadyFriend");
        final String nowFriend = messages.getString("commands.addFriend.alerts.success");

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + usage.replace("#{label}", label).replace("#{cmd}", args[0]));
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }

            Chunk currChunk = player.getLocation().getChunk();

            OwnedLand land = LandManager.getLandFromDatabase(currChunk.getWorld().getName(), currChunk.getX(), currChunk.getZ());

            //Does land exist, and if so does player own it
            if (land == null || (!land.getOwner().equals(player.getUniqueId()) && !player.hasPermission("landlord.admin.modifyfriends"))) {
                player.sendMessage(ChatColor.RED + notOwner);
                return true;
            }
            //
            OfflinePlayer possible = getOfflinePlayer(args[1]);
            if (!possible.hasPlayedBefore() && !possible.isOnline()) {
                player.sendMessage(ChatColor.RED + unknownPlayer);
                return true;
            }
            Friend friend = new Friend(possible.getUniqueId());

            if (!land.addFriend(friend)) {
                player.sendMessage(ChatColor.YELLOW + alreadyFriend.replace("#{player}", args[1]));
                return true;
            }
            if (plugin.getConfig().getBoolean("options.particleEffects", true)) {      //conf
                OwnedLand.highlightLand(player, Effect.HEART, 2);
            }

            plugin.getDatabase().save(land);
            if (plugin.getConfig().getBoolean("options.soundEffects", true)) {     //conf
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, .2f);
            }
            sender.sendMessage(ChatColor.GREEN + nowFriend.replace("#{player}", args[1]));
            plugin.getMapManager().updateAll();

        }
        return true;
    }

    public String getHelpText(CommandSender sender) {
        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.addFriend.usage"); // get the base usage string
        final String desc = messages.getString("commands.addFriend.description");   // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());
    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.addFriend.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
