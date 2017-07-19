package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by spatium on 19.07.17.
 */
public class Utils {

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
