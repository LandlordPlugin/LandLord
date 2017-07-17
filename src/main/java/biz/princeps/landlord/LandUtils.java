package biz.princeps.landlord;

import com.sk89q.worldedit.BlockVector;
import org.bukkit.Chunk;
import org.bukkit.Location;

/**
 * Created by spatium on 17.07.17.
 */
public class LandUtils {

    public static BlockVector locationToVec(Location loc) {
        return new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static String getLandName(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }
}
