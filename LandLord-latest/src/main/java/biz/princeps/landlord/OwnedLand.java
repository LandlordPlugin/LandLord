package biz.princeps.landlord;

import biz.princeps.landlord.api.IFlag;
import biz.princeps.landlord.api.IWorldGuardProxy;
import biz.princeps.landlord.util.AOwnedLand;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class OwnedLand extends AOwnedLand {

    private ProtectedRegion region;
    private World world;
    private Set<LLFlag> flags;

    public OwnedLand(ProtectedRegion region) {
        this.region = region;
        this.world = Landlord.getInstance().getWgproxy().getWorld(region.getId());
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

    @Override
    public String getName() {
        return region.getId();
    }

    @Override
    public boolean isOwner(UUID uuid) {
        return region.getOwners().getUniqueIds().contains(uuid);
    }

    @Override
    public UUID getOwner() {
        return region.getOwners().getUniqueIds().iterator().next();
    }

    @Override
    public boolean isMember(UUID uuid) {
        return getMembers().contains(uuid);
    }

    @Override
    public Set<UUID> getMembers() {
        return this.region.getMembers().getUniqueIds();
    }

    @Override
    public void addFriend(UUID uuid) {
        this.region.getMembers().addPlayer(uuid);
    }

    @Override
    public void removeFriend(UUID uuid) {
        this.region.getMembers().removePlayer(uuid);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Chunk getChunk() {
        IWorldGuardProxy wg = Landlord.getInstance().getWgproxy();
        World w = wg.getWorld(region.getId());
        int x = wg.getX(region.getId());
        int z = wg.getZ(region.getId());

        if (w != null && x != Integer.MIN_VALUE && z != Integer.MIN_VALUE) {
            return w.getChunkAt(x, z);
        }
        return null;
    }

    @Override
    public Set<IFlag> getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return "OwnedLand{" +
                "chunk=" + region.getId() +
                '}';
    }

}
