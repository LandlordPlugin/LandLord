package biz.princeps.landlord.crossversion;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 28.07.17.
 */
public interface IActionBar {

    void sendActionBar(Player player, String msg);
}
