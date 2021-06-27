package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.ClaimHeightDefinition;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.tuple.Pair;
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

    @Override
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

        final int x = (chunk.getX() << 4) + 8;
        final int z = (chunk.getZ() << 4) + 8;

        configString = configString.replace("%world%", chunk.getWorld().getName());
        configString = configString.replace("%x%", x + "");
        configString = configString.replace("%z%", z + "");

        return ChatColor.translateAlternateColorCodes('&', configString);
    }

    @Override
    public void highlightLand(Chunk chunk, Player p, Particle particle, int amount, boolean everyone) {
        final World world = chunk.getWorld();
        final int x = chunk.getX() << 4;
        final int z = chunk.getZ() << 4;
        final int y = (int) p.getLocation().getY();
        if (!everyone && p.getLocation().distance(new Location(world, x, y, z)) > 64.0) {
            // Honestly, particles beyond 4 chunks are useless and can't be seen correctly.
            return;
        }

        final Set<Location> edgeBlocks = new HashSet<>();

        for (int i = 0; i < 16; i++) {
            for (int ii = -1; ii <= 10; ii++) {
                edgeBlocks.add(new Location(world, x + i, y + ii, z + 15));
                edgeBlocks.add(new Location(world, x + i, y + ii, z));
                edgeBlocks.add(new Location(world, x, y + ii, z + i));
                edgeBlocks.add(new Location(world, x + 15, y + ii, z + i));
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
        return getSurroundings(new Location(chunk.getWorld(), (chunk.getX() << 4) + 1, 1, chunk.getZ() << 4 + 1));
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
        return getSurroundingsOwner(new Location(chunk.getWorld(), (chunk.getX() << 4) + 1, 1, (chunk.getZ() << 4) + 1), owner);
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
        final int count = regions.size();

        final Set<UUID> owners = new HashSet<>();
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

    @Override
    public Pair<Integer, Integer> calcClaimHeightBoundaries(Chunk chunk) {
        ClaimHeightDefinition boundaryMethod = ClaimHeightDefinition.parse(pl.getConfig().getString("ClaimHeight.method"));

        // We will use the full and default behaviour as default value.
        if (boundaryMethod == null) {
            boundaryMethod = ClaimHeightDefinition.FULL;
        }

        int maxHeight = chunk.getWorld().getMaxHeight() - 1;

        // Full is the default behaviour.
        // This will claim the whole chunk.
        if (boundaryMethod == ClaimHeightDefinition.FULL) {
            return Pair.of(0, maxHeight);
        }

        int bottomY = pl.getConfig().getInt("ClaimHeight.bottomY", 0);
        int topY = pl.getConfig().getInt("ClaimHeight.topY", maxHeight);

        // Fixed is the simple claim behaviour.
        // We want to handle this first.
        if (boundaryMethod == ClaimHeightDefinition.FIXED) {
            return Pair.of(Math.max(0, bottomY), Math.min(topY, maxHeight));
        }

        // Lets find all highest points in the chunk.
        List<Integer> points = new ArrayList<>();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                points.add(chunk.getWorld().getHighestBlockYAt((chunk.getX() << 4) + x, (chunk.getZ() << 4) + z));
            }
        }

        // Get the center based on the boundary Method
        int center = boundaryMethod.getCenter(points);

        bottomY = center + bottomY;
        topY = center + topY;


        if (pl.getConfig().getBoolean("ClaimHeight.appendOversize")) {
            // We append the oversize which reach out of the world on the top or the bottom if it fits.
            // We throw oversize away if we would exceed the world height limit on both ends.
            if (topY > maxHeight) {
                bottomY -= topY - maxHeight;
                topY = maxHeight;
                if (bottomY < 0) {
                    bottomY = 0;
                }
            }

            if (bottomY < 0) {
                topY += Math.abs(bottomY);
                bottomY = 0;
                if (topY > maxHeight) {
                    topY = maxHeight;
                }
            }
        } else {
            // Just clamp this stuff.
            bottomY = Math.max(bottomY, 0);
            topY = Math.min(topY, maxHeight);
        }
        return Pair.of(bottomY, topY);
    }

}
