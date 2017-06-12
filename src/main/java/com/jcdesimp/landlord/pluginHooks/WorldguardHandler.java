package com.jcdesimp.landlord.pluginHooks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

/**
 * File created by jcdesimp on 3/15/14.
 * Class for handling worldguard interactions
 */
public class WorldguardHandler {
    WorldGuardPlugin worldguard;


    /**
     * Constructor
     *
     * @param worldguard plugin instance
     */
    public WorldguardHandler(WorldGuardPlugin worldguard) {
        this.worldguard = worldguard;
    }


    /**
     * Determines if a player is allowed to claim or not by
     * checking region intersections with their current chunk
     *
     * @param player    trying to claim
     * @param currChunk that is being claimed
     * @return boolean of allowed or not
     */
    public boolean canClaim(Player player, Chunk currChunk) {
        RegionManager regionManager = worldguard.getRegionManager(player.getWorld());
        //System.out.println("Has Worldguard");
        if (regionManager != null) {
            //System.out.println("region manager not null");
            ProtectedRegion check = new ProtectedCuboidRegion("check", toVector(currChunk.getBlock(0, 0, 0)), toVector(currChunk.getBlock(15, 127, 15)));
            //System.out.println(check.getMinimumPoint() +" " + check.getMaximumPoint());


            //System.out.println("in try!");
            List<ProtectedRegion> intersects = check.getIntersectingRegions(new ArrayList<ProtectedRegion>(regionManager.getRegions().values()));
            //System.out.println("got intersects");
            for (ProtectedRegion intersect : intersects) {
                //System.out.println(intersects.get(i).toString());

                //todo confront deprecation
                if (!regionManager.getApplicableRegions(intersect).canBuild(worldguard.wrapPlayer(player))) {
                    return false;
                }
            }


        }
        return true;
    }
}
