package biz.princeps.landlord;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.protection.AOwnedLand;
import com.google.common.collect.Sets;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

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
        super(pl, pl.getWGManager().getWorld(region.getId()));
        this.region = region;
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
            Flag flag = ((WorldGuardManager) pl.getWGManager()).getFlag(s.toLowerCase());
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
        return region.getFlag(DefaultFlag.GREET_MESSAGE);
    }

    @Override
    public void setGreetMessage(String newmsg) {
        region.setFlag(DefaultFlag.GREET_MESSAGE, newmsg);
    }

    @Override
    public String getFarewellMessage() {
        return region.getFlag(DefaultFlag.FAREWELL_MESSAGE);
    }

    @Override
    public void setFarewellMessage(String newmsg) {
        region.setFlag(DefaultFlag.FAREWELL_MESSAGE, newmsg);
    }

    @Override
    public void toggleMob(IMob mob) {
        Set<EntityType> flag = region.getFlag(DefaultFlag.DENY_SPAWN);

        if (flag == null) {
            HashSet<EntityType> entityTypes = Sets.newHashSet(mob.getType());
            region.setFlag(DefaultFlag.DENY_SPAWN, entityTypes);
            return;
        }

        if (flag.contains(mob.getType())) {
            flag.remove(mob.getType());
        } else {
            flag.add(mob.getType());
        }
    }

    @Override
    public boolean isMobDenied(IMob mob) {
        Set<EntityType> flag = region.getFlag(DefaultFlag.DENY_SPAWN);
        if (flag == null) return false;
        else return flag.contains(mob.getType());
    }

    @Override
    public double getPrice() {
        Double flag = region.getFlag(WorldGuardManager.REGION_PRICE_FLAG);
        if (flag == null) {
            return -1;
        }
        return flag;
    }

    @Override
    public void setPrice(double price) {
        region.setFlag(WorldGuardManager.REGION_PRICE_FLAG, price);
    }

    private void initFlags(UUID owner) {
        List<String> rawList = pl.getConfig().getStringList("Flags");

        for (String s : rawList) {
            Flag flag = ((WorldGuardManager) pl.getWGManager()).getFlag(s.toLowerCase());
            if (!(flag instanceof StateFlag)) {
                Bukkit.getLogger().warning("Only stateflags are supported!");
                return;
            }
            region.setFlag(flag.getRegionGroupFlag(), RegionGroup.MEMBERS);
            region.setFlag(flag, StateFlag.State.ALLOW);
        }
        // add other flags
        pl.getPlayerManager().getOfflinePlayerAsync(owner, p -> {
            region.setFlag(DefaultFlag.GREET_MESSAGE, pl.getLangManager()
                    .getRawString("Alerts.defaultGreeting").replace("%owner%", p.getName()));
            region.setFlag(DefaultFlag.FAREWELL_MESSAGE, pl.getLangManager()
                    .getRawString("Alerts.defaultFarewell").replace("%owner%", p.getName()));
        });
    }
}
