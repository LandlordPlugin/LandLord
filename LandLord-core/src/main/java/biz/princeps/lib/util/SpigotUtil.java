package biz.princeps.lib.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by spatium on 11.06.17.
 */
public class SpigotUtil {

    public static Location exactlocationFromString(String s) {
        if (s == null) return null;
        if (s.isEmpty()) return null;
        if (s.equals("null")) return null;
        String[] split = s.split(":");
        Location loc;

        String world = split[0];
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[4]);
        float pitch = Float.parseFloat(split[5]);
        loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);

        return loc;
    }

    public static String exactlocationToString(Location loc) {
        if (loc == null) return "";
        if (loc.getWorld() == null || loc.getWorld().getName() == null) {
            System.out.println("Something is wrong with your world!");
            return "";
        }
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        return world + ":" + MathUtil.round(x, 3) + ":" + MathUtil.round(y, 3) + ":" + MathUtil.round(z, 3) + ":" + yaw + ":" + pitch;
    }
}
