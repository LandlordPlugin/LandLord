package biz.princeps.landlord.commands;

import biz.princeps.landlord.guis.ShopGUI;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 25.07.17.
 */
public class Shop extends LandlordCommand {


    public void onShop(Player player) {

        if (plugin.getConfig().getBoolean("Shop.enable")) {
            int regionCount = plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegionCountOfPlayer(plugin.getWgHandler().getWG().wrapPlayer(player));
            int claims = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();

            ShopGUI gui = new ShopGUI(player, plugin.getLangManager().getRawString("Shop.title")
                    .replace("%regions%", regionCount + "")
                    .replace("%claims%", claims + ""));
            gui.display();
        }
    }
}
