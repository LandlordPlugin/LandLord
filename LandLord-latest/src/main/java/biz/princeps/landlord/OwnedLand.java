package biz.princeps.landlord;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.manager.WorldGuardManager;
import biz.princeps.landlord.protection.AOwnedLand;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class OwnedLand extends AOwnedLand {

    private static final Map<String, Flag<StateFlag.State>> FLAGS_CACHE = new ConcurrentHashMap<>();

    private final ProtectedRegion region;
    private final FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();

    public static OwnedLand create(ILandLord plugin, ProtectedRegion pr, UUID owner) {
        return new OwnedLand(plugin, pr, owner);
    }

    public static OwnedLand of(ILandLord plugin, ProtectedRegion pr) {
        return new OwnedLand(plugin, pr);
    }

    private OwnedLand(ILandLord plugin, ProtectedRegion region) {
        super(plugin, plugin.getWGManager().getWorld(region.getId()), region.getId());
        this.region = region;
    }

    private OwnedLand(ILandLord plugin, ProtectedRegion region, UUID owner) {
        this(plugin, region);

        // Insert default flags.
        if (region.getFlags().size() == 0) {
            initFlags(owner);
            initRegionPriority();
        }
    }

    @Override
    public int getMaxY() {
        return region.getMaximumPoint().getY();
    }

    @Override
    public int getMinY() {
        return region.getMinimumPoint().getY();
    }

    @Override
    public String getOwnersString() {
        return formatNames(region.getOwners().getUniqueIds());
    }

    @Override
    public String getMembersString() {
        return formatNames(region.getMembers().getUniqueIds());
    }

    @Override
    public boolean isOwner(UUID uuid) {
        return region.getOwners().contains(uuid);
    }

    @Override
    public UUID getOwner() {
        Set<UUID> uniqueIds = region.getOwners().getUniqueIds();
        if (uniqueIds.size() != 1) {
            plugin.getLogger().warning("The region " + getName() + " is faulty! It does not have an owner!");
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
        return region.getMembers().contains(uuid);
    }

    @Override
    public Set<UUID> getFriends() {
        return region.getMembers().getUniqueIds();
    }

    @Override
    public void addFriend(UUID uuid) {
        region.getMembers().addPlayer(uuid);
    }

    @Override
    public void removeFriend(UUID uuid) {
        region.getMembers().removePlayer(uuid);
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
                "chunk=" + name +
                '}';
    }

    @Override
    public List<ILLFlag> getFlags() {
        List<ILLFlag> toReturn = new ArrayList<>();
        List<String> rawList = plugin.getConfig().getStringList("Flags");

        for (String s : rawList) {
            toReturn.add(getFlag(s));
        }
        return toReturn;
    }

    @Override
    public ILLFlag getFlag(String s) {
        Flag<StateFlag.State> flag = getWGFlag(s.toLowerCase());
        if (flag == null) {
            plugin.getLogger().warning("Invalid worldguard flag found: " + s);

        }
        Material mat = Material.valueOf(plugin.getConfig()
                .getString("Manage." + s.toLowerCase() + ".item"));

        return new LLFlag(region, flag, mat);
    }

    @Override
    public String getGreetMessage() {
        String greetMessage = region.getFlag(Flags.GREET_MESSAGE);
        return greetMessage == null ? "" : greetMessage;
    }

    @Override
    public void setGreetMessage(String newmsg) {
        region.setFlag(Flags.GREET_MESSAGE, newmsg);
    }

    @Override
    public String getFarewellMessage() {
        String farewellMessage = region.getFlag(Flags.FAREWELL_MESSAGE);
        return farewellMessage == null ? "" : farewellMessage;
    }

    @Override
    public void setFarewellMessage(String newmsg) {
        region.setFlag(Flags.FAREWELL_MESSAGE, newmsg);
    }

    @Override
    public void toggleMob(IMob mob) {
        Set<EntityType> flag = region.getFlag(Flags.DENY_SPAWN);

        if (flag == null) {
            Set<EntityType> entityTypes = Sets.newHashSet(EntityType.REGISTRY.get(mob.getName().toLowerCase()));
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
    public boolean isMobDenied(IMob mob) {
        Set<EntityType> flag = region.getFlag(Flags.DENY_SPAWN);
        if (flag == null) {
            return false;
        } else {
            return flag.contains(EntityType.REGISTRY.get(mob.getName().toLowerCase()));
        }
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
        List<String> rawList = plugin.getConfig().getStringList("Flags");

        for (String s : rawList) {
            Flag<StateFlag.State> flag = getWGFlag(s.toUpperCase());
            if (!(flag instanceof StateFlag)) {
                plugin.getLogger().warning("Only stateflags are supported!");
                return;
            }
            region.setFlag(flag.getRegionGroupFlag(), RegionGroup.MEMBERS);
            region.setFlag(flag, StateFlag.State.ALLOW);

            // some combinations are illegal (all true, friends false)
            if (!plugin.getConfig().getBoolean("Manage." + s + ".default.friends", true)) {
                ILLFlag flag1 = getFlag(s);
                flag1.toggleFriends();
            }

            if (plugin.getConfig().getBoolean("Manage." + s + ".default.everyone", false)) {
                ILLFlag flag1 = getFlag(s);
                flag1.toggleAll();
            }
        }
        // add other flags
        OfflinePlayer p = plugin.getServer().getOfflinePlayer(owner);
        if (p.getName() == null) {
            return;
        }
        region.setFlag(Flags.GREET_MESSAGE, plugin.getLangManager()
                .getRawString("Alerts.defaultGreeting").replace("%owner%", p.getName()));
        region.setFlag(Flags.FAREWELL_MESSAGE, plugin.getLangManager()
                .getRawString("Alerts.defaultFarewell").replace("%owner%", p.getName()));
    }

    @Override
    public void updateFlags(UUID owner) {
        List<String> rawList = plugin.getConfig().getStringList("Flags");

        // remove flags, that are no longer required
        for (Flag<?> iWrapperFlag : region.getFlags().keySet()) {
            //Flags names has -group as suffix :x
            String flagname = iWrapperFlag.getName().toLowerCase().replace("-group", "");
            if (!rawList.contains(flagname) &&
                    !flagname.equals(Flags.GREET_MESSAGE.getName().toLowerCase()) &&
                    !flagname.equals(Flags.FAREWELL_MESSAGE.getName().toLowerCase())) {

                region.setFlag(iWrapperFlag, null);
            }
        }

        // add missing flags
        for (String s : rawList) {
            Flag<StateFlag.State> flag = getWGFlag(s.toLowerCase());
            if (!region.getFlags().containsKey(flag)) {
                region.setFlag(flag.getRegionGroupFlag(), RegionGroup.MEMBERS);
                region.setFlag(flag, StateFlag.State.ALLOW);

                // some combinations are illegal (all true, friends false)
                if (!plugin.getConfig().getBoolean("Manage." + s + ".default.friends", true)) {
                    ILLFlag flag1 = getFlag(s);
                    flag1.toggleFriends();
                }

                if (plugin.getConfig().getBoolean("Manage." + s + ".default.everyone", false)) {
                    ILLFlag flag1 = getFlag(s);
                    flag1.toggleAll();
                }
            }
        }
        // add other flags
        OfflinePlayer p = plugin.getServer().getOfflinePlayer(owner);
        if (p.getName() == null) {
            return;
        }
        if (!region.getFlags().containsKey(Flags.GREET_MESSAGE)) {
            region.setFlag(Flags.GREET_MESSAGE,
                    plugin.getLangManager().getRawString("Alerts.defaultGreeting").replace("%owner%", p.getName()));
        } else if (!region.getFlags().containsKey(Flags.FAREWELL_MESSAGE)) {
            region.setFlag(Flags.FAREWELL_MESSAGE,
                    plugin.getLangManager().getRawString("Alerts.defaultFarewell").replace("%owner%", p.getName()));
        }
    }

    @Override
    public void reclaim() {
        IWorldGuardManager wg = plugin.getWGManager();
        UUID owner = getOwner();
        OwnedLand ownedLand = (OwnedLand) wg.claim(getChunk(), owner);
        ownedLand.getRegion().copyFrom(this.region);
    }

    @Override
    public void initRegionPriority() {
        region.setPriority(plugin.getConfig().getInt("Claim.regionPriority"));
    }

    private ProtectedRegion getRegion() {
        return region;
    }

    private Flag<StateFlag.State> getWGFlag(String flagName) {
        return FLAGS_CACHE.computeIfAbsent(flagName, name -> (Flag<StateFlag.State>) Flags.fuzzyMatchFlag(flagRegistry, name));
    }

}
