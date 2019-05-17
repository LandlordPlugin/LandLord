package biz.princeps.landlord;

import biz.princeps.landlord.api.ILand;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IPossessedLand;
import biz.princeps.landlord.protection.AWorldGuardManager;
import biz.princeps.landlord.protection.FreeLand;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class WorldGuardManager extends AWorldGuardManager {

    public static final Flag LL_FLAG = new StateFlag("landlord", true);

    private WorldGuardPlugin wgPlugin;
    private WorldGuard wg;

    public WorldGuardManager(ILandLord pl, WorldGuardPlugin worldGuard) {
        super(pl);
        this.wg = WorldGuard.getInstance();
        this.wgPlugin = worldGuard;
    }

    public void initFlags() {
        FlagRegistry registry = wg.getFlagRegistry();
        try {
            registry.register(LL_FLAG);
        } catch (FlagConflictException e) {
            pl.getLogger().warning("Flag could not be registered: " + e);
        }
    }

    //TODO check performance of sync loading
    void initCache() {
        for (World world : Bukkit.getWorlds()) {
            RegionManager manager = getRegionManager(world);
            for (ProtectedRegion value : manager.getRegions().values()) {

            }
        }
    }

    @Override
    public Set<?> getAllWGRegions(World world) {
        Map<String, ProtectedRegion> regions = new HashMap<>(getRegionManager(world).getRegions());
        regions.keySet().forEach(r -> {

        });
        return new HashSet<>(regions.values());
    }

    @Override
    public Set<?> getAllWGRegions() {
        Set<ProtectedRegion> set = new HashSet<>();
        Bukkit.getWorlds().forEach(w -> {
            Set<?> allWGRegions = getAllWGRegions(w);
            set.addAll(((Set<ProtectedRegion>) allWGRegions));
        });
        return set;
    }


    @NotNull
    @Override
    public ILand getRegion(Location loc) {
        if (loc.getWorld() != null) {
            ApplicableRegionSet ar = getRegionManager(loc.getWorld()).getApplicableRegions(locationToVec(loc));
            System.out.println(ar.getRegions());
            if (ar.size() != 0 && ar.testState(null, ((StateFlag) LL_FLAG))) {
                return getRegion(ar.getRegions().iterator().next().getId());
            }
        }
        return new FreeLand(loc.getWorld(), getChunkName(loc.getChunk()));
    }


    private RegionContainer getRegionContainer() {
        return wg.getPlatform().getRegionContainer();
    }

    private RegionManager getRegionManager(World world) {
        com.sk89q.worldedit.world.World worldByName = wg.getPlatform().getMatcher().getWorldByName(world.getName());
        RegionContainer regionContainer = getRegionContainer();
        return regionContainer.get(worldByName);
    }

    private BlockVector3 locationToVec(Location loc) {
        return BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }


}
