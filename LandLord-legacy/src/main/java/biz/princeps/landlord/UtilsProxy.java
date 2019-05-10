package biz.princeps.landlord;

import biz.princeps.landlord.api.IUtilsProxy;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class UtilsProxy implements IUtilsProxy {

    @Override
    public void send_basecomponent(Player player, BaseComponent[] message) {
        player.spigot().sendMessage(message);
    }

    @Override
    public void send_fake_block_packet(Player p, Location loc, Material mat) {
        // jup i wanna kill myself for that.
        for (WorldServer world : MinecraftServer.getServer().worlds) {
            if (world.worldData.getName().equals(loc.getWorld().getName())) {
                PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(world,
                        new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

                packet.block = Block.getById(mat.getId()).getBlockData();
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }
}
