package biz.princeps.landlord.handler;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardProxy;
import org.bukkit.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class AWorldGuardProxy implements IWorldGuardProxy {

    @Override
    public World getWorld(String name) {
        String[] splitted = name.split("_");

        if (splitted.length < 3) {
            return null;
        }

        // Handle world names with multiple spaces in the name
        StringBuilder sb = new StringBuilder(splitted[0]);
        for (int i = 1; i < splitted.length - 2; i++) {
            sb.append("_").append(splitted[i]);
        }

        return Bukkit.getWorld(sb.toString());
    }

    /**
     * Returns the x coordinate of a landname
     */
    public int getX(String name) {
        String[] splitted = name.split("_");

        if (splitted.length < 3) {
            return Integer.MIN_VALUE;
        }

        try {
            return Integer.parseInt(splitted[splitted.length - 2]);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Returns the z coordinate of a landname
     */
    public int getZ(String name) {
        String[] splitted = name.split("_");

        if (splitted.length < 3) {
            return Integer.MIN_VALUE;
        }

        try {
            return Integer.parseInt(splitted[splitted.length - 1]);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Checks if a string is a valid ll region e.g. world_12_34
     */
    public boolean isLLRegion(String name) {
        String[] splitted = name.split("_");

        if (splitted.length < 3) {
            return false;
        }

        StringBuilder sb = new StringBuilder(splitted[0]);
        for (int i = 1; i < splitted.length - 2; i++) {
            sb.append("_").append(splitted[i]);
        }

        World world = Bukkit.getWorld(sb.toString());
        if (world == null)
            return false;

        try {
            Integer.parseInt(splitted[splitted.length - 2]);
            Integer.parseInt(splitted[splitted.length - 1]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Map<Chunk, IOwnedLand> getNearbyLands(Location loc, int offsetX, int offsetZ) {

        Map<Chunk, IOwnedLand> lands = new HashMap<>();
        int xCoord = loc.getChunk().getX();
        int zCoord = loc.getChunk().getZ();
        for (int x = xCoord - offsetX; x <= xCoord + offsetX; x++) {
            for (int z = zCoord - offsetZ; z <= zCoord + offsetZ; z++) {
                Chunk chunk = loc.getWorld().getChunkAt(x, z);
                lands.put(chunk, this.getRegion(chunk));
            }
        }
        return lands;
    }

    @Override
    public Set<IOwnedLand> getRegions(UUID id) {
        Set<IOwnedLand> set = new HashSet<>();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        if (op != null) {
            List<String> worlds = Landlord.getInstance().getConfig().getStringList("disabled-worlds");
            for (World world : Bukkit.getWorlds()) {
                // Only count enabled worlds
                if (!worlds.contains(world.getName())) {
                    set.addAll(getRegions(id, world)
                            .stream().filter(r -> isLLRegion(r.getName()))
                            .collect(Collectors.toSet()));
                }
            }
        }
        return set;
    }
}
