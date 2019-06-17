package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.lib.PrincepsLib;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class AWorldGuardManager implements IWorldGuardManager {

    protected LandCache cache = new LandCache();
    protected ILandLord pl;

    public AWorldGuardManager(ILandLord pl) {
        this.pl = pl;
    }

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
    @Override
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
    @Override
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
    @Override
    public boolean isLLRegion(String name) {

        World world = getWorld(name);
        if (world == null)
            return false;

        int x = getX(name);
        int z = getZ(name);

        return x != Integer.MIN_VALUE && z != Integer.MIN_VALUE;
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
            List<String> worlds = pl.getConfig().getStringList("disabled-worlds");
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

    @Override
    public String getLandName(Chunk chunk) {
        return chunk.getWorld().getName().toLowerCase() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    @Override
    public String formatLocation(Chunk chunk) {
        String configString = pl.getConfig().getString("locationFormat");

        int x, z, chunkX = chunk.getX() * 16, chunkZ = chunk.getZ() * 16;
        x = chunkX + 8;
        z = chunkZ + 8;

        configString = configString.replace("%world%", chunk.getWorld().getName());
        configString = configString.replace("%x%", x + "");
        configString = configString.replace("%z%", z + "");

        return ChatColor.translateAlternateColorCodes('&', configString);
    }

    @Override
    public void highlightLand(Chunk chunk, Player p, Particle particle, int amount) {
        List<Location> edgeBlocks = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            for (int ii = -1; ii <= 10; ii++) {
                edgeBlocks.add(chunk.getBlock(i, (int) (p.getLocation().getY()) + ii, 15).getLocation());
                edgeBlocks.add(chunk.getBlock(i, (int) (p.getLocation().getY()) + ii, 0).getLocation());
                edgeBlocks.add(chunk.getBlock(0, (int) (p.getLocation().getY()) + ii, i).getLocation());
                edgeBlocks.add(chunk.getBlock(15, (int) (p.getLocation().getY()) + ii, i).getLocation());
            }
        }
        for (Location edgeBlock : edgeBlocks) {
            edgeBlock.setZ(edgeBlock.getBlockZ() + .5);
            edgeBlock.setX(edgeBlock.getBlockX() + .5);
            PrincepsLib.getStuffManager().spawnParticle(edgeBlock, particle, amount);
        }
    }

    /**
     * Return the surrounding protected regions of a location.
     *
     * @param ploc the location
     * @return an array of size 5 containing the region of the location itsself and all the surrounding regions
     */
    @Override
    public IOwnedLand[] getSurroundings(Location ploc) {
        if (ploc == null) return new IOwnedLand[0];
        return new IOwnedLand[]{
                getRegion(ploc),
                getRegion(ploc.clone().add(16, 0, 0)),
                getRegion(ploc.clone().add(0, 0, 16)),
                getRegion(ploc.clone().subtract(16, 0, 0)),
                getRegion(ploc.clone().subtract(0, 0, 16)),
        };

    }

    @Override
    public IOwnedLand[] getSurroundings(Chunk chunk) {
        return getSurroundings(chunk.getBlock(1, 1, 1).getLocation());
    }

    @Override
    public IOwnedLand[] getSurroundings(IOwnedLand land) {
        return getSurroundings(land.getChunk());
    }

    @Override
    public IOwnedLand[] getSurroundingsOwner(Location ploc, UUID owner) {
        IOwnedLand[] surroundings = getSurroundings(ploc);
        for (int i = 0; i < surroundings.length; i++) {
            if (surroundings[i] != null && !surroundings[i].isOwner(owner)) {
                surroundings[i] = null;
            }
        }
        return surroundings;
    }

    @Override
    public IOwnedLand[] getSurroundingsOwner(Chunk chunk, UUID owner) {
        return getSurroundingsOwner(chunk.getBlock(1, 1, 1).getLocation(), owner);
    }

    @Override
    public IOwnedLand[] getSurroundingsOwner(IOwnedLand land, UUID owner) {
        return getSurroundingsOwner(land.getChunk(), owner);
    }

    @Override
    public IOwnedLand getRegion(Chunk chunk) {
        String name = getLandName(chunk);
        return getRegion(name);
    }

    @Override
    public IOwnedLand getRegion(Location loc) {
        String name = loc.getWorld().getName().replace(" ", "_");
        name += "_";

        // x coord
        int x = (int) Math.floor(loc.getX() / 16);
        name += x;
        name += "_";
        // z coord
        int z = (int) Math.floor(loc.getZ() / 16);
        name += z;
        return getRegion(name);
    }


}
