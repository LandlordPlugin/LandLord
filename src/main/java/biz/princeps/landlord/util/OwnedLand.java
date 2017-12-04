package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.flags.LLFlag;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.crossversion.CParticle;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by spatium on 17.07.17.
 */
public class OwnedLand {

    private ProtectedRegion region;
    private Chunk chunk;
    private Set<LLFlag> flags;
    private World world;


    public OwnedLand(ProtectedRegion region, Chunk chunk) {
        this.region = region;
        this.chunk = chunk;
        this.flags = new HashSet<>();
        this.world = chunk.getWorld();
        this.initFlags();
    }


    private void initFlags() {
        List<String> flaggy = Landlord.getInstance().getConfig().getStringList("Flags");
        Set<String> flags = new HashSet<>();

        flaggy.forEach(s -> flags.add(s.split(" ")[0]));

        //Iterate over all existing flags
        for (Flag<?> flag : DefaultFlag.getFlags()) {
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
        return getName(chunk);
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

    public Chunk getChunk() {
        return chunk;
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
            OfflinePlayer op = Bukkit.getOfflinePlayer(it.next());
            sb.append(op.getName());
            if (it.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }

    public ProtectedRegion getWGLand() {
        return this.region;
    }


    /**
     * Highlights the border around the chunk with a particle effect.
     *
     * @param p player
     * @param e effect to play
     */
    public static void highlightLand(Player p, CParticle e) {
        highlightLand(p, e, 5);
    }

    public static void highlightLand(Player p, CParticle e, int amt) {
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
            PrincepsLib.crossVersion().spawnParticle(p, edgeBlock, e, amt);
        }
    }


    // statics
    public static BlockVector locationToVec(Location loc) {
        return new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static String getName(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    public static Location getLocationFromName(String name) {
        String[] split = name.split("_");
        if (split.length == 3) {
            World w = Bukkit.getWorld(split[0]);
            int x = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);
            return new Location(w, x * 16, w.getHighestBlockYAt(x * 16, z * 16), z * 16);
        } else
            return null;

    }

    public static double calculateCost(Player player) {
        Landlord plugin = Landlord.getInstance();

        double minCost = plugin.getConfig().getDouble("Formula.minCost");
        double maxCost = plugin.getConfig().getDouble("Formula.maxCost");
        double multiplier = plugin.getConfig().getDouble("Formula.multiplier");
        int x = plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegionCountOfPlayer(plugin.getWgHandler().getWG().wrapPlayer(player));
        int freeLands = plugin.getConfig().getInt("Freelands");

        double var = Math.pow(multiplier, x - freeLands);

        if (x < freeLands)
            return 0;
        else
            return maxCost - (maxCost - minCost) * var;
    }

    public LLFlag getFlag(DefaultFlag flag) {
        for (LLFlag llFlag : flags) {
            if (llFlag.getWGFlag().equals(flag))
                return llFlag;
        }
        return null;
    }

    public Set<LLFlag> getFlags() {
        return flags;
    }
}
