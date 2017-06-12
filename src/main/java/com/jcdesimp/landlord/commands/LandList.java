package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.util.NumberConversions.ceil;

/**
 * Created by jcdesimp on 2/18/15.
 * Command to get a list of the sender player's land
 */
public class LandList implements LandlordCommand {

    private Landlord plugin;

    public LandList(Landlord plugin) {
        this.plugin = plugin;
    }

    /**
     * Display a list of all owned land to a player
     *
     * @param sender who executed the command
     * @param args   given with command
     * @return boolean
     */
    public boolean execute(CommandSender sender, String[] args, String label) {


        FileConfiguration messages = plugin.getMessageConfig();

        final String notPlayer = messages.getString("info.warnings.playerCommand");
        final String noPerms = messages.getString("info.warnings.noPerms");
        final String badPageNum = messages.getString("info.warnings.badPage");
        final String noLand = messages.getString("commands.landList.alerts.noLand");

        final String outputHeader = messages.getString("commands.landList.alerts.listHeader");
        final String ownedLandString = messages.getString("commands.landList.alerts.outputHeader");
        final String pageNum = messages.getString("commands.landList.alerts.pageLabel");
        final String nextPageString = messages.getString("info.alerts.nextPage");


        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + notPlayer);
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("landlord.player.own")) {
                player.sendMessage(ChatColor.RED + noPerms);
                return true;
            }

            //check if page number is valid
            int pageNumber = 1;
            if (args.length > 1) {
                try {
                    pageNumber = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + badPageNum);
                    return true;
                }
            }

            List<OwnedLand> myLand = plugin.getDatabase().getLands(player.getUniqueId());
            if (myLand.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + noLand);
            } else {
                String header = ChatColor.DARK_GREEN + " | " + outputHeader + " |\n";
                ArrayList<String> landList = new ArrayList<>();
                //OwnedLand curr = myLand.get(0);
                for (OwnedLand aMyLand : myLand) {
                    landList.add((ChatColor.GOLD + " (" + aMyLand.getChunk().getX() + ", " + aMyLand.getChunk().getZ() + ") - "
                            + aMyLand.getChunk().getWorld().getName()) + "\n")
                    ;
                }
                //Amount to be displayed per page
                final int numPerPage = 7;

                int numPages = ceil((double) landList.size() / (double) numPerPage);
                if (pageNumber > numPages) {
                    player.sendMessage(ChatColor.RED + badPageNum);
                    return true;
                }

                StringBuilder pMsg = new StringBuilder();
                //String pMsg = ChatColor.DARK_GREEN + "--- " + ChatColor.YELLOW + ownedLandString + ChatColor.DARK_GREEN + " ---" + ChatColor.YELLOW + " Page " + pageNumber + ChatColor.DARK_GREEN + " ---\n" + header;
                pMsg.append(ChatColor.DARK_GREEN).append("--- ").append(ChatColor.YELLOW).append(ownedLandString.replace("#{pageNum}", pageNumber + ""))
                        .append(ChatColor.DARK_GREEN).append(" --- ").append(ChatColor.YELLOW).append(pageNum.replace("#{pageNum}", pageNumber + ""))
                        .append(ChatColor.DARK_GREEN).append(" ---\n")
                        .append(header);


                if (pageNumber == numPages) {
                    for (int i = (numPerPage * pageNumber - numPerPage); i < landList.size(); i++) {
                        pMsg.append(landList.get(i));
                    }
                    pMsg.append(ChatColor.DARK_GREEN).append("------------------------------");
                } else {
                    for (int i = (numPerPage * pageNumber - numPerPage); i < (numPerPage * pageNumber); i++) {
                        pMsg.append(landList.get(i));
                    }
                    //pMsg += ChatColor.DARK_GREEN + "--- do" + ChatColor.YELLOW + " /" + label + " list " + (pageNumber + 1) + ChatColor.DARK_GREEN + " for next page ---";

                    pMsg.append(ChatColor.DARK_GREEN).append("--- ").append(ChatColor.YELLOW).append(nextPageString
                            .replace("#{label}", "/" + label)
                            .replace("#{cmd}", args[0])
                            .replace("#{pageNumber}", "" + (pageNumber + 1))).append(ChatColor.DARK_GREEN).append(" ---");
                }

                player.sendMessage(pMsg.toString());
            }

        }
        return true;
    }

    public String getHelpText(CommandSender sender) {
        FileConfiguration messages = plugin.getMessageConfig();
        final String usage = messages.getString("commands.landList.usage"); // get the base usage string
        final String desc = messages.getString("commands.landList.description");   // get the description
        // return the constructed and colorized help string
        return Utils.helpString(usage, desc, getTriggers()[0].toLowerCase());

    }

    public String[] getTriggers() {
        final List<String> triggers = plugin.getMessageConfig().getStringList("commands.landList.triggers");
        return triggers.toArray(new String[triggers.size()]);
    }
}
