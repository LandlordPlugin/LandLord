package biz.princeps.landlord.util;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Iterator;
import java.util.UUID;

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

    public static String printOwners(ProtectedRegion pr) {
        StringBuilder sb = new StringBuilder();
        Iterator<UUID> it = pr.getOwners().getUniqueIds().iterator();
        while (it.hasNext()) {
            sb.append(Bukkit.getOfflinePlayer(it.next()).getName());
            if (it.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }

    public static String printMembers(ProtectedRegion pr) {
        StringBuilder sb = new StringBuilder();
        Iterator<UUID> it = pr.getMembers().getUniqueIds().iterator();
        while (it.hasNext()) {
            sb.append(Bukkit.getOfflinePlayer(it.next()).getName());
            if (it.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }
}
