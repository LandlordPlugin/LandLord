package biz.princeps.landlord;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.util.AOwnedLand;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class OwnedLand extends AOwnedLand {

    private ProtectedRegion region;

    public static OwnedLand create(ILandLord pl, ProtectedRegion pr, UUID owner) {
        return new OwnedLand(pl, pr, owner);
    }

    public static OwnedLand of(ILandLord pl, ProtectedRegion pr) {
        return new OwnedLand(pl, pr);
    }

    private OwnedLand(ILandLord pl, ProtectedRegion region) {
        super(pl, pl.getWGProxy().getWorld(region.getId()));
        this.region = region;
        this.world = pl.getWGProxy().getWorld(region.getId());
    }

    private OwnedLand(ILandLord pl, ProtectedRegion region, UUID owner) {
        this(pl, region);

        // insert default flags
        if (region.getFlags().size() == 0) {
            initFlags(owner);
        }
    }

    @Override
    public String getOwnersString() {
        Iterator<UUID> it = region.getOwners().getUniqueIds().iterator();
        return itToString(it);
    }

    @Override
    public String getMembersString() {
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
    public void replaceOwner(UUID uuid) {
        region.getOwners().clear();
        region.getOwners().addPlayer(uuid);
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
    public World getWorld() {
        return world;
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
            System.out.println(s + ": " + flag + ":" + pl.getConfig()
                    .getString("Manage." + s.toLowerCase() + ".item"));
            Material mat = Material.valueOf(pl.getConfig()
                    .getString("Manage." + s.toLowerCase() + ".item"));
            if (region.getFlags().containsKey(flag))
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
            HashSet<EntityType> entityTypes = Sets.newHashSet(EntityType.REGISTRY.get(mob.getName()));
            region.setFlag(Flags.DENY_SPAWN, entityTypes);
            return;
        }

        if (flag.contains(EntityType.REGISTRY.get(mob.getName()))) {
            flag.remove(EntityType.REGISTRY.get(mob.getName()));
        } else {
            flag.add(EntityType.REGISTRY.get(mob.getName()));
        }
    }

    @Override
    public boolean isMobDenied(IMob mob) {
        return Objects.requireNonNull(region.getFlag(Flags.DENY_SPAWN)).contains(EntityType.REGISTRY.get(mob.getName()));
    }

    //@Override
    public Object getFlagValue(String flag) {
        if (flag == null) return null;
        Flag wgflag = Flags.get(flag.toLowerCase());
        if (wgflag == null) return null;
        return region.getFlag(wgflag);
    }

    public void setGroupFlag(String flag) {
        if (flag == null) return;
        Flag wgflag = Flags.get(flag.toLowerCase());
        if (wgflag == null) return;
        region.setFlag(wgflag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);

    }

    //@Override
    public void setFlagValue(String flag, String grp, Object value) {
        if (flag == null) return;
        Flag wgflag = Flags.get(flag.toLowerCase());
        if (wgflag == null) return;
        region.setFlag(wgflag, value);
        if (grp != null)
            region.setFlag(wgflag.getRegionGroupFlag(), RegionGroup.valueOf(grp.toUpperCase()));
    }

    //@Override
    public void removeFlag(String flag) {
        if (flag == null) return;
        Flag wgflag = Flags.get(flag.toLowerCase());
        if (wgflag == null) return;
        region.getFlags().remove(wgflag);
    }

    //@Override
    public boolean containsFlag(String flag) {
        if (flag == null) return false;
        Flag wgflag = Flags.get(flag.toLowerCase());
        if (wgflag == null) return false;
        return region.getFlags().containsKey(wgflag);
    }


    private void initFlags(UUID owner) {
        List<String> rawList = pl.getConfig().getStringList("Flags");

        for (String s : rawList) {
            String[] s1 = s.split(":")[0].split(" ");

            Flag flag = Flags.get(s1[0].toLowerCase());
            if (!(flag instanceof StateFlag)) {
                Bukkit.getLogger().warning("Only stateflags are supported!");
                return;
            }
            StateFlag.State state = StateFlag.State.valueOf(s1[1].toUpperCase());

            if (s1[2].equalsIgnoreCase("nonmembers")) {
                region.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
            } else {
                region.setFlag(flag.getRegionGroupFlag(), RegionGroup.ALL);
            }
            region.setFlag(flag, state);
        }
        // add other flags
        pl.getPlayerManager().getOfflinePlayerAsync(owner, p -> {
            region.setFlag(Flags.GREET_MESSAGE, pl.getLangManager()
                    .getRawString("Alerts.defaultGreeting").replace("%owner%", p.getName()));
            region.setFlag(Flags.FAREWELL_MESSAGE, pl.getLangManager()
                    .getRawString("Alerts.defaultFarewell").replace("%owner%", p.getName()));
        });
    }
}
