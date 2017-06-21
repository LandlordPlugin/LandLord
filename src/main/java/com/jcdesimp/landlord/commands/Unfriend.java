package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.Friend;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by jcdesimp on 2/18/15.
 * Removed a friend form an owned chunk
 */
public class Unfriend implements LandlordCommand {

    private Landlord plugin;

    public Unfriend(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * Removes a friend from an owned chunk
     * Called when landlord remfriend is executed
     *
     * @param sender who executed the command
     * @param args   given with command
     * @return boolean
     */
    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String usage = messages.getString("commands.unfriend.usage");
        final String noPerms = messages.getString("info.warnings.noPerms");
        final String notOwner = messages.getString("info.warnings.notOwner");
        final String notFriend = messages.getString("commands.unfriend.alerts.notFriend");
        final String unfriended = messages.getString("commands.unfriend.alerts.unfriended");

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + usage.replace("#{label}", label).replace("#{command}", args[0]));
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }

            Chunk currChunk = player.getLocation().getChunk();
            /*
             * *************************************
             * mark for possible change    !!!!!!!!!
             * *************************************
             */
            Friend frd = new Friend(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
            OwnedLand land = plugin.getLandManager().getLandFromCache(currChunk.getWorld().getName(), currChunk.getX(), currChunk.getZ());
            if (land == null || (!land.getOwner().equals(player.getUniqueId()) && !player.hasPermission("landlord.admin.modifyfriends"))) {
                player.sendMessage(ChatColor.RED + notOwner);
                return true;
            }
            if (!land.removeFriend(frd.getUuid())) {
                player.sendMessage(ChatColor.YELLOW + notFriend.replace("#{playerName}", args[1]));
                return true;
            }
            if (plugin.getConfig().getBoolean("options.particleEffects", true)) { //conf
                land.highlightLand(player, Effect.ZOMBIE_CONVERTED_VILLAGER, 2);
            }
            land.save();
            if (plugin.getConfig().getBoolean("options.soundEffects", true)) { //conf
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 10, .5f);
            }
            player.sendMessage(ChatColor.GREEN + unfriended.replace("#{playerName}", args[1]));

        }
        return true;
    }

    public String getHelpText(CommandSender sender) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.unfriend.usage"); // get the base usage string
        final String desc = messages.getString("commands.unfriend.description");   // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());
    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.unfriend.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
