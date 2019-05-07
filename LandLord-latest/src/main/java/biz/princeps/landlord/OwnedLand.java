package biz.princeps.landlord;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.IWorldGuardProxy;
import biz.princeps.landlord.util.AOwnedLand;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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

    public static OwnedLand of(ProtectedRegion pr) {
        return new OwnedLand(pr);
    }

    private OwnedLand(ProtectedRegion region) {
        this.region = region;
        this.world = Landlord.getInstance().getWgproxy().getWorld(region.getId());

        // insert default flags
        if (region.getFlags().size() == 0) {
            initFlags();
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
    public boolean contains(int x, int y, int z) {
        return false;
    }

    @Override
    public String toString() {
        return "OwnedLand{" +
                "chunk=" + region.getId() +
                '}';
    }

    @Override
    public Set<ILLFlag> getFlags() {
        Set<ILLFlag> toReturn = new HashSet<>();
        List<String> rawList = Landlord.getInstance().getConfig().getStringList("Flags");

        for (String s : rawList) {
            String[] toggleleft = s.split(":")[0].split(" ");
            String[] toggleRight = s.split(":")[1].split(" ");
            Flag flag = Flags.get(toggleleft[0].toUpperCase());
            Material mat = Material.valueOf(Landlord.getInstance().getConfig()
                    .getString("Manage." + toggleleft[0].toLowerCase() + ".item"));
            StateFlag.State state1 = StateFlag.State.valueOf(toggleleft[1].toLowerCase());
            StateFlag.State state2 = StateFlag.State.valueOf(toggleRight[0].toLowerCase());
            boolean isGroup1 = toggleleft[2].equalsIgnoreCase("nonmembers");
            boolean isGroup2 = toggleRight[1].equalsIgnoreCase("nonmembers");
            toReturn.add(new LLFlag(region, flag, mat, state1, state2, isGroup1, isGroup2));
        }
        return toReturn;
    }

    @Override
    public Object getFlagValue(String flag) {
        System.out.println("Flag value for " +flag);
        if (flag == null) return null;
        Flag wgflag = Flags.get(flag.toUpperCase());
        System.out.println("wgflag" + wgflag);
        if (wgflag == null) return null;
        System.out.println(region.getFlags().keySet());
        return region.getFlag(wgflag);
    }

    @Override
    public void setFlagValue(String flag, Object value) {
        if (flag == null) return;
        Flag wgflag = Flags.get(flag.toUpperCase());
        if (wgflag == null) return;
        region.setFlag(wgflag, value);
    }

    private void initFlags() {
        List<String> rawList = Landlord.getInstance().getConfig().getStringList("Flags");

        for (String s : rawList) {
            String[] s1 = s.split(":")[0].split(" ");

            Flag flag = Flags.get(s1[0].toUpperCase());
            if (!(flag instanceof StateFlag)) {
                Bukkit.getLogger().warning("Only stateflags are supported!");
                return;
            }
            StateFlag.State state = StateFlag.State.valueOf(s1[1].toUpperCase());

            if (s1[2].equalsIgnoreCase("nonmembers")) {
                region.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
            }
            region.setFlag(flag, state);
        }
        // add other flags
        Landlord.getInstance().getPlayerManager().getOfflinePlayerAsync(getOwner(), p -> {
            region.setFlag(Flags.GREET_MESSAGE, Landlord.getInstance().getLangManager()
                    .getRawString("Alerts.defaultGreeting").replace("%owner%", p.getName()));
            region.setFlag(Flags.FAREWELL_MESSAGE, Landlord.getInstance().getLangManager()
                    .getRawString("Alerts.defaultFarewell").replace("%owner%", p.getName()));
        });
    }
}
