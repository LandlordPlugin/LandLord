package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.LandManager;
import com.jcdesimp.landlord.persistantData.Friend;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.util.NumberConversions.ceil;

/**
 * Created by jcdesimp on 2/18/15.
 * List the friends of the current land
 */
public class Friends implements LandlordCommand {

    private Landlord plugin;

    public Friends(Landlord plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args, String label) {
        FileConfiguration messages = plugin.getMessageConfig();

        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String noPerms = messages.getString("info.warnings.noPerms");
        final String notOwner = messages.getString("info.warnings.notOwner");
        final String badPageNum = messages.getString("info.warnings.badPage");
        final String friendListHeader = messages.getString("commands.friends.alerts.listHeader");
        final String noFriends = messages.getString("commands.friends.alerts.noFriends");
        final String onlineString = messages.getString("commands.friends.alerts.online");
        final String offlineString = messages.getString("commands.friends.alerts.offline");
        final String invalidPage = messages.getString("info.warnings.badPage");
        final String nextPageString = messages.getString("info.alerts.nextPage");


        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }
            OwnedLand land = plugin.getLandManager().getApplicableLand(player.getLocation());
            if (land == null || (!land.getOwner().equals(player.getUniqueId()) && !player.hasPermission("landlord.admin.friends"))) {
                player.sendMessage(ChatColor.RED + notOwner);
                return true;
            }
            if (plugin.getConfig().getBoolean("options.particleEffects", true)) {     //conf
                OwnedLand.highlightLand(player, Effect.HEART, 3);
            }
            //check if page number is valid
            int pageNumber = 1;
            if (args.length > 1 && args[0].equals("friends")) {
                try {
                    pageNumber = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + badPageNum);
                    return true;
                }
            }

            //List<OwnedLand> myLand = plugin.getDatabase().find(OwnedLand.class).where().eq("ownerName",player.getName()).findList();

            String header = ChatColor.DARK_GREEN + "----- " + friendListHeader + " -----\n";
            ArrayList<String> friendList = new ArrayList<String>();
            if (land.getFriends().isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + noFriends);
                return true;
            }
            for (Friend f : land.getFriends()) {
                String fr = ChatColor.DARK_GREEN + " - " + ChatColor.GOLD + f.getName() + ChatColor.DARK_GREEN + " - ";
                /*
                 * *************************************
                 * mark for possible change    !!!!!!!!!
                 * *************************************
                 */
                if (Bukkit.getOfflinePlayer(f.getUuid()).isOnline()) {
                    fr += ChatColor.GREEN + "" + ChatColor.ITALIC + onlineString;
                } else {
                    fr += ChatColor.RED + "" + ChatColor.ITALIC + offlineString;
                }

                fr += "\n";
                friendList.add(fr);
            }

            //Amount to be displayed per page
            final int numPerPage = 8;

            int numPages = ceil((double) friendList.size() / (double) numPerPage);
            if (pageNumber > numPages) {
                sender.sendMessage(ChatColor.RED + invalidPage);
                return true;
            }
            String pMsg = header;
            if (pageNumber == numPages) {
                for (int i = (numPerPage * pageNumber - numPerPage); i < friendList.size(); i++) {
                    pMsg += friendList.get(i);
                }
                pMsg += ChatColor.DARK_GREEN + "------------------------------";
            } else {
                for (int i = (numPerPage * pageNumber - numPerPage); i < (numPerPage * pageNumber); i++) {
                    pMsg += friendList.get(i);
                }

                pMsg += "\n" + ChatColor.DARK_GREEN + "--- " + nextPageString
                        .replace("#{label}", ChatColor.YELLOW + "/" + label)
                        .replace("#{cmd}", args[0])
                        .replace("#{pageNumber}", "" + (pageNumber + 1) + ChatColor.DARK_GREEN)
                        + " ---";
            }
            player.sendMessage(pMsg);


        }
        return true;
    }

    public String getHelpText(CommandSender sender) {
        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.friends.usage"); // get the base usage string
        final String desc = messages.getString("commands.friends.description");   // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.friends.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
