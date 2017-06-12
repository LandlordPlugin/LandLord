package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getOfflinePlayer;
import static org.bukkit.util.NumberConversions.ceil;

/**
 * Created by jcdesimp on 2/18/15.
 * List the land of a specified player (typically for administrator use)
 */
public class ListPlayer implements LandlordCommand {

    private Landlord plugin;

    public ListPlayer(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * @param sender who sent the command
     * @param args   array of arguments given with the command
     * @param label  the actual command/alias that was entered.
     * @return Boolean of success
     */
    public boolean execute(CommandSender sender, String[] args, String label) {

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.listPlayer.usage");
        final String noPerms = messages.getString("info.warnings.noPerms");
        final String badPage = messages.getString("info.warnings.badPage");
        final String ownsNone = messages.getString("commands.listPlayer.alerts.ownsNone");
        final String listHeader = messages.getString("commands.listPlayer.alerts.listHeader");
        final String ownersLand = messages.getString("commands.listPlayer.alerts.ownersLand");
        final String pageNum = messages.getString("commands.listPlayer.alerts.pageNum");
        final String nextPageString = messages.getString("commands.listPlayer.alerts.nextPage");


        String owner;

        //sender.sendMessage(ChatColor.DARK_RED + "This command can only be run by a player.");
        if (args.length > 1) {
            owner = args[1];
        } else {
            sender.sendMessage(ChatColor.RED + usage.replace("#{label}", label));
            return true;
        }

        //Player player = (Player) sender;
        if (!sender.hasPermission("landlord.admin.list")) {
            sender.sendMessage(ChatColor.RED + noPerms);
            return true;
        }

        //check if page number is valid
        int pageNumber = 1;
        if (args.length > 2) {
            try {
                pageNumber = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + badPage);
                return true;
            }
        }
        /*
         * *************************************
         * mark for possible change    !!!!!!!!!
         * *************************************
         */
        OfflinePlayer possible = getOfflinePlayer(args[1]);
        if (!possible.hasPlayedBefore() && !possible.isOnline()) {
            sender.sendMessage(ChatColor.YELLOW + ownsNone.replace("#{owner}", owner));
            return true;
        }
        List<OwnedLand> myLand = plugin.getDatabase().getLands(possible.getUniqueId());
        if (myLand.size() == 0) {
            sender.sendMessage(ChatColor.YELLOW + ownsNone.replace("#{owner}", owner));
        } else {
            String header = ChatColor.DARK_GREEN + " | " + listHeader + " |     \n";
            ArrayList<String> landList = new ArrayList<>();
            //OwnedLand curr = myLand.get(0);
            for (OwnedLand aMyLand : myLand) {
                landList.add((ChatColor.GOLD + " (" + aMyLand.getChunk().getX() + ", " + aMyLand.getChunk().getZ() + ") - "
                        + aMyLand.getChunk().getWorld().getName()) + "\n");
            }
            //Amount to be displayed per page
            final int numPerPage = 7;

            int numPages = ceil((double) landList.size() / (double) numPerPage);
            if (pageNumber > numPages) {
                sender.sendMessage(ChatColor.RED + badPage);
                return true;
            }
            String pMsg = ChatColor.DARK_GREEN + "--- " + ChatColor.YELLOW + ownersLand.replace("#{owner}", owner) + ChatColor.DARK_GREEN + " ---" + ChatColor.YELLOW +
                    pageNum.replace("#{pageNumber}", "" + pageNumber) + ChatColor.DARK_GREEN + " ---\n" +
                    header;
            if (pageNumber == numPages) {
                for (int i = (numPerPage * pageNumber - numPerPage); i < landList.size(); i++) {
                    pMsg += landList.get(i);
                }
                pMsg += ChatColor.DARK_GREEN + "------------------------------";
            } else {
                for (int i = (numPerPage * pageNumber - numPerPage); i < (numPerPage * pageNumber); i++) {
                    pMsg += landList.get(i);
                }

                pMsg += ChatColor.DARK_GREEN + "--- " + ChatColor.YELLOW + nextPageString
                        .replace("#{label}", "/" + label)
                        .replace("#{cmd}", args[0])
                        .replace("#{pageNumber}", "" + pageNumber + 1)
                        + " ---";
            }

            sender.sendMessage(pMsg);
        }


        return true;
    }

    public String getHelpText(CommandSender sender) {

        if (!sender.hasPermission("landlord.admin.list")) {   // Don't bother showing command help if player can't do it
            return null;
        }

        FileConfiguration messages = plugin.getMessageConfig();

        final String usage = messages.getString("commands.listPlayer.usage");       // get the base usage string
        final String desc = messages.getString("commands.listPlayer.description");      // get the description

        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.listPlayer.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
