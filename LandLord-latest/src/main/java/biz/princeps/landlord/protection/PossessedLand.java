package biz.princeps.landlord.protection;

import biz.princeps.landlord.LLFlag;
import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.api.IPossessedLand;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class PossessedLand extends APossessedLand {

    private ProtectedRegion region;

    private PossessedLand(ILandLord pl, World w, ProtectedRegion region) {
        super(pl, w, region.getId());
        this.region = region;
    }

    @Override
    public String getOwnerName() {
        Iterator<UUID> it = region.getOwners().getUniqueIds().iterator();
        return itToString(it);
    }

    @Override
    public String getMembersName() {
        Iterator<UUID> it = region.getMembers().getUniqueIds().iterator();
        return itToString(it);
    }

    private String itToString(Iterator<UUID> it) {
        StringBuilder sb = new StringBuilder();
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
    public boolean isFriend(UUID uuid) {
        return getFriends().contains(uuid);
    }

    @Override
    public Set<UUID> getFriends() {
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
    public void highlight(Player p, Particle pa) {

    }

    @Override
    public void highlight(Player p, Particle e, int amt) {

    }

    @Override
    public Location getALocation() {
        return null;
    }

    @Override
    public boolean contains(int x, int y, int z) {
        return region.contains(x, y, z);
    }

    @Override
    public String toString() {
        return "OwnedLand{" +
                "chunk=" + region.getId() +
                '}';
    }

    @Override
    public List<ILLFlag> getFlags() {
        List<ILLFlag> toReturn = new ArrayList<>();
        List<String> rawList = pl.getConfig().getStringList("Flags");

        for (String s : rawList) {
            Flag flag = Flags.get(s.toLowerCase());
            if (flag == null) {
                pl.getLogger().warning("Invalid worldguard flag found: " + s);
                continue;
            }
            Material mat = Material.valueOf(pl.getConfig()
                    .getString("Manage." + s.toLowerCase() + ".item"));

            toReturn.add(new LLFlag(region, flag, mat));
        }
        return toReturn;
    }

    @Override
    public String getGreetMessage() {
        return region.getFlag(Flags.GREET_MESSAGE);
    }

    @Override
    public void setGreetMessage(String newmsg) {
        region.setFlag(Flags.GREET_MESSAGE, newmsg);
    }

    @Override
    public String getFarewellMessage() {
        return region.getFlag(Flags.FAREWELL_MESSAGE);
    }

    @Override
    public void setFarewellMessage(String newmsg) {
        region.setFlag(Flags.FAREWELL_MESSAGE, newmsg);
    }

    @Override
    public void toggleMob(IMob mob) {
        Set<EntityType> flag = region.getFlag(Flags.DENY_SPAWN);

        if (flag == null) {
            HashSet<EntityType> entityTypes = Sets.newHashSet(EntityType.REGISTRY.get(mob.getName().toLowerCase()));
            region.setFlag(Flags.DENY_SPAWN, entityTypes);
            return;
        }

        if (flag.contains(EntityType.REGISTRY.get(mob.getName().toLowerCase()))) {
            flag.remove(EntityType.REGISTRY.get(mob.getName().toLowerCase()));
        } else {
            flag.add(EntityType.REGISTRY.get(mob.getName().toLowerCase()));
        }
    }


    @Override
    public void addLand(IPossessedLand land) {
        super.addLand(land);
    }

    @Override
    public void removeLand(String name) {
        super.removeLand(name);
    }
}
