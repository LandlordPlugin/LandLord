package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.flags.*;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.crossversion.CParticle;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
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
    private Map<Class<? extends IFlag>, IFlag> flags;
    private World world;

    public OwnedLand(ProtectedRegion region, Chunk chunk) {
        this(region, chunk, true);
    }

    public OwnedLand(ProtectedRegion region, Chunk chunk, boolean initFlags) {
        this.region = region;
        this.chunk = chunk;
        this.flags = new HashMap<>();
        this.world = chunk.getWorld();
        this.initFlags(initFlags);
    }


    private void initFlags(boolean setDefaultState) {
        List<String> flaggy = Landlord.getInstance().getConfig().getStringList("Flags");

        for (String localFlag : flaggy) {
            String[] split = localFlag.split(" ");
            switch (split[0]) {

                case "build":
                    flags.put(Build.class, new Build(this, setDefaultState));
                    break;

                case "chest-access":
                    flags.put(Chest_Access.class, new Chest_Access(this, setDefaultState));
                    break;

                case "interact":
                    flags.put(Interact.class, new Interact(this, setDefaultState));
                    break;

                case "creeper-explosion":
                    flags.put(Creeper_Explosion.class, new Creeper_Explosion(this, setDefaultState));
                    break;

                case "pvp":
                    flags.put(Pvp.class, new Pvp(this, setDefaultState));
                    break;
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
            // p.getWorld().spawnParticle(e, edgeBlock, amt, 0.2, 0.2, 0.2, 20.0);
            PrincepsLib.crossVersion().spawnParticle(edgeBlock, e, amt);
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

    public IFlag getFlag(Class<? extends IFlag> classy) {
        return flags.get(classy);
    }

    public Map<Class<? extends IFlag>, IFlag> getFlags() {
        return flags;
    }
}
