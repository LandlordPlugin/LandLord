package biz.princeps.landlord.listener;

import biz.princeps.landlord.api.events.PlayerBrokeSecureWorldEvent;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.PrincepsLib;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 3/12/17
 *
 * Structure wise it goes like this:
 * 1. BlockBreak, BlockPlace, BucketEmpty calls PlayerBrokeSecureWorldEvent
 * 2. PlaceBrokeSecureWorldEvent decides if the intrusion was allowed or not.
 */
public class SecureWorldListener extends BasicListener {

    private LandAlerter.LandMessageDisplay display;
    private int treshold;

    public SecureWorldListener() {
        super();
        this.treshold = plugin.getConfig().getInt("SecureWorld.threshold");

        this.display = LandAlerter.LandMessageDisplay.valueOf(plugin.getConfig().getString("SecureWorld.displayWarning"));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        OwnedLand land = plugin.getLand(e.getBlock().getLocation());

        if (land == null) {
            PlayerBrokeSecureWorldEvent event = new PlayerBrokeSecureWorldEvent(p, e.getBlock(), e);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        OwnedLand land = plugin.getLand(e.getBlockPlaced().getLocation());

        if (land == null) {
            PlayerBrokeSecureWorldEvent event = new PlayerBrokeSecureWorldEvent(p, e.getBlockPlaced(), e);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        OwnedLand land = plugin.getLand(e.getBlockClicked().getLocation());

        if (land == null) {
            PlayerBrokeSecureWorldEvent event = new PlayerBrokeSecureWorldEvent(p, e.getBlockClicked(), e);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onThresholdEvent(PlayerBrokeSecureWorldEvent event) {
        OwnedLand land = plugin.getLand(event.getBlock().getLocation());
        if (!event.isCancelled()) {
            handleLand(event.getPlayer(), event.getBlock().getLocation(), land, event.getCancellable());
        }
    }

    private void handleLand(Player p, Location loc, OwnedLand land, Cancellable e) {
        // is free land
        if (p.isOp() || p.hasPermission("landlord.admin.bypass"))
            return;

        if (!plugin.getConfig().getStringList("disabled-worlds").contains(loc.getWorld().getName())) {

            LocalPlayer localPlayer = plugin.getWgHandler().getWGPlugin().wrapPlayer(p);
            ApplicableRegionSet applicableRegions = plugin.getWgHandler().getRegionManager(
                    loc.getWorld()).getApplicableRegions(localPlayer.getLocation().toVector().toBlockPoint());
            if (applicableRegions.getRegions().size() < 1) { //Test of this list to know if it's empty.
                boolean isAllowed = false;
                for (ProtectedRegion protectedRegion : applicableRegions.getRegions()) { //Loop of this empty List.
                    //I don't want to touch that for "securiry" but i think it's useless, empty list but loop, why ?
                    if (protectedRegion.isMember(localPlayer) || protectedRegion.isOwner(localPlayer)) {
                        isAllowed = true;
                        break;
                    }
                }
                if (isAllowed) {
                    return;
                }
            }

            if (land == null) {
                int landcount = plugin.getWgHandler().getRegionCountOfPlayer(p.getUniqueId());

                if (landcount < treshold) {
                    String rawString = plugin.getLangManager().getRawString("Alerts.tresholdNotReached")
                            .replace("%x%", treshold + "");
                    if (display == LandAlerter.LandMessageDisplay.ActionBar) {
                        PrincepsLib.getStuffManager().sendActionBar(p, rawString);
                    } else if (display == LandAlerter.LandMessageDisplay.Chat) {
                        plugin.getLangManager().sendMessage(p, plugin.getLangManager().getString("Alerts.tresholdNotReached")
                                .replace("%x%", treshold + ""));
                    } else if (display == LandAlerter.LandMessageDisplay.Title) {
                        p.sendTitle(rawString, null, 10, 70, 10);
                    }

                    e.setCancelled(true);
                }
            }
        }
    }
}
