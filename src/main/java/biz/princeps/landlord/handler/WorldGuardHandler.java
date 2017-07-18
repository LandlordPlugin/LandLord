package biz.princeps.landlord.handler;

import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 17.07.17.
 */
public class WorldGuardHandler {

    private WorldGuardPlugin wg;

    public WorldGuardHandler(WorldGuardPlugin wg) {
        this.wg = wg;
    }

    public void claim(Chunk chunk, Player owner) {
        Location down = chunk.getBlock(0, 0, 0).getLocation();
        Location upper = chunk.getBlock(15, 256, 15).getLocation();

        BlockVector vec1 = OwnedLand.locationToVec(down);
        BlockVector vec2 = OwnedLand.locationToVec(upper);

        ProtectedCuboidRegion pr = new ProtectedCuboidRegion(OwnedLand.getLandName(chunk), vec1, vec2);

        DefaultDomain ownerDomain = new DefaultDomain();
        ownerDomain.addPlayer(owner.getUniqueId());
        pr.setOwners(ownerDomain);


        // flag management
        pr = setDefaultFlags(pr);

        RegionManager manager = wg.getRegionContainer().get(chunk.getWorld());

        manager.addRegion(pr);
    }

    public OwnedLand getRegion(Chunk chunk) {
        RegionManager manager = wg.getRegionContainer().get(chunk.getWorld());
        ProtectedRegion pr = manager.getRegion(OwnedLand.getLandName(chunk));
        return (pr != null ? new OwnedLand(pr, chunk) : null);
    }

    public void unclaim(Chunk chunk, String name) {
        wg.getRegionManager(chunk.getWorld()).removeRegion(name);
    }

    public ProtectedCuboidRegion setDefaultFlags(ProtectedCuboidRegion region) {
        // region.setFlag(new StateFlag("build", false, RegionGroup.OWNERS), StateFlag.State.ALLOW);
        //region.setFlag(new StateFlag("interact", false, RegionGroup.OWNERS), StateFlag.State.ALLOW);
        //region.setFlag(new StateFlag("pvp", false, RegionGroup.OWNERS), StateFlag.State.ALLOW);
        //region.setFlag(new StateFlag("tnt", false, RegionGroup.OWNERS), StateFlag.State.ALLOW);


        //   region.setFlag(new StateFlag("build", true), StateFlag.State.DENY);
        //   region.setFlag(new StateFlag("interact", true), StateFlag.State.DENY);
        //   region.setFlag(new StateFlag("pvp", true), StateFlag.State.DENY);
        //region.setFlag(new StateFlag("tnt", true), StateFlag.State.DENY);

        // region.setFlag(new StateFlag("build", true, RegionGroup.OWNERS), StateFlag.State.ALLOW);
        //  region.setFlag(new StateFlag("interact", true, RegionGroup.OWNERS), StateFlag.State.ALLOW);
        //  region.setFlag(new StateFlag("pvp", true, RegionGroup.OWNERS), StateFlag.State.ALLOW);
        //  region.setFlag(new StateFlag("tnt", true, RegionGroup.OWNERS), StateFlag.State.ALLOW);

        region.setFlag(DefaultFlag.BUILD, StateFlag.State.DENY);
        region.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
        return region;
    }

    public WorldGuardPlugin getWG() {
        return wg;
    }
}
