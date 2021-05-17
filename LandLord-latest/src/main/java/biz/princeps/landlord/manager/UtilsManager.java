package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.IUtilsManager;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
        // replaced with latest version
        p.sendBlockChange(loc, mat.createBlockData());
        /*PacketContainer fakeblock = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        fakeblock.getBlockPositionModifier().write(0, new BlockPosition(
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        fakeblock.getBlockData().write(0, WrappedBlockData.createData(mat));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(p, fakeblock);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet " + fakeblock + "! Update ProtocolLib and report back if the error persists!", e);
        }*/
    }
}
