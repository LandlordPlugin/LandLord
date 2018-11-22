package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 22/11/18
 */
public class Util {

    public static String getLocationFormatted(Chunk chunk) {
        String configString = Landlord.getInstance().getConfig().getString("locationFormat");

        int x, z, chunkX = chunk.getX() * 16, chunkZ = chunk.getZ() * 16;
        x = chunkX + 8;
        z = chunkZ + 8;
        /*
        if (chunkX >= 0 && chunkZ >= 0) {
            x = chunkX + 8;
            z = chunkZ + 8;
        }
        if (chunkX < 0 && chunkZ >= 0){
            x = chunkX + 8;
            z = chunkZ + 8;
        }
        if (chunkX < 0 && chunkZ < 0){
            x = chunkX + 8;
            z = chunkZ + 8;
        }
        */

        configString = configString.replace("%world%", chunk.getWorld().getName());
        configString = configString.replace("%x%", x + "");
        configString = configString.replace("%z%", z + "");

        return ChatColor.translateAlternateColorCodes('&', configString);
    }

}
