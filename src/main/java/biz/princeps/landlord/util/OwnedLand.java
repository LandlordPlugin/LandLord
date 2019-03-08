package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.flags.LLFlag;
import biz.princeps.landlord.handler.WorldGuardHandler;
import biz.princeps.lib.PrincepsLib;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 */
public class OwnedLand {

    private ProtectedRegion region;
    private Set<LLFlag> flags;
    private World world;

    private WorldGuardHandler wg = Landlord.getInstance().getWgHandler();

    public OwnedLand(ProtectedRegion region) {
        this.region = region;
        this.flags = new HashSet<>();
        this.world = wg.getWorld(region.getId());
        this.initFlags();
    }

    /**
     * Highlights the border around the chunk with a particle effect.
     *
     * @param p player
     * @param e effect to play
     */
    public static void highlightLand(Player p, Particle e) {
        highlightLand(p, e, 5);
    }

    public static void highlightLand(Player p, Particle e, int amt) {
        if (!Landlord.getInstance().getConfig().getBoolean("options.particleEffects", true)) {
            return;
        }
        Chunk chunk = p.getLocation().getChunk();
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
            PrincepsLib.getStuffManager().spawnParticle(edgeBlock, e, amt);
        }
    }

    // statics
    public static BlockVector locationToVec(Location loc) {
        return new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static String getName(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    /**
     * also loads a chunk
     * @param name the region id to get a location on
     * @return returns a location on top of the chunk
     */
    public static Location getLocationFromName(String name) {
        String[] split = name.split("_");
        if (split.length >= 3) {
            StringBuilder sb = new StringBuilder(split[0]);
            for (int i = 1; i < split.length - 2; i++) {
                sb.append("_").append(split[i]);
            }

            World world = Bukkit.getWorld(sb.toString());
            if (world == null)
                return null;

            int x = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);
            return new Location(world, x * 16, world.getHighestBlockYAt(x * 16, z * 16) + 1, z * 16);
        } else
            return null;

    }

    private void initFlags() {
        List<String> flaggy = Landlord.getInstance().getConfig().getStringList("Flags");
        Set<String> flags = new HashSet<>();

        flaggy.forEach(s -> flags.add(s.split(" ")[0]));

        //Iterate over all existing flags
        for (Flag<?> flag : Landlord.getInstance().getWgHandler().getFlags()) {
            if (flag instanceof StateFlag) {
                boolean failed = false;
                if (flags.contains(flag.getName())) {
                    // Filters the config list for the right line and split that line in the mid at :
                    String[] rules = flaggy.stream().filter(s -> s.startsWith(flag.getName())).findFirst().get().split(":");
                    if (rules.length == 2) {
                        LLFlag llFlag = new LLFlag(flag, this, Material.getMaterial(Landlord.getInstance().getConfig().getString("Manage." + flag.getName() + ".item")));

                        StateFlag.State state1 = null, state2 = null;
                        String g1 = null, g2 = null;

                        String[] defSplit = rules[0].split(" ");
                        if (defSplit.length == 3) {
                            state1 = StateFlag.State.valueOf(defSplit[1].toUpperCase());
                            g1 = defSplit[2];

                        } else {
                            failed = true;
                        }


                        String[] toggleSplit = rules[1].split(" ");
                        if (toggleSplit.length == 2) {
                            state2 = StateFlag.State.valueOf(toggleSplit[0].toUpperCase());
                            g2 = toggleSplit[1];

                        } else {
                            failed = true;
                        }
                        if (state1 != null && state2 != null && g1 != null && g2 != null) {
                            llFlag.setToggle(state1, g1, state2, g2);
                            this.flags.add(llFlag);
                        }

                    } else {
                        failed = true;
                    }
                }

                if (failed) {
                    Bukkit.getLogger().warning("ERROR: Your flag definition is invalid!");
                    break;
                }
            }
        }


    }

    public String getName() {
        return region.getId();
    }

    public boolean isOwner(UUID uuid) {
        return region.getOwners().getUniqueIds().contains(uuid);
    }

    public UUID getOwner() {
        return region.getOwners().getUniqueIds().iterator().next();
    }

    public World getWorld() {
        return world;
    }

    /**
     * Loads a chunk!
     * @return
     */
    public Chunk getChunk() {
        World w = wg.getWorld(region.getId());
        int x = wg.getX(region.getId());
        int z = wg.getZ(region.getId());

        if (w != null && x != Integer.MIN_VALUE && z != Integer.MIN_VALUE) {
            return w.getChunkAt(x, z);
        }
        return null;
    }

    public void addFriends(DefaultDomain domain) {
        region.getMembers().addAll(domain);
    }

    public void removeFriends(DefaultDomain defaultDomain) {
        region.getMembers().removeAll(defaultDomain);
    }

    public void removeFriend(UUID id) {
        region.getMembers().removePlayer(id);
    }

    public String printOwners() {
        StringBuilder sb = new StringBuilder();
        Iterator<UUID> it = region.getOwners().getUniqueIds().iterator();
        while (it.hasNext()) {
            sb.append(Bukkit.getOfflinePlayer(it.next()).getName());
            if (it.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }

    public String printMembers() {
        StringBuilder sb = new StringBuilder();
        Iterator<UUID> it = region.getMembers().getUniqueIds().iterator();
        while (it.hasNext()) {
            sb.append(Bukkit.getOfflinePlayer(it.next()).getName());
            if (it.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }

    public ProtectedRegion getWGLand() {
        return this.region;
    }

    public LLFlag getFlag(Flag<?> flag) {
        for (LLFlag llFlag : flags) {
            if (llFlag.getWGFlag().equals(flag))
                return llFlag;
        }
        return null;
    }

    @Override
    public String toString() {
        return "OwnedLand{" +
                "chunk=" + region.getId() +
                '}';
    }

    public Set<LLFlag> getFlags() {
        return flags;
    }

    public Set<UUID> getMembers() {
        return this.getWGLand().getMembers().getUniqueIds();
    }
}
