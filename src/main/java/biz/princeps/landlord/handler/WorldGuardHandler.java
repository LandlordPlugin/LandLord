package biz.princeps.landlord.handler;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Chunk;
import org.bukkit.Location;

import static biz.princeps.landlord.LandUtils.getLandName;
import static biz.princeps.landlord.LandUtils.locationToVec;

/**
 * Created by spatium on 17.07.17.
 */
public class WorldGuardHandler {

    private WorldGuardPlugin wg;

    public WorldGuardHandler(WorldGuardPlugin wg) {
        this.wg = wg;
    }

    public void claim(Chunk chunk) {
        Location down = chunk.getBlock(0, 0, 0).getLocation();
        Location upper = chunk.getBlock(15, 256, 15).getLocation();

        BlockVector vec1 = locationToVec(down);
        BlockVector vec2 = locationToVec(upper);

        ProtectedCuboidRegion pr = new ProtectedCuboidRegion(getLandName(chunk), vec1, vec2);

        RegionManager manager = wg.getRegionContainer().get(chunk.getWorld());
        manager.addRegion(pr);
    }


}
