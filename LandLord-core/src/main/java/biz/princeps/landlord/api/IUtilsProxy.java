package biz.princeps.landlord.api;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public interface IUtilsProxy {

    void send_basecomponent(Player player, BaseComponent[] message);

    void send_fake_block_packet(Player p, Location loc, Material mat);
}
