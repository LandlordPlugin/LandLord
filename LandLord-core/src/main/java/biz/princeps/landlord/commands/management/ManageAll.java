package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.ManageMode;
import biz.princeps.landlord.guis.ManageGuiAll;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 25/7/17
 * <p>
 * If you are looking for the gui, check AbstractManage
 */
public class ManageAll extends LandlordCommand {

    public ManageAll(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.ManageAll.name"),
                pl.getConfig().getString("CommandSettings.ManageAll.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.ManageAll.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.ManageAll.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();

        List<IOwnedLand> lands = Lists.newArrayList(plugin.getWGManager().getRegions(player.getUniqueId()));

        if (lands.size() == 0) {
            lm.sendMessage(player, plugin.getLangManager().getString("Commands.ListLands.noLands"));
            return;
        }

        ManageGuiAll gui = new ManageGuiAll(
                plugin, player, lands, ManageMode.ALL, null, -1);
        gui.display();
    }
}
