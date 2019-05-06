package biz.princeps.landlord;

import biz.princeps.landlord.api.IUtilsProxy;
import net.md_5.bungee.api.chat.BaseComponent;
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
}
