package biz.princeps.lib.util;

import com.jcdesimp.landlord.Landlord;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by spatium on 11.06.17.
 */
public class SpigotUtil {

    @Deprecated
    public static Location locationFromString(String s) {
        if (s == null) return null;
        if (s.isEmpty()) return null;
        String[] split = s.split(":");
        Location loc;

        String world = split[0];
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        loc = new Location(Bukkit.getWorld(world), x, y, z);

        return loc;
    }

    @Deprecated
    public static String locationToString(Location loc) {
        if (loc == null) return "";
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        String toPrint = "_" + world + ":" + x + ":" + y + ":" + z;
        return toPrint;
    }

    public static Location exactlocationFromString(String s) {
        if (s == null) return null;
        if (s.isEmpty()) return null;
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
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        String toPrint = world + ":" + x + ":" + y + ":" + z + ":" + yaw + ":" + pitch;
        return toPrint;
    }

    private static Method ichatbasecomponent = null;
    private static Object chatType = null;

    public static void sendActionBar(Player player, String message) {
        try {
            if (ichatbasecomponent == null)
                ichatbasecomponent = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class);
            Object basecomponent = ichatbasecomponent.invoke(null, "{\"text\": \"" + message + "\"}");
            if (chatType == null) {
                Object[] consts = getNMSClass("ChatMessageType").getEnumConstants();
                chatType = consts[2].getClass().getMethod("a", byte.class).invoke(null, (byte) 2);
            }
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType"));
            Object packet = titleConstructor.newInstance(basecomponent, chatType);

            sendPacket(player, packet);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        String version = Landlord.getInstance().getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
