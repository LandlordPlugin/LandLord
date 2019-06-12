package biz.princeps.landlord;

import biz.princeps.landlord.api.IUtilsManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class UtilsManager implements IUtilsManager {

    @Override
    public void sendBasecomponent(Player player, BaseComponent[] message) {
        player.spigot().sendMessage(message);
    }

    @Override
    public void sendFakeBlockPacket(Player p, Location loc, Material mat) {
        if (Bukkit.getServer().getVersion().contains("1.14")) return; //Waiting for a 1.14 ProtocolLib version.

        PacketContainer fakeblock = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        fakeblock.getBlockPositionModifier().write(0, new BlockPosition(
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        fakeblock.getBlockData().write(0, WrappedBlockData.createData(mat));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(p, fakeblock);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet " + fakeblock, e);
        }
    }
}
