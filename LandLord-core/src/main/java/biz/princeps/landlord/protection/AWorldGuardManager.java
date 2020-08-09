package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.lib.PrincepsLib;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class AWorldGuardManager implements IWorldGuardManager {

    protected final LandCache cache = new LandCache();
    protected final ILandLord pl;

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
    public Map<Chunk, IOwnedLand> getNearbyLands(Chunk chunk, int offsetX, int offsetZ) {
        Map<Chunk, IOwnedLand> lands = new HashMap<>();
        int xCoord = chunk.getX();
        int zCoord = chunk.getZ();
        for (int x = xCoord - offsetX; x <= xCoord + offsetX; x++) {
            for (int z = zCoord - offsetZ; z <= zCoord + offsetZ; z++) {
                Chunk chunkA = chunk.getWorld().getChunkAt(x, z);
                lands.put(chunkA, this.getRegion(chunkA));
            }
        }
        return lands;
    }

    @Override
    public Map<Chunk, IOwnedLand> getNearbyLands(Location loc, int offsetX, int offsetZ) {
        return getNearbyLands(loc.getChunk(), offsetX, offsetZ);
    }

    public abstract void moveUp(World world, int x, int z, int amt);

    @Override
    public Set<IOwnedLand> getRegions(UUID id) {
        Set<IOwnedLand> set = new HashSet<>();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        if (op != null) {
            List<String> worlds = pl.getConfig().getStringList("disabled-worlds");
            for (World world : Bukkit.getWorlds()) {
                // Only count enabled worlds
                if (!worlds.contains(world.getName())) {
                    for (IOwnedLand region : getRegions(id, world)) {
                        if (!isLLRegion(region.getName())) continue;

                        set.add(region);
                    }
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

        int x, z, chunkX = chunk.getX() << 4, chunkZ = chunk.getZ() << 4;
        x = chunkX + 8;
        z = chunkZ + 8;

        configString = configString.replace("%world%", chunk.getWorld().getName());
        configString = configString.replace("%x%", x + "");
        configString = configString.replace("%z%", z + "");

        return ChatColor.translateAlternateColorCodes('&', configString);
    }

    @Override
    public void highlightLand(Chunk chunk, Player p, Particle particle, int amount, boolean everyone) {
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
            if (everyone) {
                PrincepsLib.getStuffManager().spawnPublicParticle(edgeBlock, particle, amount);

            } else {
                PrincepsLib.getStuffManager().spawnPlayerParticle(p, edgeBlock, particle, amount);
            }
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
        String name = loc.getWorld().getName().toLowerCase().replace(" ", "_");
        name += "_";

        // x coord
        int x = loc.getBlockX() >> 4;
        name += x;
        name += "_";
        // z coord
        int z = loc.getBlockZ() >> 4;
        name += z;
        return getRegion(name);
    }

    @Override
    public int unclaim(Set<IOwnedLand> regions) {
        int count = regions.size();

        Set<UUID> owners = new HashSet<>();
        for (IOwnedLand region : regions) {
            owners.add(region.getOwner());
        }

        for (UUID owner : owners) {
            pl.getPlayerManager().getOffline(owner, player -> {
                for (IOwnedLand region : regions) {
                    if (region.isOwner(owner)) {
                        //System.out.println("isowner");
                        if (player.getHome() != null && region.contains(player.getHome())) {
                            //System.out.println("remo");
                            player.setHome(null);
                            pl.getPlayerManager().save(player, true);
                        }
                    }
                }
            });
        }

        for (IOwnedLand region : new HashSet<>(regions)) {
            unclaim(region);
        }
        return count;
    }
}
