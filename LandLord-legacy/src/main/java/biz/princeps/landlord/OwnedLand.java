package biz.princeps.landlord;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.manager.WorldGuardManager;
import biz.princeps.landlord.protection.AOwnedLand;
import com.google.common.collect.Sets;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
    private FlagRegistry flagRegistry = WorldGuardPlugin.inst().getFlagRegistry();

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
            initRegionPriority();
        }
    }

    @Override
    public String getOwnersString() {
        Set<UUID> itt = region.getOwners().getUniqueIds();
        Set<String> names = new HashSet<>();
        // ugly, maybe solve this in the future
        itt.forEach(u -> names.add(pl.getPlayerManager().getOfflineSync(u).getName()));
        return itToString(names.iterator());
    }

    @Override
    public String getMembersString() {
        Set<UUID> itt = region.getMembers().getUniqueIds();
        Set<String> names = new HashSet<>();
        // ugly, maybe solve this in the future
        itt.forEach(u -> names.add(pl.getPlayerManager().getOfflineSync(u).getName()));
        return itToString(names.iterator());
    }

    private String itToString(Iterator<String> it) {
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            sb.append(it.next());
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
        Set<UUID> uniqueIds = region.getOwners().getUniqueIds();
        if (uniqueIds.size() != 1) {
            pl.getLogger().warning("The region " + getName() + " is faulty! It does not have an owner or the name matches a ll region");
            return null;
        } else {
            return uniqueIds.iterator().next();
        }
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
            Flag flag = getFlag(s.toLowerCase());
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
        String greetMessage = region.getFlag(DefaultFlag.GREET_MESSAGE);
        return greetMessage == null ? "" : greetMessage;
    }

    @Override
    public void setGreetMessage(String newmsg) {
        region.setFlag(DefaultFlag.GREET_MESSAGE, newmsg);
    }

    @Override
    public String getFarewellMessage() {
        String farewellMessage = region.getFlag(DefaultFlag.FAREWELL_MESSAGE);
        return farewellMessage == null ? "" : farewellMessage;
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

    @Override
    public void initFlags(UUID owner) {
        List<String> rawList = pl.getConfig().getStringList("Flags");

        for (String s : rawList) {
            Flag flag = getFlag(s.toLowerCase());
            if (!(flag instanceof StateFlag)) {
                Bukkit.getLogger().warning("Only stateflags are supported!");
                return;
            }
            region.setFlag(flag.getRegionGroupFlag(), RegionGroup.MEMBERS);
            region.setFlag(flag, StateFlag.State.ALLOW);
        }
        // add other flags
        OfflinePlayer p = Bukkit.getOfflinePlayer(owner);
        if (p.getName() == null) {
            return;
        }
        region.setFlag(DefaultFlag.GREET_MESSAGE, pl.getLangManager()
                .getRawString("Alerts.defaultGreeting").replace("%owner%", p.getName()));
        region.setFlag(DefaultFlag.FAREWELL_MESSAGE, pl.getLangManager()
                .getRawString("Alerts.defaultFarewell").replace("%owner%", p.getName()));
    }

    @Override
    public void updateFlags(UUID owner) {
        List<String> rawList = pl.getConfig().getStringList("Flags");

        // remove flags, that are no longer required
        for (Flag<?> iWrapperFlag : region.getFlags().keySet()) {

            String flagname = iWrapperFlag.getName().toLowerCase().replace("-group", "");
            if (!rawList.contains(flagname) &&
                    !flagname.equals(DefaultFlag.GREET_MESSAGE.getName().toLowerCase()) &&
                    !flagname.equals(DefaultFlag.FAREWELL_MESSAGE.getName().toLowerCase())) {

                region.setFlag(iWrapperFlag, null);
            }
        }

        // add missing flags
        for (String s : rawList) {
            Flag flag = getFlag(s.toLowerCase());
            if (!region.getFlags().containsKey(flag)) {
                region.setFlag(flag.getRegionGroupFlag(), RegionGroup.MEMBERS);
                region.setFlag(flag, StateFlag.State.ALLOW);
            }
        }
        // add other flags
        OfflinePlayer p = Bukkit.getOfflinePlayer(owner);
        if (p.getName() == null) {
            return;
        }
        if (!region.getFlags().containsKey(DefaultFlag.GREET_MESSAGE)) {
            region.setFlag(DefaultFlag.GREET_MESSAGE,
                    pl.getLangManager().getRawString("Alerts.defaultGreeting").replace("%owner%", p.getName()));
        } else if (!region.getFlags().containsKey(DefaultFlag.FAREWELL_MESSAGE)) {
            region.setFlag(DefaultFlag.FAREWELL_MESSAGE,
                    pl.getLangManager().getRawString("Alerts.defaultFarewell").replace("%owner%", p.getName()));
        }
    }

    @Override
    public void initRegionPriority() {
        region.setPriority(pl.getConfig().getInt("Claim.regionPriority"));
    }

    private Flag getFlag(String flagName) {
        return DefaultFlag.fuzzyMatchFlag(flagRegistry, flagName);
    }
}
