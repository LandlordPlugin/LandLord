package biz.princeps.lib.util;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

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

    public static int getPing(Player p) {
        return ((CraftPlayer) p).getHandle().ping > 0 ? ((CraftPlayer) p).getHandle().ping : -1;
    }

    public static void sendActionBar(Player player, String message) {
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.GAME_INFO);
        p.getHandle().playerConnection.sendPacket(ppoc);
    }


}
