package biz.princeps.landlord.listener;

import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.PrincepsLib;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
 */
public class TresholdListener extends BasicListener {

    private LandAlerter.LandMessageDisplay display;
    private int treshold;

    public TresholdListener() {
        super();
        this.treshold = plugin.getConfig().getInt("SecureWorld.treshold");

        this.display = LandAlerter.LandMessageDisplay.valueOf(plugin.getConfig().getString("SecureWorld.displayWarning"));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {

        Player p = e.getPlayer();
        OwnedLand land = plugin.getLand(e.getBlock().getLocation());

        handleLand(p, e.getBlock().getLocation(), land, e);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {

        Player p = e.getPlayer();
        OwnedLand land = plugin.getLand(e.getBlock().getLocation());

        handleLand(p, e.getBlockPlaced().getLocation(), land, e);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {

        Player p = e.getPlayer();
        OwnedLand land = plugin.getLand(e.getBlockClicked().getLocation());

        handleLand(p, e.getBlockClicked().getLocation(), land, e);
    }

    public void handleLand(Player p, Location loc, OwnedLand land, Cancellable e) {
        // is free land
        if (p.isOp() || p.hasPermission("landlord.admin.bypass"))
            return;

        if (!plugin.getConfig().getStringList("disabled-worlds").contains(loc.getWorld().getName())) {

            LocalPlayer localPlayer = plugin.getWgHandler().getWGPlugin().wrapPlayer(p);
            ApplicableRegionSet applicableRegions = plugin.getWgHandler().getRegionManager(
                    loc.getWorld()).getApplicableRegions(localPlayer.getLocation().toVector().toBlockPoint());
            if (applicableRegions.getRegions().size() < 1) {
                boolean isAllowed = false;
                for (ProtectedRegion protectedRegion : applicableRegions.getRegions()) {
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
                        p.sendMessage(plugin.getLangManager().getString("Alerts.tresholdNotReached")
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
