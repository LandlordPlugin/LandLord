package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ShopGUI;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 25/07/2017
 */
public class Shop extends LandlordCommand {

    public Shop(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Shop.name"),
                plugin.getConfig().getString("CommandSettings.Shop.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Shop.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Shop.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();

        if (Options.enabled_shop() && Options.isVaultEnabled()) {
            int regionCount = plugin.getWGManager().getRegionCount(player.getUniqueId());
            int claims = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();

            ShopGUI gui = new ShopGUI(plugin, player, plugin.getLangManager().getRawString("Shop.title")
                    .replace("%regions%", regionCount + "")
                    .replace("%claims%", claims + ""));
            gui.display();
        }
    }
}
