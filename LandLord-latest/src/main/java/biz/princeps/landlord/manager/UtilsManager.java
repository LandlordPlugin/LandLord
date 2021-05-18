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
        p.sendBlockChange(loc, mat.createBlockData());
    }
}
