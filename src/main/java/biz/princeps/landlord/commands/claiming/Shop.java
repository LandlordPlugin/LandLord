package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ShopGUI;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 25/07/2017
 */
public class Shop extends LandlordCommand {


    public void onShop(Player player) {

        if (Options.enabled_shop() && Options.isVaultEnabled()) {
            int regionCount = plugin.getWgHandler().getRegionCountOfPlayer(player.getUniqueId());
            int claims = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();

            ShopGUI gui = new ShopGUI(player, plugin.getLangManager().getRawString("Shop.title")
                    .replace("%regions%", regionCount + "")
                    .replace("%claims%", claims + ""));
            gui.display();
        }
    }
}
