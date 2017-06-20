package com.jcdesimp.landlord;

import com.jcdesimp.landlord.landManagement.LandManager;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

import static biz.princeps.lib.util.SpigotUtil.sendActionBar;

/**
 * File created by jcdesimp on 4/30/14.
 * Alerts a player when they enter land.
 */
public class LandAlerter implements Listener {

    HashMap<String, String> landIn = new HashMap<String, String>();


    private Landlord plugin;


    public LandAlerter(Landlord plugin) {
        this.plugin = plugin;
    }

    public void landAlertPlayer(Player player, Location loc) {

        FileConfiguration messages = plugin.getMessageConfig();

        String leaveOwn = messages.getString("info.alerts.leaveOwnLand");
        String leaveOther = messages.getString("info.alerts.leaveOtherLand");
        String enterOwn = messages.getString("info.alerts.enterOwnLand");
        String enterOther = messages.getString("info.alerts.enterOtherLand");

        OwnedLand land = LandManager.getApplicableLand(loc);


        //Leaving Land
        if (landIn.containsKey(player.getName())) {
            if (land == null) {
                String prevName = landIn.get(player.getName());
                if (prevName.equals(player.getName())) {
                    sendActionBar(player, ChatColor.YELLOW + "** " + leaveOwn);
                } else {
                    sendActionBar(player, ChatColor.YELLOW + "** " + (leaveOther.replace("#{owner}", prevName)));
                }

            } else {
                String prevName = landIn.get(player.getName());
                if (!prevName.equals(land.getOwnerUsername())) {
                    if (prevName.equals(player.getName())) {
                        sendActionBar(player, ChatColor.YELLOW + "** " + leaveOwn);
                    } else {
                        sendActionBar(player, ChatColor.YELLOW + "** " + (leaveOther.replace("#{owner}", prevName)));
                    }
                }
            }
        }


        //Entering Land
        if (land == null) {
            landIn.remove(player.getName());
            return;
        }

        if (landIn.containsKey(player.getName())) {
            String prevName = landIn.get(player.getName());
            if (!prevName.equals(land.getOwnerUsername())) {
                landIn.put(player.getName(), land.getOwnerUsername());

                if (land.getOwnerUsername().equals(player.getName())) {
                    sendActionBar(player, ChatColor.GREEN + "** " + enterOwn);
                } else {
                    String ownerName = land.getOwnerUsername();
                    sendActionBar(player, ChatColor.YELLOW + "** " + enterOther.replace("#{owner}", ownerName));

                }
            }
        } else {
            landIn.put(player.getName(), land.getOwnerUsername());
            if (land.getOwnerUsername().equals(player.getName())) {
                sendActionBar(player, ChatColor.GREEN + "** " + enterOwn);
            } else {
                String ownerName = land.getOwnerUsername();
                sendActionBar(player, ChatColor.YELLOW + "** " + enterOther.replace("#{owner}", ownerName));

            }
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void alertPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();


        if (player.getVehicle() != null) {
            return;
        }

        // Check if changed chunk
        if (event.getFrom().getChunk().getX() != event.getTo().getChunk().getX()
                || event.getFrom().getChunk().getZ() != event.getTo().getChunk().getZ()) {

            landAlertPlayer(player, event.getTo());


        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void teleportAlert(PlayerTeleportEvent event) {
        landAlertPlayer(event.getPlayer(), event.getTo());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerLeave(PlayerQuitEvent event) {
        landIn.remove(event.getPlayer().getName());
    }


    public void clearPtrack() {
        landIn.clear();
    }
}
