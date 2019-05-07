package biz.princeps.landlord;

import biz.princeps.landlord.api.IUtilsProxy;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

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
